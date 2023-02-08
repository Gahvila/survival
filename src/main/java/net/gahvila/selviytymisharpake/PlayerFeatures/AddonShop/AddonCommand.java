package net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddonCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("addon.add")) {
            Player p = (Player) sender;
            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "enderchest":
                            if (!AddonManager.getEnderchest(p)) {
                                TextComponent accept = new TextComponent();
                                accept.setText("§a§lHyväksy"); //set clickable text
                                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa ostaaksesi.").create())); //display text msg when hovering
                                accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/addon enderchest confirm")); //runs command when they click the text

                                p.sendMessage("Haluatko varmasti ostaa enderchest lisäosan? \nHinta: §e500Ⓖ");
                                p.sendMessage(accept);
                            } else {
                                p.sendMessage("Olet jo ostanut tuon.");
                            }
                            break;
                        case "craft":
                            if (!AddonManager.getCraft(p)) {
                                TextComponent accept = new TextComponent();
                                accept.setText("§a§lHyväksy"); //set clickable text
                                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa ostaaksesi.").create())); //display text msg when hovering
                                accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/addon craft confirm")); //runs command when they click the text

                                p.sendMessage("Haluatko varmasti ostaa craft lisäosan? \nHinta: §e250Ⓖ");
                                p.sendMessage(accept);
                            } else {
                                p.sendMessage("Olet jo ostanut tuon.");
                            }
                            break;
                        case "feed":
                            if (!AddonManager.getFeed(p)) {
                                TextComponent accept = new TextComponent();
                                accept.setText("§a§lHyväksy"); //set clickable text
                                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa ostaaksesi.").create())); //display text msg when hovering
                                accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/addon feed confirm")); //runs command when they click the text

                                p.sendMessage("Haluatko varmasti ostaa feed lisäosan? \nHinta: §e5000Ⓖ");
                                p.sendMessage(accept);
                            } else {
                                p.sendMessage("Olet jo ostanut tuon.");
                            }
                            break;
                        default:
                            break;

                    }
                    break;
                case 2:
                    switch (args[0]) {
                        case "enderchest":
                            if (args[1].equalsIgnoreCase("confirm")) {
                                if (!AddonManager.getEnderchest(p)) {
                                    if (SelviytymisHarpake.getEconomy().getBalance(p) >= 500) {
                                        SelviytymisHarpake.getEconomy().withdrawPlayer(p, 500);
                                        sender.sendMessage("Success! Ostit enderchest päivityksen.");
                                        AddonManager.setEnderchest(p);
                                    } else {
                                        p.sendMessage("Sinulla ei ole tarpeeksi rahaa! Tarvitset 500Ⓖ.");
                                    }
                                }
                            }
                            break;
                        case "craft":
                            if (args[1].equalsIgnoreCase("confirm")) {
                                if (!AddonManager.getCraft(p)) {
                                    if (SelviytymisHarpake.getEconomy().getBalance(p) >= 250) {
                                        SelviytymisHarpake.getEconomy().withdrawPlayer(p, 250);
                                        sender.sendMessage("Success! Ostit craft päivityksen.");
                                        AddonManager.setCraft(p);
                                    } else {
                                        p.sendMessage("Sinulla ei ole tarpeeksi rahaa! Tarvitset 250Ⓖ.");
                                    }
                                }
                            }
                            break;
                        case "feed":
                            if (args[1].equalsIgnoreCase("confirm")) {
                                if (!AddonManager.getFeed(p)) {
                                    if (SelviytymisHarpake.getEconomy().getBalance(p) >= 5000) {
                                        SelviytymisHarpake.getEconomy().withdrawPlayer(p, 5000);
                                        sender.sendMessage("Success! Ostit feed päivityksen.");
                                        AddonManager.setFeed(p);
                                    } else {
                                        p.sendMessage("Sinulla ei ole tarpeeksi rahaa! Tarvitset 5000Ⓖ.");
                                    }
                                }
                            }
                            break;
                        default:
                            p.sendMessage("Sinun täytyy lisätä jokin argumentti:");
                            p.sendMessage("§eenderchest, craft, feed");
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