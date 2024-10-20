package net.gahvila.survival.Trade;

import de.leonhard.storage.Json;
import net.gahvila.survival.survival;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class TradeManager {

    public static HashMap<Player, TradeSession> activeTradeSessions = new HashMap<>();
    public static HashMap<Player, Player> tradeRequest = new HashMap<>(); //receiver, sender
    public static HashMap<Player, Player> latestTrader = new HashMap<>(); //receiver, sender


    public void createTradeSession(Player tradeSender, Player tradeReceiver) {
        if (activeTradeSessions.containsKey(tradeSender) || activeTradeSessions.containsKey(tradeReceiver)) {
            return;
        }

        TradeSession tradeSession = new TradeSession(tradeSender, tradeReceiver, new ArrayList<>(), new ArrayList<>(), false, false);
        activeTradeSessions.put(tradeSender, tradeSession);
        activeTradeSessions.put(tradeReceiver, tradeSession);
        survival.instance.getLogger().info("Trade session created between " + tradeSender.getName() + " and " + tradeReceiver.getName());
    }

    public void cancelTradeSession(Player tradeSender, Player tradeReceiver) {
        TradeSession session = activeTradeSessions.get(tradeSender);
        if (session != null && session.getTradeReceiver().equals(tradeReceiver)) {
            activeTradeSessions.remove(tradeSender);
            activeTradeSessions.remove(tradeReceiver);
            survival.instance.getLogger().info("Trade session canceled between " + tradeSender.getName() + " and " + tradeReceiver.getName());
        } else {
            survival.instance.getLogger().warning("Attempted to cancel an invalid TradeSession, investigate please.");
        }
    }

    public void addItemToTradeSession(TradeSession tradeSession, ItemStack itemStack) {
        if (tradeSession == null) {
            survival.instance.getLogger().warning("Attempted to add item to invalid TradeSession, investigate please.");
            return;
        }

        if (!tradeSession.isCreatorAccepted() && !tradeSession.isReceiverAccepted()) {
            if (tradeSession.getCreatorItems().contains(itemStack) || tradeSession.getReceiverItems().contains(itemStack)) {
                survival.instance.getLogger().warning("Attempted to add an already existing item to the TradeSession, investigate please.");
                return;
            }

            tradeSession.getCreatorItems().add(itemStack);
        } else {
            survival.instance.getLogger().info("Attempted to add an item to an accepted TradeSession without it being canceled automatically, what the fuck?");
        }
    }

    public void removeItemFromTradeSession(TradeSession tradeSession, ItemStack itemStack) {
        if (tradeSession == null) {
            survival.instance.getLogger().warning("Attempted to remove an item from invalid TradeSession, investigate please.");
            return;
        }

        if (!tradeSession.isCreatorAccepted() && !tradeSession.isReceiverAccepted()) {
            if (tradeSession.getCreatorItems().remove(itemStack) || tradeSession.getReceiverItems().remove(itemStack)) {
                tradeSession.getCreatorItems().remove(itemStack);
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
            survival.instance.getLogger().info("Both players have accepted the trade. Completing the trade...");

            completeTrade(session);

            activeTradeSessions.remove(session.getTradeCreator());
            activeTradeSessions.remove(session.getTradeReceiver());
        }
    }

    private void completeTrade(TradeSession session) {
        //item transfer logic here

        survival.instance.getLogger().info("Items exchanged between " + session.getTradeCreator().getName() + " and " + session.getTradeReceiver().getName());
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
