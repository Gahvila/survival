package net.gahvila.survival.Features;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

public class TeleportBlocker implements Listener {

    public static boolean canTeleport(Player player) {
        return canTeleportFromDeepDark(player) && canTeleportInTheEnd(player);
    }

    private static boolean canTeleportFromDeepDark(Player player) {
        Location location = player.getLocation();

        if (player.hasPotionEffect(PotionEffectType.DARKNESS)) return false;

        for (Entity entity : player.getNearbyEntities(32, 32, 32)) {
            if (location.getBlock().getBiome() == Biome.DEEP_DARK) {
                if (entity instanceof Warden) return false;
            }
        }

        return true;
    }

    // if outside 1000 blocks from the center of the end, prevent teleport
    private static boolean canTeleportInTheEnd(Player player) {
        Location loc = player.getLocation();

        if (loc.getWorld().getEnvironment() != World.Environment.THE_END) return true;

        double distance = Math.hypot(loc.getX(), loc.getZ());
        return !(distance > 1000);
    }
}
