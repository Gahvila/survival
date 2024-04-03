package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeMenu {
    private final HomeManager homeManager;


    public HomeMenu(HomeManager homeManager) {
        this.homeManager = homeManager;

    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(3, "§5§lKodit");
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 5, Pane.Priority.LOWEST);
        ItemStack backgroundItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.displayName(toMiniMessage(""));
        backgroundItem.setItemMeta(backgroundItemMeta);
        background.addItem(new GuiItem(backgroundItem));
        background.setRepeat(true);
        gui.addPane(background);

        PaginatedPane pages = new PaginatedPane(1, 1, 7, 3);
        List<ItemStack> items = new ArrayList<>();
        for (String warp : homeManager.getHomes(player)) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(warp);
            item.setItemMeta(meta);
            items.add(item);
        }
        pages.populateWithItemStacks(items);

        pages.setOnClick(event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1F);
            if (homeManager.getHome(player, event.getCurrentItem().getItemMeta().getDisplayName()) != null){
                player.teleportAsync(homeManager.getHome(player, event.getCurrentItem().getItemMeta().getDisplayName()));
                player.sendMessage(toMiniMessage("<white>Sinut teleportattiin kotiin</white> <#85FF00>" + event.getCurrentItem().getItemMeta().getDisplayName() + "<#/85FF00>."));
            }else {
                player.closeInventory();
                player.sendMessage("Tuota kotia ei ole olemassa. Mitä duunaat?");
            }
        });

        StaticPane navigationPane = new StaticPane(0, 5, 9, 1);
        navigationPane.addItem(new GuiItem(new ItemStack(Material.RED_WOOL), event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);

                gui.update();
            }
        }), 0, 0);
        navigationPane.addItem(new GuiItem(new ItemStack(Material.GREEN_WOOL), event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);

                gui.update();
            }
        }), 8, 0);
        navigationPane.addItem(new GuiItem(new ItemStack(Material.BARRIER), event ->
                event.getWhoClicked().closeInventory()), 4, 0);
        gui.addPane(navigationPane);

        gui.update();
    }

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}