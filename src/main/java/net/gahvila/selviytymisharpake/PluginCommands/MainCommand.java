package net.gahvila.selviytymisharpake.PluginCommands;

import dev.jorel.commandapi.CommandAPICommand;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class MainCommand {

    public void registerCommands() {
        new CommandAPICommand("selviytymishärpäke")
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
