package net.gahvila.selviytymisharpake.PlayerFeatures.Addons;

import de.leonhard.storage.Json;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class AddonManager {

    public int getPrice(String addon) {
        switch (addon) {
            case "craft":
                return 1000;
            case "enderchest":
                return 2750;
            case "feed":
                return 5000;
            case "shop":
                return 2500;
            default:
                return 0;
        }
    }

    public void setAddon(Player player, String addon) {
        Json homeData = new Json("addondata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        homeData.set(uuid + "." + addon, true);
    }

    public boolean getAddon(Player player, String addon) {
        Json homeData = new Json("addondata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        return homeData.getFileData().containsKey(uuid + "." + addon);
    }
}
