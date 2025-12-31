package net.gahvila.survival.Homes;

import de.leonhard.storage.Json;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.gahvila.survival.survival.instance;

public class HomeManager {

    private final Json homeData;

    public HomeManager() {
        this.homeData = new Json("homedata.json", instance.getDataFolder() + "/data/");
    }

    private String encode(String homeName) {
        if (homeName == null) return null;
        return Base64.getEncoder().encodeToString(homeName.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String encodedName) {
        if (encodedName == null) return null;
        try {
            return new String(Base64.getDecoder().decode(encodedName), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return encodedName;
        }
    }

    public HashMap<UUID, HashMap<String, Location>> homes = new HashMap<>();

    public void saveHome(UUID uuid, String home, Location location) {
        World world = location.getWorld();
        if (world == null) return;

        String pathKey = encode(home);

        String worldName = world.getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        Thread.startVirtualThread(() -> {
            String root = uuid + "." + pathKey;

            homeData.set(root + ".realName", home);
            homeData.set(root + ".world", worldName);
            homeData.set(root + ".x", x);
            homeData.set(root + ".y", y);
            homeData.set(root + ".z", z);
            homeData.set(root + ".yaw", yaw);
            homeData.set(root + ".pitch", pitch);
        });

        homes.computeIfAbsent(uuid, k -> new HashMap<>()).put(home, location);
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

    public void deleteHome(UUID uuid, String home) {
        String pathKey = encode(home);

        Thread.startVirtualThread(() -> {
            if (homeData.contains(uuid + "." + pathKey)) {
                homeData.set(uuid + "." + pathKey, null);
            }
        });

        getCache(uuid).remove(home);
    }

    public void deleteHomesInWorld(String worldName) {
        List<String> homeowners = new ArrayList<>(homeData.getFileData().singleLayerKeySet());

        for (String homeowner : homeowners) {
            List<String> encodedHomes = new ArrayList<>(homeData.getFileData().singleLayerKeySet(homeowner));

            for (String encodedHome : encodedHomes) {
                String world = homeData.getString(homeowner + "." + encodedHome + ".world");

                if (world != null && world.equalsIgnoreCase(worldName)) {
                    deleteHome(UUID.fromString(homeowner), decode(encodedHome));
                }
            }
        }
    }

    public Location getHomeFromStorage(UUID uuid, String home) {
        String pathKey = encode(home);
        String root = uuid + "." + pathKey;

        if (homeData.getFileData().containsKey(root)) {
            World world = Bukkit.getWorld(homeData.getString(root + ".world"));
            if (world == null) return null;

            double x = homeData.getDouble(root + ".x");
            double y = homeData.getDouble(root + ".y");
            double z = homeData.getDouble(root + ".z");
            float yaw = (float) homeData.getDouble(root + ".yaw");
            float pitch = (float) homeData.getDouble(root + ".pitch");

            return new Location(world, x, y, z, yaw, pitch);
        }
        return null;
    }

    public Location getHome(UUID uuid, String home) {
        return getCache(uuid).get(home);
    }

    public ArrayList<String> getHomesFromStorage(UUID uuid) {
        if (homeData.contains(String.valueOf(uuid))) {
            ArrayList<String> decodedHomes = new ArrayList<>();

            Set<String> encodedKeys = homeData.getFileData().singleLayerKeySet(String.valueOf(uuid));

            for (String key : encodedKeys) {
                decodedHomes.add(decode(key));
            }
            return decodedHomes;
        }
        return null;
    }

    public ArrayList<String> getHomes(UUID uuid) {
        return new ArrayList<>(getCache(uuid).keySet());
    }

    public HashMap<String, Location> getCache(UUID uuid) {
        return homes.getOrDefault(uuid, new HashMap<>());
    }

    public void editHomeName(UUID uuid, String oldHome, String newHome) {
        String oldKey = encode(oldHome);

        if (homeData.getFileData().containsKey(uuid + "." + oldKey)) {
            Location loc = getHome(uuid, oldHome);
            if (loc != null) {
                saveHome(uuid, newHome, loc);
                deleteHome(uuid, oldHome);
            }
        }
    }

    public int getAllowedHomes() {
        return 3;
    }
}