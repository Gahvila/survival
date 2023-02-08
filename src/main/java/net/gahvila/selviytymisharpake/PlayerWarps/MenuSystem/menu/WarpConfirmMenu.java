package net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.menu;

import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.Menu;
import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.PlayerMenuUtility;
import net.gahvila.selviytymisharpake.PlayerWarps.WarpManager;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class WarpConfirmMenu extends Menu {


    public WarpConfirmMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Teleporttaa: " + playerMenuUtility.getWarpToTeleport();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Integer price = WarpManager.getWarpPrice(playerMenuUtility.getWarpToTeleport());
        Player p = (Player) e.getWhoClicked();
        switch (e.getCurrentItem().getType()){
            case GREEN_STAINED_GLASS_PANE:
                if (SelviytymisHarpake.getEconomy().getBalance(p) >= price) {
                    SelviytymisHarpake.getEconomy().withdrawPlayer(p, price);
                    p.sendMessage("Tililtäsi veloitettiin §e" + price + " §fkultaa.");
                    p.closeInventory();
                    p.teleportAsync(WarpManager.getWarp(playerMenuUtility.getWarpToTeleport()));
                    p.sendMessage("Sinut teleportattiin warppiin §e" + playerMenuUtility.getWarpToTeleport() + "§f.");

                    if (!p.getName().equals(WarpManager.getWarpOwnerName(e.getCurrentItem().getItemMeta().getDisplayName()))){
                        WarpManager.addUses(playerMenuUtility.getWarpToTeleport());
                    }
                    //
                    String ownerUUID = WarpManager.getWarpOwnerUUID(playerMenuUtility.getWarpToTeleport());
                    if (Bukkit.getPlayer(UUID.fromString(ownerUUID)) == null){
                        WarpManager.addMoneyToQueue(WarpManager.getWarpOwnerUUID(playerMenuUtility.getWarpToTeleport()), price);
                    }else{
                        Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUID));
                        SelviytymisHarpake.getEconomy().depositPlayer(owner, price);
                        owner.sendMessage("Sinun maksullista warppia käytettiin, sait §e" + price + " §fkultaa.");
                    }
                }else{
                    p.closeInventory();
                    p.sendMessage("Sinulla ei ole tarpeeksi kultaa!");
                }
                break;
            case RED_STAINED_GLASS_PANE:

                //go back to the previous menu
                new WarpMenu(playerMenuUtility).open();

                break;
        }

    }

    @Override
    public void setMenuItems() {
        Integer price = WarpManager.getWarpPrice(playerMenuUtility.getWarpToTeleport());
        ItemStack yes = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta yes_meta = yes.getItemMeta();
        yes_meta.setDisplayName(ChatColor.GREEN + "Kyllä");
        ArrayList<String> yes_lore = new ArrayList<>();
        yes_lore.add("§bTililtäsi veloitetaan §e" + price + " §bkultaa.");
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
