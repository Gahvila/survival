package net.gahvila.survival.Warps;

import net.gahvila.survival.Warps.WarpApplications.WarpApplicationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class WarpEvents implements Listener {
    private final WarpManager warpManager;
    private final WarpApplicationManager warpApplicationManager; // Added reference

    public WarpEvents(WarpManager warpManager, WarpApplicationManager warpApplicationManager) {
        this.warpManager = warpManager;
        this.warpApplicationManager = warpApplicationManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        warpManager.updateWarpOwnerName(p);

        warpApplicationManager.checkPendingNotifications(p);
    }
}