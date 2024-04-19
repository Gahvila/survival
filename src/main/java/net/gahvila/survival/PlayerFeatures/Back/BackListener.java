package net.gahvila.survival.PlayerFeatures.Back;

import net.gahvila.survival.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BackListener implements Listener {

    private final BackManager backManager;


    public BackListener(BackManager backManager) {
        this.backManager = backManager;
    }

    public static HashMap<Player, Location> back = new HashMap<>();
    public static ArrayList<UUID> died = new ArrayList<UUID>();
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        Location loc = p.getLocation();
        died.add(p.getUniqueId());

        backManager.setDeath(p, loc);

    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        PlayerTeleportEvent.TeleportCause cause = e.getCause();

        // make sure teleport isn't due to a death, and not due to the causes listed
        if (!died.contains(p.getUniqueId()) &&
                cause != PlayerTeleportEvent.TeleportCause.DISMOUNT &&
                cause != PlayerTeleportEvent.TeleportCause.END_GATEWAY &&
                cause != PlayerTeleportEvent.TeleportCause.END_PORTAL &&
                cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL &&
                cause != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL &&
                cause != PlayerTeleportEvent.TeleportCause.SPECTATE) {

            World fromWorld = e.getFrom().getWorld();
            //not spawn
            if (!fromWorld.equals(Bukkit.getWorld("spawn"))) {
                backManager.setBack(p, e.getFrom());
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> died.remove(p.getUniqueId()), 100);

    }
}
