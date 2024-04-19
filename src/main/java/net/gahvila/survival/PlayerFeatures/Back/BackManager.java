package net.gahvila.survival.PlayerFeatures.Back;

import net.gahvila.survival.survival;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BackManager {

    //death
    public static HashMap<Player, Location> deathLocation = new HashMap<>();
    public static HashMap<Player, Integer> deathPrice = new HashMap<>();

    //back
    public static HashMap<Player, Location> backLocation = new HashMap<>();

    public void setDeath(Player player, Location location) {
        deathLocation.put(player, location);
        double playerBalance = survival.getEconomy().getBalance(player);
        double percentage;
        if (playerBalance >= 25000) {
            percentage = 0.03;
        } else if (playerBalance >= 5000) {
            percentage = 0.04;
        } else {
            percentage = 0.05;
        }
        deathPrice.put(player, (int) Math.round(survival.getEconomy().getBalance(player) * percentage));

    }

    public Location getDeath(Player player) {
        return deathLocation.get(player);
    }
    public Integer getDeathPrice(Player player) {
        return deathPrice.get(player);
    }

    public void setBack(Player player, Location location) {
        backLocation.put(player, location);
    }
    public Location getBack(Player player) {
        return backLocation.get(player);
    }

    public void clearData(Player player) {
        deathLocation.remove(player);
        deathPrice.remove(player);
        backLocation.remove(player);
    }
}


