package net.gahvila.selviytymisharpake.PlayerWarps;

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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class WarpManager {

    public static HashMap<UUID, Integer> editingWarp = new HashMap<UUID, Integer>();
    public static HashMap<UUID, String> editingWarpName = new HashMap<UUID, String>();
    //
    public static HashMap<UUID, Integer> settingWarp = new HashMap<UUID, Integer>();
    public static HashMap<UUID, String> settingWarpName = new HashMap<UUID, String>();
    public static HashMap<UUID, Integer> settingWarpPrice = new HashMap<UUID, Integer>();

    //
    public static String updateWarpPrice(Player player, String name, Integer price) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        warpData.set(name + ".price", price);
        return null;
    }

    //
    public static String updateWarpLocation(Player player, String warp, Location location) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        warpData.getFileData().insert(warp + ".world", location.getWorld().getName());
        warpData.getFileData().insert(warp + ".x", location.getX());
        warpData.getFileData().insert(warp + ".y", location.getY());
        warpData.getFileData().insert(warp + ".z", location.getZ());
        warpData.getFileData().insert(warp + ".yaw", location.getYaw());
        warpData.getFileData().insert(warp + ".pitch", location.getPitch());
        return null;
    }

    public static void saveWarp(Player player, String warp, Location location, Integer price) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        warpData.getFileData().insert(warp + ".owner", uuid);
        warpData.getFileData().insert(warp + ".currentOwnerName", player.getName());
        warpData.getFileData().insert(warp + ".price", price);
        warpData.getFileData().insert(warp + ".uses", 0);
        warpData.getFileData().insert(warp + ".world", location.getWorld().getName());
        warpData.getFileData().insert(warp + ".x", location.getX());
        warpData.getFileData().insert(warp + ".y", location.getY());
        warpData.getFileData().insert(warp + ".z", location.getZ());
        warpData.getFileData().insert(warp + ".yaw", location.getYaw());
        warpData.set(warp + ".pitch", location.getPitch());
    }

    //

    public static Integer getUses(String warp) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        Integer uses = warpData.getInt(warp + ".uses");
        return uses;
    }

    public static void addUses(String warp) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        warpData.set(warp + ".uses", getUses(warp) + 1);
    }

    //
    public static void deleteWarp(String warp) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        if (warpData.contains(warp)) {
            warpData.set(warp, null);
        }
    }

    //
    public static String getWarpOwnerUUID(String warp) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        if (warpData.getFileData().containsKey(warp)) {
            String UUID = warpData.getString((warp + ".owner"));
            return UUID;
        }
        return null;
    }
    public static String getWarpOwnerName(String warp) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        if (warpData.getFileData().containsKey(warp)) {
            String name = warpData.getString((warp + ".currentOwnerName"));
            return name;
        }
        return null;
    }

    public static String updateWarpOwnerName(Player player) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        warpData.getFileData().toMap().forEach((k,v) -> {
            if (v.toString().contains("owner=" + uuid)){
                if (!v.toString().contains("currentOwnerName=" + player.getName())){
                    warpData.set(k + ".currentOwnerName", player.getName());
                    return;
                }
            }
        });
        return null;
    }

    public static Integer getWarpPrice(String warp) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        if (warpData.getFileData().containsKey(warp)) {
            Integer integer = warpData.getInt((warp + ".price"));
            return integer;
        }
        return null;
    }

    public static Location getWarp(String warp) {
        Json homeData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        if (homeData.getFileData().containsKey(warp)) {
            //String name = homeData.getString(warp + "." + ".owner");
            World world = Bukkit.getWorld(homeData.getString(warp + ".world"));
            double x = homeData.getDouble(warp + ".x");
            double y = homeData.getDouble(warp + ".y");
            double z = homeData.getDouble(warp + ".z");
            float yaw = (float) homeData.getDouble(warp + ".yaw");
            float pitch = (float) homeData.getDouble(warp + ".pitch");
            Location location = new Location(world, x, y, z, yaw, pitch);
            return location;
        }


        return null;

    }


    public static ArrayList<String> getWarps(){
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        ArrayList<String> warps = new ArrayList<>();
        //warpData.getFileData().singleLayerKeySet().forEach((warp) -> warps.add(warp));

        //TODO
        //CHECK IF WORKING!
        warps.addAll(warpData.getFileData().singleLayerKeySet());
        return warps;
    }

    //
    public static ArrayList<String> getOwnedWarps(Player player){
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        ArrayList<String> warps = new ArrayList<>();
        String uuid = player.getUniqueId().toString();

        warpData.getFileData().toMap().forEach((k,v) -> {
            if (v.toString().contains("owner=" + uuid)){
                warps.add(k);
            }
        });

        return warps;
    }

    //

    public static void addAllowedWarps(Player player) {
        Json warpData = new Json("allowedwarps.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        warpData.set(uuid + ".allowed", getAllowedWarps(player) + 1);
    }

    public static Integer getAllowedWarps(Player player) {
        Json warpData = new Json("allowedwarps.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        Integer allowedHomes = warpData.getInt(uuid + ".allowed");
        return allowedHomes;
    }

    //

    public static void setMoneyInQueue(String uuid, Integer money) {
        Json warpData = new Json("warpmoney.json", instance.getDataFolder() + "/data/");;
        warpData.set(uuid + ".inQueue", + money);
    }

    public static void addMoneyToQueue(String uuid, Integer money) {
        Json warpData = new Json("warpmoney.json", instance.getDataFolder() + "/data/");
        Integer currMoney = getMoneyInQueue(uuid);
        Integer newMoney = currMoney + money;
        warpData.set(uuid + ".inQueue", + newMoney);
    }

    public static Integer getMoneyInQueue(String uuid) {
        Json warpData = new Json("warpmoney.json", instance.getDataFolder() + "/data/");
        Integer inHold = warpData.getInt(uuid + ".inQueue");
        return inHold;
    }


}