package net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.menu;


import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.gahvila.selviytymisharpake.Utils.PaginatedMenu;
import net.gahvila.selviytymisharpake.Utils.PlayerMenuUtility;
import net.gahvila.selviytymisharpake.PlayerWarps.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
                if (WarpManager.isLocationSafe(WarpManager.getWarp(e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    if (!p.getName().equals(WarpManager.getWarpOwnerName(e.getCurrentItem().getItemMeta().getDisplayName()))) {
                        WarpManager.addUses(e.getCurrentItem().getItemMeta().getDisplayName());
                    }
                    p.teleportAsync(WarpManager.getWarp(e.getCurrentItem().getItemMeta().getDisplayName()));
                    p.sendMessage("Sinut teleportattiin warppiin §e" + e.getCurrentItem().getItemMeta().getDisplayName() + "§f.");
                }else{
                    p.sendMessage("Warpin sijainti ei ole turvallinen. Teleportti peruttu.");
                }
            }

        }else if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
            //close inventory
            if (e.getClick().isKeyboardClick()){
                e.setCancelled(true);
            }else {
                p.closeInventory();
            }
        } else if (e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)) {
            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Aikaisempi sivu")) {
                if (page == 0) {
                    p.sendMessage(ChatColor.GRAY + "Olet ensimmäisellä sivulla.");
                } else {
                    page = page - 1;
                    super.open();
                }
            } else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Seuraava sivu")) {
                int maxItemsPerPage = getMaxItemsPerPage();
                int startIndex = (page + 1) * maxItemsPerPage;
                ArrayList<String> warps = new ArrayList<String>(WarpManager.getWarps());
                if (startIndex >= warps.size()) {
                    p.sendMessage(ChatColor.GRAY + "Olet viimeisellä sivulla.");
                } else {
                    page = page + 1;
                    super.open();
                }
            }
        }else if(e.getCurrentItem().getType().equals(Material.NETHER_STAR)){
            WarpManager.changeSorting(p);

            p.closeInventory();
            new WarpMenu(SelviytymisHarpake.getPlayerMenuUtility(p)).open();
        }
    }

    @Override
    public void setMenuItems() {

        addMenuBorder();

        //The thing you will be looping through to place items
        ArrayList<String> warps = new ArrayList<String>(WarpManager.getWarps());

        ItemStack orderItem = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta orderMeta = orderItem.getItemMeta();

        orderMeta.setDisplayName("§aJärjestys");

        if (WarpManager.getSorting(playerMenuUtility.getOwner()) == 0 || WarpManager.getSorting(playerMenuUtility.getOwner()) == null){
            orderMeta.setLore(List.of("§fNykyinen järjestys:", "§eUusin ensin"));
            Collections.reverse(warps);
        }else if (WarpManager.getSorting(playerMenuUtility.getOwner()) == 1){
            orderMeta.setLore(List.of("§fNykyinen järjestys:", "§eVanhin ensin"));
        }else if (WarpManager.getSorting(playerMenuUtility.getOwner()) == 2){
            orderMeta.setLore(List.of("§fNykyinen järjestys:", "§eAakkosjärjestys"));
            warps.sort(String::compareToIgnoreCase);
        }
        orderItem.setItemMeta(orderMeta);

        inventory.setItem(52, orderItem);

        ///////////////////////////////////// Pagination loop template
        int maxItemsPerPage = getMaxItemsPerPage();
        int startIndex = page * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, warps.size());

        for (int i = startIndex; i < endIndex; i++) {
            ///////////////////////////

            // Create an item from our collection and place it into the inventory
            ItemStack warpItem = new ItemStack(Material.GREEN_WOOL, 1);
            ItemMeta warpMeta = warpItem.getItemMeta();

            String currWarp = warps.get(i);
            String warpOwner = WarpManager.getWarpOwnerName(currWarp);

            Date currentTime = new Date(WarpManager.getCreationDate(currWarp));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);

            warpMeta.setDisplayName(currWarp);
            warpMeta.setLore(List.of("§fOmistaja: §e" + warpOwner, "§fKäyttökerrat: §e" + WarpManager.getUses(currWarp), "§fHinta: §e" + WarpManager.getWarpPrice(currWarp) + "Ⓖ§f", "§7§o" + dateString));
            warpItem.setItemMeta(warpMeta);

            inventory.addItem(warpItem);
        }


    }
}
