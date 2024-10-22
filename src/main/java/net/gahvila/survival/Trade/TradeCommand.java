package net.gahvila.survival.Trade;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static net.gahvila.gahvilacore.GahvilaCore.instance;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.survival.Trade.TradeManager.*;

public class TradeCommand {

    private final TradeManager tradeManager;
    private final TradeMenu tradeMenu;
    public TradeCommand(TradeManager tradeManager, TradeMenu tradeMenu) {
        this.tradeManager = tradeManager;
        this.tradeMenu = tradeMenu;
    }

    public void registerCommands() {
        new CommandAPICommand("trade")
                .withArguments(new PlayerArgument("nimi"))
                .executesPlayer((tradeSender, args) -> {
                    Player tradeReceiver = (Player) args.get("nimi");
                    if (tradeSender.getName().equals(tradeReceiver.getName())){
                        tradeSender.sendMessage("Et voi kaupata itsesi kanssa, sori.");
                        return;
                    }
                    if (tradeManager.getTradeToggle(tradeReceiver)) {
                        tradeSender.sendMessage("Et voi tehdä vaihtokauppaa tuon pelaajan kanssa.");
                        return;
                    }
                    if (tradeRequest.containsKey(tradeSender)) {
                        tradeSender.sendMessage(toMM("Sinulla on jo aktiivinen trade-pyyntö. Lähettääksesi uuden sinun täytyy perua aikaisempi klikkaamalla tätä viestiä tai /tradecancel.")
                                .hoverEvent(HoverEvent.showText(toMM("Klikkaa peruaksesi"))).clickEvent(ClickEvent.runCommand("/tradecancel")));
                        return;
                    }
                    if(Bukkit.getServer().getPluginManager().getPlugin("CarbonChat") != null) {
                        CarbonPlayer carbonPlayer = CarbonChatProvider.carbonChat().userManager().user(tradeReceiver.getUniqueId()).getNow(null);
                        if (carbonPlayer.ignoring(tradeSender.getUniqueId())) {
                            tradeSender.sendMessage("Et voi tehdä vaihtokauppaa tuon pelaajan kanssa.");
                            return;
                        }
                    }

                    tradeRequest.put(tradeSender, tradeReceiver);
                    latestTrader.put(tradeReceiver, tradeSender);
                    tradeReceiver.playSound(tradeReceiver.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2F, 1.2F);
                    tradeSender.sendMessage(toMM("Lähetit pyyntösi vaihtokaupasta <#85FF00>" + tradeReceiver.getName() + ":lle</#85FF00>."));

                    tradeReceiver.sendMessage(toMM("\n| <#85FF00>" + tradeSender.getName() + " </#85FF00>haluaa tehdä kanssasi vaihtokauppaa."));
                    tradeReceiver.sendMessage(toMM("<white>| Sinulla on <#85FF00>30 sekuntia</#85FF00> <white>aikaa hyväksyä."));
                    tradeReceiver.sendMessage(toMM("| <green><b>Hyväksy</b>: /tradeyes " + tradeSender.getName())
                            .hoverEvent(HoverEvent.showText(toMM("<green>Klikkaa hyväksyäksesi</green>"))).clickEvent(ClickEvent.runCommand("/tradeyes " + tradeSender.getName())));
                    tradeReceiver.sendMessage(toMM("| <red><b>Kieltäydy</b>: /tradeno " + tradeSender.getName())
                            .hoverEvent(HoverEvent.showText(toMM("<red>Klikkaa kieltäytyäksesi</red>"))).clickEvent(ClickEvent.runCommand("/tradeno " + tradeSender.getName())));

                    Bukkit.getServer().getScheduler().runTaskLater(instance, new Runnable() {
                        @Override
                        public void run() {
                            if (tradeRequest.get(tradeSender) != null) {
                                tradeRequest.remove(tradeSender);
                                latestTrader.remove(tradeReceiver);
                                tradeSender.sendMessage("Vaihtokauppapyyntösi vanhentui.");
                                tradeReceiver.sendMessage(toMM("<#85FF00>" + tradeSender.getName() + ":n </#85FF00>lähettämä vaihtokauppapyyntö vanhentui."));
                            }
                        }
                    }, 20 * 30);


                })
                .register();
        new CommandAPICommand("tradecancel")
                .withAliases("tpc")
                .executesPlayer((tradeSender, args) -> {
                    if (tradeRequest.get(tradeSender) != null) {
                        Player tradeReceiver = tradeRequest.get(tradeSender);

                        tradeRequest.remove(tradeSender);
                        latestTrader.remove(tradeReceiver);

                        tradeSender.sendMessage("Peruit vaihtopyyntösi.");
                        tradeReceiver.sendMessage(toMM(tradeSender.getName() + " perui vaihtokauppapyyntönsä."));
                    }
                })
                .register();
        new CommandAPICommand("tradetoggle")
                .executesPlayer((p, args) -> {
                    if (tradeManager.getTradeToggle(p)) {
                        tradeManager.toggleTrades(p);
                        p.sendMessage("Kytkit vaihtokauppapyynnöt päälle.");
                    } else {
                        tradeManager.toggleTrades(p);
                        p.sendMessage("Kytkit vaihtokauppapyynnöt pois päältä.");
                    }
                })
                .register();
        new CommandAPICommand("tradeyes")
                .withOptionalArguments(new PlayerArgument("nimi"))
                .executesPlayer((tradeReceiver, args) -> {
                    Player tradeSender;

                    if (args.get("nimi") == null) {
                        // No specific player mentioned, use the latest trader
                        if (latestTrader.containsKey(tradeReceiver)) {
                            tradeSender = latestTrader.get(tradeReceiver);
                        } else {
                            tradeReceiver.sendMessage("Sinulla ei ole vaihtokauppapyyntöjä.");
                            return;
                        }
                    } else {
                        // Player is specified in command args
                        tradeSender = (Player) args.get("nimi");
                    }

                    if (tradeRequest.containsKey(tradeSender) && tradeRequest.get(tradeSender).equals(tradeReceiver)) {
                        // Accept the trade and create the trade session
                        tradeManager.createTradeSession(tradeSender, tradeReceiver);
                        tradeSender.sendMessage(tradeReceiver.getName() + " hyväksyi vaihtokaupan.");
                        tradeReceiver.sendMessage("Hyväksyit vaihtokaupan " + tradeSender.getName() + " kanssa.");

                        // Remove trade requests after acceptance
                        tradeRequest.remove(tradeSender);
                        latestTrader.remove(tradeReceiver);

                        // Open trade GUI for the sender and receiver
                        tradeMenu.openTradeGui(tradeSender, tradeReceiver);
                    } else {
                        tradeReceiver.sendMessage(toMM("<white>Sinulla ei ole vaihtokauppapyyntöjä pelaajalta <#85FF00>" + tradeSender.getName() + "</#85FF00>."));
                    }
                })
                .register();

    }
}
