package net.gahvila.selviytymisharpake.PlayerFeatures.Events;

import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplodeEvent implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getLocation().getBlockY() >= 55){
            if (event.getEntity() instanceof Creeper) {
                // Prevent block break
                event.blockList().clear();

                // Deal damage to nearby entities
                double explosionPower = 3.0; // Adjust as needed
                double damageRadius = 5.0; // Adjust as needed

                Creeper creeper = (Creeper) event.getEntity();
                creeper.getWorld().getNearbyEntities(creeper.getLocation(), damageRadius, damageRadius, damageRadius)
                        .stream()
                        .filter(entity -> entity instanceof LivingEntity)
                        .forEach(entity -> ((LivingEntity) entity).damage(explosionPower));
            }
        }
    }
}
