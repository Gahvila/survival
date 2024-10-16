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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.MAX_VALUE;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toUndecoratedMM;

public class TradeMenu {

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(4, ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Kodit")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));
        //
        //ui drawing
        //

        //A=border of trade green/red
        //B=black glass, border
        //C=trade section (player head and bundle)
        //1=cancel trade
        //2=confirm/unconfirm trade button
        Pattern pattern = new Pattern(
                "AAAABAAAA",
                "ACCABACCA",
                "AAAABAAAA",
                "1BB2BBBBB"
        );
        PatternPane border = new PatternPane(0, 0, 9, 3, Pane.Priority.LOWEST, pattern);

        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        background.setItemMeta(backgroundMeta);

        border.bindItem('B', new GuiItem(background));
        gui.addPane(border);


        StaticPane navigationPane = new StaticPane(0, 3, 9, 1);

        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(toUndecoratedMM("<#ff0000><b>Peru vaihtokauppa"));
        cancel.setItemMeta(cancelMeta);
        navigationPane.addItem(new GuiItem(cancel, event -> {
            //fff
        }), 0, 0);
        gui.addPane(navigationPane);

        gui.update();
    }
}
