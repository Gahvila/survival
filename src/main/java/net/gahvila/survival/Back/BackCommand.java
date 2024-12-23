package net.gahvila.survival.Back;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Location;

public class BackCommand {

    private final BackManager backManager;


    public BackCommand(BackManager backManager) {
        this.backManager = backManager;
    }

    public void registerCommands() {
        new CommandAPICommand("back")
                .executesPlayer((p, args) -> {
                    Location previousLocation = backManager.getBack(p);

                    if (previousLocation != null) {
                        p.teleportAsync(previousLocation);
                    } else {
                        p.sendMessage("Sijaintia ei ole.");
                    }
                })
                .register();
    }
}
