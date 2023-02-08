package net.gahvila.selviytymisharpake.PlayerWarps;

import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditWarpCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("editwarp.yes")) {
            Player p = (Player) sender;
            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "price":
                            //Vaihda hinta
                            WarpManager.editingWarp.put(p.getUniqueId(), 1);
                            p.sendMessage("§fMitä warppia haluat muokata?");
                            p.sendMessage(WarpManager.getOwnedWarps(p).toString());
                            break;
                        case "sijainti":
                            //Vaihda sijainti
                            WarpManager.editingWarp.put(p.getUniqueId(), 2);
                            p.sendMessage("§fMitä warppia haluat muokata?");
                            p.sendMessage(WarpManager.getOwnedWarps(p).toString());
                            break;
                        default:
                            break;

                    }
                    break;
                default:
                    p.sendMessage("Sinun täytyy lisätä jokin argumentti:");
                    p.sendMessage("§eenderchest, craft, feed");
                    break;
            }
        }return true;
    }
}
