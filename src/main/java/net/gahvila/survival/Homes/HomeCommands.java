package net.gahvila.survival.Homes;

import cz.koca2000.nbs4j.Song;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.gahvila.gahvilacore.nbsminecraft.NBSAPI;
import net.gahvila.gahvilacore.nbsminecraft.platform.bukkit.player.BukkitSongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.GlobalSoundEmitter;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.survival.Features.TeleportBlocker;
import net.gahvila.survival.Messages.Message;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.survival.survival.instance;

public class HomeCommands {

    public static HashMap<Player, Integer> gambling = new HashMap<>();
    private final HomeManager homeManager;
    private final HomeMenu homeMenu;



    public HomeCommands(HomeManager homeManager, HomeMenu homeMenu) {
        this.homeManager = homeManager;
        this.homeMenu = homeMenu;
    }
    public void registerCommands() {
        new CommandAPICommand("delhome")
                .withArguments(customHomeArgument("koti"))
                .executesPlayer((p, args) -> {
                    String nimi = args.getRaw("koti");
                    UUID uuid = p.getUniqueId();
                    if (homeManager.getHomes(uuid) == null) {
                        p.sendMessage(toMM("</white>Sinulla ei ole kotia nimellä <#85FF00>" + nimi + "</#85FF00><white>.</white>"));
                    } else if (homeManager.getHomes(uuid).contains(nimi)) {
                        homeManager.deleteHome(uuid, nimi);
                        p.sendMessage(toMM("<white>Koti nimellä</white> <#85FF00>" + nimi + "</#85FF00> <white>poistettu.</white>"));
                    } else {
                        p.sendMessage(toMM("<white>Sinulla ei ole kotia nimellä</white> <#85FF00>" + nimi + "</#85FF00><white>.</white>"));
                    }
                })
                .register();
        new CommandAPICommand("renamehome")
                .withArguments(customHomeArgument("koti"))
                .executesPlayer((p, args) -> {
                    String oldName = args.getRaw("koti");
                    if (args.get("koti") == null){
                        return;
                    }

                    UUID uuid = p.getUniqueId();
                    if (homeManager.getHomes(uuid) == null) {
                        p.sendMessage(toMM("</white>Sinulla ei ole kotia nimellä <#85FF00>" + oldName + "</#85FF00><white>.</white>"));
                    } else if (homeManager.getHomes(uuid).contains(oldName)) {
                        homeMenu.showRenameHome(p, oldName, false);
                    } else {
                        p.sendMessage(toMM("<white>Sinulla ei ole kotia nimellä</white> <#85FF00>" + oldName + "</#85FF00><white>.</white>"));
                    }
                })
                .register();
        new CommandAPICommand("homes")
                .executesPlayer((p, args) -> {
                    homeMenu.show(p);
                })
                .register();
        new CommandAPICommand("edithome")
                .executesPlayer((p, args) -> {
                    homeMenu.showEdit(p, false);
                })
                .register();
        new CommandAPICommand("home")
                .withAliases("h")
                .withOptionalArguments(customHomeArgument("koti"))
                .executesPlayer((p, args) -> {
                    UUID uuid = p.getUniqueId();
                    if (args.getRaw("koti") == null) {
                        homeMenu.show(p);
                    }

                    String nimi = args.getRaw("koti");

                    if (homeManager.getHomes(uuid) != null) {
                        if (homeManager.getHomes(uuid).contains(nimi)) {
                            if (TeleportBlocker.canTeleport(p)) {
                                p.teleportAsync(homeManager.getHome(uuid, nimi));
                                p.setWalkSpeed(0.2F);
                                p.sendMessage(toMM("<white>Sinut teleportattiin kotiin</white> <#85FF00>" + nimi + "</#85FF00>."));
                            } else {
                                p.sendRichMessage(Message.TELEPORT_NOT_POSSIBLE.getText());
                            }

                        } else {
                            p.sendMessage(toMM("<white>Sinulla ei ole kotia nimellä</white> <#85FF00>" + nimi + "</#85FF00><white>!</white>"));
                        }
                    }
                })

                .register();
        new CommandAPICommand("sethome")
                .withOptionalArguments(new GreedyStringArgument("nimi"))
                .executesPlayer((p, args) -> {
                    UUID uuid = p.getUniqueId();
                    if (args.get("nimi") == null){
                        if (homeManager.getHomes(uuid) == null || homeManager.getHomes(uuid).size() < homeManager.getAllowedHomes(p)) {
                            if (gambling.containsKey(p)){
                                p.sendMessage("Et voi asettaa kotia juuri nyt.");
                                return;
                            }
                            p.sendMessage("Kotisi nimi arvotaan satunnaisesti koska et syöttänyt nimeä.");
                            String randomName = generateRandomString();

                            homeManager.saveHome(uuid, randomName, p.getLocation());

                            goGambaGoldGoldGold(p, randomName);
                        }
                        return;
                    }
                    String nimi = (String) args.get("nimi");
                    if (homeManager.getHomes(uuid) == null || homeManager.getHomes(uuid).size() < homeManager.getAllowedHomes(p)) {
                        if (nimi.matches("[a-zA-Z0-9äöåÄÖÅ!?.,\\-_]+( [a-zA-Z0-9äöåÄÖÅ!?.,\\-_]+)*") && nimi.length() <= 30) {
                            if (!p.getWorld().getName().equals("world_the_end")) {
                                homeManager.saveHome(uuid, nimi, p.getLocation());
                                p.sendMessage(toMM("<white>Koti nimellä</white> <#85FF00>" + nimi + "</#85FF00> <white>asetettu.</white>"));
                            } else {
                                p.sendMessage("Et voi asettaa kotia tässä maassa.");
                            }
                        } else {
                            p.sendMessage("\n");
                            p.sendMessage(toMM("<white>Kodin nimi voi olla maksimissaan</white> <#85FF00>30 kirjainta</#85FF00> <white>pitkä, ja se voi sisältää vain <#85FF00>aakkosia</#85FF00><white>,</white> <#85FF00>numeroita<#85FF00/><white> ja seuraavia välimerkkejä: <#85FF00>! ? , . - _</#85FF00>.</white>"));
                            p.sendMessage(toMM("Komento asettaa kodin tarkasti siihen kohtaan missä seisot, mukaanlukien sen, minne suuntaan katsot."));
                            p.sendMessage(toMM("<white>Suorita komento</white> <#85FF00>/sethome [kodin nimi]</#85FF00> <white>asettaaksesi kotisi.</white>"));
                        }
                    } else {
                        p.sendMessage("Sinulla on maksimi määrä koteja asetettuna.");
                    }
                })

                .register();

    }


    public Argument<ArrayList<String>> customHomeArgument(String nodeName) {
        // Construct our CustomArgument that takes in a String input and returns a list of home names
        return new CustomArgument<ArrayList<String>, String>(new GreedyStringArgument(nodeName), info -> {
            // Retrieve the list of home names for the player

            Player player = (Player) info.sender();

            ArrayList<String> homeNames = homeManager.getHomes(player.getUniqueId());

            // Check if the home names list is not null and contains names
            if (homeNames == null || homeNames.isEmpty()) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Ei ole koteja."));
            } else {
                return homeNames;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            if (info == null || info.sender() == null || !(info.sender() instanceof Player player)) {
                throw new IllegalArgumentException("Invalid sender information.");
            }

            if (homeManager.getHomes(player.getUniqueId()) == null) {
                return new String[0];
            }

            return homeManager.getHomes(player.getUniqueId()).toArray(new String[0]);
        }));
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
        Song song = NBSAPI.INSTANCE.readSongFile(new File(instance.getDataFolder() + "/music/gamba.nbs"));
        SongPlayer songPlayer = new BukkitSongPlayer.Builder()
                .soundEmitter(new GlobalSoundEmitter())
                .volume(25)
                .transposeNotes(false)
                .build();
        songPlayer.playSong(song);
        songPlayer.loopQueue(false);
        songPlayer.addListener(new AudioListener(player.getEntityId(), player.getUniqueId()));
        songPlayer.play();


        taskID = Bukkit.getScheduler().runTaskTimer(instance, () -> {
            int currentGamblingValue = gambling.getOrDefault(player, 0);
            if (currentGamblingValue >= 91) {
                Bukkit.getServer().getScheduler().runTask(instance, new Runnable() {
                    @Override
                    public void run() {
                        //
                        player.showTitle(Title.title(
                                toMM("<#85FF00>" + name),
                                toMM("<gradient:#FFF226:#F91526>OIJJOI!! NYT NAUKAHTI!!</gradient:#FFF226:#F91526>"),
                                Title.Times.times(
                                        Duration.ofSeconds(0),
                                        Duration.ofSeconds(8),
                                        Duration.ofSeconds(1)
                                ))
                        );
                        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5F, 1F);
                        player.sendMessage(toMM("<white>Sinun uuden kotisi nimi on</white> <#85FF00>" + name + "</#85FF00><white>. Pääset sinne komennolla</white> <#85FF00>/home " + name + "</#85FF00><white>, tai klikkaamalla tätä viestiä.</white>").hoverEvent(HoverEvent.showText(toMM("<#85FF00>Klikkaa teleportataksesi!</#85FF00>"))).clickEvent(ClickEvent.runCommand("/home " + name)));
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
                    toMM("<#FFD700>" + randomName),
                    toMM("<#78e600>Arvotaan... naukahtaako??</#78e600>"),
                    Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(1),
                            Duration.ofSeconds(1)
                    ))
            );
        }, 0L, 2L).getTaskId();
    }

}
