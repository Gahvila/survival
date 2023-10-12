package net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AddonShopMenu {
    public static void openGui(Player p){
        Inventory inv = Bukkit.createInventory(p, 18, "§8Osta lisäosa: Kauppa (2000Ⓖ)§8 §6 §7 §6 §3 §5 §1 §0 §6 §b §a §r" );
        updateGui(p, inv);
        p.openInventory(inv);
    }
    public static void updateGui(Player player, Inventory inventory) {
        //LASIT
        inventory.setItem(0, createItem(Material.LIME_STAINED_GLASS_PANE, "§a§lHyväksy"));
        inventory.setItem(1, createItem(Material.LIME_STAINED_GLASS_PANE, "§a§lHyväksy"));
        inventory.setItem(2, createItem(Material.LIME_STAINED_GLASS_PANE, "§a§lHyväksy"));
        inventory.setItem(3, createItem(Material.LIME_STAINED_GLASS_PANE, "§a§lHyväksy"));
        inventory.setItem(9, createItem(Material.LIME_STAINED_GLASS_PANE, "§a§lHyväksy"));
        inventory.setItem(10, createItem(Material.LIME_STAINED_GLASS_PANE, "§a§lHyväksy"));
        inventory.setItem(11, createItem(Material.LIME_STAINED_GLASS_PANE, "§a§lHyväksy"));
        inventory.setItem(12, createItem(Material.LIME_STAINED_GLASS_PANE, "§a§lHyväksy"));
        //
        inventory.setItem(4, createItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inventory.setItem(13, createItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        //
        inventory.setItem(5, createItem(Material.RED_STAINED_GLASS_PANE, "§c§lHylkää"));
        inventory.setItem(6, createItem(Material.RED_STAINED_GLASS_PANE, "§c§lHylkää"));
        inventory.setItem(7, createItem(Material.RED_STAINED_GLASS_PANE, "§c§lHylkää"));
        inventory.setItem(8, createItem(Material.RED_STAINED_GLASS_PANE, "§c§lHylkää"));
        inventory.setItem(14, createItem(Material.RED_STAINED_GLASS_PANE, "§c§lHylkää"));
        inventory.setItem(15, createItem(Material.RED_STAINED_GLASS_PANE, "§c§lHylkää"));
        inventory.setItem(16, createItem(Material.RED_STAINED_GLASS_PANE, "§c§lHylkää"));
        inventory.setItem(17, createItem(Material.RED_STAINED_GLASS_PANE, "§c§lHylkää"));


    }
    private static ItemStack createItem(Material material, String displayname){
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayname);
        item.setItemMeta(meta);
        return item;
    }
}
