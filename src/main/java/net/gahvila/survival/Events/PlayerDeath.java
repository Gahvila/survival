package net.gahvila.survival.Events;

import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Teleport.TeleportManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import static java.lang.Long.MAX_VALUE;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class PlayerDeath implements Listener {

    private final TeleportManager teleportManager;

    public PlayerDeath(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        if (p.getRespawnLocation() != null){
            p.teleportAsync(p.getRespawnLocation());
            e.setRespawnLocation(p.getRespawnLocation());
            p.sendMessage("");
            p.sendMessage("");
            if (e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)){
                p.sendMessage("Sinut teleportattiin sängyllesi.");
            }else{
                p.sendMessage(toMM("Sinä kuolit. Sinut teleportattiin sängyllesi."));
            }
            p.playSound(p.getRespawnLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.4F, 1F);
        }else{
            e.setRespawnLocation(teleportManager.getTeleport(GahvilaCore.instance, "spawn"));
            p.teleport(teleportManager.getTeleport(GahvilaCore.instance, "spawn"));
            p.sendMessage("");
            p.sendMessage("");
            if (e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)){
                p.sendMessage("Sinut teleportattiin spawnille.");
            }else {
                p.sendMessage(toMM("Sinä kuolit eikä sinulla ole sänkyä asetettuna. Sinut teleportattiin spawnille."));
            }
            p.playSound(p.getRespawnLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.4F, 1F);
        }
    }
}
