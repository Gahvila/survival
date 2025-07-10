package net.gahvila.survival.Features;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoPunchDamage implements Listener {

    @EventHandler
    public void onPlayerPunch(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) event.setCancelled(true);
        event.setDamage(0.0);
    }
}
