package net.gahvila.survival.Trade;

import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Single;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class TradeSession {

    private Player tradeCreator;
    private Player tradeReceiver;
    private List<ItemStack> creatorItems;
    private List<ItemStack> receiverItems;
    private boolean creatorAccepted = false;
    private boolean receiverAccepted = false;

    public TradeSession(Player tradeCreator, Player tradeReceiver, List<ItemStack> creatorItems, List<ItemStack> receiverItems, boolean creatorAccepted, boolean receiverAccepted) {
        this.tradeCreator = tradeCreator;
        this.tradeReceiver = tradeReceiver;
        this.creatorItems = creatorItems;
        this.receiverItems = receiverItems;
        this.creatorAccepted = creatorAccepted;
        this.receiverAccepted = receiverAccepted;
    }

    public Player getTradeCreator() {
        return tradeCreator;
    }

    public void setTradeCreator(Player tradeCreator) {
        this.tradeCreator = tradeCreator;
    }

    public Player getTradeReceiver() {
        return tradeReceiver;
    }

    public void setTradeReceiver(Player tradeReceiver) {
        this.tradeReceiver = tradeReceiver;
    }

    public List<ItemStack> getCreatorItems() {
        return creatorItems;
    }

    public void setCreatorItems(List<ItemStack> creatorItems) {
        this.creatorItems = creatorItems;
    }

    public List<ItemStack> getReceiverItems() {
        return receiverItems;
    }

    public void setReceiverItems(List<ItemStack> receiverItems) {
        this.receiverItems = receiverItems;
    }

    public boolean isCreatorAccepted() {
        return creatorAccepted;
    }

    public void setCreatorAccepted(boolean creatorAccepted) {
        this.creatorAccepted = creatorAccepted;
    }

    public boolean isReceiverAccepted() {
        return receiverAccepted;
    }

    public void setReceiverAccepted(boolean receiverAccepted) {
        this.receiverAccepted = receiverAccepted;
    }
}
