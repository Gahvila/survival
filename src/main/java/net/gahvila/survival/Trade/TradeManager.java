package net.gahvila.survival.Trade;

import de.leonhard.storage.Json;
import net.gahvila.survival.survival;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class TradeManager {

    public static HashMap<Player, TradeSession> activeTradeSessions = new HashMap<>();
    public static HashMap<Player, Player> tradeRequest = new HashMap<>(); //receiver, sender
    public static HashMap<Player, Player> latestTrader = new HashMap<>(); //receiver, sender
    public static NamespacedKey key = new NamespacedKey(instance, "tradeBundle");


    public void createTradeSession(Player tradeSender, Player tradeReceiver) {
        if (activeTradeSessions.containsKey(tradeSender) || activeTradeSessions.containsKey(tradeReceiver)) {
            return;
        }

        TradeSession tradeSession = new TradeSession(tradeSender, tradeReceiver, new ArrayList<>(), new ArrayList<>(), false, false);
        activeTradeSessions.put(tradeSender, tradeSession);
        activeTradeSessions.put(tradeReceiver, tradeSession);

        survival.instance.getLogger().info("Trade session created between " + tradeSender.getName() + " and " + tradeReceiver.getName());
    }

    public void cancelTradeSession(TradeSession tradeSession) {
        if (tradeSession != null) {
            Player tradeCreator = tradeSession.getTradeCreator();
            Player tradeReceiver = tradeSession.getTradeReceiver();
            activeTradeSessions.remove(tradeCreator);
            activeTradeSessions.remove(tradeReceiver);

            tradeCreator.closeInventory();
            tradeReceiver.closeInventory();

            survival.instance.getLogger().info("Trade session canceled between " + tradeCreator.getName() + " and " + tradeReceiver.getName());
        } else {
            survival.instance.getLogger().warning("Attempted to cancel an invalid TradeSession, investigate please.");
        }
    }

    public void addItemToTradeSession(Player player, TradeSession tradeSession, ItemStack itemStack) {
        if (tradeSession == null) {
            survival.instance.getLogger().warning("Attempted to add item to invalid TradeSession, investigate please.");
            return;
        }

        if (!tradeSession.isCreatorAccepted() && !tradeSession.isReceiverAccepted()) {
            if (player == tradeSession.getTradeCreator()){
                tradeSession.getCreatorItems().add(itemStack);
            } else if (player == tradeSession.getTradeReceiver()){
                tradeSession.getReceiverItems().add(itemStack);
            }
        } else {
            survival.instance.getLogger().info("Attempted to add an item to an accepted TradeSession without it being canceled automatically, what the fuck?");
        }
    }

    public void removeItemFromTradeSession(Player player, TradeSession tradeSession, ItemStack itemStack) {
        if (tradeSession == null) {
            survival.instance.getLogger().warning("Attempted to remove an item from invalid TradeSession, investigate please.");
            return;
        }

        if (!tradeSession.isCreatorAccepted() && !tradeSession.isReceiverAccepted()) {
            if (tradeSession.getCreatorItems().remove(itemStack) || tradeSession.getReceiverItems().remove(itemStack)) {
                if (player == tradeSession.getTradeCreator()){
                    tradeSession.getCreatorItems().remove(itemStack);
                } else if (player == tradeSession.getTradeReceiver()){
                    tradeSession.getReceiverItems().remove(itemStack);
                }
            } else {
                survival.instance.getLogger().warning("Tried to remove nonexistent item from TradeSession, investigate please.");
            }
        } else {
            survival.instance.getLogger().info("Attempted to remove an item from an accepted TradeSession without it being canceled automatically, what the fuck?");
        }
    }

    public void acceptTradeSessionCreator(Player tradeSender) {
        TradeSession session = activeTradeSessions.get(tradeSender);
        if (session == null) return;

        if (!session.isCreatorAccepted()) {
            session.setCreatorAccepted(true);
            survival.instance.getLogger().info(tradeSender.getName() + " has accepted the trade.");
        }

        checkTradeCompletion(session);
    }

    public void acceptTradeSessionReceiver(Player tradeReceiver) {
        TradeSession session = activeTradeSessions.get(tradeReceiver);
        if (session == null) return;

        if (!session.isReceiverAccepted()) {
            session.setReceiverAccepted(true);
            survival.instance.getLogger().info(tradeReceiver.getName() + " has accepted the trade.");
        }

        checkTradeCompletion(session);
    }

    public void revokeAcceptanceCreator(Player tradeSender) {
        TradeSession session = activeTradeSessions.get(tradeSender);
        if (session == null) return;

        if (session.isCreatorAccepted()) {
            session.setCreatorAccepted(false);
            survival.instance.getLogger().info(tradeSender.getName() + " has revoked their acceptance.");
        } else {
            survival.instance.getLogger().info(tradeSender.getName() + " has not accepted the trade yet.");
        }
    }

    public void revokeAcceptanceReceiver(Player tradeReceiver) {
        TradeSession session = activeTradeSessions.get(tradeReceiver);
        if (session == null) return;

        if (session.isReceiverAccepted()) {
            session.setReceiverAccepted(false);
            survival.instance.getLogger().info(tradeReceiver.getName() + " has revoked their acceptance.");
        } else {
            survival.instance.getLogger().info(tradeReceiver.getName() + " has not accepted the trade yet.");
        }
    }

    private void checkTradeCompletion(TradeSession session) {
        if (session.isCreatorAccepted() && session.isReceiverAccepted()) {
            Player tradeCreator = session.getTradeCreator();
            Player tradeReceiver = session.getTradeReceiver();

            survival.instance.getLogger().info("Both players have accepted the trade. Completing the trade...");

            completeTrade(session);

            tradeCreator.closeInventory();
            tradeReceiver.closeInventory();

            activeTradeSessions.remove(session.getTradeCreator());
            activeTradeSessions.remove(session.getTradeReceiver());
        }
    }

    private void completeTrade(TradeSession session) {
        Player tradeCreator = session.getTradeCreator();
        Player tradeReceiver = session.getTradeReceiver();

        tradeCreator.closeInventory();
        tradeReceiver.closeInventory();

        if (!session.getReceiverItems().isEmpty()) givePlayerBundle(tradeCreator, session.getReceiverItems());

        if (!session.getCreatorItems().isEmpty()) givePlayerBundle(tradeReceiver, session.getCreatorItems());

        survival.instance.getLogger().info("Items exchanged between " + session.getTradeCreator().getName() + " and " + session.getTradeReceiver().getName());
    }

    private void givePlayerBundle(Player player, List<ItemStack> items) {
        ItemStack bundle = new ItemStack(Material.BUNDLE);
        BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();

        for (ItemStack item : items) bundleMeta.addItem(item);

        bundleMeta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        bundle.setItemMeta(bundleMeta);
        player.getInventory().addItem(bundle);
    }

    public void toggleTrades(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        UUID uuid = player.getUniqueId();

        if (getTradeToggle(player) == null || !getTradeToggle(player)) {
            playerData.set(uuid + ".tradeDisabled", true);
        } else {
            playerData.set(uuid + ".tradeDisabled", false);
        }
    }

    public Boolean getTradeToggle(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        UUID uuid = player.getUniqueId();
        Boolean toggle = playerData.getBoolean(uuid + ".tradeDisabled");
        return toggle;
    }
}
