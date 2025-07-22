package net.gahvila.survival.Warps;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Single;
import net.gahvila.survival.Features.TeleportBlocker;
import net.gahvila.survival.Messages.Message;
import net.gahvila.survival.Warps.WarpApplications.WarpApplication;
import net.gahvila.survival.Warps.WarpApplications.WarpApplicationManager;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                                p.setWalkSpeed(0.2F);
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
                .withSubcommand(new CommandAPICommand("review")
                        .withArguments(new UUIDArgument("application"))
                        .withOptionalArguments(new BooleanArgument("accepted"))
                        .executes((sender, args) -> {
                            UUID application = UUID.fromString(args.getRaw("application"));
                            if (!warpApplicationManager.getApplications().contains(application)) {
                                sender.sendMessage("Tuota hakemusta ei löytynyt. Onko se jo käsitelty?");
                                return;
                            }
                            boolean accepted = Boolean.parseBoolean(args.getRaw("accepted"));
                            if (args.getRaw("accepted") == null) {
                                sender.sendRichMessage("Vaihtoehtoja:");
                                if (sender instanceof Player) {
                                    Location location = warpApplicationManager.getApplicationLocation(application);
                                    String teleportCommand = "/tp " + sender.getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + location.getYaw() + " " + location.getPitch();
                                    sender.sendRichMessage("<click:run_command:'" + teleportCommand + "'><white><u>Teleporttaa warpin sijaintiin");
                                    sender.sendRichMessage("");
                                }
                                String acceptCommand = "/adminwarp review " + application + " true";
                                String denyCommand = "/adminwarp review " + application + " false";
                                sender.sendRichMessage("<click:run_command:'" + acceptCommand + "'><white><u>Hyväksy hakemus (lisää komennon perään true)");
                                sender.sendRichMessage("");
                                sender.sendRichMessage("<click:run_command:'" + denyCommand + "'><white><u>Hylkää hakemus (lisää komennon perään false)");
                                return;
                            }

                            if (accepted) {
                                sender.sendMessage("Hyväksyit hakemuksen " + application);
                               warpApplicationManager.acceptApplication(application);
                            } else {
                                sender.sendMessage("Hylkäsit hakemuksen " + application);
                                warpApplicationManager.denyApplication(application);
                            }
                        }))
                .register();


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
