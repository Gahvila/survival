package net.gahvila.survival;

import dev.jorel.commandapi.CommandAPICommand;

import static net.gahvila.survival.survival.instance;

public class MainCommand {

    public void registerCommands() {
        new CommandAPICommand("gahvilasurvival")
                .withPermission("sh.admin")
                .withSubcommand(new CommandAPICommand("reload")
                        .executes((sender, args) -> {
                            instance.reloadConfig();
                            sender.sendMessage("ladattu uusiks toivotaan että servu ei lahonnut");
                        }))
                .register();

    }
}
