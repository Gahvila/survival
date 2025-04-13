package net.gahvila.survival.Trade;

import de.leonhard.storage.Json;
import net.gahvila.survival.survival;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class TradeManager {

    public static HashMap<Player, Player> tradeRequest = new HashMap<>(); //receiver, sender
    public static HashMap<Player, Player> latestTrader = new HashMap<>(); //receiver, sender
    public static NamespacedKey key = new NamespacedKey(instance, "tradeBundle");


    private void givePlayerBundle(Player player, List<ItemStack> items) {
        ItemStack bundle = new ItemStack(Material.BUNDLE);
        BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();

        for (ItemStack item : items) bundleMeta.addItem(item);

        bundleMeta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        bundle.setItemMeta(bundleMeta);
        player.getInventory().addItem(bundle);
    }

    public long getNextTradeId() {
        //in the future this code will increment by one as trades are being made
        return 0;
    }

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
        return playerData.getBoolean(uuid + ".tradeDisabled");
    }
}
