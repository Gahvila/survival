package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("back")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!p.getWorld().getName().equalsIgnoreCase("rules")) {
                    String prefix = "§a§lSurvival §8> §r";
                    if (BackListener.back.get(p) == null) {
                        p.sendMessage("Minne matka? Et ole vielä teleportannut minnekään, joten sinulla ei ole aikaisempaa sijaintia.");
                    } else {
                        Location loc = BackListener.back.get(p);
                        p.teleportAsync(loc);
                        p.setGameMode(GameMode.SURVIVAL);
                        p.sendMessage(prefix + "Sinut teleportattiin aiempaan sijaintiin.");
                    }
                    return true;
                }
            }
        }
        return false;
    }


}
