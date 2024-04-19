package net.gahvila.survival.PlayerFeatures.Warps;

import net.gahvila.survival.SelviytymisHarpake;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static net.gahvila.survival.Utils.MiniMessageUtils.toMM;

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