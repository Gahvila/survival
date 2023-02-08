package net.gahvila.selviytymisharpake.PlayerWarps;

import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.PlayerMenuUtility;
import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.menu.WarpConfirmMenu;
import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.menu.WarpMenu;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class WarpsCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("warps.yes")) {
                if (args.length == 1) {
                    if (WarpManager.getWarps().contains(args[0])) {
                        Integer price = WarpManager.getWarpPrice(args[0]);
                        if (price > 0 && price < 51) {
                            TextComponent accept = new TextComponent();
                            accept.setText("§a§lTeleporttaa & hyväksy"); //set clickable text
                            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa ostaaksesi.").create())); //display text msg when hovering
                            accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/warp " + args[0] + " confirm")); //runs command when they click the text

                            p.sendMessage("§fHaluatko teleportata warppiin §e" + args[0] + "§f?");
                            p.sendMessage("Teleporttauksen hinta on §e" + price + " §fkultaa.");
                            p.sendMessage(accept);
                            //p.sendMessage("Maksulliset warpit eivät tällä hetkellä toimi komennon kautta! Käytäthän valikkoa: §e/warp");
                        } else {
                            p.teleport(WarpManager.getWarp(args[0]));
                            p.sendMessage(WarpManager.getWarpPrice(args[0]).toString());
                            p.setGameMode(GameMode.SURVIVAL);
                            p.setWalkSpeed(0.2F);
                        }
                    } else {
                        new WarpMenu(SelviytymisHarpake.getPlayerMenuUtility(p)).open();
                    }
                } else if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("confirm")) {
                        Integer price = WarpManager.getWarpPrice(args[0]);
                        if (SelviytymisHarpake.getEconomy().getBalance(p) >= price) {
                            SelviytymisHarpake.getEconomy().withdrawPlayer(p, price);
                            p.sendMessage("Tililtäsi veloitettiin §e" + price + " §fkultaa.");
                            p.closeInventory();
                            p.teleportAsync(WarpManager.getWarp(args[0]));
                            p.sendMessage("Sinut teleportattiin warppiin §e" + args[0] + "§f.");

                            if (!p.getName().equals(WarpManager.getWarpOwnerName(args[0]))) {
                                WarpManager.addUses(args[0]);
                            }
                        } else {
                            p.sendMessage("§e" + args[0] + " §fmaksaa §e" + SelviytymisHarpake.getEconomy().getBalance(p) + " §fkultaa, sinulla ei ole tarpeeksi.");
                        }
                    }else{
                        new WarpMenu(SelviytymisHarpake.getPlayerMenuUtility(p)).open();
                    }

                } else {
                    new WarpMenu(SelviytymisHarpake.getPlayerMenuUtility(p)).open();
                }
            }
        }
        return false;
    }
}