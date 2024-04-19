package net.gahvila.survival.PlayerFeatures.Events;

import net.gahvila.survival.PlayerFeatures.Homes.HomeCommands;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        e.quitMessage(null);
        p.setInvulnerable(false);
        HomeCommands.gambling.remove(p);
    }
}
