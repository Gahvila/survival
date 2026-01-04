package net.gahvila.survival.Warps;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Single;
import net.gahvila.survival.Features.TeleportBlocker;
import net.gahvila.survival.Messages.Message;
import net.gahvila.survival.Warps.WarpApplications.WarpApplication;
import net.gahvila.survival.Warps.WarpApplications.WarpApplicationManager;
import net.gahvila.survival.survival;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Float.MAX_VALUE;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class WarpCommands {

    private final WarpManager warpManager;
    private final WarpMenu warpMenu;
    private final WarpApplication warpApplication;
    private final WarpApplicationManager warpApplicationManager;

    public WarpCommands(WarpManager warpManager, WarpMenu warpMenu, WarpApplication warpApplication, WarpApplicationManager warpApplicationManager) {
        this.warpManager = warpManager;
        this.warpMenu = warpMenu;
        this.warpApplication = warpApplication;
        this.warpApplicationManager = warpApplicationManager;
    }

    public void registerCommands() {
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
                .executesPlayer((p, args) -> {
                    warpApplication.show(p);
                })
                .register();

        new CommandAPICommand("warp")
                .withOptionalArguments(customWarpArgument("warp"))
                .executesPlayer((p, args) -> {
                    String nimi = args.getRaw("warp");
                    if (args.getRaw("warp") == null) {
                        if (warpManager.getWarps(Optional.of(p)) != null) {
                            warpMenu.showGUI(p);
                            return;
                        }
                        return;
                    }

                    Optional<Warp> warp = warpManager.getWarp(nimi);
                    if (warp.isPresent()) {
                        if (warpManager.isLocationSafe(warp.get().getLocation())){
                            if (TeleportBlocker.canTeleport(p)) {
                                p.teleportAsync(warp.get().getLocation());
                                p.sendMessage(toMM("Sinut teleportattiin warppiin <#85FF00>" + warp.get().getName() + "</#85FF00>."));
                                Bukkit.getServer().getScheduler().runTaskLater(survival.instance, new Runnable() {
                                    @Override
                                    public void run() {
                                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, MAX_VALUE, 1F);
                                    }
                                }, 5);
                            } else {
                                p.sendRichMessage(Message.TELEPORT_NOT_POSSIBLE.getText());
                            }
                        }else{
                            p.sendMessage("Warpin sijainti ei ole turvallinen. Teleportti peruttu.");
                        }

                    } else {
                        warpMenu.showGUI(p);
                    }
                })
                .register();
        new CommandAPICommand("adminwarp")
                .withPermission("survival.warp.admin")
                .withSubcommand(new CommandAPICommand("editwarp")
                        .withArguments(customWarpArgument("warp"))
                        .executesPlayer((player, args) -> {
                            String name = args.getRaw("warp");
                            Optional<Warp> warp = warpManager.getWarp(name);
                            if (warp.isPresent()) {
                                warpMenu.showWarpEditMenu(player, warp.get());
                            } else {
                                player.sendMessage("Tapahtui virhe.");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("delete")
                        .withArguments(customWarpArgument("warp"))
                        .executesPlayer((player, args) -> {
                            String name = args.getRaw("warp");
                            Optional<Warp> warp = warpManager.getWarp(name);
                            if (warp.isPresent()) {
                                warpManager.deleteWarp(warp.get());
                                player.sendMessage("warp nimellä '" + name + "' poistettu");

                            } else {
                                player.sendMessage("Tapahtui virhe.");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("create")
                        .withArguments(new TextArgument("name"))
                        .withArguments(new LocationArgument("location"))
                        .withArguments(new FloatArgument("yaw"))
                        .withArguments(new FloatArgument("pitch"))

                        .executesPlayer((player, args) -> {
                            String name = (String) args.get("name");
                            Location location;
                            if (args.get("location") == null) {
                                location = player.getLocation();
                            } else if (args.getRaw("location") != null) {
                                location = (Location) args.get("location");
                                location.setYaw((Float) args.get("yaw"));
                                location.setPitch((Float) args.get("pitch"));
                            } else {
                                return;
                            }
                            UUID adminWarpUUID = UUID.nameUUIDFromBytes("OfflinePlayer:#AdminWarp".getBytes(StandardCharsets.UTF_8));
                            warpManager.setWarp(adminWarpUUID, "#AdminWarp", name, location, Single.VALKOINEN, Material.LODESTONE);
                            player.sendMessage("warp nimellä '" + name + "' asetettu");
                        }))
                .withSubcommand(new CommandAPICommand("review")
                        .withArguments(new UUIDArgument("application"))
                        .withOptionalArguments(new TextArgument("action")
                                .replaceSuggestions(ArgumentSuggestions.strings("true", "false", "teleport")))
                        .withOptionalArguments(new GreedyStringArgument("reason"))
                        .executes((sender, args) -> {
                            UUID application = (UUID) args.get("application");

                            if (!warpApplicationManager.getApplications().contains(application)) {
                                sender.sendMessage("Tuota hakemusta ei löytynyt, tai se on jo käsitelty.");
                                return;
                            }

                            String action = (String) args.get("action");

                            if (action == null) {
                                sender.sendRichMessage("Vaihtoehtoja:");
                                if (sender instanceof Player) {
                                    String tpArgCommand = "/adminwarp review " + application + " teleport";
                                    sender.sendRichMessage("<click:run_command:'" + tpArgCommand + "'><white><u>Teleporttaa warpin sijaintiin");
                                }
                                String acceptCommand = "/adminwarp review " + application + " true ";
                                String denyCommand = "/adminwarp review " + application + " false ";
                                sender.sendRichMessage("<click:suggest_command:'" + acceptCommand + "'><white><u>Hyväksy hakemus (kirjoita perään syy)");
                                sender.sendRichMessage("");
                                sender.sendRichMessage("<click:suggest_command:'" + denyCommand + "'><white><u>Hylkää hakemus (kirjoita perään syy)");
                                return;
                            }

                            if (action.equalsIgnoreCase("teleport")) {
                                if (sender instanceof Player player) {
                                    Location location = warpApplicationManager.getApplicationLocation(application);
                                    if (location != null) {
                                        player.teleportAsync(location);
                                        sender.sendMessage("Teleportattiin hakemuksen sijaintiin.");
                                    } else {
                                        sender.sendMessage("Hakemuksen sijaintia ei löytynyt.");
                                    }
                                } else {
                                    sender.sendMessage("Vain pelaajat voivat teleportata.");
                                }
                                return;
                            }

                            boolean accepted = Boolean.parseBoolean(action);
                            String reason = (String) args.get("reason");

                            if (reason == null) reason = "";

                            if (accepted) {
                                sender.sendMessage("Hyväksyit hakemuksen " + application + " (Syy: " + reason + ")");
                                warpApplicationManager.acceptApplication(application, reason);
                            } else {
                                sender.sendMessage("Hylkäsit hakemuksen " + application + " (Syy: " + reason + ")");
                                warpApplicationManager.denyApplication(application, reason);
                            }
                        }))
                .register();
    }

    public Argument<List<String>> customWarpArgument(String nodeName) {
        return new CustomArgument<List<String>, String>(new GreedyStringArgument(nodeName), info -> {
            List<String> homeNames = warpManager.getWarpNames();
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
        return new CustomArgument<List<String>, String>(new GreedyStringArgument(nodeName), info -> {
            Player player = (Player) info.sender();
            UUID uuid = player.getUniqueId();
            List<String> warpNames = warpManager.getOwnedWarpNames(uuid);

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