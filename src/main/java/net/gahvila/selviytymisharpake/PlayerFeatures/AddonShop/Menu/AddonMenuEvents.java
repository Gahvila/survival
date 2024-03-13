package net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Menu;

import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AddonMenuEvents implements Listener {

    private final AddonManager addonManager;


    public AddonMenuEvents(AddonManager addonManager) {
        this.addonManager = addonManager;
    }

    @EventHandler
    private void inventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        switch (e.getView().getTitle()) {
            case "§8Osta lisäosa: Kauppa (2000Ⓖ)§8 §6 §7 §6 §3 §5 §1 §0 §6 §b §a §r":
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 0, 1, 2, 3, 9, 10, 11, 12:
                        if (!addonManager.getShop(p)) {
                            if (SelviytymisHarpake.getEconomy().getBalance(p) >= 2000) {
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1F);
                                SelviytymisHarpake.getEconomy().withdrawPlayer(p, 2000);
                                p.sendMessage("Jee! Ostit kauppa päivityksen.");
                                addonManager.setShop(p);
                                p.closeInventory();
                            } else {
                                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                                p.closeInventory();
                                p.sendMessage("Sinulla ei ole tarpeeksi rahaa! Tarvitset 2000Ⓖ.");
                            }
                        }
                        break;
                    case 5, 6, 7, 8, 14, 15, 16, 17:
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                        p.closeInventory();
                        break;
                }
                break;
            case "§8Osta lisäosa: Feed (2000Ⓖ)§8 §6 §7 §6 §3 §5 §1 §0 §6 §b §a §r":
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 0, 1, 2, 3, 9, 10, 11, 12:
                        if (!addonManager.getFeed(p)) {
                            if (SelviytymisHarpake.getEconomy().getBalance(p) >= 2000) {
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1F);
                                SelviytymisHarpake.getEconomy().withdrawPlayer(p, 2000);
                                p.sendMessage("Jee! Ostit feed päivityksen.");
                                addonManager.setFeed(p);
                                p.closeInventory();
                            } else {
                                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                                p.closeInventory();
                                p.sendMessage("Sinulla ei ole tarpeeksi rahaa! Tarvitset 2000Ⓖ.");
                            }
                        }
                        break;
                    case 5, 6, 7, 8, 14, 15, 16, 17:
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                        p.closeInventory();
                        break;
                }
                break;
            case "§8Osta lisäosa: EChest (1500Ⓖ)§8 §6 §7 §6 §3 §5 §1 §0 §6 §b §a §r":
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 0, 1, 2, 3, 9, 10, 11, 12:
                        if (!addonManager.getEnderchest(p)) {
                            if (SelviytymisHarpake.getEconomy().getBalance(p) >= 1500) {
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1F);
                                SelviytymisHarpake.getEconomy().withdrawPlayer(p, 1500);
                                p.sendMessage("Jee! Ostit enderchest päivityksen.");
                                addonManager.setEnderchest(p);
                                p.closeInventory();
                            } else {
                                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                                p.closeInventory();
                                p.sendMessage("Sinulla ei ole tarpeeksi rahaa! Tarvitset 1500Ⓖ.");
                            }
                        }
                        break;
                    case 5, 6, 7, 8, 14, 15, 16, 17:
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                        p.closeInventory();
                        break;
                }
                break;
            case "§8Osta lisäosa: Craft (1000Ⓖ)§8 §6 §7 §6 §3 §5 §1 §0 §6 §b §a §r":
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 0, 1, 2, 3, 9, 10, 11, 12:
                        if (!addonManager.getCraft(p)) {
                            if (SelviytymisHarpake.getEconomy().getBalance(p) >= 1000) {
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1F);
                                SelviytymisHarpake.getEconomy().withdrawPlayer(p, 1000);
                                p.sendMessage("Jee! Ostit craft päivityksen.");
                                addonManager.setCraft(p);
                                p.closeInventory();
                            } else {
                                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                                p.closeInventory();
                                p.sendMessage("Sinulla ei ole tarpeeksi rahaa! Tarvitset 1000Ⓖ.");
                            }
                        }
                        break;
                    case 5, 6, 7, 8, 14, 15, 16, 17:
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                        p.closeInventory();
                        break;
                }
                break;
            case "§8§lLisäosat§8 §6 §7 §6 §3 §5 §1 §0 §6 §b §a §r":
                e.setCancelled(true);
                if (e.getCurrentItem() == null || (e.getCurrentItem().getType().equals(Material.AIR))) {
                    return;
                }
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1F);
                switch (e.getSlot()) {
                    case 1:
                        p.closeInventory();
                        if (!addonManager.getCraft(p)) {
                            AddonCraftMenu.openGui(p);
                        }else{
                            p.sendMessage("Sinulla on jo tuo lisäosa.");
                        }
                        break;
                    case 3:
                        p.closeInventory();
                        if (!addonManager.getEnderchest(p)) {
                            AddonEnderchestMenu.openGui(p);
                        }else{
                            p.sendMessage("Sinulla on jo tuo lisäosa.");
                        }
                        break;
                    case 5:
                        p.closeInventory();
                        if (!addonManager.getFeed(p)) {
                            AddonFeedMenu.openGui(p);
                        }else{
                            p.sendMessage("Sinulla on jo tuo lisäosa.");
                        }
                        break;
                    case 7:
                        p.closeInventory();
                        if (!addonManager.getShop(p)) {
                            AddonShopMenu.openGui(p);
                        }else{
                            p.sendMessage("Sinulla on jo tuo lisäosa.");
                        }
                        break;
                    default:
                        e.setCancelled(true);
                        break;
                }
                break;
        }
    }
}
