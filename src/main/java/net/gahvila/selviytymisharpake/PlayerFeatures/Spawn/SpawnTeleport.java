package net.gahvila.selviytymisharpake.PlayerFeatures.Spawn;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Random;

import static java.lang.Long.MAX_VALUE;

public class SpawnTeleport {

    public static void teleportSpawn(Player p) {
        p.setWalkSpeed(0.2F);
        p.teleportAsync(new Location(Bukkit.getWorld("spawn"), 20.5, 81, -40.5, 180.0f, 0.0f));
    }
}