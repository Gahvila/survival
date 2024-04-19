package net.gahvila.survival.PluginCommands;

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
                .withSubcommand(new CommandAPICommand("resetnether")
                        .executes((sender, args) -> {
                            sender.sendMessage("nether reset alotettu toivottavasti");
                            instance.getResurssinetherReset().performNetherReset();
                        }))
                .register();

    }
}
