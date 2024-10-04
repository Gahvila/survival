package net.gahvila.survival;

import dev.jorel.commandapi.CommandAPICommand;
import net.gahvila.gahvilacore.Teleport.TeleportManager;

import static net.gahvila.survival.survival.instance;

public class MainCommand {

    private final TeleportManager teleportManager;

    public MainCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    public void registerCommands() {
        new CommandAPICommand("adminsurvival")
                .withPermission("sh.admin")
                .withSubcommand(new CommandAPICommand("reload")
                        .executes((sender, args) -> {
                            instance.reloadConfig();
                            sender.sendMessage("ladattu uusiks toivotaan että servu ei lahonnut");
                        }))
                .withSubcommand(new CommandAPICommand("setspawn")
                        .executesPlayer((player, args) -> {
                            teleportManager.saveTeleport("spawn", player.getLocation());
                            player.sendMessage("Asetit spawnin uuden sijainnin.");
                        }))
                .register();

    }
}
