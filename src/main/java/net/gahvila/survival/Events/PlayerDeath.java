package net.gahvila.survival.Events;

import net.gahvila.gahvilacore.Teleport.TeleportManager;
import net.gahvila.survival.Back.BackListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
            if (!BackListener.died.contains(p.getUniqueId())){
                BackListener.back.put(p, p.getLocation());
            }
            p.teleportAsync(p.getRespawnLocation());
            e.setRespawnLocation(p.getRespawnLocation());
            p.sendMessage("");
            p.sendMessage("");
            if (e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)){
                p.sendMessage("Sinut teleportattiin sängyllesi.");
            }else{
                p.sendMessage(toMM("Sinä kuolit. Sinut teleportattiin sängyllesi. Voit teleportata kuolinpaikallesi komennolla <#85FF00>/back</#85FF00>."));
            }
        }else{
            p.sendMessage("");
            p.sendMessage("");
            if (e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)){
                p.sendMessage("Sinut teleportattiin spawnille.");
            }else {
                p.sendMessage(toMM("Sinä kuolit eikä sinulla ole sänkyä asetettuna. Sinut teleportattiin spawnille. Voit teleportata kuolinpaikallesi komennolla <#85FF00>/back</#85FF00>."));
            }
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, MAX_VALUE, 1F); //TODO: vaiha tää parempaan
            e.setRespawnLocation(new Location(Bukkit.getWorld("spawn"), 20.5, 81, -40.5, 180.0f, 0.0f));
            if (!BackListener.died.contains(p.getUniqueId())){
                if (!e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)){
                    BackListener.back.put(p, p.getLocation());
                }
            }
            p.teleport(teleportManager.getTeleport("spawn"));

        }
    }
}
