package net.gahvila.selviytymisharpake.PlayerFeatures.VehicleBuffs;

import me.frep.vulcan.api.check.Check;
import me.frep.vulcan.api.event.VulcanFlagEvent;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class BoatBuff implements Listener {
    private HashMap<UUID, Integer> soundCooldown = new HashMap<UUID, Integer>();
    @EventHandler
    public void onVehicleDrive(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        long len = event.getVehicle().getPassengers().size();
        if (len == 0) {
            return;
        }
        Entity passenger = event.getVehicle().getPassengers().get(0);

        if (vehicle instanceof Boat boat && passenger instanceof Player player) {
            if (player.getInventory().getItemInMainHand().getItemMeta() == null) {
                return;
            }
            if (player.getInventory().getItemInMainHand().getType() != Material.PISTON) {
                return;
            }
            String itemName = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
            if (!itemName.equals("§6§lVenemoottori")) {
                return;
            }
            ItemMeta itemMeta = player.getInventory().getItemInMainHand().getItemMeta();
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

            // Retrieve the stored value using the key
            NamespacedKey key = new NamespacedKey(SelviytymisHarpake.instance, "power");
            Integer storedValue = persistentDataContainer.get(key, PersistentDataType.INTEGER);
            if (soundCooldown.get(player.getUniqueId()) == null) {
                soundCooldown.put(player.getUniqueId(), 1);
            } else {
                soundCooldown.put(player.getUniqueId(), soundCooldown.get(player.getUniqueId()) + 1);
            }
            for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                if (soundCooldown.get(player.getUniqueId()) > 11) {
                    soundCooldown.put(player.getUniqueId(), 0);
                    allPlayers.playSound(vehicle.getLocation(), Sound.ENTITY_WOLF_SHAKE, 1F, 6.0F);
                    player.playSound(vehicle.getLocation(), Sound.ENTITY_WOLF_SHAKE, 1F, 6.0F);
                    vehicle.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, vehicle.getLocation(), 30, 0.1, 0.1, 0.1, 0.1);
                }
            }

            if (storedValue.equals(1)){
                boat.setVelocity(new Vector(boat.getLocation().getDirection().multiply(0.4).getX(), 0.0, boat.getLocation().getDirection().multiply(0.5).getZ()));
            }
            if (storedValue.equals(2)){
                boat.setVelocity(new Vector(boat.getLocation().getDirection().multiply(0.6).getX(), 0.0, boat.getLocation().getDirection().multiply(0.7).getZ()));
            }
            if (storedValue.equals(3)){
                boat.setVelocity(new Vector(boat.getLocation().getDirection().multiply(0.8).getX(), 0.0, boat.getLocation().getDirection().multiply(1.0).getZ()));
            }
        }
    }

    @EventHandler
    public void onItemChange(PlayerItemHeldEvent e){
        Player player = e.getPlayer();
        ItemStack previousItem = player.getInventory().getItem(e.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(e.getNewSlot());
        if (player.isInsideVehicle()) {
            Entity vehicle = player.getVehicle();
            if (previousItem != null && previousItem.getType().equals(Material.PISTON)) {
                String itemName = previousItem.getItemMeta().getDisplayName();
                if (!itemName.equals("§6§lVenemoottori")) {
                    return;
                }
                if (vehicle.getPassengers().get(0) == player) {
                    player.playSound(vehicle.getLocation(), Sound.ENTITY_WOLF_SHAKE, 0.5F, 2.0F);
                }
            } else if (newItem != null && newItem.getType().equals(Material.PISTON)) {
                String itemName = newItem.getItemMeta().getDisplayName();
                if (!itemName.equals("§6§lVenemoottori")) {
                    return;
                }
                if (vehicle.getPassengers().get(0) == player) {
                    player.playSound(vehicle.getLocation(), Sound.ENTITY_WOLF_SHAKE, 0.5F, 2.0F);
                    vehicle.setVelocity(new Vector(vehicle.getLocation().getDirection().multiply(0.1).getX(), 0.0, vehicle.getLocation().getDirection().multiply(0.1).getZ()));
                }
            }
        }
    }

    @EventHandler
    public void onFlag(VulcanFlagEvent e){
        Bukkit.broadcastMessage("§c" + e.getCheck().getName());
        System.out.println(e.getCheck().getName());
        e.setCancelled(true);
        if (e.getCheck().getName().contains("EntitySpeed")){
            Player p = e.getPlayer();
            if (p.isInsideVehicle()) {
                if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
                    return;
                }
                if (p.getInventory().getItemInMainHand().getType() != Material.PISTON) {
                    return;
                }
                String itemName = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
                if (!itemName.equals("§6§lVenemoottori")) {
                    return;
                }
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if (e.getItemInHand().getItemMeta().getDisplayName().equals("§6§lVenemoottori")){
            e.getPlayer().sendMessage("§cVenemoottoria ei voi asettaa maahan.");
            e.setCancelled(true);
        }
    }
}
