package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.menu.WarpMenu;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("back")) {
            if (sender instanceof Player p) {
                new BackMenu(SelviytymisHarpake.getPlayerMenuUtility(p)).open();
                return true;
            }
        }else if (cmd.getName().equalsIgnoreCase("fback")) {
            if (sender instanceof Player p) {
                if (BackManager.getBack(p, 1) != null){
                    p.teleportAsync(BackManager.getBack(p, 1));
                }else{
                    p.sendMessage("Sijaintia ei ole.");
                }
                return true;
            }
        }return true;
    }
}
