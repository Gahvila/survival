package net.gahvila.survival.Features;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.gahvila.survival.survival;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class RegionBorderVisualizer {

    private final Map<Long, List<Location>> chunkParticleMap = new HashMap<>();

    private static final String REGION_NAME = "spawn";
    private static final double PARTICLE_SPACING = 5.0;
    private static final double HEIGHT_OFFSET = 1.5;
    private static final int VIEW_RADIUS_CHUNKS = 2;

    private final survival plugin;
    private final Particle.DustOptions laserStyle;

    public RegionBorderVisualizer(survival plugin) {
        this.plugin = plugin;
        this.laserStyle = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.5f);
    }

    public void enableVisualizer() {
        getServer().getScheduler().runTaskLater(plugin, this::cacheRegionBorder, 40L);

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
                            loc.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, laserStyle);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 5L);
    }

    private void cacheRegionBorder() {
        chunkParticleMap.clear();

        World world = Bukkit.getWorld("world");
        if (world == null) {
            plugin.getLogger().warning("World 'world' not found! Check your server.properties level-name.");
            return;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(world));

        if (regions == null || !regions.hasRegion(REGION_NAME)) {
            plugin.getLogger().warning("Region '" + REGION_NAME + "' not found in world!");
            return;
        }

        ProtectedRegion region = regions.getRegion(REGION_NAME);
        List<BlockVector2> points = getRegionPoints(region);

        if (points.isEmpty()) return;

        for (int i = 0; i < points.size(); i++) {
            BlockVector2 current = points.get(i);
            BlockVector2 next = points.get((i + 1) % points.size());
            interpolateLine(world, current, next);
        }

        int totalPoints = chunkParticleMap.values().stream().mapToInt(List::size).sum();
        plugin.getLogger().info("Border Visualization: Cached " + totalPoints + " points in " + chunkParticleMap.size() + " chunks.");
    }

    private void interpolateLine(World world, BlockVector2 p1, BlockVector2 p2) {
        double distance = Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getZ() - p1.getZ(), 2));
        double dx = (p2.getX() - p1.getX()) / distance;
        double dz = (p2.getZ() - p1.getZ()) / distance;

        for (double i = 0; i <= distance; i += PARTICLE_SPACING) {
            double x = p1.getX() + (dx * i);
            double z = p1.getZ() + (dz * i);

            int highestY = world.getHighestBlockAt((int) x, (int) z, HeightMap.MOTION_BLOCKING_NO_LEAVES).getY();

            Location loc = new Location(world, x, highestY + HEIGHT_OFFSET, z);

            int cx = ((int)x) >> 4;
            int cz = ((int)z) >> 4;

            long key = getChunkKey(cx, cz);
            chunkParticleMap.computeIfAbsent(key, k -> new ArrayList<>()).add(loc);
        }
    }

    private long getChunkKey(int x, int z) {
        return ((long) x & 0xFFFFFFFFL) | (((long) z & 0xFFFFFFFFL) << 32);
    }

    private List<BlockVector2> getRegionPoints(ProtectedRegion region) {
        if (region instanceof ProtectedPolygonalRegion) {
            return ((ProtectedPolygonalRegion) region).getPoints();
        } else if (region instanceof ProtectedCuboidRegion) {
            ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion) region;
            BlockVector2 min = cuboid.getMinimumPoint().toBlockVector2();
            BlockVector2 max = cuboid.getMaximumPoint().toBlockVector2();
            return Arrays.asList(
                    BlockVector2.at(min.getX(), min.getZ()),
                    BlockVector2.at(max.getX(), min.getZ()),
                    BlockVector2.at(max.getX(), max.getZ()),
                    BlockVector2.at(min.getX(), max.getZ())
            );
        }
        return new ArrayList<>();
    }
}