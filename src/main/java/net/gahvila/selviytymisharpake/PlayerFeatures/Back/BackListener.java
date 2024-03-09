package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackListener implements Listener {

    public static HashMap<Player, Location> back = new HashMap<>();
    public static ArrayList<UUID> died = new ArrayList<UUID>();
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        Location loc = p.getLocation();
        EntityDamageEvent.DamageCause damageCause = e.getEntity().getLastDamageCause().getCause();
        died.add(p.getUniqueId());

        BackManager.saveDeath(p, loc, damageCause.toString(), SelviytymisHarpake.getEconomy().getBalance(p), hasDiamondArmorAdvancement(p), hasElytraAdvancement(p), hasNetheriteAdvancement(p), hasIronArmor(p));

    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (!died.contains(p.getUniqueId())){
            if (!(e.getCause().equals(PlayerTeleportEvent.TeleportCause.DISMOUNT))){
                if (!(e.getCause().equals(PlayerTeleportEvent.TeleportCause.END_GATEWAY))){
                    if (!(e.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL))){
                        if (!(e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL))){
                            if (!(e.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL))){
                                if (!(e.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE))){
                                    if (!e.getFrom().getWorld().equals(Bukkit.getWorld("spawn"))){
                                        if (distanceChecker(e.getFrom(), e.getTo())){
                                            BackManager.saveBackLocation(p, e.getFrom());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public boolean distanceChecker(Location oldLoc, Location newLoc) {
        double distance = oldLoc.distance(newLoc);
        double maxDistance = 50.0;

        return distance <= maxDistance;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> died.remove(p.getUniqueId()), 100);

    }

    public boolean hasDiamondArmorAdvancement(Player player) {
        Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft("story/shiny_gear"));
        return player.getAdvancementProgress(advancement).isDone();
    }

    public boolean hasElytraAdvancement(Player player) {
        Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft("end/elytra"));
        return player.getAdvancementProgress(advancement).isDone();
    }

    public boolean hasNetheriteAdvancement(Player player) {
        Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft("nether/netherite_armor"));
        return player.getAdvancementProgress(advancement).isDone();
    }

    public boolean hasIronArmor(Player player) {
        Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft("story/obtain_armor"));
        return player.getAdvancementProgress(advancement).isDone();
    }
}
