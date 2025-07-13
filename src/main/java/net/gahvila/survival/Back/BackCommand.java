package net.gahvila.survival.Back;

import dev.jorel.commandapi.CommandAPICommand;
import net.gahvila.survival.Features.TeleportBlocker;
import net.gahvila.survival.Messages.Message;
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
                        if (TeleportBlocker.canTeleport(p)) {
                            p.teleportAsync(previousLocation);
                        } else {
                            p.sendRichMessage(Message.TELEPORT_NOT_POSSIBLE.getText());
                        }
                    } else {
                        p.sendMessage("Sijaintia ei ole.");
                    }
                })
                .register();
    }
}
