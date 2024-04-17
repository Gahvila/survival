package net.gahvila.selviytymisharpake.PlayerFeatures.Addons;

import de.leonhard.storage.Json;
import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.permissions.PermissionRoute;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import org.bukkit.Bukkit;
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
import static net.gahvila.selviytymisharpake.Utils.MiniMessageUtils.toMM;

public class AddonManager {
    private final HomeManager homeManager;
    protected CrashClaim crashClaim;

    public AddonManager(HomeManager homeManager, CrashClaim crashClaim) {
        this.homeManager = homeManager;
        this.crashClaim = crashClaim;
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

    int taskID;
    public void flyScheduler() {
        taskID = Bukkit.getScheduler().runTaskTimer(instance, () -> {
            for (Player player : Bukkit.getOnlinePlayers()){
                if (!getAddon(player, Addon.FLY)) return;
                if (!player.getAllowFlight()) return;
                if (player.getLocation().getY() < 63) {
                    player.sendMessage("Voit lentää vain vedenpinnan yläpuolella.");
                    player.sendMessage(toMM("Lentotila: <red>pois päältä"));
                    player.setAllowFlight(false);
                    return;
                }
                if (crashClaim.getApi().getClaim(player.getLocation()) == null) {
                    player.sendMessage("Et ole suojauksessa.");
                    player.sendMessage(toMM("Lentotila: <red>pois päältä"));
                    player.setAllowFlight(false);
                    return;
                }
                if (crashClaim.getApi().getPermissionHelper().hasPermission(player.getLocation(), PermissionRoute.BUILD)){
                    player.sendMessage("Sinulla ei ole tarpeeksi oikeuksia tässä suojauksessa lentääksesi. Tarvitset rakennusoikeudet.");
                    player.sendMessage(toMM("Lentotila: <red>pois päältä"));
                    player.setAllowFlight(false);
                    return;
                }
            }
        }, 0, 20).getTaskId();
    }
}
