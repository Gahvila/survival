package net.gahvila.selviytymisharpake.PlayerFeatures.Commands;

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

public class TPACMD implements CommandExecutor {

    public static HashMap<Player, Player> tpa = new HashMap<>();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p1 = (Player) sender;
            switch (cmd.getName()){
                case "tpa":
                    if (args.length == 1) {
                        if (!args[0].equalsIgnoreCase(p1.getName())) {
                            if (Bukkit.getServer().getPlayer(args[0]) != null) {
                                Player p2 = Bukkit.getServer().getPlayer(args[0]);
                                if (tpa.get(p2) != p1) {


                                    //Player 1
                                    p1.sendMessage("§a§lSurvival §8> §fLähetit TPA-Pyynnön pelaajalle §e" + p2.getName() + "§f.");
                                    p1.playSound(p1.getLocation(), Sound.ENTITY_VILLAGER_YES, 2F, 1F);
                                    Bukkit.getServer().getScheduler().runTaskLater(SelviytymisHarpake.instance, new Runnable() {
                                        @Override
                                        public void run() {
                                            if (tpa.get(p1) != null) {
                                                tpa.remove(p1, p2);
                                                p1.sendMessage("§a§lSurvival §8> §fPyyntösi vanhentui!");
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

                                    p2.sendMessage("§r");
                                    p2.sendMessage("§e" + p1.getName() + "§r §7lähetti sinulle TPA-Pyynnön");
                                    p2.sendMessage("§7Hyväksyäksesi, suorita komento §e/tpaccept");
                                    p2.sendMessage("§7Kieltäytyäksesi, suorita komento §e/tpadeny");
                                    p2.sendMessage("§7Pyyntö vanhenee §e40 sekunnin §7kuluttua.");
                                    p2.spigot().sendMessage(accept, text, deny);
                                    p2.playSound(p1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2F, 1F);


                                    //Both
                                    tpa.put(p2, p1);
                                } else {
                                    p1.sendMessage("§a§lSurvival §8> §fEt voi lähettää pelaajalle §e" + p2.getName() + "§r §fTPA-pyyntöä, koska olet jo lähettänyt hänelle pyynnön.");
                                }
                            }
                        }
                    } else {
                        p1.sendMessage("§a§lSurvival §8> §fHupsista! Tuota komentoa käytetään näin: §e/tpa (pelaajan nimi)§f.");
                    }
                    break;
                case "tpaccept":
                    if (tpa.get(p1) != null) {
                        Player player = tpa.get(p1);

                        if (player.getGameMode().equals(GameMode.ADVENTURE)) {
                            player.setGameMode(GameMode.SURVIVAL);
                        }
                        player.teleportAsync(p1.getLocation());
                        p1.sendMessage("§a§lSurvival §8> §fHyväksyit TPA-Pyynnön pelaajalta §e" + player.getName() + "§f.");
                        player.sendMessage("§a§lSurvival §8> §fTeleporttasit pelaajaan §e" + p1.getName() + "§f.");
                        player.playSound(p1.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2F, 1F);
                        p1.playSound(p1.getLocation(), Sound.ENTITY_VILLAGER_YES, 2F, 1F);
                        tpa.remove(p1);
                        tpa.remove(player);
                    } else {
                        p1.sendMessage("§a§lSurvival §8> §fSinulla ei ole TPA-Pyyntöjä.");
                    }
                    break;
                case "tpadeny":
                    if (tpa.get(p1) != null) {
                        Player player = tpa.get(p1);
                        p1.sendMessage("§a§lSurvival §8> §fKieltäydyit TPA-Pyynnöstä.");
                        player.sendMessage("§a§lSurvival §8> §fSinua ei teleportattu koska §e" + p1.getName() + "§r §fkieltäytyi siitä.");
                        tpa.remove(p1);
                        tpa.remove(player);
                    }
                    break;

            }
        }return true;
    }
}