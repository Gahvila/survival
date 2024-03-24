package net.gahvila.selviytymisharpake.PlayerFeatures.Addons;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddonMenu {

    private final AddonManager addonManager;


    public AddonMenu(AddonManager addonManager) {
        this.addonManager = addonManager;
    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(3, "§5§lLisäosat");
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);

        ItemStack backgroundItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.displayName(toMiniMessage(""));
        backgroundItem.setItemMeta(backgroundItemMeta);

        background.addItem(new GuiItem(backgroundItem));

        background.setRepeat(true);

        gui.addPane(background);

        OutlinePane navigationPane = new OutlinePane(2, 1, 5, 1);

        ItemStack craft = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta craftMeta = craft.getItemMeta();
        craftMeta.displayName(toMiniMessage("<white><b>Craft</b> <#85FF00>1000Ⓖ"));
        craftMeta.lore(List.of(toMiniMessage("<white>Antaa oikeudet <#85FF00>/craft <white>komentoon, "), toMiniMessage("<white>jolla voit craftata missä vain.")));
        craft.setItemMeta(craftMeta);

        navigationPane.addItem(new GuiItem(craft, event -> {
            if (!addonManager.getAddon(player, "craft")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1F);
                confirmMenu(player, craft, "craft");
            }else{
                player.sendMessage("Sinulla on jo tuo lisäosa.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                craft.setType(Material.BARRIER);
                gui.update();
            }
        }));

        ItemStack enderchest = new ItemStack(Material.ENDER_CHEST);
        ItemMeta enderchestMeta = enderchest.getItemMeta();
        enderchestMeta.displayName(toMiniMessage("<white><b>Ender Chest</b> <#85FF00>1500Ⓖ"));
        enderchestMeta.lore(List.of(toMiniMessage("<white>Antaa oikeudet <#85FF00>/ec <white>komentoon, "), toMiniMessage("<white>jolla voit avata enderchestin missä vain.")));
        enderchest.setItemMeta(enderchestMeta);

        navigationPane.addItem(new GuiItem(enderchest, event -> {
            if (!addonManager.getAddon(player, "enderchest")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1F);
                confirmMenu(player, enderchest, "enderchest");
            }else{
                player.sendMessage("Sinulla on jo tuo lisäosa.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                enderchest.setType(Material.BARRIER);
                gui.update();
            }
        }));

        ItemStack feed = new ItemStack(Material.COOKED_BEEF);
        ItemMeta feedMeta = feed.getItemMeta();
        feedMeta.displayName(toMiniMessage("<white><b>Feed</b> <#85FF00>1500Ⓖ"));
        feedMeta.lore(List.of(toMiniMessage("<white>Antaa oikeudet <#85FF00>/feed <white>komentoon, "), toMiniMessage("<white>jolla voit täyttää ruokapalkkisi minuutin välein.")));
        feed.setItemMeta(feedMeta);

        navigationPane.addItem(new GuiItem(feed, event -> {
            if (!addonManager.getAddon(player, "feed")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1F);
                confirmMenu(player, feed, "feed");
            }else{
                player.sendMessage("Sinulla on jo tuo lisäosa.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                feed.setType(Material.BARRIER);
                gui.update();
            }
        }));

        ItemStack shop = new ItemStack(Material.CHEST);
        ItemMeta shopMeta = shop.getItemMeta();
        shopMeta.displayName(toMiniMessage("<white><b>Kauppa</b> <#85FF00>1500Ⓖ"));
        shopMeta.lore(List.of(toMiniMessage("<white>Antaa oikeudet <#85FF00>/kauppa <white>komentoon, "), toMiniMessage("<white>jolla voit avata kaupan valikon missä vain.")));
        shop.setItemMeta(shopMeta);

        navigationPane.addItem(new GuiItem(shop, event -> {
            if (!addonManager.getAddon(player, "shop")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1F);
                confirmMenu(player, shop, "shop");
            }else{
                player.sendMessage("Sinulla on jo tuo lisäosa.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                shop.setType(Material.BARRIER);
                gui.update();
            }
        }));

        ItemStack coming = new ItemStack(Material.BARRIER);
        ItemMeta comingMeta = coming.getItemMeta();
        comingMeta.displayName(toMiniMessage("<red><b>???"));
        coming.setItemMeta(comingMeta);

        navigationPane.addItem(new GuiItem(coming));


        gui.addPane(navigationPane);

        gui.update();
    }

    private void confirmMenu(Player player, ItemStack item, String addon) {
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
        pane.bindItem('I', new GuiItem(background));

        pane.bindItem('A', new GuiItem(accept, event -> {
            player.closeInventory();

            Integer price = addonManager.getPrice(addon);

            if (SelviytymisHarpake.getEconomy().getBalance(player) >= price) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1F);
                SelviytymisHarpake.getEconomy().withdrawPlayer(player, 1000);
                player.sendMessage("Jee! Ostit "+ addon +" päivityksen.");
                addonManager.setAddon(player, addon);
                player.closeInventory();
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                player.sendMessage("Sinulla ei ole tarpeeksi rahaa! Tarvitset " + price + "Ⓖ.");
                showGUI(player);
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
