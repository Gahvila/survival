package net.gahvila.selviytymisharpake.PlayerWarps;

import de.leonhard.storage.Json;
import de.leonhard.storage.Toml;
import de.leonhard.storage.Yaml;
import it.unimi.dsi.fastutil.Hash;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class WarpManager {

    public HashMap<UUID, Integer> editingWarp = new HashMap<UUID, Integer>();
    public HashMap<UUID, String> editingWarpName = new HashMap<UUID, String>();
    //
    public HashMap<UUID, Integer> settingWarp = new HashMap<UUID, Integer>();
    public HashMap<UUID, String> settingWarpName = new HashMap<UUID, String>();
    public HashMap<UUID, Integer> settingWarpPrice = new HashMap<UUID, Integer>();


    public static HashSet<Material> bad_blocks = new HashSet<>();
    static {
        bad_blocks.add(Material.LAVA);
        bad_blocks.add(Material.FIRE);
        bad_blocks.add(Material.CACTUS);
        bad_blocks.add(Material.MAGMA_BLOCK);
    }
    public static HashSet<Material> ground_blocks = new HashSet<>();
    static {
        bad_blocks.add(Material.AIR);
        bad_blocks.add(Material.LAVA);
        bad_blocks.add(Material.FIRE);
        bad_blocks.add(Material.CACTUS);
        bad_blocks.add(Material.MAGMA_BLOCK);
    }


    public boolean isLocationSafe(Location location) {

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        //Get instances of the blocks around where the player would spawn
        Block block = location.getWorld().getBlockAt(x, y, z);
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        Block above = location.getWorld().getBlockAt(x, y + 1, z);
        //spawnrtp
        if (!ground_blocks.contains(below.getType())){
            return !(bad_blocks.contains(below.getType())) || (block.isSolid()) || (above.getType().isSolid());
        }else{
            return false;
        }
    }

    //

    //0 eli default: uusimmat warpit ensin
    //1 vanhimmat warpit ensin
    //2 aakkosjärjestys

    public String changeSorting(Player player) {
        Json warpData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        UUID uuid = player.getUniqueId();
        if (getSorting(player) == null || getSorting(player) == 0) {
            warpData.set(uuid + ".sorting", 1);
        } else if (getSorting(player) == 1){
            warpData.set(uuid + ".sorting", 2);
        } else if (getSorting(player) == 2){
            warpData.set(uuid + ".sorting", 0);
        }
        return null;
    }

    public Integer getSorting(Player player) {
        Json warpData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        UUID uuid = player.getUniqueId();
        Integer uses = warpData.getInt(uuid + ".sorting");
        return uses;
    }

    //
    public String updateWarpPrice(Player player, String name, Integer price) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        warpData.set(name + ".price", price);
        return null;
    }

    //
    public String updateWarpLocation(Player player, String warp, Location location) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        warpData.getFileData().insert(warp + ".world", location.getWorld().getName());
        warpData.getFileData().insert(warp + ".x", location.getX());
        warpData.getFileData().insert(warp + ".y", location.getY());
        warpData.getFileData().insert(warp + ".z", location.getZ());
        warpData.getFileData().insert(warp + ".yaw", location.getYaw());
        warpData.getFileData().insert(warp + ".pitch", location.getPitch());
        return null;
    }

    public void saveWarp(Player player, String warp, Location location, Integer price) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        warpData.getFileData().insert(warp + ".owner", uuid);
        warpData.getFileData().insert(warp + ".currentOwnerName", player.getName());
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

    //

    public Integer getUses(String warp) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        Integer uses = warpData.getInt(warp + ".uses");
        return uses;
    }

    public void addUses(String warp) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        warpData.set(warp + ".uses", getUses(warp) + 1);
    }

    //

    public Long getCreationDate(String warp) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        Long date = warpData.getLong(warp + ".creationdate");
        return date;
    }

    //
    public void deleteWarp(String warp) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        if (warpData.contains(warp)) {
            warpData.set(warp, null);
        }
    }

    //
    public String getWarpOwnerUUID(String warp) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        if (warpData.getFileData().containsKey(warp)) {
            String UUID = warpData.getString((warp + ".owner"));
            return UUID;
        }
        return null;
    }
    public String getWarpOwnerName(String warp) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        if (warpData.getFileData().containsKey(warp)) {
            String name = warpData.getString((warp + ".currentOwnerName"));
            return name;
        }
        return null;
    }

    public String updateWarpOwnerName(Player player) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
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

    public Integer getWarpPrice(String warp) {
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
        if (warpData.getFileData().containsKey(warp)) {
            Integer integer = warpData.getInt((warp + ".price"));
            return integer;
        }
        return null;
    }

    public Location getWarp(String warp) {
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


    public ArrayList<String> getWarps() {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        ArrayList<String> warps = new ArrayList<>();
        warps.addAll(warpData.getFileData().singleLayerKeySet());

        //retrieve and store the timestamps in a separate list
        ArrayList<Long> timestamps = new ArrayList<>();
        for (String warp : warps) {
            long timestamp = getCreationDate(warp);
            timestamps.add(timestamp);
        }

        //sort the warps based on the stored timestamps
        Collections.sort(warps, (warp1, warp2) -> {
            int index1 = warps.indexOf(warp1);
            int index2 = warps.indexOf(warp2);
            long timestamp1 = timestamps.get(index1);
            long timestamp2 = timestamps.get(index2);
            return Long.compare(timestamp1, timestamp2);
        });

        return warps;
    }

    //
    public ArrayList<String> getOwnedWarps(Player player){
        Json warpData = new Json("warpdata.toml", instance.getDataFolder() + "/data/");
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

    public void addAllowedWarps(Player player) {
        Json warpData = new Json("allowedwarps.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        warpData.set(uuid + ".allowed", getAllowedWarps(player) + 1);
    }

    public Integer getAllowedWarps(Player player) {
        Json warpData = new Json("allowedwarps.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        Integer allowedHomes = warpData.getInt(uuid + ".allowed");
        return allowedHomes;
    }

    //

    public void setMoneyInQueue(String uuid, Integer money) {
        Json warpData = new Json("warpmoney.json", instance.getDataFolder() + "/data/");;
        warpData.set(uuid + ".inQueue", + money);
    }

    public void addMoneyToQueue(String uuid, Integer money) {
        Json warpData = new Json("warpmoney.json", instance.getDataFolder() + "/data/");
        Integer currMoney = getMoneyInQueue(uuid);
        Integer newMoney = currMoney + money;
        warpData.set(uuid + ".inQueue", + newMoney);
    }

    public Integer getMoneyInQueue(String uuid) {
        Json warpData = new Json("warpmoney.json", instance.getDataFolder() + "/data/");
        Integer inHold = warpData.getInt(uuid + ".inQueue");
        return inHold;
    }


}