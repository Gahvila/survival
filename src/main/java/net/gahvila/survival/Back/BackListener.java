package net.gahvila.survival.Back;

import net.gahvila.survival.survival;
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
        died.add(p.getUniqueId());

        backManager.setBack(p, p.getLocation());

    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (!e.getFrom().getWorld().equals(Bukkit.getWorld("spawn"))) {
            backManager.setBack(p, e.getFrom());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(survival.instance, () -> died.remove(p.getUniqueId()), 100);

    }
}
