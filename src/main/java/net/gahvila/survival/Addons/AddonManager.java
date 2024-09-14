package net.gahvila.survival.Addons;

import de.leonhard.storage.Json;
import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.permissions.PermissionRoute;
import net.gahvila.survival.Homes.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.gahvila.survival.survival.instance;
import static net.gahvila.survival.Utils.MiniMessageUtils.toMM;

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

    public void flyScheduler(Player player) {
        Bukkit.getScheduler().runTaskTimer(instance, task -> {
            if (player.hasPermission("survival.fly.bypass")){
                task.cancel();
                return;
            }
            if (!getAddon(player, Addon.FLY)) {
                task.cancel();
                return;
            }
            if (!player.getAllowFlight()) {
                task.cancel();
                return;
            }
            if (crashClaim.getApi().getClaim(player.getLocation()) == null) {
                player.sendMessage("Et ole suojauksessa.");
                player.sendMessage(toMM("Lentotila: <red>pois päältä"));
                player.setAllowFlight(false);
                task.cancel();
                return;
            }
            if (!crashClaim.getApi().getPermissionHelper().hasPermission(player.getUniqueId(), player.getLocation(), PermissionRoute.BUILD)) {
                player.sendMessage("Sinulla ei ole tarpeeksi oikeuksia tässä suojauksessa lentääksesi. Tarvitset rakennusoikeudet.");
                player.sendMessage(toMM("Lentotila: <red>pois päältä"));
                player.setAllowFlight(false);
                task.cancel();
                return;
            }
            if (player.getLocation().getY() < 63) {
                player.sendMessage("Voit lentää vain vedenpinnan yläpuolella.");
                player.sendMessage(toMM("Lentotila: <red>pois päältä"));
                player.setAllowFlight(false);
                task.cancel();
                return;
            }
        }, 0, 20);
    }
}
