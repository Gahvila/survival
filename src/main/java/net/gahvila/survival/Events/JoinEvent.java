package net.gahvila.survival.Events;

import net.gahvila.gahvilacore.Teleport.TeleportManager;
import net.gahvila.survival.survival;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class JoinEvent implements Listener {

    private final TeleportManager teleportManager;

    public JoinEvent(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        p.setInvulnerable(false);
        if (!p.hasPlayedBefore()){

            p.teleport(teleportManager.getTeleport(survival.instance, "spawn"));

            e.joinMessage(toMM("<#85FF00>" + p.getName() + "</#85FF00> liittyi Survivaliin ensimmäistä kertaa, tervetuloa!"));

            //join kit
            p.sendMessage(toMM("Sait aloituspakkauksen:<br>" +
                    "<gray>- <#85FF00>3kpl paistettua perunaa<br>" +
                    "<gray>- <#85FF00>1kpl nahkakypärä"));
            p.getInventory().addItem(new ItemStack(Material.BAKED_POTATO, 3));
            p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
        }else{
            e.joinMessage(null);
        }
    }
}
