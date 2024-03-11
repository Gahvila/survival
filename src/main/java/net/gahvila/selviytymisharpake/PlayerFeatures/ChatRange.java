package net.gahvila.selviytymisharpake.PlayerFeatures;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

public class ChatRange implements Listener {

    public ArrayList<Player> kuiskaus = new ArrayList<>();
    public ArrayList<Player> huuto = new ArrayList<>();


    public void registerCommands() {
        new CommandAPICommand("puhu")
                .withAliases("localchat")
                .executesPlayer((p, args) -> {
                    boolean inKuiskaus = kuiskaus.contains(p);
                    boolean inHuuto = huuto.contains(p);

                    if (inKuiskaus) {
                        kuiskaus.remove(p);
                        huuto.add(p);
                        p.sendMessage("Huudat nyt! [100m]");
                    } else if (inHuuto) {
                        huuto.remove(p);
                        p.sendMessage("Puhut nyt maailmanlaajuisesti! [∞]");
                    } else {
                        kuiskaus.add(p);
                        p.sendMessage("Kuiskaat nyt! [10m]");
                    }
                })
                .register();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        int chatRange = kuiskaus.contains(p) ? 10 : (huuto.contains(p) ? 100 : 0);

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
