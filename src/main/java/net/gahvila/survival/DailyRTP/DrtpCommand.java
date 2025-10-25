package net.gahvila.survival.DailyRTP;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.gahvila.survival.Features.TeleportBlocker;
import net.gahvila.survival.Messages.Message;
import net.gahvila.survival.survival;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class DrtpCommand {

    private final DrtpManager drtpManager;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public DrtpCommand(DrtpManager drtpManager) {
        this.drtpManager = drtpManager;
    }

    public void register(survival plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(createMain());
            event.registrar().register(createAlias());
        });
    }

    private LiteralCommandNode<CommandSourceStack> createMain() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("drtp")
                .requires(source -> source.getSender().hasPermission("dailyrtp.command.drtp"))
                .executes(context -> executeDrtp(context.getSource()));

        builder.then(Commands.literal("reroll")
                .requires(source -> source.getSender().hasPermission("dailyrtp.admin.reroll"))
                .executes(context -> executeReroll(context.getSource()))
        );

        return builder.build();
    }

    private LiteralCommandNode<CommandSourceStack> createAlias() {
        return Commands.literal("daily")
                .redirect(createMain())
                .build();
    }

    private int executeDrtp(CommandSourceStack source) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player player)) {
            return 1;
        }

        if (!TeleportBlocker.canTeleport(player)) {
            player.sendRichMessage(Message.TELEPORT_NOT_POSSIBLE.getText());
            return 1;
        }

        int cooldownTime = 60; // seconds

        // Cooldown Check
        if (cooldowns.containsKey(player.getUniqueId())) {
            long secondsLeft = ((cooldowns.get(player.getUniqueId()) / 1000) + cooldownTime)
                    - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                player.sendRichMessage("<red>Malta hetki! Sinun täytyy odottaa vielä " + secondsLeft + " sekuntia ennen kuin voit tehdä tuon uudelleen.");
                return 1;
            }
        }

        Location dailyLocation = drtpManager.getDailyTeleportLocation();
        if (dailyLocation == null) {
            player.sendRichMessage("<yellow>Päivän paikkaa ei ole vielä arvottu. Kärsivällisyyttä. Yritä pian uudelleen.");
            return 1;
        }

        player.teleportAsync(dailyLocation).thenAccept(success -> {
            if (success) {
                player.sendRichMessage("<white>Siirretään sinut päivän paikkaan.");
                cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
            } else {
                player.sendRichMessage("<red>Teleportti epäonnistui! Jokin taitaa olla tiellä.");
            }
        });

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    private int executeReroll(CommandSourceStack source) {
        CommandSender sender = source.getSender();

        sender.sendRichMessage("<yellow>Etsitään uutta satunnaista päivän paikkaa...</yellow>");

        try {
            drtpManager.findNewTeleportLocation();
            sender.sendRichMessage("<white>Uuden paikan haku on nyt käynnissä.</white>");
        } catch (Exception e) {
            sender.sendRichMessage("<red>Jotain meni vituilleen, ja paikan haku epäonnistui. Ota yhteyttä ylläpitoon!</red>");
            e.printStackTrace();
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}