package net.gahvila.selviytymisharpake.PlayerFeatures.Spawn;

import net.gahvila.selviytymisharpake.PlayerFeatures.Commands.RTP.TeleportUtils;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.TimeSkipEvent;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static java.lang.Float.MAX_VALUE;

public class SpawnProtection implements Listener {
    public static HashMap<UUID, Boolean> messagesent = new HashMap<UUID, Boolean>();
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        if (e.getBlock().getWorld().getName().equals("spawn")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getEntity().getWorld().getName().equals("spawn")) {
            if (e.getEntityType().equals(EntityType.PLAYER)) {
                e.setCancelled(true);
            }
        } else if (e.getEntity().getWorld().getName().equals("world")) {
            if (e.getEntityType().equals(EntityType.PLAYER)) {
                Player p = (Player) e.getEntity();
                if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL) || e.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL)){
                    if (!TeleportUtils.fallprotection.isEmpty() ){
                        if (TeleportUtils.fallprotection.containsKey(p.getUniqueId())){
                            if (TeleportUtils.fallprotection.get(p.getUniqueId())){
                                e.setCancelled(true);
                                Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> TeleportUtils.fallprotection.put(p.getUniqueId(), false), 60);
                                if (!messagesent.get(p.getUniqueId())){
                                    messagesent.put(p.getUniqueId(), true);
                                    Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> messagesent.put(p.getUniqueId(), false), 60);
                                    p.sendMessage("§fJos sijainti ei ole mieleinen, voit kokeilla saada paremman komennolla §e/rtp§f.");
                                    p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, MAX_VALUE, 1F);
                                    p.spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 5);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("spawn.grefk")) {
            if (p.getWorld().getName().equals("spawn")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("spawn.grefk")) {
            if (p.getWorld().getName().equals("spawn")) {
                p.sendMessage("Spawnilla ei voi pudottaa esineitä.");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if (!p.hasPermission("spawn.grefk")) {
            if (p.getWorld().getName().equals("spawn")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("spawn.grefk")) {
            if (p.getWorld().getName().equals("spawn")) {
                if (!e.getAction().equals(Action.RIGHT_CLICK_AIR)){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onTimeSkip(TimeSkipEvent e){
        if (e.getWorld().equals("world")){
            Bukkit.getWorld("spawn").setTime(Bukkit.getWorld("world").getTime());
        }
    }
}