package net.gahvila.selviytymisharpake.PlayerFeatures.ChatRange;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatRangeEvents implements Listener {


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        int chatRange = ChatRangeCommand.kuiskaus.contains(p) ? 10 : (ChatRangeCommand.huuto.contains(p) ? 100 : 0);

        if (chatRange == 0) {
            return;
        }

        e.setCancelled(true);

        for (Player recipient : Bukkit.getOnlinePlayers()) {
            if (recipient.getWorld() == p.getWorld()) {
                double distance = recipient.getLocation().distance(p.getLocation());

                Bukkit.getLogger().info("[LC SPY] " + p.getName() + " > " + e.getMessage());
                if (distance <= chatRange) {
                    String rangeIndicator = (chatRange == 10) ? "[§e10m§8] " : "[§e100m§8] ";
                    recipient.sendMessage("§8" + rangeIndicator + "§f" + p.getName() + " §e§l> §r" + e.getMessage());
                }
            }
        }
    }
}