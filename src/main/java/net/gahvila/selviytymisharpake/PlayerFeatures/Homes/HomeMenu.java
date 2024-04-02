package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class HomeMenu extends PaginatedMenu implements Listener {

    private final HomeManager homeManager;

    public HomeMenu(PlayerMenuUtility playerMenuUtility, HomeManager homeManager) {
        super(playerMenuUtility);
        this.homeManager = homeManager;
    }

    @Override
    public String getMenuName() {
        return "§5§lKotilista";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        ArrayList<Player> players = new ArrayList<Player>(getServer().getOnlinePlayers());

        if (e.getCurrentItem().getType().equals(Material.GREEN_BED)) {
            if (homeManager.getHome(p, e.getCurrentItem().getItemMeta().getDisplayName()) != null){
                p.teleportAsync(homeManager.getHome(p, e.getCurrentItem().getItemMeta().getDisplayName()));
                p.sendMessage(toMiniMessage("<white>Sinut teleportattiin kotiin</white> <#85FF00>" + e.getCurrentItem().getItemMeta().getDisplayName() + "<#/85FF00>."));
            }else {
                p.closeInventory();
                p.sendMessage("Tuota kotia ei ole olemassa. Mitä duunaat?");
            }
        }else if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
            //close inventory
            if (e.getClick().isKeyboardClick()){
                e.setCancelled(true);
            }else {
                p.closeInventory();
            }
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
        ArrayList<String> homes = new ArrayList<String>(homeManager.getHomes(playerMenuUtility.getOwner()));

        ///////////////////////////////////// Pagination loop template
        if(homes != null && !homes.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= homes.size()) break;
                if (homes.get(index) != null){
                    ///////////////////////////

                    //Create an item from our collection and place it into the inventory
                    ItemStack homeItem = new ItemStack(Material.GREEN_BED, 1);
                    ItemMeta homeMeta = homeItem.getItemMeta();
                    homeMeta.setDisplayName(homeManager.getHomes(playerMenuUtility.getOwner()).get(i));
                    homeMeta.setLore(List.of("§fKlikkaa teleportataksesi"));
                    homeItem.setItemMeta(homeMeta);

                    inventory.addItem(homeItem);

                }
            }
        }
    }

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }
}
