package net.gahvila.selviytymisharpake.PlayerFeatures.Commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TPACMD {

    public static HashMap<Player, Player> tpa = new HashMap<>();

    public void registerCommands() {
        new CommandAPICommand("tpa")
                .withArguments(new EntitySelectorArgument.OnePlayer("pelaaja"))
                .executesPlayer((tpasender, args) -> {
                    Player tpareceiver = (Player) args.get("pelaaja");
                    if (tpasender.getName().equals(tpareceiver.getName())){
                        tpasender.sendMessage("Et voi teleportata itseesi.");
                        return;
                    }
                    if (tpa.get(tpareceiver) != tpasender) {
                        //Player 1
                        tpasender.sendMessage("§fLähetit TPA-Pyynnön pelaajalle §e" + tpareceiver.getName() + "§f.");
                        tpasender.playSound(tpasender.getLocation(), Sound.ENTITY_VILLAGER_YES, 2F, 1F);
                        Bukkit.getServer().getScheduler().runTaskLater(SelviytymisHarpake.instance, new Runnable() {
                            @Override
                            public void run() {
                                if (tpa.get(tpasender) != null) {
                                    tpa.remove(tpareceiver, tpasender);
                                    tpasender.sendMessage("§fPyyntösi vanhentui!");
                                }
                            }
                        }, 20 * 40);


                        //Player 2
                        TextComponent accept = new TextComponent();
                        accept.setText("§a§lHyväksy");
                        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa minua hyväksyäksesi!").create())); //display text msg when hovering
                        accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tpaccept")); //runs command when they click the text

                        TextComponent deny = new TextComponent();
                        deny.setText("§c§lkieltäydy");
                        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa minua kieltäytyäksesi!").create())); //display text msg when hovering
                        deny.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tpadeny")); //runs command when they click the text

                        TextComponent text = new TextComponent();
                        text.setText(" §7tai ");

                        tpareceiver.sendMessage("§r");
                        tpareceiver.sendMessage("§e" + tpasender.getName() + "§r §7lähetti sinulle TPA-Pyynnön");
                        tpareceiver.sendMessage("§7Hyväksyäksesi, suorita komento §e/tpaccept");
                        tpareceiver.sendMessage("§7Kieltäytyäksesi, suorita komento §e/tpadeny");
                        tpareceiver.sendMessage("§7Pyyntö vanhenee §e40 sekunnin §7kuluttua.");
                        tpareceiver.spigot().sendMessage(accept, text, deny);
                        tpareceiver.playSound(tpasender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2F, 1F);


                        tpa.put(tpareceiver, tpasender);
                    } else {
                        tpasender.sendMessage("§fEt voi lähettää pelaajalle §e" + tpareceiver.getName() + "§r §fTPA-pyyntöä, koska olet jo lähettänyt hänelle pyynnön.");
                    }
                })

                .register();
        new CommandAPICommand("tpahere")
                .withArguments(new EntitySelectorArgument.OnePlayer("pelaaja"))
                .executesPlayer((tpasender, args) -> {
                    Player tpareceiver = (Player) args.get("pelaaja");
                    if (tpasender.getName().equals(tpareceiver.getName())){
                        tpasender.sendMessage("Et voi teleportata itseesi.");
                        return;
                    }
                    if (tpa.get(tpareceiver) != tpasender) {


                        //Player 1
                        tpasender.sendMessage("§fLähetit TPAHere-Pyynnön pelaajalle §e" + tpareceiver.getName() + "§f.");
                        tpasender.playSound(tpasender.getLocation(), Sound.ENTITY_VILLAGER_YES, 2F, 1F);
                        Bukkit.getServer().getScheduler().runTaskLater(SelviytymisHarpake.instance, new Runnable() {
                            @Override
                            public void run() {
                                if (tpa.get(tpasender) != null) {
                                    tpa.remove(tpasender, tpareceiver);
                                    tpa.remove(tpareceiver, tpasender);
                                    tpasender.sendMessage("§fPyyntösi vanhentui!");
                                }
                            }
                        }, 20 * 40);


                        //Player 2
                        TextComponent accept = new TextComponent();
                        accept.setText("§a§lHyväksy");
                        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa minua hyväksyäksesi!").create())); //display text msg when hovering
                        accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tpaccept")); //runs command when they click the text

                        TextComponent deny = new TextComponent();
                        deny.setText("§c§lkieltäydy");
                        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa minua kieltäytyäksesi!").create())); //display text msg when hovering
                        deny.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tpadeny")); //runs command when they click the text

                        TextComponent text = new TextComponent();
                        text.setText(" §7tai ");

                        tpareceiver.sendMessage("§r");
                        tpareceiver.sendMessage("§e" + tpasender.getName() + "§r §7lähetti sinulle TPA-Pyynnön");
                        tpareceiver.sendMessage("§7Hyväksyäksesi, suorita komento §e/tpaccept");
                        tpareceiver.sendMessage("§7Kieltäytyäksesi, suorita komento §e/tpadeny");
                        tpareceiver.sendMessage("§7Pyyntö vanhenee §e40 sekunnin §7kuluttua.");
                        tpareceiver.spigot().sendMessage(accept, text, deny);
                        tpareceiver.playSound(tpasender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2F, 1F);


                        //Both
                        tpa.put(tpareceiver, tpasender);
                        tpa.put(tpasender, tpareceiver);
                    } else {
                        tpasender.sendMessage("§fEt voi lähettää pelaajalle §e" + tpareceiver.getName() + "§r §fTPA-pyyntöä, koska olet jo lähettänyt hänelle pyynnön.");
                    }
                })

                .register();
        new CommandAPICommand("tpaccept")
                .withAliases("tpy")
                .executesPlayer((tpasender, args) -> {
                    if (tpa.get(tpasender) != null) {
                        Player tpareceiver = tpa.get(tpasender);
                        tpareceiver.teleportAsync(tpasender.getLocation());
                        tpasender.sendMessage("§fHyväksyit TPA-Pyynnön pelaajalta §e" + tpareceiver.getName() + "§f.");
                        tpareceiver.sendMessage("§fTeleporttasit pelaajaan §e" + tpasender.getName() + "§f.");
                        tpareceiver.playSound(tpasender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2F, 1F);
                        tpasender.playSound(tpasender.getLocation(), Sound.ENTITY_VILLAGER_YES, 2F, 1F);
                        tpa.remove(tpasender);
                        tpa.remove(tpareceiver);
                    } else {
                        tpasender.sendMessage("§fSinulla ei ole TPA-Pyyntöjä.");
                    }
                })

                .register();
        new CommandAPICommand("tpadeny")
                .executesPlayer((tpasender, args) -> {
                    if (tpa.get(tpasender) != null) {
                        Player player = tpa.get(tpasender);
                        tpasender.sendMessage("§fKieltäydyit TPA-Pyynnöstä.");
                        player.sendMessage("§fSinua ei teleportattu koska §e" + tpasender.getName() + "§r §fkieltäytyi siitä.");
                        tpa.remove(tpasender);
                        tpa.remove(player);
                    } else {
                        tpasender.sendMessage("§fSinulla ei ole TPA-Pyyntöjä.");
                    }
                })

                .register();
        new CommandAPICommand("tpacancel")
                .executesPlayer((tpasender, args) -> {
                    if (tpa.get(tpasender) != null) {
                        Player player = tpa.get(tpasender);
                        tpasender.sendMessage("Peruit TPA-pyynnön.");
                        player.sendMessage("§e" + tpasender.getName() + " §fperui lähettämänsä TPA-pyynnön.");
                        tpa.remove(player, tpasender);
                        tpa.remove(tpasender, player);
                    } else {
                        tpasender.sendMessage("§fSinulla ei ole TPA-Pyyntöjä.");
                    }
                })

                .register();

    }

}