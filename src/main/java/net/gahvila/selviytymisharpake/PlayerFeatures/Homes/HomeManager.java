package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import de.leonhard.storage.Json;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class HomeManager {

    public static void saveHome(Player player, String home, Location location) {
        Json homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        homeData.getFileData().insert(uuid + "." + home + ".world", location.getWorld().getName());
        homeData.getFileData().insert(uuid + "." + home + ".x", location.getX());
        homeData.getFileData().insert(uuid + "." + home + ".y", location.getY());
        homeData.getFileData().insert(uuid + "." + home + ".z", location.getZ());
        homeData.getFileData().insert(uuid + "." + home + ".yaw", location.getYaw());
        homeData.set(uuid + "." + home + ".pitch", location.getPitch());
    }

    //
    public static void deleteHome(Player player, String home) {
        Json homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (homeData.contains(uuid + "." + home)) {
            homeData.set(uuid + "." + home, null);
        }
    }

    //
    public static Location getHome(Player player, String home) {
        Json homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (homeData.getFileData().containsKey(uuid + "." + home)) {
            World world = Bukkit.getWorld(homeData.getString(uuid + "." + home + ".world"));
            double x = homeData.getDouble(uuid + "." + home + ".x");
            double y = homeData.getDouble(uuid + "." + home + ".y");
            double z = homeData.getDouble(uuid + "." + home + ".z");
            float yaw = (float) homeData.getDouble(uuid + "." + home + ".yaw");
            float pitch = (float) homeData.getDouble(uuid + "." + home + ".pitch");
            Location location = new Location(world, x, y, z, yaw, pitch);
            return location;
        }


        return null;
    }

    //
    public static ArrayList<String> getHomes(Player player) {
        Json homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (homeData.contains(uuid)) {
            ArrayList<String> homes = new ArrayList<>();
            homeData.getFileData().singleLayerKeySet(uuid).forEach((home) -> homes.add(home));
            return homes;
        }
        return null;
    }


    public static void addAllowedHomes(Player player) {
        Json homeData = new Json("allowedhomes.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        homeData.set(uuid + ".allowed", getAllowedHomes(player) + 1);
    }

    public static Integer getAllowedHomes(Player player) {
        Json homeData = new Json("allowedhomes.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        Integer allowedHomes = homeData.getInt(uuid + ".allowed");
        return allowedHomes;
    }

    public static void setAllowedHomes(Player player) {
        Json homeData = new Json("allowedhomes.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        if (player.hasPermission("survival.homes.9")) {
            homeData.set(uuid + ".allowed", 9);
        } else if (player.hasPermission("survival.homes.5")) {
            homeData.set(uuid + ".allowed", 6);
        } else if (player.hasPermission("survival.homes.4")) {
            homeData.set(uuid + ".allowed", 4);
        } else if (player.hasPermission("survival.homes.3")) {
            homeData.set(uuid  + ".allowed", 3);
        }
    }

    public static Boolean getIfDefaulted(Player player) {
        Json homeData = new Json("allowedhomes.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        Boolean allowedHomes = homeData.getBoolean(uuid + ".default");
        return allowedHomes;
    }

    public static Boolean setIfDefaulted(Player player) {
        Json homeData = new Json("allowedhomes.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        homeData.set(uuid + ".default", true);
        return null;
    }
}