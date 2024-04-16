package net.gahvila.selviytymisharpake.PlayerFeatures.Addons;

import de.leonhard.storage.Json;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class AddonManager {
    private final HomeManager homeManager;

    public AddonManager(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    public int getPrice(Addon addon, Player player) {
        switch (addon) {
            case CRAFT:
                return 1000;
            case ENDERCHEST:
                return 2750;
            case FEED:
                return 5000;
            case SHOP:
                return 2500;
            case HOME:
                return homeManager.getNextHomeCost(player);
            case FLY:
                return 50000;
            default:
                return 0;
        }
    }

    public void setAddon(Player player, Addon addon) {
        Json homeData = new Json("addondata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        homeData.set(uuid + "." + addon, true);
    }

    public boolean getAddon(Player player, Addon addon) {
        Json homeData = new Json("addondata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        return homeData.getFileData().containsKey(uuid + "." + addon);
    }
}
