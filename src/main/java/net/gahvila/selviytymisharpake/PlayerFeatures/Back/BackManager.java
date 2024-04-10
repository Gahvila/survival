package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import de.leonhard.storage.Json;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class BackManager {

    //death
    public static HashMap<Player, Location> deathLocation = new HashMap<>();
    public static HashMap<Player, Integer> deathPrice = new HashMap<>();

    //back
    public static HashMap<Player, Location> backLocation = new HashMap<>();

    public void setDeath(Player player, Location location) {
        deathLocation.put(player, location);
        double playerBalance = SelviytymisHarpake.getEconomy().getBalance(player);
        double percentage;
        if (playerBalance >= 50000) {
            percentage = 0.03; // 3% for balances above or equal to 1000
        } else if (playerBalance >= 1000) {
            percentage = 0.04; // 4% for balances above or equal to 500
        } else {
            percentage = 0.05; // 5% for balances below 500
        }
        deathPrice.put(player, (int) Math.round(SelviytymisHarpake.getEconomy().getBalance(player) * percentage)); //5% of player balance

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


