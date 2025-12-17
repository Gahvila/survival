package net.gahvila.survival.DailyRTP;

import net.crashcraft.crashclaim.api.CrashClaimAPI;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.time.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.survival.survival.instance;

public class DrtpManager {

    private Location dailyTeleportLocation;
    private long nextRerollTime;

    private final File dataFile;
    private final FileConfiguration dataConfig;

    private static final Set<Material> UNSAFE_BLOCKS = new HashSet<>(Arrays.asList(
            Material.LAVA, Material.FIRE, Material.CACTUS, Material.MAGMA_BLOCK,
            Material.SWEET_BERRY_BUSH, Material.WITHER_ROSE, Material.WATER, Material.POWDER_SNOW,
            Material.ICE, Material.BLUE_ICE, Material.PACKED_ICE
    ));

    private static final Set<Tag<Material>> UNSAFE_TAGS = new HashSet<>(Arrays.asList(
            Tag.FENCES, Tag.FENCE_GATES, Tag.WALLS, Tag.BUTTONS, Tag.PRESSURE_PLATES, Tag.LEAVES
    ));

    public DrtpManager() {
        this.dataFile = new File(instance.getDataFolder(), "drtp.yml");
        if (!dataFile.exists()) {
            try {
                boolean created = dataFile.createNewFile();
                instance.getLogger().info("Created new drtp.yml file: " + created);
            } catch (IOException e) {
                instance.getLogger().severe("Failed to create drtp.yml!");
                e.printStackTrace();
            }
        }

        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        instance.getLogger().info("Loading saved data...");
        loadData();

        if (this.dailyTeleportLocation == null) {
            instance.getLogger().warning("No daily RTP location in drtp.yml. Generating new one...");
            findNewTeleportLocation();
        } else {
            instance.getLogger().info("Loaded saved location: " + locationToString(this.dailyTeleportLocation));
        }

        scheduleTasks();
    }

    private void scheduleTasks() {
        instance.getLogger().info("Scheduling reroll check task...");
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (now >= nextRerollTime) {
                    instance.getLogger().info("Reroll time! Finding new daily RTP location...");
                    findNewTeleportLocation();
                }
            }
        }.runTaskTimer(instance, 20L * 60, 20L * 60); // check every minute
    }

    public void findNewTeleportLocation() {
        this.dailyTeleportLocation = null;
        instance.getLogger().info("Starting async search for new teleport location...");

        CompletableFuture.runAsync(() -> {
            try {
                World world = Bukkit.getWorld("world");
                if (world == null) {
                    instance.getLogger().severe("World 'world' does not exist!");
                    return;
                }

                int searchRadius = 50000;
                int claimCheckRadius = 100;
                Location candidateLocation = null;
                boolean validLocationFound = false;
                int attempts = 0;

                while (!validLocationFound && attempts < 500) {
                    attempts++;
                    int x = ThreadLocalRandom.current().nextInt(-searchRadius, searchRadius + 1);
                    int z = ThreadLocalRandom.current().nextInt(-searchRadius, searchRadius + 1);
                    if (attempts % 50 == 0) {
                        instance.getLogger().info("Attempt " + attempts + "...");
                    }

                    Location testLocation = world.getHighestBlockAt(x, z).getLocation().add(0.5, 1, 0.5);

                    try {
                        if (isLocationSafe(testLocation)) {
                            if (!isClaimInRadius(testLocation, claimCheckRadius)) {
                                validLocationFound = true;
                                candidateLocation = testLocation;
                                instance.getLogger().info("Found safe location at " + locationToString(testLocation));
                            } else {
                                instance.getLogger().fine("Location " + x + "," + z + " was in claimed land.");
                            }
                        }
                    } catch (Exception e) {
                        instance.getLogger().warning("Exception during safety check at " + x + "," + z + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                if (validLocationFound && candidateLocation != null) {
                    Location finalLoc = candidateLocation;
                    Bukkit.getScheduler().runTask(instance, () -> {
                        this.dailyTeleportLocation = finalLoc;
                        this.nextRerollTime = getNextMidnightMillis();
                        saveData();
                        instance.getLogger().info("New Daily RTP location set: " + locationToString(finalLoc));
                        Bukkit.broadcast(toMM("Päivän uusi teleporttipaikka on arvottu!"));
                    });
                } else {
                    instance.getLogger().warning("Failed to find a safe location after 500 attempts!");
                }
            } catch (Exception e) {
                instance.getLogger().severe("Unexpected error while finding new teleport location!");
                e.printStackTrace();
            }
        });
    }

    private long getNextMidnightMillis() {
        try {
            ZoneId helsinki = ZoneId.of("Europe/Helsinki");
            ZonedDateTime now = ZonedDateTime.now(helsinki);
            ZonedDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(helsinki);
            return nextMidnight.toInstant().toEpochMilli();
        } catch (Exception e) {
            instance.getLogger().severe("Error calculating next reroll time: " + e.getMessage());
            return System.currentTimeMillis() + 86400000L;
        }
    }

    private boolean isLocationSafe(Location location) {
        if (location == null || location.getWorld() == null) {
            instance.getLogger().fine("Location or world was null");
            return false;
        }

        try {
            Block blockBelow = location.clone().subtract(0, 1, 0).getBlock();
            Block feetBlock = location.getBlock();
            Block headBlock = location.clone().add(0, 1, 0).getBlock();

            Biome biome = blockBelow.getBiome();
            if (biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.FROZEN_OCEAN ||
                    biome == Biome.RIVER || biome == Biome.LUSH_CAVES) {
                return false;
            }

            if (!blockBelow.getType().isSolid() || UNSAFE_BLOCKS.contains(blockBelow.getType())) {
                return false;
            }

            for (Tag<Material> tag : UNSAFE_TAGS) {
                if (tag.isTagged(blockBelow.getType())) return false;
            }

            return isSafeAirLike(feetBlock) && isSafeAirLike(headBlock);
        } catch (Exception e) {
            instance.getLogger().warning("Error checking location safety: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean isSafeAirLike(Block block) {
        return !block.getType().isSolid() && !UNSAFE_BLOCKS.contains(block.getType());
    }

    public boolean isClaimInRadius(Location center, int radius) {
        if (isClaimAt(center)) return true;

        if (isClaimAt(center.clone().add(radius, 0, radius))) return true;
        if (isClaimAt(center.clone().add(-radius, 0, radius))) return true;
        if (isClaimAt(center.clone().add(radius, 0, -radius))) return true;
        if (isClaimAt(center.clone().add(-radius, 0, -radius))) return true;

        return false;
    }

    private boolean isClaimAt(Location location) {
        if (instance.getCrashClaim() == null) {
            instance.getLogger().warning("CrashClaim not found! Assuming no claim.");
            return false;
        }

        return instance.getCrashClaim().getApi().getClaim(location) != null;
    }

    public void loadData() {
        try {
            if (dataConfig.contains("location")) {
                this.dailyTeleportLocation = dataConfig.getLocation("location");
                instance.getLogger().info("Loaded location from drtp.yml: " + locationToString(dailyTeleportLocation));
            }
            this.nextRerollTime = dataConfig.getLong("next-reroll-time", getNextMidnightMillis());
            instance.getLogger().info("Next reroll time: " + Instant.ofEpochMilli(nextRerollTime));
        } catch (Exception e) {
            instance.getLogger().severe("Error loading drtp.yml!");
            e.printStackTrace();
        }
    }

    public void saveData() {
        try {
            dataConfig.set("location", this.dailyTeleportLocation);
            dataConfig.set("next-reroll-time", this.nextRerollTime);
            dataConfig.save(dataFile);
            instance.getLogger().info("Saved new drtp.yml successfully.");
        } catch (IOException e) {
            instance.getLogger().severe("Could not save drtp.yml!");
            e.printStackTrace();
        }
    }

    public Location getDailyTeleportLocation() {
        return dailyTeleportLocation;
    }

    private String locationToString(Location loc) {
        if (loc == null) return "N/A";
        return String.format("%s, X: %.1f, Y: %.1f, Z: %.1f",
                loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
    }
}