package net.gahvila.selviytymisharpake.PlayerFeatures.Warps;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class WarpCommands {

    private final WarpManager warpManager;
    private final WarpMenu warpMenu;



    public WarpCommands(WarpManager warpManager, WarpMenu warpMenu) {
        this.warpManager = warpManager;
        this.warpMenu = warpMenu;
    }

    public void registerCommands() {
        new CommandAPICommand("buywarp")
                .withSubcommand(new CommandAPICommand("forcebuy")
                        .executesPlayer((p, args) -> {
                            Integer price = getNextWarpCost(p);

                            if (SelviytymisHarpake.getEconomy().getBalance(p) >= price) {
                                SelviytymisHarpake.getEconomy().withdrawPlayer(p, price);

                                warpManager.addAllowedWarps(p);
                                p.sendMessage("Sinulla on nyt §e" + warpManager.getAllowedWarps(p) + " §fwarppia yhteensä.");
                            } else {
                                p.sendMessage("Warpin osto maksaa §e" + price + "Ⓖ§f, ja sinulla on vain §e" + SelviytymisHarpake.getEconomy().getBalance(p));
                            }
                        }))
                .executesPlayer((p, args) -> {
                    TextComponent accept = new TextComponent();
                    accept.setText("§a§lHyväksy"); //set clickable text
                    accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa ostaaksesi.").create())); //display text msg when hovering
                    accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/buywarp forcebuy")); //runs command when they click the text

                    Integer price = getNextWarpCost(p);

                    p.sendMessage("§fSinulla on §e" + warpManager.getAllowedWarps(p) + " §fwarppia yhteensä.");
                    p.sendMessage("Haluatko varmasti ostaa warpin? \nHinta: §e" + price + "Ⓖ");
                    p.sendMessage("Jokaisen warpin osto nostaa hintaa §e10%§f.");
                    p.sendMessage(accept);
                })

                .register();
        new CommandAPICommand("delwarp")
                .withArguments(customWarpArgument("warp"))
                .executesPlayer((p, args) -> {
                    String nimi = args.getRaw("warp");
                    if (warpManager.getWarps() == null) {
                        p.sendMessage("Warppeja ei ole.");
                        return;
                    }
                    if (!warpManager.getWarpOwnerUUID(nimi).equals(p.getUniqueId().toString())) {
                        p.sendMessage("§7Et omista warppia nimellä §e" + nimi + "§f.");
                        return;
                    }
                    warpManager.deleteWarp(nimi);
                    p.sendMessage("§fWarp nimellä §e" + nimi + " §fpoistettu.");
                })
                .register();
        new CommandAPICommand("setwarp")
                .executesPlayer((p, args) -> {
                    if (warpManager.getOwnedWarps(p).size() < warpManager.getAllowedWarps(p)) {
                        if (p.getWorld().getName().equals("world")) {
                            p.sendMessage("§7Aloitit warpin asettamisen.");
                            p.sendMessage("§cKirjoita vastauksesi chattiin!");
                            p.sendMessage("");
                            p.sendMessage("§fMikäs sinun warppisi nimi on? Nimi voi sisältää vain kirjaimia ja numeroita.");
                            p.sendMessage("§7Jos haluat perua warpin luonnin, kirjoita '§elopeta§7' chattiin.");
                            warpManager.settingWarp.put(p.getUniqueId(), 1);
                        }else{
                            p.sendMessage("Voit asettaa warpin vain päämaailmaan.");
                        }
                    }else{
                        p.sendMessage("Sinulla ei ole tarpeeksi warppeja! Voit ostaa warpin komennolla §e/buywarp");
                    }

                    if (warpManager.getAllowedWarps(p) == 0) {
                        p.sendMessage("Sinulla ei ole tarpeeksi warppeja! Voit ostaa warpin komennolla §e/buywarp");
                    }
                })
                .register();

        new CommandAPICommand("warp")
                .withOptionalArguments(customWarpArgument("warp"))
                .executesPlayer((p, args) -> {
                    String nimi = args.getRaw("warp");
                    if (args.getRaw("warp") == null) {
                        if (warpManager.getWarps() != null) {
                            warpMenu.showGUI(p);
                            return;
                        }
                        return;
                    }
                    if (warpManager.getWarps().contains(nimi)) {
                        Integer price = warpManager.getWarpPrice(nimi);
                        if (price > 0 && price < 51) {
                            p.sendMessage("Maksullisiin warppeihin pääsee vain valikon kautta.");
                        } else {
                            if (warpManager.isLocationSafe(warpManager.getWarp(nimi))){
                                p.teleportAsync(warpManager.getWarp(nimi));
                                p.setWalkSpeed(0.2F);
                            }else{
                                p.sendMessage("Warpin sijainti ei ole turvallinen. Teleportti peruttu.");
                            }
                        }

                    } else {
                        warpMenu.showGUI(p);
                    }
                })
                .register();

    }

    public int getNextWarpCost(Player p) {
        double rate = 0.10;
        int initialCost = 1000;
        double cost = initialCost * Math.pow(1 + rate, warpManager.getAllowedWarps(p));
        return (int) cost;
    }

    public Argument<ArrayList<String>> customWarpArgument(String nodeName) {
        // Construct our CustomArgument that takes in a String input and returns a list of home names
        return new CustomArgument<ArrayList<String>, String>(new GreedyStringArgument(nodeName), info -> {

            ArrayList<String> homeNames = warpManager.getWarps();

            // Check if the warp names list is not null and contains names
            if (homeNames == null || homeNames.isEmpty()) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Warppeja ei ole."));
            } else {
                return homeNames;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            if (warpManager.getWarps() == null) {
                return new String[0];
            }

            return warpManager.getWarps().toArray(new String[0]);
        }));
    }
}
