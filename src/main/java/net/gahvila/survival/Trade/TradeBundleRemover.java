package net.gahvila.survival.Trade;

import com.destroystokyo.paper.MaterialSetTag;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static net.gahvila.survival.survival.instance;
import static org.yaml.snakeyaml.tokens.Token.ID.Tag;

public class TradeBundleRemover implements Listener {

    @EventHandler
    public void onItemUse(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Bukkit.broadcastMessage(String.valueOf(item.getType()));
        Bukkit.broadcastMessage(event.getAction().toString());

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BundleMeta bundleMeta)) {
            return;
        }

        if (!Boolean.TRUE.equals(bundleMeta.getPersistentDataContainer().get(TradeManager.key, PersistentDataType.BOOLEAN))) {
            return;
        }
        if (event.getAction() != InventoryAction.PICKUP_HALF) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            if (bundleMeta.getItems().isEmpty()) {
                item.setAmount(0);
            }
        }, 1L);
    }
}
