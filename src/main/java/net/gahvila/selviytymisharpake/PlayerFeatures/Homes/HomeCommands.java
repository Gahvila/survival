package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
                                p.sendMessage("Sinulla on nyt §e" + homeManager.getAllowedHomes(p) + " §fkotia yhteensä. §7(Rank: " + homeManager.getAllowedHomesOfRank(p) + " §8| §7Lisäkodit: " + homeManager.getAllowedAdditionalHomes(p) + ")§r");
                            } else {
                                p.sendMessage("Kodin osto maksaa §e" + price + "Ⓖ§f, ja sinulla on vain §e" + SelviytymisHarpake.getEconomy().getBalance(p));
                            }
                        }))
                .executesPlayer((p, args) -> {
                    TextComponent accept = new TextComponent();
                    accept.setText("§a§lHyväksy"); //set clickable text
                    accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa ostaaksesi.").create())); //display text msg when hovering
                    accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/buyhomes forcebuy")); //runs command when they click the text

                    int price = getNextHomeCost(p);

                    p.sendMessage("§fSinulla on §e" + homeManager.getAllowedHomes(p) + " §fkotia yhteensä. §7(Rank: " + homeManager.getAllowedHomesOfRank(p) + " §8| §7Lisäkodit: " + homeManager.getAllowedAdditionalHomes(p) + ")§r");
                    p.sendMessage("Haluatko varmasti ostaa lisäkodin? \nHinta: §e" + price + "Ⓖ");
                    p.sendMessage("§fJokaisen kodin osto nostaa hintaa §e10%§f.");
                    p.sendMessage(accept);
                })

                .register();
        new CommandAPICommand("delhome")
                .withArguments(customHomeArgument("koti"))
                .executesPlayer((p, args) -> {
                    String nimi = args.getRaw("koti");
                    if (homeManager.getHomes(p) == null) {
                        p.sendMessage("Sinulla ei ole kotia nimellä §e" + nimi + "§f.");
                    } else if (homeManager.getHomes(p).contains(nimi)) {
                        Bukkit.getScheduler().runTaskAsynchronously(SelviytymisHarpake.instance, new Runnable() {
                            @Override
                            public void run() {
                                homeManager.deleteHome(p, nimi);
                            }
                        });
                        p.sendMessage("§fKoti §e" + nimi + " §fpoistettu.");
                    } else {
                        p.sendMessage("Sinulla ei ole kotia nimellä §e" + nimi + "§f.");
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
                                p.sendMessage("§a§lSurvival §8> §fSinulla ei ole sänkyä asetettuna.");
                            }
                        }))
                .withOptionalArguments(customHomeArgument("koti"))
                .executesPlayer((p, args) -> {
                    if (args.getRaw("koti") == null) {
                        if (homeManager.getHomes(p) != null) {
                            new HomeMenu(SelviytymisHarpake.getPlayerMenuUtility(p), homeManager).open();
                        }else{
                            p.sendMessage("Sinulla ei ole kotia. Voit asettaa kodin komennolla §e/sethome§f.");
                        }
                        return;
                    }

                    String nimi = args.getRaw("koti");

                    if (homeManager.getHomes(p) != null) {
                        if (homeManager.getHomes(p).contains(nimi)) {
                            p.teleportAsync(homeManager.getHome(p, nimi));
                            p.setWalkSpeed(0.2F);
                        } else {
                            p.sendMessage("§a§lSurvival §8> §fSinulla ei ole kotia nimellä §e" + nimi + "§f!");
                        }
                    }
                })

                .register();
        new CommandAPICommand("sethome")
                .withOptionalArguments(new GreedyStringArgument("nimi"))
                .executesPlayer((p, args) -> {
                    if (args.get("nimi") == null){
                        p.sendMessage("\n \n \n");
                        p.sendMessage("§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
                        p.sendMessage("§fKodin nimi voi olla maksimissaan §e16 kirjainta §fpitkä, ja se voi sisältää vain §eaakkosia §fja §enumeroita§f.\n \n§fKomento asettaa kodin tarkasti siihen kohtaan missä seisot, mukaanlukien sen, minne suuntaan katsot.");
                        p.sendMessage("§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
                        p.sendMessage("§fSuorita komento §e/sethome [kodin nimi] §fasettaaksesi kotisi.");
                        return;
                    }
                    String nimi = (String) args.get("nimi");
                    if (homeManager.getHomes(p).size() < homeManager.getAllowedHomes(p)) {
                        if (nimi.matches("[a-zA-ZöÖäÄåÅ0-9- ]*")) {
                            if (nimi.length() <= 16) {
                                if (!p.getWorld().getName().equals("spawn") || (!p.getWorld().getName().equals("resurssinether"))) {
                                    Bukkit.getScheduler().runTaskAsynchronously(SelviytymisHarpake.instance, new Runnable() {
                                        @Override
                                        public void run() {
                                            homeManager.saveHome(p, nimi, p.getLocation());
                                        }
                                    });
                                    p.sendTitle("§a" + nimi, "§7Koti asetettu.", 1, 60, 1);
                                } else {
                                    p.sendMessage("Et voi asettaa kotia tässä maassa.");
                                }
                            } else {
                                p.sendMessage("\n \n \n");
                                p.sendMessage("§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
                                p.sendMessage("§fKodin nimi voi olla maksimissaan §e16 kirjainta §fpitkä, ja se voi sisältää vain §eaakkosia §fja §enumeroita§f.\n \n§fKomento asettaa kodin tarkasti siihen kohtaan missä seisot, mukaanlukien sen, minne suuntaan katsot.");
                                p.sendMessage("§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
                                p.sendMessage("§fSuorita komento §e/sethome [kodin nimi] §fasettaaksesi kotisi.");
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
}
