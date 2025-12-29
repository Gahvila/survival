package net.gahvila.survival.Config;

import de.leonhard.storage.Yaml;

import static net.gahvila.survival.survival.instance;

public class ConfigManager {

    public static String getWarpApplicationWebhookUrl() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("warp-application-webhook-url", "");
    }

    public static String getAnnouncementWebhookUrl() {
        Yaml data = new Yaml("config.yml", instance.getDataFolder() + "/");
        return data.getOrDefault("announcement-webhook-url", "");
    }
}
