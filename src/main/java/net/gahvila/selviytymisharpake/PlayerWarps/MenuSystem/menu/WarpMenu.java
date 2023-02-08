package net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.menu;


import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.PaginatedMenu;
import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.PlayerMenuUtility;
import net.gahvila.selviytymisharpake.PlayerWarps.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class WarpMenu extends PaginatedMenu implements Listener {

    public WarpMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "§5§lWarppilista";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        ArrayList<Player> players = new ArrayList<Player>(getServer().getOnlinePlayers());

        if (e.getCurrentItem().getType().equals(Material.GREEN_WOOL)) {
            Integer price = WarpManager.getWarpPrice(e.getCurrentItem().getItemMeta().getDisplayName());
            if (price > 0 && price < 51){
                playerMenuUtility.setWarpToTeleport(e.getCurrentItem().getItemMeta().getDisplayName());

                new WarpConfirmMenu(playerMenuUtility).open();
            }else{
                p.closeInventory();
                if (!p.getName().equals(WarpManager.getWarpOwnerName(e.getCurrentItem().getItemMeta().getDisplayName()))){
                    WarpManager.addUses(e.getCurrentItem().getItemMeta().getDisplayName());
                }
                p.teleportAsync(WarpManager.getWarp(e.getCurrentItem().getItemMeta().getDisplayName()));
                p.sendMessage("Sinut teleportattiin warppiin §e" + e.getCurrentItem().getItemMeta().getDisplayName() + "§f.");
            }

        }else if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

            //close inventory
            p.closeInventory();

        }else if(e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)){
            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Aikaisempi sivu")){
                if (page == 0){
                    p.sendMessage(ChatColor.GRAY + "Olet ensimmäisellä sivulla.");
                }else{
                    page = page - 1;
                    super.open();
                }
            }else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Seuraava sivu")){
                if (!((index + 1) >= players.size())){
                    page = page + 1;
                    super.open();
                }else{
                    p.sendMessage(ChatColor.GRAY + "Olet viimeisellä sivulla.");
                }
            }
        }
    }

    @Override
    public void setMenuItems() {

        addMenuBorder();

        //The thing you will be looping through to place items
        ArrayList<String> warps = new ArrayList<String>(WarpManager.getWarps());

        ///////////////////////////////////// Pagination loop template
        if(warps != null && !warps.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= warps.size()) break;
                if (warps.get(index) != null){
                    ///////////////////////////

                    //Create an item from our collection and place it into the inventory
                    ItemStack warpItem = new ItemStack(Material.GREEN_WOOL, 1);
                    ItemMeta warpMeta = warpItem.getItemMeta();

                    warpMeta.setDisplayName(WarpManager.getWarps().get(i));
                    String currWarp = WarpManager.getWarps().get(i);
                    String warpOwner = WarpManager.getWarpOwnerName(currWarp);
                    warpMeta.setLore(List.of("§fOmistaja: §e" + warpOwner, "§fKäyttökerrat: §e" + WarpManager.getUses(currWarp), "§fHinta: §e" + WarpManager.getWarpPrice(currWarp) + " §fkultaa"));
                    warpItem.setItemMeta(warpMeta);

                    inventory.addItem(warpItem);

                }
            }
        }


    }
}
