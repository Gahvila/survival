package net.gahvila.selviytymisharpake.PlayerFeatures.Commands;

import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getWorld;

public class SpawnCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        SpawnTeleport.teleportSpawn(p);
        p.sendMessage("Teleporttasit spawnille.");
        return true;
    }


}