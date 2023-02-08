package net.gahvila.selviytymisharpake.NewSeason.EndBlocker;

import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PortalEnterEvent implements Listener {

    @EventHandler
    public void onPortalEnter(PlayerPortalEvent e){
        Player p = e.getPlayer();
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (SelviytymisHarpake.instance.getConfig().contains("end-enabled")) {
                if (SelviytymisHarpake.instance.getConfig().getBoolean("end-enabled")) return;
                e.setCancelled(true);
                SpawnTeleport.teleportSpawn(p);
                p.sendMessage("End on suljettu.");
            }
        }
    }
}
