package net.gahvila.survival.DailyRTP;

import net.crashcraft.crashclaim.api.CrashClaimAPI;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.survival.survival.instance;

public class DrtpManager {

    private Location dailyTeleportLocation;
    private long nextRerollTime;

    private final File dataFile;
    private final FileConfiguration dataConfig;

    private final Map<Long, List<Location>> chunkParticleMap = new ConcurrentHashMap<>();
    private final Particle.DustOptions borderStyle = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.5f);

    private static final int BORDER_RADIUS = 100;
    private static final double PARTICLE_SPACING = 1.0;
    private static final int VIEW_RADIUS_CHUNKS = 4;

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
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadData();

        if (this.dailyTeleportLocation == null) {
            findNewTeleportLocation();
        } else {
            cacheDrtpBorder(this.dailyTeleportLocation);
        }

        scheduleTasks();
        startVisualizationTask();
    }

    private void scheduleTasks() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (now >= nextRerollTime) {
                    findNewTeleportLocation();
                }
            }
        }.runTaskTimer(instance, 20L * 60, 20L * 60);
    }

    private void startVisualizationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (chunkParticleMap.isEmpty()) return;

                Set<Long> chunksToRender = new HashSet<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.getWorld().getName().equals("world")) continue;

                    int px = p.getLocation().getBlockX() >> 4;
                    int pz = p.getLocation().getBlockZ() >> 4;

                    for (int x = px - VIEW_RADIUS_CHUNKS; x <= px + VIEW_RADIUS_CHUNKS; x++) {
                        for (int z = pz - VIEW_RADIUS_CHUNKS; z <= pz + VIEW_RADIUS_CHUNKS; z++) {
                            chunksToRender.add(getChunkKey(x, z));
                        }
                    }
                }

                for (Long chunkKey : chunksToRender) {
                    List<Location> points = chunkParticleMap.get(chunkKey);
                    if (points != null) {
                        for (Location loc : points) {
                            loc.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, borderStyle);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(instance, 0L, 10L);
    }

    public void findNewTeleportLocation() {
        this.dailyTeleportLocation = null;
        this.chunkParticleMap.clear();

        instance.getLogger().info("Starting async search for new teleport location...");

        CompletableFuture.runAsync(() -> {
            try {
                World world = Bukkit.getWorld("world");
                if (world == null) return;

                int searchRadius = 50000;
                Location candidateLocation = null;
                boolean validLocationFound = false;
                int attempts = 0;

                while (!validLocationFound && attempts < 500) {
                    attempts++;
                    int x = ThreadLocalRandom.current().nextInt(-searchRadius, searchRadius + 1);
                    int z = ThreadLocalRandom.current().nextInt(-searchRadius, searchRadius + 1);

                    Location testLocation = null;
                    try {
                        testLocation = CompletableFuture.supplyAsync(() ->
                                world.getHighestBlockAt(x, z).getLocation().add(0.5, 1, 0.5), instance.getServer().getScheduler().getMainThreadExecutor(instance)
                        ).join();
                    } catch (Exception e) { continue; }

                    if (isLocationSafe(testLocation)) {
                        if (!isClaimInRadius(testLocation, BORDER_RADIUS)) {
                            validLocationFound = true;
                            candidateLocation = testLocation;
                        }
                    }
                }

                if (validLocationFound && candidateLocation != null) {
                    Location finalLoc = candidateLocation;
                    Bukkit.getScheduler().runTask(instance, () -> {
                        this.dailyTeleportLocation = finalLoc;
                        this.nextRerollTime = getNextMidnightMillis();
                        saveData();
                        cacheDrtpBorder(finalLoc);
                        instance.getLogger().info("New Daily RTP location set: " + locationToString(finalLoc));
                        Bukkit.broadcast(toMM("Päivän uusi teleporttipaikka on arvottu!"));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void cacheDrtpBorder(Location center) {
        chunkParticleMap.clear();
        if (center == null || center.getWorld() == null) return;

        World world = center.getWorld();
        double cx = center.getX();
        double cz = center.getZ();
        double r = BORDER_RADIUS;

        List<Point> pointsToCalculate = new ArrayList<>();
        double increment = PARTICLE_SPACING / r;

        for (double angle = 0; angle < 2 * Math.PI; angle += increment) {
            double x = cx + (r * Math.cos(angle));
            double z = cz + (r * Math.sin(angle));

            int blockX = (int) Math.round(x - 0.5);
            int blockZ = (int) Math.round(z - 0.5);

            pointsToCalculate.add(new Point(blockX, blockZ, blockX + 0.5, blockZ + 0.5));
        }

        for (Point p : pointsToCalculate) {
            world.getChunkAtAsync(p.blockX >> 4, p.blockZ >> 4).thenAccept(chunk -> {
                int highestY = world.getHighestBlockAt(p.blockX, p.blockZ, HeightMap.MOTION_BLOCKING_NO_LEAVES).getY();
                Location loc = new Location(world, p.realX, highestY + 1.5, p.realZ);
                long key = getChunkKey(p.blockX >> 4, p.blockZ >> 4);
                chunkParticleMap.computeIfAbsent(key, k -> new ArrayList<>()).add(loc);
            });
        }
        instance.getLogger().info("Cached Border with Radius: " + r);
    }

    private record Point(int blockX, int blockZ, double realX, double realZ) {}

    private long getChunkKey(int x, int z) {
        return ((long) x & 0xFFFFFFFFL) | (((long) z & 0xFFFFFFFFL) << 32);
    }

    private long getNextMidnightMillis() {
        try {
            ZoneId helsinki = ZoneId.of("Europe/Helsinki");
            ZonedDateTime now = ZonedDateTime.now(helsinki);
            ZonedDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(helsinki);
            return nextMidnight.toInstant().toEpochMilli();
        } catch (Exception e) {
            return System.currentTimeMillis() + 86400000L;
        }
    }

    private boolean isLocationSafe(Location location) {
        if (location == null || location.getWorld() == null) return false;
        try {
            Block blockBelow = location.clone().subtract(0, 1, 0).getBlock();
            Biome biome = blockBelow.getBiome();
            if (biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.FROZEN_OCEAN ||
                    biome == Biome.RIVER || biome == Biome.LUSH_CAVES) return false;

            if (!blockBelow.getType().isSolid() || UNSAFE_BLOCKS.contains(blockBelow.getType())) return false;
            for (Tag<Material> tag : UNSAFE_TAGS) {
                if (tag.isTagged(blockBelow.getType())) return false;
            }
            return isSafeAirLike(location.getBlock()) && isSafeAirLike(location.clone().add(0, 1, 0).getBlock());
        } catch (Exception e) { return false; }
    }

    private boolean isSafeAirLike(Block block) {
        return !block.getType().isSolid() && !UNSAFE_BLOCKS.contains(block.getType());
    }

    public boolean isClaimInRadius(Location center, int radius) {
        try {
            if (instance.getCrashClaim() == null) return false;
            CrashClaimAPI api = instance.getCrashClaim().getApi();
            if (api.getClaim(center) != null) return true;
            if (isClaimAt(center.clone().add(radius, 0, radius))) return true;
            if (isClaimAt(center.clone().add(-radius, 0, radius))) return true;
            if (isClaimAt(center.clone().add(radius, 0, -radius))) return true;
            if (isClaimAt(center.clone().add(-radius, 0, -radius))) return true;
            return false;
        } catch (Exception e) { return true; }
    }

    private boolean isClaimAt(Location location) {
        if (instance.getCrashClaim() == null) return false;
        return instance.getCrashClaim().getApi().getClaim(location) != null;
    }

    public void loadData() {
        try {
            if (dataConfig.contains("location")) {
                this.dailyTeleportLocation = dataConfig.getLocation("location");
            }
            this.nextRerollTime = dataConfig.getLong("next-reroll-time", getNextMidnightMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        try {
            dataConfig.set("location", this.dailyTeleportLocation);
            dataConfig.set("next-reroll-time", this.nextRerollTime);
            dataConfig.save(dataFile);
        } catch (IOException e) {
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