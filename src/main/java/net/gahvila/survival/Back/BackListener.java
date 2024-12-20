package net.gahvila.survival.Back;

import net.gahvila.survival.survival;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        backManager.setBack(p, p.getLocation());

    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        backManager.setBack(p, e.getFrom());
    }
}
