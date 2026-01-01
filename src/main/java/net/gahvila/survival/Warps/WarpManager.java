package net.gahvila.survival.Warps;

import de.leonhard.storage.Json;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Single;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

import static net.gahvila.survival.survival.instance;

public class WarpManager {

    private final PlaytimeManager playtimeManager;

    public WarpManager(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    public HashSet<Warp> warps = new HashSet<>();

    // Define blocks that are dangerous to stand IN (feet/head)
    private static final Set<Material> DANGEROUS_BLOCKS = new HashSet<>();
    // Define blocks that are dangerous to stand ON (floor)
    private static final Set<Material> UNSAFE_FLOOR = new HashSet<>();

    static {
        DANGEROUS_BLOCKS.add(Material.LAVA);
        DANGEROUS_BLOCKS.add(Material.FIRE);
        DANGEROUS_BLOCKS.add(Material.SWEET_BERRY_BUSH);
        DANGEROUS_BLOCKS.add(Material.CACTUS);

        UNSAFE_FLOOR.add(Material.AIR);
        UNSAFE_FLOOR.add(Material.LAVA);
        UNSAFE_FLOOR.add(Material.FIRE);
        UNSAFE_FLOOR.add(Material.MAGMA_BLOCK);
        UNSAFE_FLOOR.add(Material.CACTUS);
    }

    public boolean isLocationSafe(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        var world = location.getWorld();

        if (world == null) return false;

        Block centerGround = world.getBlockAt(x, y - 1, z);
        Block centerFeet = world.getBlockAt(x, y, z);
        Block centerHead = world.getBlockAt(x, y + 1, z);

        if (UNSAFE_FLOOR.contains(centerGround.getType()) || !centerGround.getType().isSolid()) {
            return false;
        }
        if (centerFeet.getType().isSolid() || centerHead.getType().isSolid()) {
            return false;
        }

        for (int ix = x - 1; ix <= x + 1; ix++) {
            for (int iz = z - 1; iz <= z + 1; iz++) {

                Block surroundingFeet = world.getBlockAt(ix, y, iz);
                Block surroundingHead = world.getBlockAt(ix, y + 1, iz);

                if (DANGEROUS_BLOCKS.contains(surroundingFeet.getType()) ||
                        DANGEROUS_BLOCKS.contains(surroundingHead.getType())) {
                    return false;
                }
            }
        }

        return true;
    }

    //
    public void changeSorting(Player player) {
        Json warpData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        UUID uuid = player.getUniqueId();

        WarpSorting currentSorting = getSorting(uuid);

        WarpSorting[] values = WarpSorting.values();

        int nextOrdinal = (currentSorting.ordinal() + 1) % values.length;
        WarpSorting nextSorting = values[nextOrdinal];

        warpData.set(uuid + ".sorting", nextSorting.name());
    }

    public WarpSorting getSorting(UUID uuid) {
        Json warpData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        return WarpSorting.valueOf(warpData.getOrDefault(uuid + ".sorting", WarpSorting.ALPHABETICAL.name()));
    }

    //
    public void setWarp(UUID uuid, String currentPlayerName, String warp, Location location, Single color, Material customItem) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        warpData.getFileData().insert(warp + ".owner", uuid);
        warpData.getFileData().insert(warp + ".currentOwnerName", currentPlayerName);
        warpData.getFileData().insert(warp + ".uses", 0);
        warpData.getFileData().insert(warp + ".ownerNotified", false);
        warpData.getFileData().insert(warp + ".creationdate", System.currentTimeMillis());
        warpData.getFileData().insert(warp + ".color", color == null ? Single.VALKOINEN : color);
        warpData.getFileData().insert(warp + ".customItem", customItem == Material.AIR ? Material.LODESTONE : customItem);
        warpData.getFileData().insert(warp + ".world", location.getWorld().getName());
        warpData.getFileData().insert(warp + ".x", location.getX());
        warpData.getFileData().insert(warp + ".y", location.getY());
        warpData.getFileData().insert(warp + ".z", location.getZ());
        warpData.getFileData().insert(warp + ".yaw", location.getYaw());
        warpData.set(warp + ".pitch", location.getPitch());
        warps.add(new Warp(warp, uuid,
                currentPlayerName,
                0,
                false,
                System.currentTimeMillis(),
                location,
                color,
                customItem == Material.AIR ? Material.LODESTONE : customItem));
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

    public void editWarpColor(Warp warp, Single color) {
        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        warpData.set(warp.getName() + ".color", color == null ? Single.VALKOINEN : color);
        warp.setColor(color);
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
        warpData.getFileData().insert(newName + ".uses", warp.getUses());
        warpData.getFileData().insert(warp + ".ownerNotified", false);
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
            Single color = Single.VALKOINEN;
            Material customItem = Material.LODESTONE;
            if (homeData.contains(key + ".color")) {
                color = homeData.getEnum(key + ".color", Single.class);
            }
            if (homeData.contains(key + ".customItem")) {
                customItem = Material.getMaterial(homeData.getString(key + ".customItem"));
            }
            Warp temp = new Warp(key, UUID.fromString(homeData.getString(key + ".owner")),
                    homeData.getString(key + ".currentOwnerName"),
                    homeData.getInt(key + ".uses"),
                    homeData.getBoolean(key + ".ownerNotified"),
                    homeData.getLong(key + ".creationdate"),
                    new Location(Bukkit.getWorld(homeData.getString(key + ".world")),
                            homeData.getDouble(key + ".x"),
                            homeData.getDouble(key + ".y"),
                            homeData.getDouble(key + ".z"),
                            homeData.getFloat(key + ".yaw"),
                            homeData.getFloat(key + ".pitch")
                    ),
                    color,
                    customItem);
            warps.add(temp);
        });
    }

    public List<String> getWarpNames() {
        return warps.stream().map(Warp::getName).toList();
    }

    public List<Warp> getWarps(Optional<Player> optionalPlayer) {
        return optionalPlayer.map(player -> {
            WarpSorting sorting = getSorting(player.getUniqueId());
            return warps.stream()
                    .sorted(switch (sorting) {
                        case ALPHABETICAL -> Comparator.comparing(Warp::getName, String.CASE_INSENSITIVE_ORDER);
                        case REVERSE_ALPHABETICAL -> Comparator.comparing(Warp::getName, String.CASE_INSENSITIVE_ORDER).reversed();
                        case NEWEST_WARP -> Comparator.comparing(Warp::getCreationDate).reversed();
                        case OLDEST_WARP -> Comparator.comparing(Warp::getCreationDate);
                    })
                    .toList();
        }).orElseGet(() -> warps.stream().toList());
    }

    public List<Warp> getOwnedWarps(UUID uuid) {
        WarpSorting sorting = getSorting(uuid);

        return warps.stream()
                .filter(warp -> warp.getOwner().equals(uuid))
                .sorted(switch (sorting) {
                    case ALPHABETICAL -> Comparator.comparing(Warp::getName, String.CASE_INSENSITIVE_ORDER);
                    case REVERSE_ALPHABETICAL -> Comparator.comparing(Warp::getName, String.CASE_INSENSITIVE_ORDER).reversed();
                    case NEWEST_WARP -> Comparator.comparing(Warp::getCreationDate).reversed();
                    case OLDEST_WARP -> Comparator.comparing(Warp::getCreationDate);
                })
                .toList();
    }

    public List<String> getOwnedWarpNames(UUID uuid){
        return warps.stream().filter(warp -> warp.getOwner().equals(uuid)).map(Warp::getName).toList();
    }

    public Integer getAllowedWarps(Player player) {
        long playtime = playtimeManager.getPlaytime(player).join();
        long initialWarpTime = 36000; // 10 hours in seconds
        long timePerWarp = 270000; // 75 hours in seconds

        if (playtime < initialWarpTime) {
            return 0;
        }

        return 1 + (int) ((playtime - initialWarpTime) / timePerWarp);
    }

    public boolean warpExists(String warpName) {
        if (warpName == null || warpName.isBlank()) return false;

        boolean inCache = warps.stream()
                .anyMatch(warp -> warp.getName().equalsIgnoreCase(warpName));
        if (inCache) return true;

        Json warpData = new Json("warpdata.json", instance.getDataFolder() + "/data/");
        return warpData.contains(warpName);
    }
}