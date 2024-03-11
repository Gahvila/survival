package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.menu.WarpMenu;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {

    private final BackManager backManager;


    public BackCommand(BackManager backManager) {
        this.backManager = backManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return false;
        }

        if (cmd.getName().equalsIgnoreCase("back")) {
            new BackMenu(SelviytymisHarpake.getPlayerMenuUtility(p), backManager).open();
        } else if (cmd.getName().equalsIgnoreCase("fback")) {
            Location previousLocation = backManager.getBack(p, 1);

            if (previousLocation != null) {
                p.teleportAsync(previousLocation);
            } else {
                p.sendMessage("Sijaintia ei ole.");
            }
        } return true;
    }
}
