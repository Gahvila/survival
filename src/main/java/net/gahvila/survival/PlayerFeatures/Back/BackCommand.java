package net.gahvila.survival.PlayerFeatures.Back;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Location;

public class BackCommand {

    private final BackManager backManager;
    private final BackMenu backMenu;


    public BackCommand(BackManager backManager, BackMenu backMenu) {
        this.backManager = backManager;
        this.backMenu = backMenu;
    }

    public void registerCommands() {
        new CommandAPICommand("back")
                .executesPlayer((p, args) -> {
                    backMenu.showGUI(p);
                })
                .register();
        new CommandAPICommand("fback")
                .withAliases("fb")
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
