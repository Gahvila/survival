package net.gahvila.survival.DailyRTP.integration;

import net.crashcraft.crashclaim.api.events.PreClaimCreateEvent;
import net.crashcraft.crashclaim.api.events.PreClaimResizeEvent;
import net.crashcraft.crashclaim.claimobjects.Claim;
import net.gahvila.survival.DailyRTP.DrtpManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClaimBlockListener implements Listener {

    private final DrtpManager drtpManager;
    private final int protectionRadius = 100; // radius in blocks around the daily teleport

    public ClaimBlockListener(DrtpManager drtpManager) {
        this.drtpManager = drtpManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();

        if (isNearDailyTeleport(loc)) {
            event.setCancelled(true);
            event.getPlayer().sendRichMessage("<red>Et voi tehdä tuota.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Location loc = event.getBlock().getLocation();

        if (isNearDailyTeleport(loc)) {
            event.setCancelled(true);
            event.getPlayer().sendRichMessage("<red>Et voi tehdä tuota.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Location loc = event.getLocation();

        if (isNearDailyTeleport(loc)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Location loc = event.getBlock().getLocation();

        if (isNearDailyTeleport(loc)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Enemy) {
            if (isNearDailyTeleport(event.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof Enemy) {
            if (event.getTarget() != null && isNearDailyTeleport(event.getTarget().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (isNearDailyTeleport(player.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        Location loc = event.getClickedBlock().getLocation();
        if (isNearDailyTeleport(loc)) {
            event.setCancelled(true);
            event.getPlayer().sendRichMessage("<red>Et voi tehdä tuota.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPreClaimCreate(PreClaimCreateEvent event) {
        Location min = event.getMinCorner();
        Location max = event.getMaxCorner();

        if (doesClaimOverlap(min.getWorld(), min.getX(), min.getZ(), max.getX(), max.getZ())) {
            event.setCancelled(true);
            event.getPlayer().sendRichMessage("<red>Et voi luoda claimia näin lähelle päivän teleporttia.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPreClaimResize(PreClaimResizeEvent event) {
        Claim claim = event.getClaim();
        Location draggedCorner = event.getDraggedCorner();
        Location newTarget = event.getNewTargetLocation();

        double stationaryX = (draggedCorner.getBlockX() == claim.getMinX()) ? claim.getMaxX() : claim.getMinX();
        double stationaryZ = (draggedCorner.getBlockZ() == claim.getMinZ()) ? claim.getMaxZ() : claim.getMinZ();

        double newMinX = Math.min(stationaryX, newTarget.getX());
        double newMaxX = Math.max(stationaryX, newTarget.getX());
        double newMinZ = Math.min(stationaryZ, newTarget.getZ());
        double newMaxZ = Math.max(stationaryZ, newTarget.getZ());

        if (doesClaimOverlap(newTarget.getWorld(), newMinX, newMinZ, newMaxX, newMaxZ)) {
            event.setCancelled(true);
            event.getPlayer().sendRichMessage("<red>Et voi laajentaa claimia näin lähelle päivän teleporttia.");
        }
    }

    private boolean isNearDailyTeleport(Location location) {
        Location dailyLocation = drtpManager.getDailyTeleportLocation();
        if (dailyLocation == null || location == null) {
            return false;
        }

        if (location.getWorld() == null || !location.getWorld().equals(dailyLocation.getWorld())) {
            return false;
        }

        double dx = location.getX() - dailyLocation.getX();
        double dz = location.getZ() - dailyLocation.getZ();

        return (dx * dx) + (dz * dz) <= (protectionRadius * protectionRadius);
    }

    private boolean doesClaimOverlap(World world, double claimMinX, double claimMinZ, double claimMaxX, double claimMaxZ) {
        Location dailyLocation = drtpManager.getDailyTeleportLocation();
        if (dailyLocation == null || world == null || !world.equals(dailyLocation.getWorld())) {
            return false;
        }

        double closestX = clamp(dailyLocation.getX(), claimMinX, claimMaxX);
        double closestZ = clamp(dailyLocation.getZ(), claimMinZ, claimMaxZ);

        double dx = dailyLocation.getX() - closestX;
        double dz = dailyLocation.getZ() - closestZ;

        double distanceSquared = (dx * dx) + (dz * dz);

        return distanceSquared <= (protectionRadius * protectionRadius);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}