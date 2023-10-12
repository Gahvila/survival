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

    /*public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (p.hasPermission("buyhomes.yes")){
            if (HomeManager.getAllowedHomes(p) == 9){
                p.sendMessage("Voit ostaa vain 9 kotia, ja sinulla on jo 9 kotia.");

            }else{
                if (SelviytymisHarpake.getEconomy().getBalance(p) >= 5000){
                    SelviytymisHarpake.getEconomy().withdrawPlayer(p, 5000);
                    HomeManager.addAdditionalHomes(p);
                    p.sendMessage("Ostit uuden kodin! Sinulla on nyt §e" + HomeManager.getAllowedHomes(p) + " §fkotia yhteensä.");
                }else{
                    p.sendMessage("Kodin osto maksaa §e5000 §fkolikkoa, ja sinulla on vain §e" + SelviytymisHarpake.getEconomy().getBalance(p));
                }
            }
        }return false;
    }

     */


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) {
            TextComponent accept = new TextComponent();
            accept.setText("§a§lHyväksy"); //set clickable text
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa ostaaksesi.").create())); //display text msg when hovering
            accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/buyhomes confirm")); //runs command when they click the text

            Integer price = getNextHomeCost(p);

            p.sendMessage("§fSinulla on §e" + HomeManager.getAllowedHomes(p) + " §fkotia yhteensä. §7(Rank: " + HomeManager.getAllowedHomesOfRank(p) + " §8| §7Lisäkodit: " + HomeManager.getAllowedAdditionalHomes(p) + ")§r");
            p.sendMessage("Haluatko varmasti ostaa lisäkodin? \nHinta: §e" + price + "Ⓖ");
            if (p.hasPermission("survival.homes.pro")) {
                p.sendMessage("§5§lPRO§f-Alennus: §fJokaisen kodin osto nostaa hintaa §e§m20%§r §e10%§f.");
            } else if (p.hasPermission("survival.homes.mvp")) {
                p.sendMessage("§6§lMVP§f-Alennus: §fJokaisen kodin osto nostaa hintaa §e§m20%§r §e15%§f.");
            } else {
                p.sendMessage("Jokaisen kodin osto nostaa hintaa §e20%§f.");
            }
            p.sendMessage(accept);
        } else {
            switch (args.length) {
                case 1:
                    if (args[0].equalsIgnoreCase("confirm")) {

                        Integer price = getNextHomeCost(p);

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
        if (p.hasPermission("survival.homes.pro")){
            double rate = 0.10;
            int initialCost = 5000;
            double cost = initialCost * Math.pow(1 + rate, HomeManager.getAllowedAdditionalHomes(p));
            return (int) cost;
        }else if (p.hasPermission("survival.homes.mvp")){
            double rate = 0.15;
            int initialCost = 5000;
            double cost = initialCost * Math.pow(1 + rate, HomeManager.getAllowedAdditionalHomes(p));
            return (int) cost;
        }else{
            double rate = 0.20;
            int initialCost = 5000;
            double cost = initialCost * Math.pow(1 + rate, HomeManager.getAllowedAdditionalHomes(p));
            return (int) cost;
        }
    }
}