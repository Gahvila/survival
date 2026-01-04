package net.gahvila.survival.Warps.WarpApplications;

import de.leonhard.storage.Json;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Single;
import net.gahvila.survival.Config.ConfigManager;
import net.gahvila.survival.Utils.DiscordWebhook;
import net.gahvila.survival.Warps.WarpManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

    public void sendWarpApplication(Player player, String warpName, Location location) throws IOException {
        if (!warpManager.isLocationSafe(location)) {
            player.sendRichMessage("<red>Sijainti jossa olet ei ole turvallinen.");
            return;
        }

        if (warpName == null || warpName.trim().isEmpty()) {
            player.sendRichMessage("<red>Warpin nimi ei voi olla tyhjä.");
            return;
        }

        if (warpName.length() > 32) {
            player.sendRichMessage("<red>Warpin nimi on liian pitkä (max 32 merkkiä).");
            return;
        }

        if (!warpName.matches("^[a-zA-Z0-9_ äöåÄÖÅ]+$")) {
            player.sendRichMessage("<red>Warpin nimessä saa olla vain kirjaimia, numeroita ja alaviivoja.");
            return;
        }

        if (warpManager.warpExists(warpName)) {
            player.sendRichMessage("<red>Warp nimellä '" + warpName + "' on jo olemassa.");
            return;
        }

        if (getActiveApplicationCount(player.getUniqueId()) >= 2) {
            player.sendRichMessage("<red>Sinulla on jo 2 käsittelemätöntä warp-hakemusta. Odota niiden käsittelyä.");
            return;
        }

        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        for (String key : warpData.getFileData().singleLayerKeySet()) {
            String existingWarpName = warpData.getString(key + ".warpName");
            boolean isProcessed = warpData.getBoolean(key + ".processed");

            if (!isProcessed && existingWarpName != null && existingWarpName.equalsIgnoreCase(warpName)) {
                player.sendRichMessage("<red>Warp nimellä '" + warpName + "' on jo hakemuksessa.");
                return;
            }
        }

        UUID randomUUID = UUID.randomUUID();
        saveApplication(randomUUID, player.getUniqueId(), player.getName(), warpName, location);
        sendApplicationWebhook(randomUUID, player.getUniqueId(), player.getName(), warpName, location);

        player.sendRichMessage("Warp-hakemus '<yellow>" + warpName + "</yellow>' on lähetetty tarkistettavaksi.");
    }

    private int getActiveApplicationCount(UUID playerUUID) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        int count = 0;
        for (String key : warpData.getFileData().singleLayerKeySet()) {
            String existingPlayerUUID = warpData.getString(key + ".playerUUID");
            boolean processed = warpData.getBoolean(key + ".processed");

            if (existingPlayerUUID != null && existingPlayerUUID.equals(playerUUID.toString()) && !processed) {
                count++;
            }
        }
        return count;
    }

    private void saveApplication(UUID applicationUUID, UUID playerUUID, String currentPlayerName, String warpName, Location location) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        String uuid = applicationUUID.toString();
        warpData.getFileData().insert(uuid + ".warpName", warpName);
        warpData.getFileData().insert(uuid + ".playerUUID", playerUUID.toString());
        warpData.getFileData().insert(uuid + ".currentPlayerName", currentPlayerName);
        warpData.getFileData().insert(uuid + ".applicationdate", System.currentTimeMillis());
        warpData.getFileData().insert(uuid + ".world", location.getWorld().getName());
        warpData.getFileData().insert(uuid + ".x", location.getX());
        warpData.getFileData().insert(uuid + ".y", location.getY());
        warpData.getFileData().insert(uuid + ".z", location.getZ());
        warpData.getFileData().insert(uuid + ".yaw", location.getYaw());
        warpData.set(uuid + ".pitch", location.getPitch());

        warpData.set(uuid + ".processed", false);
        warpData.set(uuid + ".acknowledged", false);
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


    public ArrayList<UUID> getApplications() {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        ArrayList<UUID> applicationUUIDs = new ArrayList<>();

        for (String key : warpData.getFileData().singleLayerKeySet()) {
            if (!warpData.getBoolean(key + ".processed")) {
                applicationUUIDs.add(UUID.fromString(key));
            }
        }

        return applicationUUIDs;
    }

    public String getApplicationWarpName(UUID applicationUUID) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        return warpData.getString(applicationUUID.toString() + ".warpName");
    }

    public String getApplicationPlayerName(UUID applicationUUID) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        return warpData.getString(applicationUUID.toString() + ".currentPlayerName");
    }

    public UUID getApplicationPlayerUUID(UUID applicationUUID) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        String uuidStr = warpData.getString(applicationUUID.toString() + ".playerUUID");
        return uuidStr != null ? UUID.fromString(uuidStr) : null;
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
            return new Location(world, x, y, z, yaw, pitch);
        }
        return null;
    }

    public void acceptApplication(UUID applicationUUID, String reason) {
        processApplication(applicationUUID, true, reason);

        UUID playerUUID = getApplicationPlayerUUID(applicationUUID);
        String playerName = getApplicationPlayerName(applicationUUID);
        String warpName = getApplicationWarpName(applicationUUID);
        Location location = getApplicationLocation(applicationUUID);

        warpManager.setWarp(playerUUID, playerName, warpName, location, Single.VALKOINEN, Material.LODESTONE);
    }

    public void denyApplication(UUID applicationUUID, String reason) {
        processApplication(applicationUUID, false, reason);
    }

    private void processApplication(UUID applicationUUID, boolean accepted, String reason) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");
        String uuid = applicationUUID.toString();

        if (!warpData.contains(uuid)) return;

        warpData.set(uuid + ".processed", true);
        warpData.set(uuid + ".accepted", accepted);
        warpData.set(uuid + ".reason", reason);

        UUID playerUUID = getApplicationPlayerUUID(applicationUUID);
        Player player = Bukkit.getPlayer(playerUUID);

        if (player != null && player.isOnline()) {
            sendResultNotification(player, getApplicationWarpName(applicationUUID), accepted, reason);
            warpData.set(uuid + ".acknowledged", true);
        } else {
            warpData.set(uuid + ".acknowledged", false);
        }
    }

    public void checkPendingNotifications(Player player) {
        Json warpData = new Json("warpapplications.json", instance.getDataFolder() + "/data/");

        for (String key : warpData.getFileData().singleLayerKeySet()) {
            String appPlayerUUID = warpData.getString(key + ".playerUUID");
            boolean processed = warpData.getBoolean(key + ".processed");
            boolean acknowledged = warpData.getBoolean(key + ".acknowledged");

            if (appPlayerUUID != null && appPlayerUUID.equals(player.getUniqueId().toString())
                    && processed && !acknowledged) {

                String warpName = warpData.getString(key + ".warpName");
                boolean accepted = warpData.getBoolean(key + ".accepted");
                String reason = warpData.getString(key + ".reason");

                sendResultNotification(player, warpName, accepted, reason);

                warpData.set(key + ".acknowledged", true);
            }
        }
    }

    private void sendResultNotification(Player player, String warpName, boolean accepted, String reason) {
        MiniMessage mm = MiniMessage.miniMessage();
        String statusColor = accepted ? "<green>" : "<red>";
        String statusText = accepted ? "HYVÄKSYTTY" : "HYYLÄTTY";

        player.sendMessage(mm.deserialize("<gray>--------------------------------"));
        player.sendMessage(mm.deserialize("<yellow>Warp-hakemus: <white>" + warpName));
        player.sendMessage(mm.deserialize("<gray>Tila: " + statusColor + "<bold>" + statusText));

        if (reason != null && !reason.isEmpty()) {
            player.sendMessage(mm.deserialize("<gray>Syy: <white>" + reason));
        }

        player.sendMessage(mm.deserialize("<gray>--------------------------------"));

        if (accepted) {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        } else {
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        }
    }
}