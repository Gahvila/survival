package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import net.gahvila.selviytymisharpake.PlayerWarps.WarpManager;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddHomes implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) {
            TextComponent accept = new TextComponent();
            accept.setText("§a§lHyväksy"); //set clickable text
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa ostaaksesi.").create())); //display text msg when hovering
            accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/buyhomes confirm")); //runs command when they click the text

            int price = getNextHomeCost(p);

            p.sendMessage("§fSinulla on §e" + HomeManager.getAllowedHomes(p) + " §fkotia yhteensä. §7(Rank: " + HomeManager.getAllowedHomesOfRank(p) + " §8| §7Lisäkodit: " + HomeManager.getAllowedAdditionalHomes(p) + ")§r");
            p.sendMessage("Haluatko varmasti ostaa lisäkodin? \nHinta: §e" + price + "Ⓖ");
            p.sendMessage("§fJokaisen kodin osto nostaa hintaa §e10%§f.");
            p.sendMessage(accept);
        } else {
            switch (args.length) {
                case 1:
                    if (args[0].equalsIgnoreCase("confirm")) {

                        int price = getNextHomeCost(p);

                        if (SelviytymisHarpake.getEconomy().getBalance(p) >= price) {
                            SelviytymisHarpake.getEconomy().withdrawPlayer(p, price);

                            HomeManager.addAdditionalHomes(p);
                            p.sendMessage("Sinulla on nyt §e" + HomeManager.getAllowedHomes(p) + " §fkotia yhteensä. §7(Rank: " + HomeManager.getAllowedHomesOfRank(p) + " §8| §7Lisäkodit: " + HomeManager.getAllowedAdditionalHomes(p) + ")§r");
                        } else {
                            p.sendMessage("Kodin osto maksaa §e" + price + "Ⓖ§f, ja sinulla on vain §e" + SelviytymisHarpake.getEconomy().getBalance(p));
                        }
                    }
            }
        }return true;
    }

    public int getNextHomeCost(Player p) {
        double rate = 0.10;
        int initialCost = 5000;
        double cost = initialCost * Math.pow(1 + rate, HomeManager.getAllowedAdditionalHomes(p));
        return (int) cost;
    }
}