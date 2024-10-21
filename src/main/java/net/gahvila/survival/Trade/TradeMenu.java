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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    public void openTradeGui(Player player) {
        ChestGui gui = new ChestGui(4, ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Vaihtokauppa")));
        gui.show(player);

        gui.setOnGlobalClick(event -> {
            if (event.getClickedInventory().getHolder() instanceof ChestGui) {
                event.setCancelled(true);
            }
        });

        TradeSession tradeSession = TradeManager.activeTradeSessions.get(player);

        gui.setOnClose(event -> {
            tradeManager.cancelTradeSession(tradeSession);
        });

        Pattern borderPattern = new Pattern(
                "AAAABAAAA",
                "AAAABAAAA",
                "AAAABAAAA",
                "BBBBBBBBB"
        );
        PatternPane border = new PatternPane(0, 0, 9, 4, Pane.Priority.LOWEST, borderPattern);

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


        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        background.setItemMeta(backgroundMeta);

        border.bindItem('B', new GuiItem(background));
        gui.addPane(border);

        StaticPane creatorTradePane = new StaticPane(1, 1, 2, 1);

        ItemStack tradeBundleCreator = new ItemStack(Material.BUNDLE);
        ItemMeta tradeBundleCreatorMeta = tradeBundleCreator.getItemMeta();
        tradeBundleCreatorMeta.displayName(toUndecoratedMM(tradeSession.getTradeCreator().getName() + ":n tavarat"));
        tradeBundleCreator.setItemMeta(tradeBundleCreatorMeta);

        for (HumanEntity viewers : gui.getViewers()){
            Player viewer = (Player) viewers;
            if (tradeSession.getTradeCreator() == viewer) {
                creatorTradePane.addItem(new GuiItem(tradeBundleCreator, event -> {
                    ItemStack item = viewer.getItemOnCursor();
                    viewer.setItemOnCursor(null);

                    tradeSession.getCreatorItems().add(item);
                }), 0, 0);
            } else if (tradeSession.getTradeReceiver() == viewer) {
                creatorTradePane.addItem(new GuiItem(tradeBundleCreator, event -> {
                    //open gui with all the items here
                }), 0, 0);
            }
        }

        gui.addPane(creatorTradePane);

        StaticPane receiverTradePane = new StaticPane(6, 1, 2, 1);

        ItemStack tradeBundleReceiver = new ItemStack(Material.BUNDLE);
        ItemMeta tradeBundleReceiverMeta = tradeBundleReceiver.getItemMeta();
        tradeBundleReceiverMeta.displayName(toUndecoratedMM(tradeSession.getTradeReceiver().getName() + ":n tavarat"));
        tradeBundleReceiver.setItemMeta(tradeBundleReceiverMeta);

        for (HumanEntity viewers : gui.getViewers()){
            Player viewer = (Player) viewers;
            if (tradeSession.getTradeReceiver() == viewer) {
                receiverTradePane.addItem(new GuiItem(tradeBundleReceiver, event -> {
                    ItemStack item = viewer.getItemOnCursor();
                    viewer.setItemOnCursor(null);

                    tradeSession.getReceiverItems().add(item);
                }), 1, 0);
            } else if (tradeSession.getTradeCreator() == viewer) {
                receiverTradePane.addItem(new GuiItem(tradeBundleReceiver, event -> {
                    //open gui with all the items here
                }), 1, 0);
            }
        }

        gui.addPane(receiverTradePane);

        StaticPane navigationPane = new StaticPane(0, 3, 9, 1);

        // Cancel button
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(toUndecoratedMM("<#ff0000><b>Peru vaihtokauppa"));
        cancel.setItemMeta(cancelMeta);
        for (HumanEntity viewers : gui.getViewers()){
            Player viewer = (Player) viewers;
            if (tradeSession.getTradeCreator() == viewer) {
                navigationPane.addItem(new GuiItem(cancel, event -> {
                    tradeManager.cancelTradeSession(tradeSession);
                    player.sendMessage("Peruit vaihtokaupan.");
                }), 0, 0);
            } else if (tradeSession.getTradeReceiver() == viewer) {
                navigationPane.addItem(new GuiItem(cancel, event -> {
                    tradeManager.cancelTradeSession(tradeSession);
                    viewer.sendMessage("Peruit vaihtokaupan.");
                }), 8, 0);
            }
        }

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

        for (HumanEntity viewers : gui.getViewers()) {
            Player viewer = (Player) viewers;
            if (tradeSession.getTradeCreator() == viewer) {
                navigationPane.addItem(new GuiItem(confirm, event -> {
                    tradeManager.acceptTradeSessionCreator(player);
                    player.sendMessage("Hyväksyit vaihtokaupan.");
                }), 3, 0);
            } else if (tradeSession.getTradeReceiver() == viewer) {
                navigationPane.addItem(new GuiItem(confirm, event -> {
                    tradeManager.acceptTradeSessionReceiver(player);
                    player.sendMessage("Hyväksyit vaihtokaupan.");
                }), 5, 0);
            }
        }
        gui.addPane(navigationPane);
        gui.update();
    }
}
