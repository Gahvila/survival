package net.gahvila.selviytymisharpake.PlayerFeatures.Resurssinether;

import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class RNPortalDisabler implements Listener {

    @EventHandler
    public void onPortal (PlayerPortalEvent e){
        Player p = e.getPlayer();
        if (p.getWorld().getName().equals("resurssinether")){
            e.setCancelled(true);
            SpawnTeleport.teleportSpawn(p);
        }
    }

    @EventHandler
    public void onEntityPortal (EntityPortalEvent e){
        Entity entity = e.getEntity();
        if (entity.getWorld().getName().equals("resurssinether")){
            e.setCancelled(true);
        }
    }
}
