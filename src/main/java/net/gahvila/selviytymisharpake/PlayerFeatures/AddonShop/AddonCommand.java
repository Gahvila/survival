package net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop;

import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Menu.AddonMainMenu;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddonCommand implements CommandExecutor {

    public static void AddonCommand() {
        AddonManager addonManager = new AddonManager();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        AddonMainMenu.openGui(p);
        p.playSound(p.getLocation(), Sound.ENTITY_LLAMA_SWAG, 2F, 1F);
        return true;
    }
}