package net.gahvila.survival.Trade;

import de.leonhard.storage.Json;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class TradeManager {

    public static HashMap<Player, Player> trade = new HashMap<>(); //sender, receiver
    public static HashMap<Player, Player> latestTrader = new HashMap<>(); //receiver, sender


    public void toggleTrades(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        UUID uuid = player.getUniqueId();

        if (getTradeToggle(player) == null || !getTradeToggle(player)) {
            playerData.set(uuid + ".tradeDisabled", true);
        } else {
            playerData.set(uuid + ".tradeDisabled", false);
        }
    }

    public Boolean getTradeToggle(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        UUID uuid = player.getUniqueId();
        Boolean toggle = playerData.getBoolean(uuid + ".tradeDisabled");
        return toggle;
    }
}
