package net.gahvila.selviytymisharpake.PlayerWarps;

import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyWarpCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) {
            TextComponent accept = new TextComponent();
            accept.setText("§a§lHyväksy"); //set clickable text
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa ostaaksesi.").create())); //display text msg when hovering
            accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/buywarp confirm")); //runs command when they click the text

            Integer price = getNextWarpCost(p);

            p.sendMessage("§fSinulla on §e" + WarpManager.getAllowedWarps(p) + " §fwarppia yhteensä.");
            p.sendMessage("Haluatko varmasti ostaa warpin? \nHinta: §e" + price + "Ⓖ");
            p.sendMessage("Jokaisen warpin osto nostaa hintaa §e10%§f.");
            p.sendMessage(accept);
        } else {
            switch (args.length) {
                case 1:
                    if (args[0].equalsIgnoreCase("confirm")) {

                        Integer price = getNextWarpCost(p);

                        if (SelviytymisHarpake.getEconomy().getBalance(p) >= price) {
                            SelviytymisHarpake.getEconomy().withdrawPlayer(p, price);

                            WarpManager.addAllowedWarps(p);
                            p.sendMessage("Sinulla on nyt §e" + WarpManager.getAllowedWarps(p) + " §fwarppia yhteensä.");
                        } else {
                            p.sendMessage("Warpin osto maksaa §e" + price + "Ⓖ§f, ja sinulla on vain §e" + SelviytymisHarpake.getEconomy().getBalance(p));
                        }
                    }
            }
        }return true;
    }

    public int getNextWarpCost(Player p) {
        double rate = 0.10;
        int initialCost = 1000;
        double cost = initialCost * Math.pow(1 + rate, WarpManager.getAllowedWarps(p));
        return (int) cost;
    }
}