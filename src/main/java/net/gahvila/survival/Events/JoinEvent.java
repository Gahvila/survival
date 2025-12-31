package net.gahvila.survival.Events;

import net.gahvila.gahvilacore.Teleport.TeleportManager;
import net.gahvila.survival.survival;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        p.setInvulnerable(false);
        if (!p.hasPlayedBefore()){
            e.joinMessage(toMM("<#85FF00>" + p.getName() + "</#85FF00> liittyi Survivaliin ensimmäistä kertaa, tervetuloa!"));
            if (Bukkit.getOnlinePlayers().size() == 1) {
                p.getWorld().setTime(0);
            }
        }else{
            e.joinMessage(null);
        }
    }
}
