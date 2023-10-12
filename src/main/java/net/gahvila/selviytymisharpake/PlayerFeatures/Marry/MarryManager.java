package net.gahvila.selviytymisharpake.PlayerFeatures.Marry;

import de.leonhard.storage.Json;
import de.leonhard.storage.Toml;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class MarryManager {

    public static void saveMarriage(Player player, String warp, Location location, Integer price) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        warpData.getFileData().insert(warp + ".owner", uuid);
        warpData.getFileData().insert(warp + ".currentOwnerName", player.getName());
        warpData.getFileData().insert(warp + ".spouse", uuid);
        warpData.getFileData().insert(warp + ".currentSpouseName", player.getName());
        warpData.getFileData().insert(warp + ".price", price);
        warpData.getFileData().insert(warp + ".uses", 0);
        warpData.getFileData().insert(warp + ".creationdate", System.currentTimeMillis());
        warpData.getFileData().insert(warp + ".world", location.getWorld().getName());
        warpData.getFileData().insert(warp + ".x", location.getX());
        warpData.getFileData().insert(warp + ".y", location.getY());
        warpData.getFileData().insert(warp + ".z", location.getZ());
        warpData.getFileData().insert(warp + ".yaw", location.getYaw());
        warpData.set(warp + ".pitch", location.getPitch());
    }
}
