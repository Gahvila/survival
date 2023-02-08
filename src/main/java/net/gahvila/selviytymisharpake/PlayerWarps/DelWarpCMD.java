package net.gahvila.selviytymisharpake.PlayerWarps;

import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelWarpCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (p.hasPermission("warps.yes")) {
            if (args.length == 1) {
                if (!WarpManager.getWarpOwnerUUID(args[0]).equals(p.getUniqueId().toString())) {
                    p.sendMessage("§7Et omista warppia nimellä §e" + args[0] + "§f.");
                    return true;
                }
                WarpManager.deleteWarp(args[0]);
                p.sendMessage("§fWarp nimellä §e" + args[0] + " §fpoistettu.");
            }
        }return true;
    }
}