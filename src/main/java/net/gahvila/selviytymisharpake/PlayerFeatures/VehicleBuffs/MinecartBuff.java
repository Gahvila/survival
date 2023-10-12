package net.gahvila.selviytymisharpake.PlayerFeatures.VehicleBuffs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class MinecartBuff implements Listener {

//Code from https://github.com/rmellis/MinecartSpeedPlus

    private static final double DEFAULT_SPEED_METERS_PER_TICK = 0.4d;


    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleMove(VehicleMoveEvent event) {

        if (event.getVehicle() instanceof Minecart) {
            Minecart cart = (Minecart) event.getVehicle();
            Location cartLocation = cart.getLocation();
            World cartsWorld = cart.getWorld();

            Block rail = cartsWorld.getBlockAt(cartLocation);
            Block blockBelow = cartsWorld.getBlockAt(cartLocation.add(0, -1, 0));

            if (rail.getType() == Material.POWERED_RAIL) {
                if (blockBelow.getType() == Material.REDSTONE_BLOCK) {
                    cart.setMaxSpeed(DEFAULT_SPEED_METERS_PER_TICK * 8.0);
                }
                else {
                    cart.setMaxSpeed(DEFAULT_SPEED_METERS_PER_TICK);
                }
                RedstoneRail railBlockData = (RedstoneRail) rail.getBlockData();
                if (!railBlockData.isPowered()
                        && blockBelow.getType() == Material.OBSIDIAN) {
                    Vector cartVelocity = cart.getVelocity();
                    cartVelocity.multiply(8.0);
                    cart.setVelocity(cartVelocity);
                }
            }
        }
    }
}
