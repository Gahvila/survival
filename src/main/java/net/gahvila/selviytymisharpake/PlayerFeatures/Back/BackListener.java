package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BackListener implements Listener {

    private final BackManager backManager;


    public BackListener(BackManager backManager) {
        this.backManager = backManager;
    }

    public static HashMap<Player, Location> back = new HashMap<>();
    public static ArrayList<UUID> died = new ArrayList<UUID>();
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        Location loc = p.getLocation();
        EntityDamageEvent.DamageCause damageCause = e.getEntity().getLastDamageCause().getCause();
        died.add(p.getUniqueId());

        backManager.saveDeath(p, loc, damageCause.toString(), SelviytymisHarpake.getEconomy().getBalance(p), hasDiamondArmorAdvancement(p), hasElytraAdvancement(p), hasNetheriteAdvancement(p), hasIronArmor(p));

    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        PlayerTeleportEvent.TeleportCause cause = e.getCause();

        // make sure teleport isn't due to a death, and not due to the causes listed
        if (!died.contains(p.getUniqueId()) &&
                cause != PlayerTeleportEvent.TeleportCause.DISMOUNT &&
                cause != PlayerTeleportEvent.TeleportCause.END_GATEWAY &&
                cause != PlayerTeleportEvent.TeleportCause.END_PORTAL &&
                cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL &&
                cause != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL &&
                cause != PlayerTeleportEvent.TeleportCause.SPECTATE) {

            World fromWorld = e.getFrom().getWorld();
            Location toLocation = e.getTo();

            //not spawn
            if (!fromWorld.equals(Bukkit.getWorld("spawn"))) {
                backManager.saveBackLocation(p, e.getFrom());
            }
        }
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
