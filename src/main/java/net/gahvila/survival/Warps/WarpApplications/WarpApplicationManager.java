package net.gahvila.survival.Warps.WarpApplications;

import de.leonhard.storage.Json;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Single;
import net.gahvila.survival.Config.ConfigManager;
import net.gahvila.survival.Utils.DiscordWebhook;
import net.gahvila.survival.Warps.Warp;
import net.gahvila.survival.Warps.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static net.gahvila.survival.survival.instance;

public class WarpApplicationManager {

    private final WarpManager warpManager;

    public WarpApplicationManager(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    //application creation
    public void sendWarpApplication(Player player, String warpName, Location location) throws IOException {
        if (warpName == null || warpName.trim().isEmpty()) {
            player.sendRichMessage("<red>Warpin nimi ei voi olla tyhjä.");
            return;
        }

        if (warpName.length() > 32) {
            player.sendRichMessage("<red>Warpin nimi on liian pitkä (max 32 merkkiä).");
            return;
        }

        if (!warpName.matches("^[a-zA-Z0-9_]+$")) {
            player.sendRichMessage("<red>Warpin nimessä saa olla vain kirjaimia, numeroita ja alaviivoja.");
            return;
        }

        if (warpManager.warpExists(warpName)) {
            player.sendRichMessage("<red>Warp nimellä '" + warpName + "' on jo olemassa.");
            return;
        }

        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        for (String key : warpData.getFileData().singleLayerKeySet()) {
            String existingWarpName = warpData.getString(key + ".warpName");
            String existingPlayerUUID = warpData.getString(key + ".playerUUID");
            if (existingWarpName != null && existingPlayerUUID != null &&
                    existingWarpName.equalsIgnoreCase(warpName) &&
                    existingPlayerUUID.equals(player.getUniqueId().toString())) {
                player.sendRichMessage("Sinulla on jo avoin warp-hakemus nimellä '" + warpName + "'.");
                return;
            }
        }

        UUID randomUUID = UUID.randomUUID();
        saveApplication(randomUUID, player.getUniqueId(), player.getName(), warpName, location);
        sendApplicationWebhook(randomUUID, player.getUniqueId(), player.getName(), warpName, location);

        player.sendRichMessage("Warp-hakemus '<yellow>" + warpName + "</yellow>' on lähetetty tarkistettavaksi.");
    }


    private void saveApplication(UUID applicationUUID, UUID playerUUID, String currentPlayerName, String warpName, Location location) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        String uuid = applicationUUID.toString();
        warpData.getFileData().insert(uuid + ".warpName", warpName);
        warpData.getFileData().insert(uuid + ".playerUUID", playerUUID);
        warpData.getFileData().insert(uuid + ".currentPlayerName", currentPlayerName);
        warpData.getFileData().insert(uuid + ".applicationdate", System.currentTimeMillis());
        warpData.getFileData().insert(uuid + ".world", location.getWorld().getName());
        warpData.getFileData().insert(uuid + ".x", location.getX());
        warpData.getFileData().insert(uuid + ".y", location.getY());
        warpData.getFileData().insert(uuid + ".z", location.getZ());
        warpData.getFileData().insert(uuid + ".yaw", location.getYaw());
        warpData.set(uuid + ".pitch", location.getPitch());
    }

    private void sendApplicationWebhook(UUID applicationUUID, UUID playerUUID, String currentPlayerName, String warpName, Location location) throws IOException {
        DiscordWebhook webhook = new DiscordWebhook(ConfigManager.getWarpApplicationWebhookUrl());
        webhook.setContent("Uusi warp hakemus!");
        webhook.setUsername("warp hakemus");
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .addField("Pelaajan nimi", currentPlayerName, true)
                .addField("Pelaajan UUID", playerUUID.toString(), true)
                .addField("Warpin nimi", warpName, true)
                .addField("Sijainti", location.toString(), false));
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setDescription("/adminwarp review " + applicationUUID.toString()));
        webhook.execute();
    }


    //data retrieval
    public ArrayList<UUID> getApplications() {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        ArrayList<UUID> applicationUUIDs = new ArrayList<>();

        for (String key : warpData.getFileData().singleLayerKeySet()) {
            applicationUUIDs.add(UUID.fromString(key));
        }

        return applicationUUIDs;
    }


    public String getApplicationWarpName(UUID applicationUUID) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        String uuid = applicationUUID.toString();
        if (warpData.getFileData().containsKey(uuid)) {
            return warpData.getString(uuid + ".warpName");
        }
        return null;
    }

    public String getApplicationPlayerName(UUID applicationUUID) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        String uuid = applicationUUID.toString();
        if (warpData.getFileData().containsKey(uuid)) {
            return warpData.getString(uuid + ".currentPlayerName");
        }
        return null;
    }

    public UUID getApplicationPlayerUUID(UUID applicationUUID) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        String uuid = applicationUUID.toString();
        if (warpData.getFileData().containsKey(uuid)) {
            return UUID.fromString(warpData.getString(uuid + ".playerUUID"));
        }
        return null;
    }

    public Location getApplicationLocation(UUID applicationUUID) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        String uuid = applicationUUID.toString();
        if (warpData.getFileData().containsKey(uuid)) {
            World world = Bukkit.getWorld(warpData.getString(uuid + ".world"));
            double x = warpData.getDouble(uuid + ".x");
            double y = warpData.getDouble(uuid + ".y");
            double z = warpData.getDouble(uuid + ".z");
            float yaw = (float) warpData.getDouble(uuid + ".yaw");
            float pitch = (float) warpData.getDouble(uuid + ".pitch");
            Location location = new Location(world, x, y, z, yaw, pitch);
            return location;
        }


        return null;
    }

    public void deleteApplication(UUID applicationUUID) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        if (warpData.contains(applicationUUID.toString())) {
            warpData.set(applicationUUID.toString(), null);
        }
    }

    // application controls
    public void acceptApplication(UUID applicationUUID) {
        UUID playerUUID = getApplicationPlayerUUID(applicationUUID);
        String playerName = getApplicationPlayerName(applicationUUID);
        String warpName = getApplicationWarpName(applicationUUID);
        Location location = getApplicationLocation(applicationUUID);
        warpManager.setWarp(playerUUID, playerName, warpName, location, Single.VALKOINEN, Material.LODESTONE);

        deleteApplication(applicationUUID);
        //TODO: send webhook about new warp here
    }

    public void denyApplication(UUID applicationUUID) {
        deleteApplication(applicationUUID);
    }
}
