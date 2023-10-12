package net.gahvila.selviytymisharpake.PlayerFeatures.Commands;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class MainCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1) {
            if (!sender.hasPermission("sh.admin")) return true;
            if (args[0].equalsIgnoreCase("reload")) {
                SelviytymisHarpake.instance.reloadConfig();
                sender.sendMessage("Ya did it boeh!");
                return true;
            }
            if (args[0].equalsIgnoreCase("resetnether")) {
                SelviytymisHarpake.instance.performNetherReset();
                return true;
            }
        }return true;
    }
}
