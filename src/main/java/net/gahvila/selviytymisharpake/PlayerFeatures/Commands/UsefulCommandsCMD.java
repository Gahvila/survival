package net.gahvila.selviytymisharpake.PlayerFeatures.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UsefulCommandsCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage("Hyödyllisiä komentoja, joita et ehkä tiedä!");
            p.sendMessage("§e/addon §8- §fVoit ostaa lisäosia, esimerkiksi oikeudet /ec komentoon.");
            p.sendMessage("§e/buyhomes §8- §fVoit ostaa lisää koteja.");
        }
        return false;
    }
}
