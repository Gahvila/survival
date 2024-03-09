package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import de.leonhard.storage.Json;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class BackManager {

    public static void saveDeath(Player player, Location location, String damageCause, Double balance, Boolean hasDiamond, Boolean hasElytra, Boolean hasNetherite, Boolean hasIron) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        backData.getFileData().insert(uuid + "." + "death" + ".cause", damageCause);
        backData.getFileData().insert(uuid + "." + "death" + ".hasDiamond", hasDiamond);
        backData.getFileData().insert(uuid + "." + "death" + ".hasElytra", hasElytra);
        backData.getFileData().insert(uuid + "." + "death" + ".hasNetherite", hasNetherite);
        backData.getFileData().insert(uuid + "." + "death" + ".hasIron", hasIron);
        backData.getFileData().insert(uuid + "." + "death" + ".balance", balance);
        backData.getFileData().insert(uuid + "." + "death" + ".world", location.getWorld().getName());
        backData.getFileData().insert(uuid + "." + "death" + ".x", location.getX());
        backData.getFileData().insert(uuid + "." + "death" + ".y", location.getY());
        backData.getFileData().insert(uuid + "." + "death" + ".z", location.getZ());
        backData.getFileData().insert(uuid + "." + "death" + ".yaw", location.getYaw());
        backData.set(uuid + "." + "death" + ".pitch", location.getPitch());
    }

    public static void saveBackLocation(Player player, Location location) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();

        // Shift existing positions up by 1
        int positionLimit = 4;

        // Shift existing positions up by 1
        for (int i = positionLimit; i >= 2; i--) {
            if (backData.getFileData().containsKey(uuid + "." + (i - 1))) {
                // Move the existing position to the next one
                backData.getFileData().insert(uuid + "." + i, backData.getFileData().get(uuid + "." + (i - 1)));
            } else {
                // If there's no location at the previous position, remove the current position
                backData.getFileData().remove(uuid + "." + i);
            }
        }

        // Save the new location at position 1
        backData.getFileData().insert(uuid + "." + 1 + ".world", location.getWorld().getName());
        backData.getFileData().insert(uuid + "." + 1 + ".x", location.getX());
        backData.getFileData().insert(uuid + "." + 1 + ".y", location.getY());
        backData.getFileData().insert(uuid + "." + 1 + ".z", location.getZ());
        backData.getFileData().insert(uuid + "." + 1 + ".yaw", location.getYaw());
        backData.set(uuid + "." + 1 + ".pitch", location.getPitch());
    }

    public static Location getBack(Player player, Integer position) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + position)) {
            World world = Bukkit.getWorld(backData.getString(uuid + "." + position + ".world"));
            double x = backData.getDouble(uuid + "." + position + ".x");
            double y = backData.getDouble(uuid + "." + position + ".y");
            double z = backData.getDouble(uuid + "." + position + ".z");
            float yaw = (float) backData.getDouble(uuid + "." + position + ".yaw");
            float pitch = (float) backData.getDouble(uuid + "." + position + ".pitch");
            Location location = new Location(world, x, y, z, yaw, pitch);
            return location;
        }


        return null;
    }

    public static Location getDeath(Player player) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + "death")) {
            World world = Bukkit.getWorld(backData.getString(uuid + "." + "death" + ".world"));
            double x = backData.getDouble(uuid + "." + "death" + ".x");
            double y = backData.getDouble(uuid + "." + "death" + ".y");
            double z = backData.getDouble(uuid + "." + "death" + ".z");
            float yaw = (float) backData.getDouble(uuid + "." + "death" + ".yaw");
            float pitch = (float) backData.getDouble(uuid + "." + "death" + ".pitch");
            Location location = new Location(world, x, y, z, yaw, pitch);
            return location;
        }


        return null;
    }

    public static Double calculateDeathPrice(Player player) {
        double price = 0.0;
        boolean isRich = false;

        final double DEFAULT_PRICE = 50.0;
        final double VALUABLES_PRICE = 100.0;
        final double DEATH_BALANCE_MULTIPLIER = 0.1;

        if (hasIron(player)){
            if (getDeathBalance(player) <= 50.0) {
                price = price + getDeathBalance(player) * DEATH_BALANCE_MULTIPLIER;
            }else{
                price = price + DEFAULT_PRICE;
            }
        }
        if (hasDiamond(player)){
            isRich = true;
            price = price + VALUABLES_PRICE;
        }
        if (hasElytra(player)){
            isRich = true;
            price = price + VALUABLES_PRICE;
        }
        if (hasNetherite(player)){
            isRich = true;
            price = price + VALUABLES_PRICE;
        }

        if (isRich) {
            price = price + getDeathBalance(player) * DEATH_BALANCE_MULTIPLIER;
        }

        switch (getDeathCause(player)) {
            case "LAVA":
                price = DEFAULT_PRICE;
                break;
            //add more in the future?
            default:
                break;
        }
        return (double) Math.round(price / 3);
    }

    public static Double getXdeath(Player player) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + "death")) {
            Double x = backData.getDouble(uuid + "." + "death" + ".x");
            double roundOff = Math.round(x * 100.0) / 100.0;
            return roundOff;
        }
        return null;
    }

    public static Double getZdeath(Player player) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + "death")) {
            Double z = backData.getDouble(uuid + "." + "death" + ".z");
            double roundOff = Math.round(z * 100.0) / 100.0;
            return roundOff;
        }
        return null;
    }

    public static Double getX(Player player, Integer position) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + position)) {
            Double x = backData.getDouble(uuid + "." + position + ".x");
            double roundOff = Math.round(x * 100.0) / 100.0;
            return roundOff;
        }
        return null;
    }

    public static Double getZ(Player player, Integer position) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + position)) {
            Double z = backData.getDouble(uuid + "." + position + ".z");
            double roundOff = Math.round(z * 100.0) / 100.0;
            return roundOff;
        }
        return null;
    }


    public static Double getDeathBalance(Player player) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + "death")) {
            return backData.getDouble(uuid + "." + "death" + ".balance");
        }
        return null;
    }

    public static String getDeathCause(Player player) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + "death" + ".cause")) {
            return backData.getString(uuid + "." + "death" + ".cause");
        }
        return null;
    }

    public static Boolean hasDiamond(Player player) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + "death" + ".hasDiamond")) {
            return backData.getBoolean(uuid + "." + "death" + ".hasDiamond");
        }
        return false;
    }
    public static Boolean hasElytra(Player player) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + "death" + ".hasElytra")) {
            return backData.getBoolean(uuid + "." + "death" + ".hasElytra");
        }
        return false;
    }
    public static Boolean hasNetherite(Player player) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + "death" + ".hasNetherite")) {
            return backData.getBoolean(uuid + "." + "death" + ".hasNetherite");
        }
        return false;
    }
    public static Boolean hasIron(Player player) {
        Json backData = new Json("backdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (backData.getFileData().containsKey(uuid + "." + "death" + ".hasIron")) {
            return backData.getBoolean(uuid + "." + "death" + ".hasIron");
        }
        return false;
    }
}


