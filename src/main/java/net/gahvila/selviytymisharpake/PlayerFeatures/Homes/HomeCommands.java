package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.model.playmode.MonoStereoMode;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class HomeCommands {

    public static HashMap<Player, Integer> gambling = new HashMap<>();
    private final HomeManager homeManager;


    public HomeCommands(HomeManager homeManager) {
        this.homeManager = homeManager;
    }
    public void registerCommands() {
        new CommandAPICommand("buyhome")
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

                    int price = getNextHomeCost(p);

                    p.sendMessage(toMiniMessage("<white>Sinulla on</white> <#85FF00>" + homeManager.getAllowedHomes(p) + "</#85FF00> <white>kotia yhteensä.</white> <gray>(Rank: " + homeManager.getAllowedHomesOfRank(p) + "</gray> <dark_gray>|</dark_gray> <gray>Lisäkodit: " + homeManager.getAllowedAdditionalHomes(p) + ")</gray>"));
                    p.sendMessage(toMiniMessage("<white>Haluatko varmasti ostaa lisäkodin?\nHinta:</white> <#85FF00>" + price + "Ⓖ</#85FF00>"));
                    p.sendMessage(toMiniMessage("<white>Jokaisen kodin osto nostaa hintaa</white> <#85FF00>10%</#85FF00><white>.</white>"));
                    //accept button
                    p.sendMessage(toMiniMessage("<#85FF00><b>Hyväksy</b></#85FF00>").hoverEvent(HoverEvent.showText(toMiniMessage("<#85FF00>Klikkaa ostaaksesi!</#85FF00>"))).clickEvent(ClickEvent.runCommand("/buyhome forcebuy")));

                })

                .register();
        new CommandAPICommand("delhome")
                .withArguments(customHomeArgument("koti"))
                .executesPlayer((p, args) -> {
                    String nimi = args.getRaw("koti");
                    if (homeManager.getHomes(p) == null) {
                        p.sendMessage(toMiniMessage("</white>Sinulla ei ole kotia nimellä <#85FF00>" + nimi + "</#85FF00><white>.</white>"));
                    } else if (homeManager.getHomes(p).contains(nimi)) {
                        homeManager.deleteHome(p, nimi);
                        p.sendMessage(toMiniMessage("<white>Koti nimellä</white> <#85FF00>" + nimi + "</#85FF00> <white>poistettu.</white>"));
                    } else {
                        p.sendMessage(toMiniMessage("<white>Sinulla ei ole kotia nimellä</white> <#85FF00>" + nimi + "</#85FF00><white>.</white>"));
                    }
                })
                .register();
        new CommandAPICommand("renamehome")
                .withArguments(customHomeArgument("koti"))
                .withOptionalArguments(new GreedyStringArgument("nimi"))
                .executesPlayer((p, args) -> {
                    String oldName = args.getRaw("koti");
                    String newName = args.getRaw("nimi");
                    if (args.get("nimi") == null){
                        p.sendMessage("test");
                        return;
                    }

                    if (homeManager.getHomes(p) == null) {
                        p.sendMessage(toMiniMessage("</white>Sinulla ei ole kotia nimellä <#85FF00>" + oldName + "</#85FF00><white>.</white>"));
                    } else if (homeManager.getHomes(p).contains(oldName) && !(homeManager.getHomes(p).contains(newName))) {
                        homeManager.editHomeName(p, oldName, newName);
                        p.sendMessage(toMiniMessage("<white>Kodin <#85FF00>" + oldName + " <white>nimi muutettu: <#85FF00>" + newName));
                    } else {
                        p.sendMessage(toMiniMessage("<white>Sinulla ei ole kotia nimellä</white> <#85FF00>" + oldName + "</#85FF00><white>.</white>"));
                    }
                })
                .register();
        new CommandAPICommand("home")
                .withSubcommand(new CommandAPICommand("sänky")
                        .executesPlayer((p, args) -> {
                            if (p.getBedSpawnLocation() != null){
                                p.teleportAsync(p.getBedSpawnLocation());
                                p.setWalkSpeed(0.2F);
                                p.sendMessage(toMiniMessage("<white>Sinut teleportattiin kotiin</white> <#85FF00>sänky<#/85FF00>."));
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
                            p.sendMessage(toMiniMessage("<white>Sinut teleportattiin kotiin</white> <#85FF00>" + nimi + "<#/85FF00>."));

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
                        if (homeManager.getHomes(p) == null || homeManager.getHomes(p).size() < homeManager.getAllowedHomes(p)) {
                            if (gambling.containsKey(p)){
                                p.sendMessage("Et voi asettaa kotia juuri nyt.");
                                return;
                            }
                            p.sendMessage("Kotisi nimi arvotaan satunnaisesti koska et syöttänyt nimeä.");
                            String randomName = generateRandomString();

                            homeManager.saveHome(p, randomName, p.getLocation());

                            goGambaGoldGoldGold(p, randomName);
                        }
                        return;
                    }
                    String nimi = (String) args.get("nimi");
                    if (homeManager.getHomes(p) == null || homeManager.getHomes(p).size() < homeManager.getAllowedHomes(p)) {
                        if (nimi.matches("[a-zA-Z0-9]*") && nimi.length() <= 16) {
                            if (nimi.equals("sänky")) {
                                p.sendMessage("Et voi käyttää tuota nimeä.");
                                return;
                            }
                            if (!p.getWorld().getName().equals("spawn") || (!p.getWorld().getName().equals("resurssinether"))) {
                                homeManager.saveHome(p, nimi, p.getLocation());
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
                    } else {
                        p.sendMessage("Sinulla on maksimi määrä koteja asetettuna.");
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
        return new CustomArgument<ArrayList<String>, String>(new StringArgument(nodeName), info -> {
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

    private String generateRandomString() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            char randomChar = (char) (random.nextInt(26) + 'a');
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }


    int taskID;
    private void goGambaGoldGoldGold(Player player, String name) {
        if (gambling.containsKey(player)){
            return;
        }
        Song song = NBSDecoder.parse(new File(instance.getDataFolder() + "/music/gamba.nbs"));
        RadioSongPlayer rsp = new RadioSongPlayer(song);
        rsp.setChannelMode(new MonoStereoMode());
        rsp.setVolume((byte) 25);
        rsp.addPlayer(player);
        rsp.setAutoDestroy(true);

        rsp.setPlaying(true);

        taskID = Bukkit.getScheduler().runTaskTimer(instance, () -> {
            int currentGamblingValue = gambling.getOrDefault(player, 0);
            if (currentGamblingValue >= 91) {
                Bukkit.getServer().getScheduler().runTask(instance, new Runnable() {
                    @Override
                    public void run() {
                        //
                        player.showTitle(Title.title(
                                toMiniMessage("<#85FF00>" + name),
                                toMiniMessage("<gradient:#FFF226:#F91526>max win!!</gradient:#FFF226:#F91526>"),
                                Title.Times.times(
                                        Duration.ofSeconds(0),
                                        Duration.ofSeconds(8),
                                        Duration.ofSeconds(1)
                                ))
                        );
                        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5F, 1F);
                        player.sendMessage(toMiniMessage("<white>Sinun uuden kotisi nimi on</white> <#85FF00>" + name + "</#85FF00><white>. Pääset sinne komennolla</white> <#85FF00>/home " + name + "</#85FF00><white>, tai klikkaamalla tätä viestiä.</white>").hoverEvent(HoverEvent.showText(toMiniMessage("<#85FF00>Klikkaa teleportataksesi!</#85FF00>"))).clickEvent(ClickEvent.runCommand("/home " + name)));
                        gambling.remove(player);
                        Bukkit.getScheduler().cancelTask(taskID);
                        //

                    }
                });
                return;
            }

            //code
            gambling.put(player, currentGamblingValue + 1);


            String randomName = generateRandomString();

            player.showTitle(Title.title(
                    toMiniMessage("<#FFD700>" + randomName),
                    toMiniMessage("<#78e600>Arvotaan kodin nimeä...</#78e600>"),
                    Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(1),
                            Duration.ofSeconds(1)
                    ))
            );
        }, 0L, 2L).getTaskId();
    }

}
