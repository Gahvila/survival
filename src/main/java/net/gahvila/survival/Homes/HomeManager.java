package net.gahvila.survival.Homes;

import de.leonhard.storage.Json;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import net.gahvila.survival.survival;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.gahvila.survival.survival.instance;

public class HomeManager {

    private final PlaytimeManager playtimeManager;
    public HomeManager(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    public HashMap<UUID, HashMap<String, Location>> homes = new HashMap<>();
    public void saveHome(UUID uuid, String home, Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(survival.instance, () -> {
            Json homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");
            homeData.getFileData().insert(uuid + "." + home + ".world", location.getWorld().getName());
            homeData.getFileData().insert(uuid + "." + home + ".x", location.getX());
            homeData.getFileData().insert(uuid + "." + home + ".y", location.getY());
            homeData.getFileData().insert(uuid + "." + home + ".z", location.getZ());
            homeData.getFileData().insert(uuid + "." + home + ".yaw", location.getYaw());
            homeData.set(uuid + "." + home + ".pitch", location.getPitch());
        });
        HashMap<String, Location> data = homes.getOrDefault(uuid, new HashMap<>());
        data.put(home, location);
        homes.put(uuid, data);
    }

    public void putHomeIntoCache(UUID uuid) {
        HashMap<String, Location> data = homes.getOrDefault(uuid, new HashMap<>());
        List<String> homesFromStorage = getHomesFromStorage(uuid);
        if (homesFromStorage == null) return;
        for (String homeName : homesFromStorage) {
            data.put(homeName, getHomeFromStorage(uuid, homeName));
        }
        homes.put(uuid, data);
    }

    //
    public void deleteHome(UUID uuid, String home) {
        Bukkit.getScheduler().runTaskAsynchronously(survival.instance, () -> {
            Json homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");
            if (homeData.contains(uuid + "." + home)) {
                homeData.set(uuid + "." + home, null);
            }
        });
        getCache(uuid).remove(home);
    }

    //
    public void deleteHomesInWorld(String worldName) {
        Json homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");

        List<String> homeowners = new ArrayList<>(homeData.getFileData().singleLayerKeySet());

        for (String homeowner : homeowners) {
            List<String> homes = new ArrayList<>(homeData.getFileData().singleLayerKeySet(homeowner));

            for (String home : homes) {
                String world = homeData.getString(homeowner + "." + home + ".world");

                if (world != null && world.equalsIgnoreCase(worldName)) {
                    deleteHome(UUID.fromString(homeowner), home);
                }
            }
        }
    }
    //
    public Location getHomeFromStorage(UUID uuid, String home) {
        Json homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");
        if (homeData.getFileData().containsKey(uuid + "." + home)) {
            World world = Bukkit.getWorld(homeData.getString(uuid + "." + home + ".world"));
            double x = homeData.getDouble(uuid + "." + home + ".x");
            double y = homeData.getDouble(uuid + "." + home + ".y");
            double z = homeData.getDouble(uuid + "." + home + ".z");
            float yaw = (float) homeData.getDouble(uuid + "." + home + ".yaw");
            float pitch = (float) homeData.getDouble(uuid + "." + home + ".pitch");
            Location location = new Location(world, x, y, z, yaw, pitch);
            return location;
        }


        return null;
    }
    public Location getHome(UUID uuid, String home) {
        return getCache(uuid).get(home);
    }

    //from storage
    public ArrayList<String> getHomesFromStorage(UUID uuid) {
        Json homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");
        if (homeData.contains(String.valueOf(uuid))) {
            ArrayList<String> homes = new ArrayList<>();
            homeData.getFileData().singleLayerKeySet(String.valueOf(uuid)).forEach((home) -> homes.add(home));
            return homes;
        }
        return null;
    }
    //from cache
    public ArrayList<String> getHomes(UUID uuid) {
        return new ArrayList<>(getCache(uuid).keySet());
    }

    public HashMap<String, Location> getCache(UUID uuid) {
        return homes.getOrDefault(uuid, new HashMap<>());
    }

    public void editHomeName(UUID uuid, String oldHome, String newHome) {
        Json homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");
        if (homeData.getFileData().containsKey(uuid + "." + oldHome)) {
            saveHome(uuid, newHome, getHome(uuid, oldHome));
            deleteHome(uuid, oldHome);
        }
    }

    public Integer getAllowedHomes(Player player) {
        long playtime = playtimeManager.getPlaytime(player).join();
        long timePerHome = 180000L; //50 hours
        int allowedHomes = (int) (playtime / timePerHome) + 1;

        return Math.max(allowedHomes, 1);
    }
}