package net.gahvila.survival.Trade;

import net.gahvila.inventoryframework.adventuresupport.ComponentHolder;
import net.gahvila.inventoryframework.gui.GuiItem;
import net.gahvila.inventoryframework.gui.type.ChestGui;
import net.gahvila.inventoryframework.pane.OutlinePane;
import net.gahvila.inventoryframework.pane.Pane;
import net.gahvila.inventoryframework.pane.PatternPane;
import net.gahvila.inventoryframework.pane.util.Pattern;
import org.bukkit.Bukkit;
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
        ChestGui gui = new ChestGui(6, ComponentHolder.of(toUndecoratedMM("Omat tavarat <| |> Heidän tavarat")));
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
            skullMeta.displayName(toUndecoratedMM("<white>Teet vaihtokauppaa <yellow>" + tradeSession.getTrader1().getName() + ":n</yellow> <white>kanssa."));
        } else if (player != tradeSession.getTrader2()) {
            skullMeta.setOwningPlayer(tradeSession.getTrader2());
            skullMeta.displayName(toUndecoratedMM("<white>Teet vaihtokauppaa <yellow>" + tradeSession.getTrader2().getName() + ":n</yellow> <white>kanssa."));
        }
        skull.setItemMeta(skullMeta);
        border.bindItem('P', new GuiItem(skull));

        gui.addPane(border);

        OutlinePane ownItems = new OutlinePane(0, 1, 4, 5);
        OutlinePane partnerItems = new OutlinePane(5, 1, 4, 5);

        ownItems.setOnClick(event -> {
            if (player.getItemOnCursor().getType() != Material.AIR) {
                tradeSessionManager.addItemToTrade(tradeSession, player, player.getItemOnCursor());
                player.setItemOnCursor(new ItemStack(Material.AIR));
            }else if (event.getCurrentItem() != null) {
                ItemStack item = event.getCurrentItem();
                tradeSessionManager.removeItemFromTrade(tradeSession, player, item);
                player.setItemOnCursor(item);
            }
            updateBothGuis(tradeSession);
        });

        if (player == tradeSession.getTrader1()) {
            tradeSession.setTrader1OwnPane(ownItems);
            tradeSession.setTrader1PartnerPane(partnerItems);
        } else if (player == tradeSession.getTrader2()) {
            tradeSession.setTrader2OwnPane(ownItems);
            tradeSession.setTrader2PartnerPane(partnerItems);
        }

        gui.addPane(ownItems);
        gui.addPane(partnerItems);

        gui.update();
    }

    private void updateBothGuis(TradeSession tradeSession) {
        // Trader 1
        OutlinePane trader1OwnPane = tradeSession.getTrader1OwnPane();
        OutlinePane trader1PartnerPane = tradeSession.getTrader1PartnerPane();
        trader1OwnPane.clear();
        trader1PartnerPane.clear();
        for (ItemStack item : tradeSession.getTrader1Items()) {
            trader1OwnPane.addItem(new GuiItem(item));
        }
        for (ItemStack item : tradeSession.getTrader2Items()) {
            trader1PartnerPane.addItem(new GuiItem(item));
        }
        tradeSession.getTrader1Gui().update();

        // Trader 2
        OutlinePane trader2OwnPane = tradeSession.getTrader2OwnPane();
        OutlinePane trader2PartnerPane = tradeSession.getTrader2PartnerPane();
        trader2OwnPane.clear();
        trader2PartnerPane.clear();
        for (ItemStack item : tradeSession.getTrader2Items()) {
            trader2OwnPane.addItem(new GuiItem(item));
        }
        for (ItemStack item : tradeSession.getTrader1Items()) {
            trader2PartnerPane.addItem(new GuiItem(item));
        }
        tradeSession.getTrader2Gui().update();
    }
}
