package net.gahvila.survival.PlayerFeatures.Homes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HomeEvents implements Listener {
    private final HomeManager homeManager;


    public HomeEvents(HomeManager homeManager) {
        this.homeManager = homeManager;

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        homeManager.putHomeIntoRam(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        homeManager.homes.remove(event.getPlayer().getUniqueId());
    }
}
