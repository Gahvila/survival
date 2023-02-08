package net.gahvila.selviytymisharpake.PlayerFeatures.Spawn;

import net.gahvila.selviytymisharpake.PlayerFeatures.Commands.RTP.TeleportUtils;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class VoidTP implements Listener {

    private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();
    private int cooldowntime = 10;

    //Pelaajien suojaus vakavalta kuolemalta!
    @EventHandler
    public void onDamage(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getWorld().getName().equals("spawn")){
            if (p.getLocation().getBlockY() < 175){
                if (!cooldown.containsKey(p.getUniqueId())) {
                    cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> cooldown.remove(p.getUniqueId()), 200);

                    Location randomLocation1 = TeleportUtils.findSafeLocation(p, 0);
                    //Teleport player
                    randomLocation1.setY(randomLocation1.getBlockY() + 200);
                    Vector vec = p.getVelocity();
                    p.setVelocity(vec);
                    p.teleportAsync(randomLocation1);

                }
            }
        }
    }
}
