package net.gahvila.selviytymisharpake.PlayerFeatures.Warps;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import static net.gahvila.selviytymisharpake.Utils.MiniMessageUtils.toMM;

public class WarpEvents implements Listener {
    private final WarpManager warpManager;


    public WarpEvents(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        warpManager.updateWarpOwnerName(p);

        Integer money = warpManager.getMoneyInQueue(p.getUniqueId().toString());
        if (money > 0){
            Integer toBePaid = warpManager.getMoneyInQueue(String.valueOf(p.getUniqueId()));
            warpManager.setMoneyInQueue(p.getUniqueId().toString(), 0);
            SelviytymisHarpake.getEconomy().depositPlayer(p, toBePaid);
            p.sendMessage(toMM("Sinun maksullista warppia käytettiin kun olit poissa, sait <#85FF00>" + toBePaid + "</#85FF00>."));
        }
    }
}