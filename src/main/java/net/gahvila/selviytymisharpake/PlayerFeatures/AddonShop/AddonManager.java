package net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class AddonManager {



    //ENDERCHEST
    public Integer setShop(Player player) {
        File addons = new File(instance.getDataFolder(), "addons.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(addons);
        try {
            String uuid = player.getUniqueId().toString();
            f.set(uuid + "." + ".shop", true);
            f.save(addons);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Boolean getShop(Player player) {
        File addons = new File(instance.getDataFolder(), "addons.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(addons);
        String uuid = player.getUniqueId().toString();
        Boolean enderchestAllowed = f.getBoolean(uuid + ".shop");
        return enderchestAllowed;
    }


    //ENDERCHEST
    public Integer setEnderchest(Player player) {
        File addons = new File(instance.getDataFolder(), "addons.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(addons);
        try {
            String uuid = player.getUniqueId().toString();
            f.set(uuid + "." + ".enderchest", true);
            f.save(addons);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Boolean getEnderchest(Player player) {
        File addons = new File(instance.getDataFolder(), "addons.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(addons);
        String uuid = player.getUniqueId().toString();
        Boolean enderchestAllowed = f.getBoolean(uuid + ".enderchest");
        return enderchestAllowed;
    }

    //CRAFT
    public Integer setCraft(Player player) {
        File addons = new File(instance.getDataFolder(), "addons.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(addons);
        try {
            String uuid = player.getUniqueId().toString();
            f.set(uuid + "." + ".craft", true);
            f.save(addons);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Boolean getCraft(Player player) {
        File addons = new File(instance.getDataFolder(), "addons.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(addons);
        String uuid = player.getUniqueId().toString();
        Boolean craftAllowed = f.getBoolean(uuid + ".craft");
        return craftAllowed;
    }

    //FEED
    //CRAFT
    public Integer setFeed(Player player) {
        File addons = new File(instance.getDataFolder(), "addons.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(addons);
        try {
            String uuid = player.getUniqueId().toString();
            f.set(uuid + "." + ".feed", true);
            f.save(addons);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Boolean getFeed(Player player) {
        File addons = new File(instance.getDataFolder(), "addons.yml");
        FileConfiguration f = YamlConfiguration.loadConfiguration(addons);
        String uuid = player.getUniqueId().toString();
        Boolean feedAllowed = f.getBoolean(uuid + ".feed");
        return feedAllowed;
    }
}
