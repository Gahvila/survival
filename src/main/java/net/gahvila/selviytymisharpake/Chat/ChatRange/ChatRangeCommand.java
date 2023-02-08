package net.gahvila.selviytymisharpake.Chat.ChatRange;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ChatRangeCommand implements CommandExecutor {

    public static ArrayList<Player> kuiskaus = new ArrayList<>();
    public static ArrayList<Player> huuto = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (kuiskaus.contains(p)) {
                kuiskaus.remove(p);
                huuto.add(p);
                p.sendMessage("Huudat nyt! [100m]");
                return true;
            } else {
                if (huuto.contains(p)) {
                    huuto.remove(p);
                    p.sendMessage("Puhut nyt maailmanlaajuisesti! [∞]");
                    return true;
                } else {
                    kuiskaus.add(p);
                    p.sendMessage("Kuiskaat nyt! [10m]");
                    return true;
                }
            }
        }
        return false;
    }
}
