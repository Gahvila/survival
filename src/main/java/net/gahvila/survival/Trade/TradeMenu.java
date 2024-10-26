package net.gahvila.survival.Trade;

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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Float.MAX_VALUE;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toUndecoratedMM;

public class TradeMenu {
    private final TradeManager tradeManager;

    public TradeMenu(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    public void openTradeGui(Player creator, Player receiver) {
        ChestGui gui = new ChestGui(3, ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Vaihtokauppa")));
        gui.show(creator);
        gui.show(receiver);

        gui.setOnGlobalClick(event -> {
            if (event.getClickedInventory().getHolder() instanceof ChestGui) {
                event.setCancelled(true);
            }
        });

        TradeSession tradeSession = TradeManager.activeTradeSessions.get(creator);

        Pattern borderPattern = new Pattern(
                "AAAABAAAA",
                "AAAABAAAA",
                "AAAABAAAA"
        );
        PatternPane border = new PatternPane(0, 0, 9, 3, Pane.Priority.LOWEST, borderPattern);

        Pattern unAcceptedPattern = new Pattern(
                "RRRR",
                "RAAR",
                "RRRR"
        );

        Pattern acceptedPattern = new Pattern(
                "GGGG",
                "GAAG",
                "GGGG"
        );

        ItemStack redGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redGlassMeta = redGlass.getItemMeta();
        redGlassMeta.displayName(toUndecoratedMM(""));
        redGlassMeta.setHideTooltip(true);
        redGlass.setItemMeta(redGlassMeta);

        PatternPane creatorBorder = new PatternPane(0, 0, 4, 3, Pane.Priority.LOWEST, unAcceptedPattern);
        creatorBorder.bindItem('R', new GuiItem(redGlass));
        PatternPane receiverBorder = new PatternPane(5, 0, 4, 3, Pane.Priority.LOWEST, unAcceptedPattern);
        receiverBorder.bindItem('R', new GuiItem(redGlass));

        gui.addPane(creatorBorder);
        gui.addPane(receiverBorder);

        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        backgroundMeta.setHideTooltip(true);
        background.setItemMeta(backgroundMeta);

        border.bindItem('B', new GuiItem(background));
        gui.addPane(border);

        StaticPane creatorTradePane = new StaticPane(1, 1, 2, 1);

        ItemStack creatorSkull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta creatorSkullMeta = (SkullMeta) creatorSkull.getItemMeta();
        creatorSkullMeta.setOwningPlayer(tradeSession.getTradeCreator());
        creatorSkull.setItemMeta(creatorSkullMeta);

        ItemStack tradeBundleCreator = new ItemStack(Material.BUNDLE);
        BundleMeta tradeBundleCreatorMeta = (BundleMeta) tradeBundleCreator.getItemMeta();
        tradeBundleCreatorMeta.displayName(toUndecoratedMM(tradeSession.getTradeCreator().getName() + ":n tavarat"));
        for (ItemStack item : tradeSession.getCreatorItems()) tradeBundleCreatorMeta.addItem(item);
        tradeBundleCreator.setItemMeta(tradeBundleCreatorMeta);

        creatorTradePane.addItem(new GuiItem(creatorSkull), 1, 0);

        creatorTradePane.addItem(new GuiItem(tradeBundleCreator, event -> {
            Player clicker = (Player) event.getWhoClicked();
            if (tradeSession.getTradeCreator() == clicker) {
                ItemStack item = clicker.getItemOnCursor();
                clicker.setItemOnCursor(null);

                if (item.getType() != Material.AIR) {
                    tradeManager.addItemToTradeSession(clicker, tradeSession, item);
                    tradeBundleCreatorMeta.addItem(item);
                    tradeBundleCreator.setItemMeta(tradeBundleCreatorMeta);

                    gui.update();
                }
                gui.update();
            }
        }), 0, 0);

        gui.addPane(creatorTradePane);

        StaticPane receiverTradePane = new StaticPane(6, 1, 2, 1);

        ItemStack receiverSkull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta receiverSkullMeta = (SkullMeta) receiverSkull.getItemMeta();
        receiverSkullMeta.setOwningPlayer(tradeSession.getTradeReceiver());
        receiverSkull.setItemMeta(receiverSkullMeta);

        ItemStack tradeBundleReceiver = new ItemStack(Material.BUNDLE);
        BundleMeta tradeBundleReceiverMeta = (BundleMeta) tradeBundleReceiver.getItemMeta();
        tradeBundleReceiverMeta.displayName(toUndecoratedMM(tradeSession.getTradeReceiver().getName() + ":n tavarat"));
        for (ItemStack item : tradeSession.getReceiverItems()) tradeBundleReceiverMeta.addItem(item);
        tradeBundleReceiver.setItemMeta(tradeBundleReceiverMeta);

        receiverTradePane.addItem(new GuiItem(receiverSkull), 0, 0);

        receiverTradePane.addItem(new GuiItem(tradeBundleReceiver, event -> {
            Player clicker = (Player) event.getWhoClicked();
            if (clicker == tradeSession.getTradeReceiver()) {
                ItemStack item = clicker.getItemOnCursor();
                clicker.setItemOnCursor(null);

                if (item.getType() != Material.AIR) {
                    tradeManager.addItemToTradeSession(clicker, tradeSession, item);
                    tradeBundleReceiverMeta.addItem(item);
                    tradeBundleReceiver.setItemMeta(tradeBundleReceiverMeta);
                }

                gui.update();
            }
        }), 1, 0);

        gui.addPane(receiverTradePane);

        StaticPane navigationPane = new StaticPane(4, 0, 1, 3);

        // Cancel button
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(toUndecoratedMM("<#ff0000><b>Peru vaihtokauppa"));
        cancel.setItemMeta(cancelMeta);
        navigationPane.addItem(new GuiItem(cancel, event -> {
            tradeManager.cancelTradeSession(tradeSession);
        }), 0, 2);

        // Confirm button

        ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.displayName(toUndecoratedMM("<green><b>Hyväksy vaihtokauppa"));
        confirmMeta.lore(List.of(
                toUndecoratedMM("<white>Kun hyväksyt vaihtokaupan, vaihtokauppa lukitaan sinulta.</white>"),
                toUndecoratedMM("<white>Jos vaihtokaupan toinen osapuoli vaihtaa tavaroita, hyväksyminen poistuu."),
                toUndecoratedMM("<white>Hyväksy vaihtokauppa jatkaaksesi.")
        ));
        confirm.setItemMeta(confirmMeta);

        navigationPane.addItem(new GuiItem(confirm, event -> {
            Player clicker = (Player) event.getWhoClicked();

            if (tradeSession.getTradeCreator() == clicker) {
                tradeManager.acceptTradeSessionCreator(clicker);
            } else if (tradeSession.getTradeReceiver() == clicker) {
                tradeManager.acceptTradeSessionReceiver(clicker);
            }
            clicker.sendMessage("Hyväksyit vaihtokaupan.");
            gui.update();
        }), 0, 0);

        gui.addPane(navigationPane);
        gui.update();
    }
}
