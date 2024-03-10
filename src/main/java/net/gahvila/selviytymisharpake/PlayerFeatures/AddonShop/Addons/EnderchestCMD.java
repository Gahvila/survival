package net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Addons;

import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EnderchestCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (AddonManager.getEnderchest(p)){
            p.openInventory(p.getEnderChest());
            p.sendMessage("§fSinun äärilaatikko avattiin!");
            p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 2F, 1F);
        }else{
            Component message = toMiniMessage("<white>Käytä</white> <yellow>/addon</yellow> <white>komentoa saadaksesi oikeudet tähän.");
            p.sendMessage(message);
        }return true;
    }

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }
}
