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

import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Float.MAX_VALUE;

public class WarpMenu {
    private final WarpManager warpManager;


    public WarpMenu(WarpManager warpManager) {
        this.warpManager = warpManager;

    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(5, "§5§lWarpit");
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "111AAA111"
        );
        PatternPane border = new PatternPane(0, 0, 9, 5, Pane.Priority.LOWEST, pattern);
        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toMiniMessage(""));
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));
        gui.addPane(border);

        PaginatedPane pages = new PaginatedPane(1, 1, 7, 3);
        List<ItemStack> items = new ArrayList<>();
        for (Warp warp : warpManager.getWarps()) {
            Date currentTime = new Date(warp.getCreationDate());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);

            ItemStack item = new ItemStack(warp.getCustomItem());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(warp.getName());
            meta.setLore(List.of("§fOmistaja: §e" + warp.getOwnerName(), "§fKäyttökerrat: §e" + warp.getUses(), "§fHinta: §e" + warp.getPrice() + "Ⓖ§f", "§7§o" + dateString));
            item.setItemMeta(meta);
            items.add(item);
        }
        pages.populateWithItemStacks(items);
        gui.addPane(pages);

        pages.setOnClick(event -> {
            if (event.getCurrentItem() == null) return;
            Optional<Warp> warp = warpManager.getWarp(event.getCurrentItem().getItemMeta().getDisplayName());
            if (warp.get().getPrice() == 0){
                player.closeInventory();
                player.teleportAsync(warp.get().getLocation());
                player.sendMessage(toMiniMessage("Sinut teleportattiin warppiin <#85FF00>" + warp.get().getName() + "</#85FF00>."));
                Bukkit.getServer().getScheduler().runTaskLater(SelviytymisHarpake.instance, new Runnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, MAX_VALUE, 1F);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, MAX_VALUE, 1F);
                    }
                }, 5);
                if (!player.getName().equals(warp.get().getOwnerName())){
                    warpManager.addUses(warp.get());
                }
            }else {
                confirmMenu(player, event.getCurrentItem(), warp.get());
            }
        });


        StaticPane navigationPane = new StaticPane(0, 4, 9, 1);

        ItemStack previous = new ItemStack(Material.MANGROVE_BUTTON);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(toMiniMessage("<b>Takaisin"));
        previous.setItemMeta(previousMeta);
        navigationPane.addItem(new GuiItem(previous, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);

                gui.update();
            }
        }), 3, 0);
        ItemStack sorting = new ItemStack(Material.NETHER_STAR);
        ItemMeta sortingMeta = sorting.getItemMeta();
        sortingMeta.displayName(toMiniMessage("<white><b>Järjestys"));
        sortingMeta.lore(List.of(toMiniMessage("<white>Järjestäminen on tulossa pian.")));
        sorting.setItemMeta(sortingMeta);
        navigationPane.addItem(new GuiItem(sorting, event -> {
            player.sendMessage("Tulossa pian.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
        }), 4, 0);
        ItemStack next = new ItemStack(Material.WARPED_BUTTON);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(toMiniMessage("<b>Seuraava"));
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

    private void confirmMenu(Player player, ItemStack item, Warp warp) {
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
            int price = warp.getPrice();

            if (SelviytymisHarpake.getEconomy().getBalance(player) >= price) {
                SelviytymisHarpake.getEconomy().withdrawPlayer(player, price);
                player.sendMessage(toMiniMessage("Tililtäsi veloitettiin <#85FF00>" + price + "Ⓖ</#85FF00>."));
                player.closeInventory();
                player.teleportAsync(warp.getLocation());
                player.sendMessage(toMiniMessage("Sinut teleportattiin warppiin <#85FF00>" + warp.getName() + "</#85FF00>."));

                if (!player.getName().equals(warp.getOwnerName())){
                    warpManager.addUses(warp);
                }
                //
                UUID ownerUUID = warp.getOwner();
                if (Bukkit.getPlayer(ownerUUID) == null){
                    warpManager.addMoneyToQueue(ownerUUID.toString(), price);
                }else{
                    Player owner = Bukkit.getPlayer(ownerUUID);
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