package net.gahvila.survival.WeatherVote;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class WeatherVoteListener implements Listener {

    public WeatherVoteListener() {
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        World world = Bukkit.getWorld("world");
        long time = world.getTime();

        if (time <= 12300 && world.isThundering()) {
            event.getPlayer().sendMessage(toMM("Et voi ohittaa ukkosta nukkumalla. Jos haluat ohittaa ukkosen, käytä <#85FF00>/sää</#85FF00>."));
            event.setCancelled(true);
        }
    }
}
