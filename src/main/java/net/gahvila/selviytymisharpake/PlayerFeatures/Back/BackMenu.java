package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.menu.WarpConfirmMenu;
import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.menu.WarpMenu;
import net.gahvila.selviytymisharpake.PlayerWarps.WarpManager;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.gahvila.selviytymisharpake.Utils.Menu;
import net.gahvila.selviytymisharpake.Utils.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class BackMenu extends Menu implements InventoryHolder {

    public BackMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "§2§lBack §0- §8Mitä tehdään?";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem().getType().equals(Material.SKELETON_SKULL)) {
            new BackMenuConfirm(playerMenuUtility).open();
        }else if(e.getCurrentItem().getType().equals(Material.MAP)){
            if (e.getSlot() == 13){
                if (BackManager.getBack(p, 1) != null){
                    p.teleportAsync(BackManager.getBack(p, 1));
                }
            } else if (e.getSlot() == 14){
                if (BackManager.getBack(p, 2) != null){
                    p.teleportAsync(BackManager.getBack(p, 2));
                }
            } else if (e.getSlot() == 15){
                if (BackManager.getBack(p, 3) != null){
                    p.teleportAsync(BackManager.getBack(p, 3));
                }
            } else if (e.getSlot() == 16){
                if (BackManager.getBack(p, 4) != null){
                    p.teleportAsync(BackManager.getBack(p, 4));
                }
            }
            p.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {

        setFillerGlass();

        Player p = playerMenuUtility.getOwner();
        if (BackManager.getDeath(p) != null) {
            ItemStack deathItem = new ItemStack(Material.SKELETON_SKULL, 1);
            ItemMeta deathMeta = deathItem.getItemMeta();
            deathMeta.setDisplayName("§fKuolinsijainti");
            deathMeta.setLore(List.of("§fHinta: §e" + BackManager.calculateDeathPrice(p) + "Ⓖ", "§fKoordinaatit: §e" + BackManager.getXdeath(p) + " §8| §e" + BackManager.getZdeath(p)));
            deathItem.setItemMeta(deathMeta);

            inventory.setItem(10, deathItem);
        }else{
            ItemStack deathItem = new ItemStack(Material.BARRIER, 1);
            ItemMeta deathMeta = deathItem.getItemMeta();
            deathMeta.setDisplayName("§fSijaintia ei ole");
            deathItem.setItemMeta(deathMeta);

            inventory.setItem(10, deathItem);
        }

        //1
        if (BackManager.getBack(p, 1) != null) {
            ItemStack back1 = new ItemStack(Material.MAP, 1);
            ItemMeta back1meta = back1.getItemMeta();

            back1meta.setDisplayName("§f1");
            back1meta.setLore(List.of("§fKoordinaatit: §e" + BackManager.getX(p, 1) + " §8| §e" + BackManager.getZ(p, 1)));
            back1.setItemMeta(back1meta);
            inventory.setItem(13, back1);
        }else {
            ItemStack back1 = new ItemStack(Material.BARRIER, 1);
            ItemMeta back1meta = back1.getItemMeta();

            back1meta.setDisplayName("§fSijaintia ei ole");
            back1.setItemMeta(back1meta);
            inventory.setItem(13, back1);
        }

        //2
        if (BackManager.getBack(p, 2) != null) {
            if (p.hasPermission("gahvilacore.rank.vip")) {
                ItemStack back2 = new ItemStack(Material.MAP, 1);
                ItemMeta back2meta = back2.getItemMeta();

                back2meta.setDisplayName("§f2");
                back2meta.setLore(List.of("§fKoordinaatit: §e" + BackManager.getX(p, 2) + " §8| §e" + BackManager.getZ(p, 2)));
                back2.setItemMeta(back2meta);
                inventory.setItem(14, back2);
            } else {
                ItemStack back2 = new ItemStack(Material.BARRIER, 1);
                ItemMeta back2meta = back2.getItemMeta();

                back2meta.setDisplayName("§fTarvitset §e§lVIP §frankin käyttääksesi tätä");
                back2meta.setLore(List.of("§fKoordinaatit: §e" + BackManager.getX(p, 2) + " §8| §e" + BackManager.getZ(p, 2)));
                back2.setItemMeta(back2meta);
                inventory.setItem(14, back2);
            }
        }else{
            ItemStack back2 = new ItemStack(Material.BARRIER, 1);
            ItemMeta back2meta = back2.getItemMeta();

            back2meta.setDisplayName("§fSijaintia ei ole");
            back2.setItemMeta(back2meta);
            inventory.setItem(14, back2);
        }

        //3
        if (BackManager.getBack(p, 3) != null) {
            if (p.hasPermission("gahvilacore.rank.mvp")) {
                ItemStack back3 = new ItemStack(Material.MAP, 1);
                ItemMeta back3meta = back3.getItemMeta();

                back3meta.setDisplayName("§f3");
                back3meta.setLore(List.of("§fKoordinaatit: §e" + BackManager.getX(p, 3) + " §8| §e" + BackManager.getZ(p, 3)));
                back3.setItemMeta(back3meta);
                inventory.setItem(15, back3);
            } else {
                ItemStack back3 = new ItemStack(Material.BARRIER, 1);
                ItemMeta back3meta = back3.getItemMeta();

                back3meta.setDisplayName("§fTarvitset §6§lMVP §frankin käyttääksesi tätä");
                back3meta.setLore(List.of("§fKoordinaatit: §e" + BackManager.getX(p, 3) + " §8| §e" + BackManager.getZ(p, 3)));
                back3.setItemMeta(back3meta);
                inventory.setItem(15, back3);
            }
        }else{
            ItemStack back3 = new ItemStack(Material.BARRIER, 1);
            ItemMeta back3meta = back3.getItemMeta();

            back3meta.setDisplayName("§fSijaintia ei ole");
            back3.setItemMeta(back3meta);
            inventory.setItem(15, back3);
        }

        //4
        if (BackManager.getBack(p, 4) != null) {
            if (p.hasPermission("gahvilacore.rank.pro")) {
                ItemStack back4 = new ItemStack(Material.MAP, 1);
                ItemMeta back4meta = back4.getItemMeta();

                back4meta.setDisplayName("§f4");
                back4meta.setLore(List.of("§fKoordinaatit: §e" + BackManager.getX(p, 4) + " §8| §e" + BackManager.getZ(p, 4)));
                back4.setItemMeta(back4meta);
                inventory.setItem(16, back4);
            } else {
                ItemStack back4 = new ItemStack(Material.BARRIER, 1);
                ItemMeta back4meta = back4.getItemMeta();

                back4meta.setDisplayName("§fTarvitset §5§lPRO §frankin käyttääksesi tätä");
                back4meta.setLore(List.of("§fKoordinaatit: §e" + BackManager.getX(p, 4) + " §8| §e" + BackManager.getZ(p, 4)));
                back4.setItemMeta(back4meta);
                inventory.setItem(16, back4);
            }
        }else{
            ItemStack back4 = new ItemStack(Material.BARRIER, 1);
            ItemMeta back4meta = back4.getItemMeta();

            back4meta.setDisplayName("§fSijaintia ei ole");
            back4.setItemMeta(back4meta);
            inventory.setItem(16, back4);
        }

    }}