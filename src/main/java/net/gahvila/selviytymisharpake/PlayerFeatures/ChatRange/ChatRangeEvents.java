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
        if (ChatRangeCommand.kuiskaus.contains(p)) {
            e.setCancelled(true);
            for (Player other : Bukkit.getOnlinePlayers()) {

                if (other.hasPermission("chatrange.see")) {
                    if (other.getWorld() == p.getWorld()) {
                        if (!(other.getLocation().distance(p.getLocation()) <= 10)) {
                            other.sendMessage(ChatColor.GRAY + "[SPY] " + p.getName() + "" + ChatColor.BOLD + " > " + ChatColor.GRAY + e.getMessage());
                        }
                    } else {
                        other.sendMessage(ChatColor.GRAY + "[SPY] " + p.getName() + "" + ChatColor.BOLD + " > " + ChatColor.GRAY + e.getMessage());
                    }
                }

                if (other.getWorld() == p.getWorld()) {
                    if (other.getLocation().distance(p.getLocation()) <= 10) {
                        if (other.getPlayer().equals(p)){
                            other.sendMessage("§8[§e10m§8] §f" + p.getName() + " §e§l> §r" + e.getMessage());
                        }else{
                            Long distance = Math.round(p.getLocation().distance(other.getLocation()));
                            other.sendMessage("§8[§e"+ distance + "§7/§e10m§8] §f" + p.getName() + " §e§l> §r" + e.getMessage());
                        }
                    }
                }
            }


        } else if (ChatRangeCommand.huuto.contains(p)) {
            e.setCancelled(true);
            for (Player other : Bukkit.getOnlinePlayers()) {

                if (other.hasPermission("chatrange.see")) {
                    if (other.getWorld() == p.getWorld()) {
                        if (!(other.getLocation().distance(p.getLocation()) <= 100)) {
                            other.sendMessage(ChatColor.GRAY + "[SPY] " + p.getName() + "" + ChatColor.BOLD + " > " + ChatColor.GRAY + e.getMessage());
                        }
                    } else {
                        other.sendMessage(ChatColor.GRAY + "[SPY] " + p.getName() + "" + ChatColor.BOLD + " > " + ChatColor.GRAY + e.getMessage());
                    }
                }

                if (other.getWorld() == p.getWorld()) {
                    if (other.getLocation().distance(p.getLocation()) <= 100) {
                        if (other.getPlayer().equals(p)){
                            other.sendMessage("§8[§e100m§8] §f" + p.getName() + " §e§l> §r" + e.getMessage());
                        }else{
                            Long distance = Math.round(p.getLocation().distance(other.getLocation()));
                            other.sendMessage("§8[§e"+ distance + "§7/§e100m§8] §f" + p.getName() + " §e§l> §r" + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
