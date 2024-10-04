package net.gahvila.survival.Warps;

import net.gahvila.survival.survival;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;


public class WarpEvents implements Listener {
    private final WarpManager warpManager;


    public WarpEvents(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        warpManager.updateWarpOwnerName(p);
    }
}