package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddHomes implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (p.hasPermission("buyhomes.yes")){
            if (HomeManager.getAllowedHomes(p) == 9){
                p.sendMessage("Voit ostaa vain 9 kotia, ja sinulla on jo 9 kotia.");

            }else{
                if (SelviytymisHarpake.getEconomy().getBalance(p) >= 5000){
                    SelviytymisHarpake.getEconomy().withdrawPlayer(p, 5000);
                    HomeManager.addAllowedHomes(p);
                    p.sendMessage("Ostit uuden kodin! Sinulla on nyt §e" + HomeManager.getAllowedHomes(p) + " §fkotia yhteensä.");
                }else{
                    p.sendMessage("Kodin osto maksaa §e5000 §fkolikkoa, ja sinulla on vain §e" + SelviytymisHarpake.getEconomy().getBalance(p));
                }
            }
        }return false;
    }
}
