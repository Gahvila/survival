package net.gahvila.survival.PlayerFeatures.VehicleBuffs;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class MinecartBuff implements Listener {

//Code originally from https://github.com/rmellis/MinecartSpeedPlus

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Minecart cart)) {
            return;
        }

        World world = cart.getWorld();
        Block rail = world.getBlockAt(cart.getLocation());
        Block blockBelow = world.getBlockAt(cart.getLocation().add(0, -1, 0));

        if (rail.getType() != Material.POWERED_RAIL) {
            return;
        }

        if (blockBelow.getType() == Material.REDSTONE_BLOCK) {
            cart.setMaxSpeed(0.4 * 8.0);
        } else if (blockBelow.getType() == Material.OBSIDIAN) {
            Vector cartVelocity = cart.getVelocity();
            cartVelocity.multiply(0.3);
            cart.setVelocity(cartVelocity);
        }
    }
}