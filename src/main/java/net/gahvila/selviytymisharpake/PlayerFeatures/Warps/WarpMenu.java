package net.gahvila.selviytymisharpake.PlayerFeatures.Warps;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.*;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpMenu {
    private final WarpManager warpManager;


    public WarpMenu(WarpManager warpManager) {
        this.warpManager = warpManager;

    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(3, "§5§lWarpit");
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
        for (String warp : warpManager.getWarps()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(warp);
            item.setItemMeta(meta);
            items.add(item);
        }
        pages.populateWithItemStacks(items);

        pages.setOnClick(event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1F);
            confirmMenu(player, event.getCurrentItem(), event.getCurrentItem().getItemMeta().getDisplayName());
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

    private void confirmMenu(Player player, ItemStack item, String warpName) {
        ChestGui gui = new ChestGui(3, "§4§lVarmista osto");
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAICCC1",
                "111111111"
        );

        PatternPane pane = new PatternPane(0, 0, 9, 3, pattern);

        ItemStack accept = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta acceptMeta = accept.getItemMeta();
        acceptMeta.displayName(toMiniMessage("<green><b>Hyväksy"));
        accept.setItemMeta(acceptMeta);

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(toMiniMessage("<red><b>Hylkää"));
        cancel.setItemMeta(cancelMeta);

        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toMiniMessage(""));
        background.setItemMeta(backgroundMeta);

        pane.bindItem('1', new GuiItem(background));
        pane.bindItem('I', new GuiItem(item));

        pane.bindItem('A', new GuiItem(accept, event -> {
            player.closeInventory();
            int price = warpManager.getWarpPrice(warpName);

            if (SelviytymisHarpake.getEconomy().getBalance(player) >= price) {
                SelviytymisHarpake.getEconomy().withdrawPlayer(player, price);
                player.sendMessage(toMiniMessage("Tililtäsi veloitettiin <#85FF00>" + price + "Ⓖ</#85FF00>."));
                player.closeInventory();
                player.teleportAsync(warpManager.getWarp(warpName));
                player.sendMessage(toMiniMessage("Sinut teleportattiin warppiin <#85FF00>" + warpName + "</#85FF00>."));

                if (!player.getName().equals(warpManager.getWarpOwnerName(warpName))){
                    warpManager.addUses(warpName);
                }
                //
                String ownerUUID = warpManager.getWarpOwnerUUID(warpName);
                if (Bukkit.getPlayer(UUID.fromString(ownerUUID)) == null){
                    warpManager.addMoneyToQueue(warpManager.getWarpOwnerUUID(warpName), price);
                }else{
                    Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUID));
                    SelviytymisHarpake.getEconomy().depositPlayer(owner, price);
                    owner.sendMessage("Sinun maksullista warppia käytettiin, sait §e" + price + "Ⓖ§f.");
                }
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                player.sendMessage(toMiniMessage("Nyt kyllä loppu hilut kesken, tarviit <#85FF00>" + price + "Ⓖ</#85FF00> käyttääksesi tätä warppia."));
                player.closeInventory();
            }
        }));

        pane.bindItem('C', new GuiItem(cancel, event -> {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
            showGUI(player);
        }));

        gui.addPane(pane);

        gui.update();
    }

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}