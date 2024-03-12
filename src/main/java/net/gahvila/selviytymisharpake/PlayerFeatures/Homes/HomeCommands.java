package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeCommands {
    private final HomeManager homeManager;


    public HomeCommands(HomeManager homeManager) {
        this.homeManager = homeManager;
    }
    public void registerCommands() {
        new CommandAPICommand("buyhomes")
                .withSubcommand(new CommandAPICommand("forcebuy")
                        .executesPlayer((p, args) -> {
                            int price = getNextHomeCost(p);

                            if (SelviytymisHarpake.getEconomy().getBalance(p) >= price) {
                                SelviytymisHarpake.getEconomy().withdrawPlayer(p, price);

                                homeManager.addAdditionalHomes(p);
                                p.sendMessage(toMiniMessage("<white>Sinulla on nyt</white> <#85FF00>" + homeManager.getAllowedHomes(p) + "</#85FF00> <white>kotia yhteensä.</white> <gray>(Rank: " + homeManager.getAllowedHomesOfRank(p) + "</gray> <dark_gray>|</dark_gray> <gray>Lisäkodit: " + homeManager.getAllowedAdditionalHomes(p) + ")</gray>"));
                            } else {
                                p.sendMessage(toMiniMessage("<white>Kodin osto maksaa</white> <#85FF00>" + price + "Ⓖ</#85FF00><white>, ja sinulla on vain</white> <#85FF00>" + SelviytymisHarpake.getEconomy().getBalance(p) + "</#85FF00>"));
                            }
                        }))
                .executesPlayer((p, args) -> {
                    TextComponent accept = new TextComponent();
                    accept.setText("§a§lHyväksy"); //set clickable text
                    accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa ostaaksesi.").create())); //display text msg when hovering
                    accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/buyhomes forcebuy")); //runs command when they click the text

                    int price = getNextHomeCost(p);

                    p.sendMessage(toMiniMessage("<white>Sinulla on</white> <#85FF00>" + homeManager.getAllowedHomes(p) + "</#85FF00> <white>kotia yhteensä.</white> <gray>(Rank: " + homeManager.getAllowedHomesOfRank(p) + "</gray> <dark_gray>|</dark_gray> <gray>Lisäkodit: " + homeManager.getAllowedAdditionalHomes(p) + ")</gray>"));
                    p.sendMessage(toMiniMessage("<white>Haluatko varmasti ostaa lisäkodin?\nHinta:</white> <#85FF00>" + price + "Ⓖ</#85FF00>"));
                    p.sendMessage(toMiniMessage("<white>Jokaisen kodin osto nostaa hintaa</white> <#85FF00>10%</#85FF00><white>.</white>"));
                    p.sendMessage(accept);
                })

                .register();
        new CommandAPICommand("delhome")
                .withArguments(customHomeArgument("koti"))
                .executesPlayer((p, args) -> {
                    String nimi = args.getRaw("koti");
                    if (homeManager.getHomes(p) == null) {
                        p.sendMessage(toMiniMessage("</white>Sinulla ei ole kotia nimellä <#85FF00>" + nimi + "</#85FF00><white>.</white>"));
                    } else if (homeManager.getHomes(p).contains(nimi)) {
                        Bukkit.getScheduler().runTaskAsynchronously(SelviytymisHarpake.instance, new Runnable() {
                            @Override
                            public void run() {
                                homeManager.deleteHome(p, nimi);
                            }
                        });
                        p.sendMessage(toMiniMessage("<white>Koti nimellä</white> <#85FF00>" + nimi + "</#85FF00> <white>poistettu.</white>"));
                    } else {
                        p.sendMessage(toMiniMessage("<white>Sinulla ei ole kotia nimellä</white> <#85FF00>" + nimi + "</#85FF00><white>.</white>"));
                    }
                })
                .register();
        new CommandAPICommand("home")
                .withSubcommand(new CommandAPICommand("sänky")
                        .executesPlayer((p, args) -> {
                            if (p.getBedSpawnLocation() != null){
                                p.teleportAsync(p.getBedSpawnLocation());
                                p.setWalkSpeed(0.2F);
                            }else{
                                p.sendMessage("Sinulla ei ole sänkyä asetettuna.");
                            }
                        }))
                .withOptionalArguments(customHomeArgument("koti"))
                .executesPlayer((p, args) -> {
                    if (args.getRaw("koti") == null) {
                        if (homeManager.getHomes(p) != null) {
                            new HomeMenu(SelviytymisHarpake.getPlayerMenuUtility(p), homeManager).open();
                        }else{
                            p.sendMessage(toMiniMessage("<white>Sinulla ei ole kotia. Voit asettaa kodin komennolla</white> <#85FF00>/sethome</#85FF00><white>.</white>"));
                        }
                        return;
                    }

                    String nimi = args.getRaw("koti");

                    if (homeManager.getHomes(p) != null) {
                        if (homeManager.getHomes(p).contains(nimi)) {
                            p.teleportAsync(homeManager.getHome(p, nimi));
                            p.setWalkSpeed(0.2F);
                        } else {
                            p.sendMessage(toMiniMessage("<white>Sinulla ei ole kotia nimellä</white> <#85FF00>" + nimi + "</#85FF00><white>!</white>"));
                        }
                    }
                })

                .register();
        new CommandAPICommand("sethome")
                .withOptionalArguments(new GreedyStringArgument("nimi"))
                .executesPlayer((p, args) -> {
                    if (args.get("nimi") == null){
                        p.sendMessage("\n");
                        p.sendMessage(toMiniMessage("<white>Kodin nimi voi olla maksimissaan</white> <#85FF00>16 kirjainta</#85FF00> <white>pitkä, ja se voi sisältää vain <#85FF00>aakkosia</#85FF00> <white>ja</white> <#85FF00>numeroita<#85FF00/><white>.</white>"));
                        p.sendMessage(toMiniMessage("Komento asettaa kodin tarkasti siihen kohtaan missä seisot, mukaanlukien sen, minne suuntaan katsot."));
                        p.sendMessage(toMiniMessage("<white>Suorita komento</white> <#85FF00>/sethome [kodin nimi]</#85FF00> <white>asettaaksesi kotisi.</white>"));
                        return;
                    }
                    String nimi = (String) args.get("nimi");
                    if (homeManager.getHomes(p) == null || homeManager.getHomes(p).size() < homeManager.getAllowedHomes(p)) {
                        if (nimi.matches("[a-zA-ZöÖäÄåÅ0-9- ]*")) {
                            if (nimi.length() <= 16) {
                                if (!p.getWorld().getName().equals("spawn") || (!p.getWorld().getName().equals("resurssinether"))) {
                                    Bukkit.getScheduler().runTaskAsynchronously(SelviytymisHarpake.instance, new Runnable() {
                                        @Override
                                        public void run() {
                                            homeManager.saveHome(p, nimi, p.getLocation());
                                        }
                                    });
                                    p.sendMessage(toMiniMessage("<white>Koti nimellä</white> <#85FF00>" + nimi + "</#85FF00> <white>asetettu.</white>"));
                                } else {
                                    p.sendMessage("Et voi asettaa kotia tässä maassa.");
                                }
                            } else {
                                p.sendMessage("\n");
                                p.sendMessage(toMiniMessage("<white>Kodin nimi voi olla maksimissaan</white> <#85FF00>16 kirjainta</#85FF00> <white>pitkä, ja se voi sisältää vain <#85FF00>aakkosia</#85FF00> <white>ja</white> <#85FF00>numeroita<#85FF00/><white>.</white>"));
                                p.sendMessage(toMiniMessage("Komento asettaa kodin tarkasti siihen kohtaan missä seisot, mukaanlukien sen, minne suuntaan katsot."));
                                p.sendMessage(toMiniMessage("<white>Suorita komento</white> <#85FF00>/sethome [kodin nimi]</#85FF00> <white>asettaaksesi kotisi.</white>"));
                            }
                        }
                    }
                })

                .register();

    }

    public int getNextHomeCost(Player p) {
        double rate = 0.10;
        int initialCost = 5000;
        double cost = initialCost * Math.pow(1 + rate, homeManager.getAllowedAdditionalHomes(p));
        return (int) cost;
    }

    public Argument<ArrayList<String>> customHomeArgument(String nodeName) {
        // Construct our CustomArgument that takes in a String input and returns a list of home names
        return new CustomArgument<ArrayList<String>, String>(new GreedyStringArgument(nodeName), info -> {
            // Retrieve the list of home names for the player

            Player player = (Player) info.sender();

            ArrayList<String> homeNames = homeManager.getHomes(player);

            // Check if the home names list is not null and contains names
            if (homeNames == null || homeNames.isEmpty()) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Pelaajalla ei ole koteja."));
            } else {
                return homeNames;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            if (info == null || info.sender() == null || !(info.sender() instanceof Player player)) {
                throw new IllegalArgumentException("Invalid sender information.");
            }

            if (homeManager.getHomes(player) == null) {
                return new String[0];
            }

            return homeManager.getHomes((Player) info.sender()).toArray(new String[0]);
        }));
    }

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }
}
