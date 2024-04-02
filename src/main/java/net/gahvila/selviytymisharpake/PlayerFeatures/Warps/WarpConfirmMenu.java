package net.gahvila.selviytymisharpake.PlayerFeatures.Warps;

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
    private final WarpManager warpManager;

    public WarpConfirmMenu(PlayerMenuUtility playerMenuUtility, WarpManager warpManager) {
        super(playerMenuUtility);
        this.warpManager = warpManager;
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
        Integer price = warpManager.getWarpPrice(playerMenuUtility.getWarpToTeleport());
        Player p = (Player) e.getWhoClicked();
        switch (e.getCurrentItem().getType()){
            case GREEN_STAINED_GLASS_PANE:
                if (SelviytymisHarpake.getEconomy().getBalance(p) >= price) {
                    SelviytymisHarpake.getEconomy().withdrawPlayer(p, price);
                    p.sendMessage("Tililtäsi veloitettiin §e" + price + "Ⓖ§f.");
                    p.closeInventory();
                    p.teleportAsync(warpManager.getWarp(playerMenuUtility.getWarpToTeleport()));
                    p.sendMessage("Sinut teleportattiin warppiin §e" + playerMenuUtility.getWarpToTeleport() + "§f.");

                    if (!p.getName().equals(warpManager.getWarpOwnerName(e.getCurrentItem().getItemMeta().getDisplayName()))){
                        warpManager.addUses(playerMenuUtility.getWarpToTeleport());
                    }
                    //
                    String ownerUUID = warpManager.getWarpOwnerUUID(playerMenuUtility.getWarpToTeleport());
                    if (Bukkit.getPlayer(UUID.fromString(ownerUUID)) == null){
                        warpManager.addMoneyToQueue(warpManager.getWarpOwnerUUID(playerMenuUtility.getWarpToTeleport()), price);
                    }else{
                        Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUID));
                        SelviytymisHarpake.getEconomy().depositPlayer(owner, price);
                        owner.sendMessage("Sinun maksullista warppia käytettiin, sait §e" + price + "Ⓖ§f.");
                    }
                }else{
                    p.closeInventory();
                    p.sendMessage("Sinulla ei ole tarpeeksi Ⓖ!");
                }
                break;
            case RED_STAINED_GLASS_PANE:

                //go back to the previous menu
                new WarpMenu(playerMenuUtility, warpManager).open();

                break;
        }

    }

    @Override
    public void setMenuItems() {
        Integer price = warpManager.getWarpPrice(playerMenuUtility.getWarpToTeleport());
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
