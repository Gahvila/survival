package net.gahvila.survival.PlayerFeatures.Events;

import net.gahvila.survival.PlayerFeatures.Spawn.SpawnTeleport;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import static net.gahvila.survival.Utils.MiniMessageUtils.toMM;

public class JoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        p.setInvulnerable(false);
        if (!p.hasPlayedBefore()){

            SpawnTeleport.teleportSpawn(p);

            e.joinMessage(toMM("<#85FF00>" + p.getName() + "</#85FF00> liittyi Survivaliin ensimmäistä kertaa, tervetuloa!"));

            //join kit

            p.getInventory().addItem(new ItemStack(Material.APPLE, 4));
            p.getInventory().addItem(new ItemStack(Material.GOLDEN_AXE, 1));
        }else{
            e.joinMessage(null);
        }
    }
}
