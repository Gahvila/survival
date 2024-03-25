package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import dev.jorel.commandapi.CommandAPICommand;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Location;

public class BackCommand {

    private final BackManager backManager;


    public BackCommand(BackManager backManager) {
        this.backManager = backManager;
    }

    public void registerCommands() {
        new CommandAPICommand("back")
                .executesPlayer((p, args) -> {
                    new BackMenu(SelviytymisHarpake.getPlayerMenuUtility(p), backManager).open();
                })
                .register();
        new CommandAPICommand("fback")
                .withAliases("fb")
                .executesPlayer((p, args) -> {
                    Location previousLocation = backManager.getBack(p, 1);

                    if (previousLocation != null) {
                        p.teleportAsync(previousLocation);
                    } else {
                        p.sendMessage("Sijaintia ei ole.");
                    }
                })
                .register();
    }
}
