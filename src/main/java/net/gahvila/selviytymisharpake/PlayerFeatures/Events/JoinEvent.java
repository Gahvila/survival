package net.gahvila.selviytymisharpake.PlayerFeatures.Events;

import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class JoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        p.setInvulnerable(false);
        if (!p.hasPlayedBefore()){

            SpawnTeleport.teleportSpawn(p);

            e.setJoinMessage("§e" + p.getName() + "§r §fliittyi Survivaliin ensimmäistä kertaa, tervetuloa!");

            //join kit

            p.getInventory().addItem(new ItemStack(Material.APPLE, 4));
            p.getInventory().addItem(new ItemStack(Material.GOLDEN_AXE, 1));
        }else{
            e.setJoinMessage(null);
        }
    }


}
