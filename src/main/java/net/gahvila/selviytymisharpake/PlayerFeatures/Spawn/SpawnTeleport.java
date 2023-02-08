package net.gahvila.selviytymisharpake.PlayerFeatures.Spawn;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static java.lang.Long.MAX_VALUE;

public class SpawnTeleport {

    public static void teleportSpawn(Player p){
        p.setGameMode(GameMode.SURVIVAL);
        p.setWalkSpeed(0.2F);
        p.teleportAsync(new Location(Bukkit.getWorld("spawn"), -0.5, 194, 0.5, 90.0f, 0.0f));
    }
}
