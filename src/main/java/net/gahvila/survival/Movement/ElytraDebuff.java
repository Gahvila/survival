package net.gahvila.survival.Movement;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class ElytraDebuff implements Listener {

    @EventHandler
    public void onElytraGlide(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.sendActionBar(toMM("<red><b>Et voi käyttää elytraa."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onElytraBoost(PlayerElytraBoostEvent event) {
        event.getPlayer().sendActionBar(toMM("<red><b>Et voi käyttää elytraa."));
        event.setCancelled(true);
    }
}
