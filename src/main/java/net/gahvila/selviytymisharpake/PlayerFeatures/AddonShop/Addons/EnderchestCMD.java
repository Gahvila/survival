package net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Addons;

import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderchestCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (AddonManager.getEnderchest(p)){
            p.openInventory(p.getEnderChest());
            p.sendMessage("§fSinun äärilaatikko avattiin!");
            p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 2F, 1F);
        }else{
            p.sendMessage("§fKäytä §e/addon enderchest §fkomentoa saadaksesi oikeudet tähän.");
        }return true;
    }
}
