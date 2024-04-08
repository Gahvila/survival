package net.gahvila.selviytymisharpake.PlayerFeatures.Warps;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                    Optional<Warp> warp = warpManager.getWarp(nimi);
                    if (warp.isEmpty()) {
                        p.sendMessage("Warppia ei löytynyt.");
                        return;
                    }
                    if (!warp.get().getOwner().equals(p.getUniqueId())) {
                        p.sendMessage("§7Et omista warppia nimellä §e" + nimi + "§f.");
                        return;
                    }
                    warpManager.deleteWarp(warp.get());
                    p.sendMessage("§fWarp nimellä §e" + nimi + " §fpoistettu.");
                })
                .register();
        new CommandAPICommand("editwarp")
                .withArguments(new CommandArgument("komento"))
                .withArguments(new ItemStackArgument("item")
                .executesPlayer((p, args) -> {
                    String name = args.getRaw("nimi");
                    Material customItem = args.get("item") == null ?
                            Material.DIRT : ((ItemStack) args.get("item")).getType();
                    Optional<Warp> warp = warpManager.getWarp(name);
                    if (warp.isEmpty()) {
                        p.sendMessage("Warppia ei löytynyt.");
                        return;
                    }
                    if (!warp.get().getOwner().equals(p.getUniqueId())) {
                        p.sendMessage("§7Et omista warppia nimellä §e" + name + "§f.");
                        return;
                    }
                    warpManager.editWarpItem(warp.get(), customItem);
                    p.sendMessage("§fWarpin §e" + name + " §fitemiä muokattu: " + customItem.name());
                }))

                .register();
        new CommandAPICommand("setwarp")
                .withArguments(new StringArgument("nimi"))
                .withArguments(new IntegerArgument("hinta", 0, 100))
                .withOptionalArguments(new ItemStackArgument("item"))
                .executesPlayer((p, args) -> {
                    String name = args.getRaw("nimi");
                    Integer price = (Integer) args.get("hinta");

                    if (warpManager.getOwnedWarps(p).size() < warpManager.getAllowedWarps(p)) {
                        if (!warpManager.getWarpNames().contains(name)) {
                            if (p.getWorld().getName().equals("world")) {
                                warpManager.setWarp(p, name, p.getLocation(), price, args.get("item") == null ?
                                        Material.DIRT : ((ItemStack) args.get("item")).getType());
                                p.sendMessage(toMiniMessage("Asetit warpin " + name + " hintaan " + price + "."));
                            }else{
                                p.sendMessage("Voit asettaa warpin vain päämaailmaan.");
                            }
                        } else {
                            p.sendMessage("Warppi tuolla nimellä on jo olemassa");
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
        int initialCost = 1000;
        double cost = initialCost * Math.pow(1 + rate, warpManager.getAllowedWarps(p));
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

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }
}
