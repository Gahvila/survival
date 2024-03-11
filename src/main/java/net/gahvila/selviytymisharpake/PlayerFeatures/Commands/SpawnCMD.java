package net.gahvila.selviytymisharpake.PlayerFeatures.Commands;

import dev.jorel.commandapi.CommandAPICommand;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getWorld;

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