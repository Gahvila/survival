package net.gahvila.survival.Trade;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static net.gahvila.survival.survival.instance;

public class TradeBundleRemover implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        Bukkit.broadcastMessage("§c" + item.getType());
        Bukkit.broadcastMessage("§e" + event.getAction());

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BundleMeta bundleMeta)) {
            Bukkit.broadcastMessage("testi4");
            return;
        }

        if (!Boolean.TRUE.equals(bundleMeta.getPersistentDataContainer().get(TradeManager.key, PersistentDataType.BOOLEAN))) {
            Bukkit.broadcastMessage("testi3");
            return;
        }

        if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
            event.getWhoClicked().sendMessage("Et voi tehdä tuota vaihtokaupasta saadulla pussilla.");
            event.setCancelled(true);
        }

        if (event.getAction() == InventoryAction.PICKUP_HALF) {
            Bukkit.getScheduler().runTaskLater(instance, () -> {
                Bukkit.broadcastMessage("§7" + bundleMeta.getItems().size());
                if (bundleMeta.getItems().isEmpty()) {
                    Bukkit.broadcastMessage("§9" + bundleMeta.getItems().size());
                    item.setAmount(0);
                }
            }, 5L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BundleMeta bundleMeta)) {
            return;
        }

        if (!Boolean.TRUE.equals(bundleMeta.getPersistentDataContainer().get(TradeManager.key, PersistentDataType.BOOLEAN))) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.getPlayer().sendMessage("Et voi tehdä tuota vaihtokaupasta saadulla pussilla.");
            event.setCancelled(true);
        }
    }
}
