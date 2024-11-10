package net.gahvila.survival.Back;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BackManager {
    //back
    public static HashMap<Player, Location> backLocation = new HashMap<>();

    public void setBack(Player player, Location location) {
        backLocation.put(player, location);
    }
    public Location getBack(Player player) {
        return backLocation.get(player);
    }
}


