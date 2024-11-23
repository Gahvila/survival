package net.gahvila.survival.Trade;

import net.gahvila.survival.survival;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class TradeSessionManager {

    private final TradeManager tradeManager;
    private final TradeMenu tradeMenu;
    public TradeSessionManager(TradeManager tradeManager, TradeMenu tradeMenu) {
        this.tradeManager = tradeManager;
        this.tradeMenu = tradeMenu;
    }

    public static HashMap<Player, TradeSession> activeTradeSessions = new HashMap<>();

    public void createTradeSession(Player trader1, Player trader2) {
        if (activeTradeSessions.containsKey(trader1) || activeTradeSessions.containsKey(trader2)) {
            return;
        }

        TradeSession tradeSession = new TradeSession(tradeManager.getNextTradeId(), System.currentTimeMillis(), trader1, trader2,
                null, null, new ArrayList<>(), new ArrayList<>(), false, false);
        activeTradeSessions.put(trader1, tradeSession);
        activeTradeSessions.put(trader2, tradeSession);

        tradeMenu.openTradeGui(trader1, tradeSession);
        tradeMenu.openTradeGui(trader2, tradeSession);

        survival.instance.getLogger().info("Trade session created between " + trader1.getName() + " and " + trader2.getName());
    }

    public void addItemToTrade(TradeSession tradeSession, Player player, ItemStack itemStack) {
        if (player == tradeSession.getTrader1()) {
            tradeSession.getTrader1Items().add(itemStack);
        } else if (player == tradeSession.getTrader2()) {
            tradeSession.getTrader2Items().add(itemStack);
        }

        tradeSession.getTrader1().playSound(tradeSession.getTrader1().getLocation(), Sound.BLOCK_BEEHIVE_ENTER, 1.0f, 1.0f);
        tradeSession.getTrader2().playSound(tradeSession.getTrader2().getLocation(), Sound.BLOCK_BEEHIVE_ENTER, 1.0f, 1.0f);

        tradeSession.getTrader1Gui().update();
        tradeSession.getTrader2Gui().update();
    }

    public void removeItemFromTrade(TradeSession tradeSession, Player player, ItemStack itemStack) {
        if (player == tradeSession.getTrader1()) {
            tradeSession.getTrader1Items().remove(itemStack);
        } else if (player == tradeSession.getTrader2()) {
            tradeSession.getTrader2Items().remove(itemStack);
        }

        tradeSession.getTrader1().playSound(tradeSession.getTrader1().getLocation(), Sound.BLOCK_BEEHIVE_EXIT, 1.0f, 1.0f);
        tradeSession.getTrader2().playSound(tradeSession.getTrader2().getLocation(), Sound.BLOCK_BEEHIVE_EXIT, 1.0f, 1.0f);

        tradeSession.getTrader1Gui().update();
        tradeSession.getTrader2Gui().update();
    }
}
