package net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AddonMainMenu {
    public static void openGui(Player p){
        Inventory inv = Bukkit.createInventory(p, 9, "§8§lLisäosat§8 §6 §7 §6 §3 §5 §1 §0 §6 §b §a §r" );
        updateGui(p, inv);
        p.openInventory(inv);
    }
    public static void updateGui(Player player, Inventory inventory) {
        //LASIT
        inventory.setItem(0, createItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inventory.setItem(2, createItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inventory.setItem(4, createItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inventory.setItem(6, createItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inventory.setItem(8, createItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        //ITEMIT
        inventory.setItem(1, createItem(Material.CRAFTING_TABLE, "§6§lCraft"));
        inventory.setItem(3, createItem(Material.ENDER_CHEST, "§6§lEnderchest"));
        inventory.setItem(5, createItem(Material.COOKED_BEEF, "§6§lFeed"));
        inventory.setItem(7, createItem(Material.EMERALD, "§6§lKauppa"));
    }
    private static ItemStack createItem(Material material, String displayname){
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayname);
        item.setItemMeta(meta);
        return item;
    }
}
