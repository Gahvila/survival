package net.gahvila.selviytymisharpake.PlayerFeatures.ChatRange;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ChatRangeCommand implements CommandExecutor{

    public static ArrayList<Player> kuiskaus = new ArrayList<>();
    public static ArrayList<Player> huuto = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return false; // Exit if command sender is not a player
        }

        Player p = (Player) sender;
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

        return true;
    }
}
