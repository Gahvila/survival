package net.gahvila.survival.Movement;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import static net.gahvila.survival.survival.instance;

public class RidableBuff {

    public void ridableBuffScheduler() {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(instance, () -> {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (player.getVehicle() != null && player.getVehicle() instanceof LivingEntity livingEntity
                        && !(player.getVehicle() instanceof Player)) {

                    if (isInLiquid(livingEntity)) {
                        if (hasLand(livingEntity)) {
                            jump(livingEntity);
                        } else {
                            swim(livingEntity);
                        }
                    }
                }
            }
        }, 0L, 1L);

    }

    public void jump(LivingEntity livingEntity) {
        livingEntity.setVelocity(livingEntity.getVelocity().setY(0.20));
    }

    public void swim(LivingEntity livingEntity) {
        livingEntity.setVelocity(livingEntity.getVelocity().setY(0.10));
    }

    public boolean hasLand(LivingEntity livingEntity) {
        return livingEntity.getEyeLocation().add(livingEntity.getLocation().getDirection())
                .getBlock().getType() != Material.WATER;
    }

    public boolean isInLiquid(LivingEntity livingEntity) {
        Block block = livingEntity.getLocation().clone().add(0, 1, 0).getBlock();

        return block.getType() == Material.WATER || block.getType() == Material.LAVA;
    }
}
