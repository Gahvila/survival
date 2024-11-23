package net.gahvila.survival.Trade;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toUndecoratedMM;

public class TradeMenu {
    private final TradeManager tradeManager;
    private TradeSessionManager tradeSessionManager;

    public TradeMenu(TradeManager tradeManager, TradeSessionManager tradeSessionManager) {
        this.tradeManager = tradeManager;
        this.tradeSessionManager = tradeSessionManager;
    }

    public void setTradeSessionManager(TradeSessionManager tradeSessionManager) {
        this.tradeSessionManager = tradeSessionManager;
    }

    private final Pattern pattern = new Pattern(
            "1111P1111",
            "AAAA1AAAA",
            "AAAA1AAAA",
            "AAAA1AAAA",
            "AAAA1AAAA",
            "AAAA1AAAA"
    );

    public void openTradeGui(Player player, TradeSession tradeSession) {
        ChestGui gui = new ChestGui(6, ComponentHolder.of(toUndecoratedMM(" Omat tavarat <||> Heidän tavarat")));
        gui.show(player);

        gui.setOnGlobalClick(event -> {
            if (event.getClickedInventory().getHolder() instanceof ChestGui) {
                event.setCancelled(true);
            }
        });

        //add gui to tradesession
        if (player == tradeSession.getTrader1()) {
            tradeSession.setTrader1Gui(gui);
        } else if (player == tradeSession.getTrader2()) {
            tradeSession.setTrader2Gui(gui);
        }

        PatternPane border = new PatternPane(0, 0, 9, 6, Pane.Priority.LOWEST, pattern);

        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setHideTooltip(true);
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (player != tradeSession.getTrader1()) {
            skullMeta.setOwningPlayer(tradeSession.getTrader1());
            skullMeta.displayName(toUndecoratedMM("<white>Teet vaihtokauppaa <yellow>" + tradeSession.getTrader1() + ":n</yellow> <white>kanssa."));
        } else if (player != tradeSession.getTrader2()) {
            skullMeta.setOwningPlayer(tradeSession.getTrader2());
            skullMeta.displayName(toUndecoratedMM("<white>Teet vaihtokauppaa <yellow>" + tradeSession.getTrader2() + ":n</yellow> <white>kanssa."));
        }
        skull.setItemMeta(skullMeta);
        border.bindItem('P', new GuiItem(skull));

        gui.addPane(border);


        OutlinePane ownItems = new OutlinePane(0, 1, 4, 5);
        //WHEN PLAYER IS PLAYER1
        if (player == tradeSession.getTrader1()) {
            for (ItemStack item : tradeSession.getTrader1Items()) {
                ownItems.addItem(new GuiItem(item));
            }
        //WHEN PLAYER IS PLAYER2
        } else if (player == tradeSession.getTrader2()) {
            for (ItemStack item : tradeSession.getTrader2Items()) {
                ownItems.addItem(new GuiItem(item));
            }
        }

        ownItems.setOnClick(event -> {
            if (player.getItemOnCursor().getType() != Material.AIR) {
                tradeSessionManager.addItemToTrade(tradeSession, player, player.getItemOnCursor());
                player.setItemOnCursor(new ItemStack(Material.AIR));
                ownItems.addItem(new GuiItem(player.getItemOnCursor()));
            } else if (event.getCurrentItem() != null) {
                tradeSessionManager.removeItemFromTrade(tradeSession, player, event.getCurrentItem());
                ownItems.removeItem(new GuiItem(event.getCurrentItem()));
            }
        });

        OutlinePane partnerItems = new OutlinePane(5, 1, 4, 5);
        //WHEN PLAYER IS PLAYER1
        if (player == tradeSession.getTrader1()) {
            for (ItemStack item : tradeSession.getTrader2Items()) {
                partnerItems.addItem(new GuiItem(item));
            }
            //WHEN PLAYER IS PLAYER2
        } else if (player == tradeSession.getTrader2()) {
            for (ItemStack item : tradeSession.getTrader1Items()) {
                partnerItems.addItem(new GuiItem(item));
            }
        }

        gui.update();
    }
}
