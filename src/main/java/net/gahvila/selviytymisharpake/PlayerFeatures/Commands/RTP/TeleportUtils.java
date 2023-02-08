package net.gahvila.selviytymisharpake.PlayerFeatures.Commands.RTP;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnProtection;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.papermc.lib.PaperLib.getChunkAtAsync;

public class TeleportUtils {
    static SelviytymisHarpake plugin;

    public TeleportUtils(SelviytymisHarpake plugin) {
        this.plugin = plugin;
    }

    public static HashMap<UUID, Boolean> fallprotection = new HashMap<UUID, Boolean>();

    public static HashSet<Biome> spawnrtp_allowed_biomes = new HashSet<>();

    static {
        spawnrtp_allowed_biomes.add(Biome.WINDSWEPT_FOREST);
        spawnrtp_allowed_biomes.add(Biome.SUNFLOWER_PLAINS);
        spawnrtp_allowed_biomes.add(Biome.SAVANNA);
        spawnrtp_allowed_biomes.add(Biome.PLAINS);
        spawnrtp_allowed_biomes.add(Biome.OLD_GROWTH_BIRCH_FOREST);
        spawnrtp_allowed_biomes.add(Biome.SPARSE_JUNGLE);
        spawnrtp_allowed_biomes.add(Biome.FLOWER_FOREST);
        spawnrtp_allowed_biomes.add(Biome.OLD_GROWTH_SPRUCE_TAIGA);
        spawnrtp_allowed_biomes.add(Biome.OLD_GROWTH_PINE_TAIGA);
        spawnrtp_allowed_biomes.add(Biome.TAIGA);
        spawnrtp_allowed_biomes.add(Biome.MEADOW);
        spawnrtp_allowed_biomes.add(Biome.STONY_PEAKS);
        spawnrtp_allowed_biomes.add(Biome.MANGROVE_SWAMP);
        spawnrtp_allowed_biomes.add(Biome.FOREST);
    }

    public static HashSet<Biome> rtpcmd_allowed_biomes = new HashSet<>();

    static {
        rtpcmd_allowed_biomes.add(Biome.WOODED_BADLANDS);
        rtpcmd_allowed_biomes.add(Biome.ERODED_BADLANDS);
        rtpcmd_allowed_biomes.add(Biome.BADLANDS);
        rtpcmd_allowed_biomes.add(Biome.STONY_PEAKS);
        rtpcmd_allowed_biomes.add(Biome.SNOWY_SLOPES);
        rtpcmd_allowed_biomes.add(Biome.SWAMP);
        rtpcmd_allowed_biomes.add(Biome.DESERT);
        rtpcmd_allowed_biomes.add(Biome.BAMBOO_JUNGLE);
        rtpcmd_allowed_biomes.add(Biome.JUNGLE);
        rtpcmd_allowed_biomes.add(Biome.WINDSWEPT_FOREST);
        rtpcmd_allowed_biomes.add(Biome.SUNFLOWER_PLAINS);
        rtpcmd_allowed_biomes.add(Biome.SAVANNA);
        rtpcmd_allowed_biomes.add(Biome.PLAINS);
        rtpcmd_allowed_biomes.add(Biome.OLD_GROWTH_BIRCH_FOREST);
        rtpcmd_allowed_biomes.add(Biome.SPARSE_JUNGLE);
        rtpcmd_allowed_biomes.add(Biome.FLOWER_FOREST);
        rtpcmd_allowed_biomes.add(Biome.OLD_GROWTH_SPRUCE_TAIGA);
        rtpcmd_allowed_biomes.add(Biome.OLD_GROWTH_PINE_TAIGA);
        rtpcmd_allowed_biomes.add(Biome.TAIGA);
        rtpcmd_allowed_biomes.add(Biome.MEADOW);
        rtpcmd_allowed_biomes.add(Biome.STONY_PEAKS);
        rtpcmd_allowed_biomes.add(Biome.FOREST);
        rtpcmd_allowed_biomes.add(Biome.MANGROVE_SWAMP);
    }

    public static HashSet<Material> bad_blocks = new HashSet<>();

    static {
        bad_blocks.add(Material.STONE);
        bad_blocks.add(Material.LAVA);
        bad_blocks.add(Material.FIRE);
        bad_blocks.add(Material.CACTUS);
        bad_blocks.add(Material.WATER);
        bad_blocks.add(Material.MAGMA_BLOCK);
        bad_blocks.add(Material.KELP);
        bad_blocks.add(Material.KELP_PLANT);
        bad_blocks.add(Material.SAND);
        bad_blocks.add(Material.SNOW);
    }


    public static Location generateLocation(Player player, Integer integer) {

        SplittableRandom random = new SplittableRandom();

        int x = 0;
        int z = 0;
        int y = 0;

        x = random.nextInt(-12500, 12500);
        z = random.nextInt(-12500, 12500);
        if (integer == 3) {
            y = 256;
        } else {
            y = 150;
        }


        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        Location randomLocation = new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
        y = randomLocation.getWorld().getHighestBlockYAt(randomLocation);
        randomLocation.setY(y);
        return randomLocation;
    }

    public static Location findSafeLocation(Player player, Integer integer) {
        Location randomLocation = generateLocation(player, integer);
        while (!isLocationSafe(randomLocation, integer)) {
            //Keep looking for a safe location
            randomLocation = generateLocation(player, integer);
        }
        if (player.getWorld().getName().equals("spawn")) {
            SpawnProtection.messagesent.put(player.getUniqueId(), false);
            fallprotection.put(player.getUniqueId(), true);
            Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> SpawnProtection.messagesent.remove(player.getUniqueId(), false), 200);
            Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> fallprotection.put(player.getUniqueId(), false), 200);
        }
        return randomLocation;
    }


    public static boolean isLocationSafe(Location location, Integer integer) {

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        getChunkAtAsync(location.getWorld(), x, z);
        //Get instances of the blocks around where the player would spawn
        Block block = location.getWorld().getBlockAt(x, y, z);
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        Block above = location.getWorld().getBlockAt(x, y + 1, z);
        //spawnrtp
        switch (integer) {
            case 0:
                if (spawnrtp_allowed_biomes.contains(block.getBiome())) {
                    if (GriefPrevention.instance.dataStore.getClaimAt(location, true, null) == null) {
                        return !(bad_blocks.contains(below.getType())) || (block.isSolid()) || (above.getType().isSolid());
                    } else {
                        return false;
                    }
                }
                return false;
            case 1:
                if (rtpcmd_allowed_biomes.contains(block.getBiome())) {
                    if (GriefPrevention.instance.dataStore.getClaimAt(location, true, null) == null) {
                        return !(bad_blocks.contains(below.getType())) || (block.isSolid()) || (above.getType().isSolid());
                    } else {
                        return false;
                    }
                }
                return false;
        }return false;
    }
}