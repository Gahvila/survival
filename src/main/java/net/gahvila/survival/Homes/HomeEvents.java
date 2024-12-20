package net.gahvila.survival.Homes;

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
        homeManager.putHomeIntoCache(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        HomeCommands.gambling.remove(event.getPlayer());
        homeManager.homes.remove(event.getPlayer().getUniqueId());
    }
}
