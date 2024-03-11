package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.menu.WarpMenu;
import net.gahvila.selviytymisharpake.PlayerWarps.WarpManager;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.gahvila.selviytymisharpake.Utils.Menu;
import net.gahvila.selviytymisharpake.Utils.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class BackMenuConfirm extends Menu {

    private final BackManager backManager;

    public BackMenuConfirm(PlayerMenuUtility playerMenuUtility, BackManager backManager) {
        super(playerMenuUtility);
        this.backManager = backManager;
    }

    @Override
    public String getMenuName() {
        return "Hyväksytkö maksun?";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Double price = backManager.calculateDeathPrice(p);
        switch (e.getCurrentItem().getType()){
            case GREEN_STAINED_GLASS_PANE:
                if (SelviytymisHarpake.getEconomy().getBalance(p) >= price) {
                    SelviytymisHarpake.getEconomy().withdrawPlayer(p, price);
                    p.sendMessage("Tililtäsi veloitettiin §e" + price + "Ⓖ§f.");
                    p.closeInventory();
                    p.teleportAsync(backManager.getDeath(p));
                    p.sendMessage("Sinut teleportattiin sinun viimeisimpään kuolinpaikkaan§f.");
                }else{
                    p.closeInventory();
                    p.sendMessage("Sinulla ei ole tarpeeksi Ⓖ!");
                }
                break;
            case RED_STAINED_GLASS_PANE:

                //go back to the previous menu
                new BackMenu(playerMenuUtility, backManager).open();

                break;
        }

    }

    @Override
    public void setMenuItems() {
        Double price = backManager.calculateDeathPrice(playerMenuUtility.getOwner());
        ItemStack yes = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta yes_meta = yes.getItemMeta();
        yes_meta.setDisplayName(ChatColor.GREEN + "Kyllä");
        ArrayList<String> yes_lore = new ArrayList<>();
        yes_lore.add("§bTililtäsi veloitetaan §e" + price + "Ⓖ§b.");
        yes_lore.add("§cOletko varma?");
        yes_meta.setLore(yes_lore);
        yes.setItemMeta(yes_meta);
        ItemStack no = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta no_meta = no.getItemMeta();
        no_meta.setDisplayName(ChatColor.RED + "Peru teleporttaus");
        no.setItemMeta(no_meta);

        inventory.setItem(3, yes);
        inventory.setItem(5, no);

        setFillerGlass();

    }

}
