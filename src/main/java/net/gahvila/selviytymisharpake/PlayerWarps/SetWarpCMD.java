package net.gahvila.selviytymisharpake.PlayerWarps;

import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SetWarpCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("warps.yes")) {
                if (WarpManager.getOwnedWarps(p).size() < WarpManager.getAllowedWarps(p)) {
                    if (p.getWorld().getName().equals("world")) {
                        p.sendMessage("§7Aloitit warpin asettamisen.");
                        p.sendMessage("§cKirjoita vastauksesi chattiin!");
                        p.sendMessage("");
                        p.sendMessage("§fMikäs sinun warppisi nimi on? Nimi voi sisältää vain kirjaimia ja numeroita.");
                        p.sendMessage("§7Jos haluat perua warpin luonnin, kirjoita '§elopeta§7' chattiin.");
                        WarpManager.settingWarp.put(p.getUniqueId(), 1);
                        return true;
                    }else{
                        p.sendMessage("Voit asettaa warpin vain päämaailmaan.");
                    }
                }else{
                    p.sendMessage("Sinulla ei ole tarpeeksi warppeja! Voit ostaa warpin komennolla §e/buywarp");
                }
            }

            if (WarpManager.getAllowedWarps(p) == 0) {
                p.sendMessage("Sinulla ei ole tarpeeksi warppeja! Voit ostaa warpin komennolla §e/buywarp");
            }
        }return true;
    }
}