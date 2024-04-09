package net.gahvila.selviytymisharpake.PlayerFeatures.Warps;

import de.leonhard.storage.Json;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class WarpManager {
    public HashSet<Warp> warps = new HashSet<>();



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
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        warpData.set(name + ".price", price);
        return null;
    }

    //
    public void setWarp(Player player, String warp, Location location, Integer price, Material customItem) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        warpData.getFileData().insert(warp + ".owner", uuid);
        warpData.getFileData().insert(warp + ".currentOwnerName", player.getName());
        warpData.getFileData().insert(warp + ".price", price);
        warpData.getFileData().insert(warp + ".uses", 0);
        warpData.getFileData().insert(warp + ".creationdate", System.currentTimeMillis());
        warpData.getFileData().insert(warp + ".customItem", customItem == Material.AIR ? Material.DIRT : customItem);
        warpData.getFileData().insert(warp + ".world", location.getWorld().getName());
        warpData.getFileData().insert(warp + ".x", location.getX());
        warpData.getFileData().insert(warp + ".y", location.getY());
        warpData.getFileData().insert(warp + ".z", location.getZ());
        warpData.getFileData().insert(warp + ".yaw", location.getYaw());
        warpData.set(warp + ".pitch", location.getPitch());
        warps.add(new Warp(warp, player.getUniqueId(),
                player.getName(),
                price,
                0,
                System.currentTimeMillis(),
                location,
                customItem == Material.AIR ? Material.DIRT : customItem));
    }

    //

    public void addUses(Warp warp) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        warpData.set(warp.getName() + ".uses", warp.getUses()  + 1);
        warp.setUses(warp.getUses()  + 1);
    }

    //

    //
    public void deleteWarp(Warp warp) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        if (warpData.contains(warp.getName())) {
            warpData.set(warp.getName(), null);
        }
        warps.remove(warp);
    }

    public void editWarpItem(Warp warp, Material customItem) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        warpData.set(warp.getName() + ".customItem", customItem == Material.AIR ? Material.DIRT : customItem);
        warp.setCustomItem(customItem);
    }

    public void editWarpName(Warp warp, String newName) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        if (getWarpNames().contains(newName)) return;
        if (warpData.contains(warp.getName())) {
            warpData.set(warp.getName(), null);
        }
        String uuid = warp.getOwner().toString();
        warpData.getFileData().insert(newName + ".owner", uuid);
        warpData.getFileData().insert(newName + ".currentOwnerName", warp.getOwnerName());
        warpData.getFileData().insert(newName + ".price", warp.getPrice());
        warpData.getFileData().insert(newName + ".uses", warp.getUses());
        warpData.getFileData().insert(newName + ".creationdate", warp.getCreationDate());
        warpData.getFileData().insert(newName + ".customItem", warp.getCustomItem());
        warpData.getFileData().insert(newName + ".world", warp.getLocation().getWorld().getName());
        warpData.getFileData().insert(newName + ".x", warp.getLocation().getX());
        warpData.getFileData().insert(newName + ".y", warp.getLocation().getY());
        warpData.getFileData().insert(newName + ".z", warp.getLocation().getZ());
        warpData.getFileData().insert(newName + ".yaw", warp.getLocation().getYaw());
        warpData.set(newName + ".pitch", warp.getLocation().getPitch());

        warp.setName(newName);
    }

    //
    public void updateWarpOwnerName(Player player) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        warpData.getFileData().toMap().forEach((k,v) -> {
            if (v.toString().contains("owner=" + uuid)){
                if (!v.toString().contains("currentOwnerName=" + player.getName())){
                    warpData.set(k + ".currentOwnerName", player.getName());
                }
            }
        });
        warps.stream().filter(warp -> warp.getOwner().equals(player.getUniqueId())).forEach(warp -> warp.setOwnerName(player.getName()));
    }

    public Optional<Warp> getWarp(String name) {
        return warps.stream().filter(warp -> warp.getName().equals(name)).findFirst();
    }

    public void loadWarps() {
        Json homeData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        homeData.getFileData().singleLayerKeySet().forEach(key -> {
            System.err.println(homeData.getString(key + ".owner"));
            Material customItem = Material.DIRT;
            if (homeData.contains(key + ".customItem")) {
                customItem = Material.getMaterial(homeData.getString(key + ".customItem"));
            }
            Warp temp = new Warp(key, UUID.fromString(homeData.getString(key + ".owner")),
                    homeData.getString(key + ".currentOwnerName"),
                    homeData.getInt(key + ".price"),
                    homeData.getInt(key + ".uses"),
                    homeData.getLong(key + ".creationdate"),
                    new Location(Bukkit.getWorld(homeData.getString(key + ".world")),
                            homeData.getDouble(key + ".x"),
                            homeData.getDouble(key + ".y"),
                            homeData.getDouble(key + ".z"),
                            homeData.getFloat(key + ".yaw"),
                            homeData.getFloat(key + ".pitch")
                    ), customItem);
            warps.add(temp);
        });
    }

    public List<String> getWarpNames() {
        return warps.stream().map(Warp::getName).toList();
    }
    public List<Warp> getWarps() {
        return warps.stream().toList();
    }

    public List<Warp> getOwnedWarps(UUID uuid){
        return warps.stream().filter(warp -> warp.getOwner().equals(uuid)).toList();
    }

    //
    public List<String> getOwnedWarpNames(UUID uuid){
        return warps.stream().filter(warp -> warp.getOwner().equals(uuid)).map(Warp::getName).toList();
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