package net.gahvila.survival.Homes;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import net.gahvila.survival.survival;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.MAX_VALUE;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toUndecoratedMM;

public class HomeMenu {
    private final HomeManager homeManager;


    public HomeMenu(HomeManager homeManager) {
        this.homeManager = homeManager;

    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(3, ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Kodit")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAAAAA1",
                "111111111"
        );
        PatternPane border = new PatternPane(0, 0, 9, 3, Pane.Priority.LOWEST, pattern);
        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));
        gui.addPane(border);

        PaginatedPane pages = new PaginatedPane(1, 1, 7, 1);
        List<ItemStack> items = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(survival.instance, "gahvilasurvival");
        for (String home : homeManager.getHomes(player.getUniqueId())) {
            ItemStack item;
            switch (homeManager.getHome(player.getUniqueId(), home).getWorld().getEnvironment()){
                case NETHER -> item = new ItemStack(Material.RED_BED);
                case THE_END -> item = new ItemStack(Material.PURPLE_BED);
                default -> item = new ItemStack(Material.LIME_BED);
            }
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, home);
            meta.displayName(Component.text(home));
            item.setItemMeta(meta);
            items.add(item);
        }
        pages.populateWithItemStacks(items);
        gui.addPane(pages);

        pages.setOnClick(event -> {
            if (event.getCurrentItem() == null) return;
            String homeName = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            Location homeLocation = homeManager.getHome(player.getUniqueId(), homeName);
            if (homeLocation != null){
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, MAX_VALUE, 1F);
                player.teleportAsync(homeLocation);
                player.sendMessage(toMM("<white>Sinut teleportattiin kotiin</white> <#85FF00>" + homeName + "</#85FF00>."));
                Bukkit.getServer().getScheduler().runTaskLater(survival.instance, new Runnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 0.5F, 1F);
                    }
                }, 5);
            }else {
                player.closeInventory();
                player.sendMessage("Tuota kotia ei ole olemassa. Mitä duunaat?");
            }
        });

        StaticPane navigationPane = new StaticPane(0, 2, 9, 1);

        ItemStack previous = new ItemStack(Material.MANGROVE_BUTTON);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(toUndecoratedMM("<b>Takaisin"));
        previous.setItemMeta(previousMeta);
        navigationPane.addItem(new GuiItem(previous, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);

                gui.update();
            }
        }), 3, 0);
        ItemStack next = new ItemStack(Material.WARPED_BUTTON);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(toUndecoratedMM("<b>Seuraava"));
        next.setItemMeta(nextMeta);
        navigationPane.addItem(new GuiItem(next, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                gui.update();
            }
        }), 5, 0);
        gui.addPane(navigationPane);

        gui.update();
    }
}