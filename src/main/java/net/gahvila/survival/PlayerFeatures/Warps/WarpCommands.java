package net.gahvila.survival.PlayerFeatures.Warps;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.gahvila.survival.survival;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.gahvila.survival.Utils.MiniMessageUtils.toMM;

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

                            if (survival.getEconomy().getBalance(p) >= price) {
                                survival.getEconomy().withdrawPlayer(p, price);

                                warpManager.addAllowedWarps(p);
                                p.sendMessage(toMM("Sinulla on nyt <#85FF00>" + warpManager.getAllowedWarps(p) + "</#85FF00> warppia yhteensä."));
                            } else {
                                p.sendMessage(toMM("Warpin osto maksaa <#85FF00>" + price + "Ⓖ</#85FF00>, ja sinulla on vain <#85FF00>" + survival.getEconomy().getBalance(p)));
                            }
                        }))
                .executesPlayer((p, args) -> {
                    Integer price = getNextWarpCost(p);

                    p.sendMessage(toMM("Sinulla on <#85FF00>" + warpManager.getAllowedWarps(p) + "</#85FF00> warppia yhteensä. Tässä on mukana myös sinun ilmaiswarppi."));
                    p.sendMessage(toMM("Haluatko varmasti ostaa warpin? \nHinta: <#85FF00>" + price + "Ⓖ"));
                    p.sendMessage(toMM("Jokaisen warpin osto nostaa hintaa <#85FF00>10%</#85FF00>."));
                    p.sendMessage(toMM("<#85FF00><b>Hyväksy painamalla")
                            .hoverEvent(HoverEvent.showText(toMM("<#85FF00>Klikkaa ostaaksesi."))).clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/buywarp forcebuy")));
                })

                .register();
        new CommandAPICommand("delwarp")
                .withArguments(customOwnedWarpArgument("warp"))
                .executesPlayer((p, args) -> {
                    String nimi = args.getRaw("warp");
                    Optional<Warp> warp = warpManager.getWarp(nimi);
                    if (warp.isEmpty()) {
                        p.sendMessage("Warppia ei löytynyt.");
                        return;
                    }
                    if (!warp.get().getOwner().equals(p.getUniqueId())) {
                        p.sendMessage(toMM("Et omista warppia nimellä <#85FF00>" + nimi + "</#85FF00>."));
                        return;
                    }
                    warpManager.deleteWarp(warp.get());
                    p.sendMessage(toMM("Warp nimellä <#85FF00>" + nimi + "</#85FF00> poistettu."));
                })
                .register();
        new CommandAPICommand("editwarp")
                .withArguments(customOwnedWarpArgument("warp"))
                .executesPlayer((p, args) -> {
                    String nimi = args.getRaw("warp");
                    Optional<Warp> warp = warpManager.getWarp(nimi);
                    if (warp.isEmpty()) {
                        p.sendMessage("Warppia ei löytynyt.");
                        return;
                    }
                    if (!warp.get().getOwner().equals(p.getUniqueId())) {
                        p.sendMessage(toMM("Et omista warppia nimellä <#85FF00>" + nimi + "</#85FF00>."));
                        return;
                    }
                    warpMenu.showWarpEditMenu(p, warp.get());
                })
                .register();
        new CommandAPICommand("setwarp")
                .withArguments(new GreedyStringArgument("nimi"))
                .executesPlayer((p, args) -> {
                    String name = args.getRaw("nimi");

                    if (warpManager.getOwnedWarps(p.getUniqueId()).size() < warpManager.getAllowedWarps(p)) {
                        if (!warpManager.getWarpNames().contains(name)) {
                            if (p.getWorld().getName().equals("world")) {
                                if (name.matches("[\\p{L}\\p{N}]+") && name.length() <= 16) {
                                    warpManager.setWarp(p, name, p.getLocation(), 0, "white", Material.LODESTONE);
                                    p.sendMessage(toMM("Asetit warpin nimellä <#85FF00>" + name + "</#85FF00>. Voit muokata warpin nimeä, materiaalia ja hintaa komennolla <#85FF00>/editwarp " + name + "</#85FF00>.")
                                            .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(toMM("Klikkaa muokataksesi")))
                                            .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/editwarp " + name)));
                                } else {
                                    p.sendMessage("Nimi voi sisältää vain aakkosia ja numeroita, ja se voi olla maks. 16 kirjainta pitkä.");
                                }
                            }else{
                                p.sendMessage("Voit asettaa warpin vain päämaailmaan.");
                            }
                        } else {
                            p.sendMessage("Warppi tuolla nimellä on jo olemassa");
                        }
                    }else{
                        p.sendMessage(toMM("Sinulla ei ole tarpeeksi warppeja! Voit ostaa warpin komennolla <#85FF00>/buywarp</#85FF00>."));
                    }

                    if (warpManager.getAllowedWarps(p) == 0) {
                        p.sendMessage(toMM("Sinulla ei ole tarpeeksi warppeja! Voit ostaa warpin komennolla <#85FF00>/buywarp</#85FF00>."));
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

                    Optional<Warp> warp = warpManager.getWarp(nimi);
                    if (warp.isPresent()) {
                        Integer price = warp.get().getPrice();
                        if (price > 0 && price < 51) {
                            p.sendMessage("Maksullisiin warppeihin pääsee vain valikon kautta.");
                        } else {
                            if (warpManager.isLocationSafe(warp.get().getLocation())){
                                p.teleportAsync(warp.get().getLocation());
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
        int initialCost = 10000;
        double cost = initialCost * Math.pow(1 + rate, (warpManager.getAllowedWarps(p) - 1));
        return (int) cost;
    }


    public Argument<List<String>> customWarpArgument(String nodeName) {
        // Construct our CustomArgument that takes in a String input and returns a list of home names
        return new CustomArgument<List<String>, String>(new GreedyStringArgument(nodeName), info -> {

            List<String> homeNames = warpManager.getWarpNames();

            // Check if the warp names list is not null and contains names
            if (homeNames == null || homeNames.isEmpty()) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Warppeja ei ole."));
            } else {
                return homeNames;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            if (warpManager.getWarpNames() == null) {
                return new String[0];
            }

            return warpManager.getWarpNames().toArray(new String[0]);
        }));
    }

    public Argument<List<String>> customOwnedWarpArgument(String nodeName) {
        // Construct our CustomArgument that takes in a String input and returns a list of home names
        return new CustomArgument<List<String>, String>(new GreedyStringArgument(nodeName), info -> {
            // Retrieve the list of home names for the player

            Player player = (Player) info.sender();
            UUID uuid = player.getUniqueId();
            List<String> warpNames = warpManager.getOwnedWarpNames(uuid);

            // Check if the home names list is not null and contains names
            if (warpNames == null || warpNames.isEmpty()) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Ei ole warppeja."));
            } else {
                return warpNames;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            if (info == null || info.sender() == null || !(info.sender() instanceof Player player)) {
                throw new IllegalArgumentException("Invalid sender information.");
            }

            List<String> warpNames = warpManager.getOwnedWarpNames(player.getUniqueId());

            if (warpNames == null) {
                return new String[0];
            }

            return warpNames.toArray(new String[0]);
        }));
    }
}
