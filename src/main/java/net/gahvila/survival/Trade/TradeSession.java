package net.gahvila.survival.Trade;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TradeSession {

    private long tradeId;
    private long creationTime;
    private Player trader1;
    private Player trader2;
    private Gui trader1Gui;
    private Gui trader2Gui;
    private OutlinePane trader1OwnPane;
    private OutlinePane trader1PartnerPane;
    private OutlinePane trader2OwnPane;
    private OutlinePane trader2PartnerPane;
    private List<ItemStack> trader1Items;
    private List<ItemStack> trader2Items;
    private boolean trader1Accepted;
    private boolean trader2Accepted;

    public TradeSession(long tradeId, long creationTime, Player trader1, Player trader2, Gui trader1Gui, Gui trader2Gui, OutlinePane trader1OwnPane, OutlinePane trader1PartnerPane, OutlinePane trader2OwnPane, OutlinePane trader2PartnerPane, List<ItemStack> trader1Items, List<ItemStack> trader2Items, boolean trader1Accepted, boolean trader2Accepted) {
        this.tradeId = tradeId;
        this.creationTime = creationTime;
        this.trader1 = trader1;
        this.trader2 = trader2;
        this.trader1Gui = trader1Gui;
        this.trader2Gui = trader2Gui;
        this.trader1OwnPane = trader1OwnPane;
        this.trader1PartnerPane = trader1PartnerPane;
        this.trader2OwnPane = trader2OwnPane;
        this.trader2PartnerPane = trader2PartnerPane;
        this.trader1Items = trader1Items;
        this.trader2Items = trader2Items;
        this.trader1Accepted = trader1Accepted;
        this.trader2Accepted = trader2Accepted;
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public Player getTrader1() {
        return trader1;
    }

    public void setTrader1(Player trader1) {
        this.trader1 = trader1;
    }

    public Player getTrader2() {
        return trader2;
    }

    public void setTrader2(Player trader2) {
        this.trader2 = trader2;
    }

    public Gui getTrader1Gui() {
        return trader1Gui;
    }

    public void setTrader1Gui(Gui trader1Gui) {
        this.trader1Gui = trader1Gui;
    }

    public Gui getTrader2Gui() {
        return trader2Gui;
    }

    public void setTrader2Gui(Gui trader2Gui) {
        this.trader2Gui = trader2Gui;
    }


    public OutlinePane getTrader1PartnerPane() {
        return trader1PartnerPane;
    }

    public void setTrader1PartnerPane(OutlinePane trader1PartnerPane) {
        this.trader1PartnerPane = trader1PartnerPane;
    }

    public OutlinePane getTrader1OwnPane() {
        return trader1OwnPane;
    }

    public void setTrader1OwnPane(OutlinePane trader1OwnPane) {
        this.trader1OwnPane = trader1OwnPane;
    }

    public OutlinePane getTrader2OwnPane() {
        return trader2OwnPane;
    }

    public void setTrader2OwnPane(OutlinePane trader2OwnPane) {
        this.trader2OwnPane = trader2OwnPane;
    }

    public OutlinePane getTrader2PartnerPane() {
        return trader2PartnerPane;
    }

    public void setTrader2PartnerPane(OutlinePane trader2PartnerPane) {
        this.trader2PartnerPane = trader2PartnerPane;
    }


    public List<ItemStack> getTrader1Items() {
        return trader1Items;
    }

    public void setTrader1Items(List<ItemStack> trader1Items) {
        this.trader1Items = trader1Items;
    }

    public List<ItemStack> getTrader2Items() {
        return trader2Items;
    }

    public void setTrader2Items(List<ItemStack> trader2Items) {
        this.trader2Items = trader2Items;
    }

    public boolean isTrader1Accepted() {
        return trader1Accepted;
    }

    public void setTrader1Accepted(boolean trader1Accepted) {
        this.trader1Accepted = trader1Accepted;
    }

    public boolean isTrader2Accepted() {
        return trader2Accepted;
    }

    public void setTrader2Accepted(boolean trader2Accepted) {
        this.trader2Accepted = trader2Accepted;
    }
}
