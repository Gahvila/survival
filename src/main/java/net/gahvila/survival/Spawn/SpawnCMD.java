package net.gahvila.survival.Spawn;

import dev.jorel.commandapi.CommandAPICommand;

public class SpawnCMD {

    public void registerCommands() {
        new CommandAPICommand("spawn")
                .executesPlayer((p, args) -> {
                    SpawnTeleport.teleportSpawn(p);
                    p.sendMessage("Teleporttasit spawnille.");
                })
            .register();
    }
}