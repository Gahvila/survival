package net.gahvila.selviytymisharpake.PlayerFeatures;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

public class Sit implements Listener, CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player plr = (Player) sender;
        if (!plr.hasPermission("sit.use"))return true;
        if(plr.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)return true;
        if (sit(plr.getLocation(), plr, 0.3))
            plr.sendMessage("§7§oLaskit peppusi maahan, hyvä fiilis...");
        return true;
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER || e.getDismounted().getType() != EntityType.ARMOR_STAND)
            return;
        Player plr = (Player) e.getEntity();
        ArmorStand as = (ArmorStand) e.getDismounted();
        as.remove();
        plr.sendMessage("§7§oNousit ylös!");
        Bukkit.getScheduler().scheduleSyncDelayedTask(SelviytymisHarpake.instance, () -> plr.teleport(plr.getLocation().add(0.0D, 1.5D, 0.0D)));
    }


    public boolean sit(Location loc, Player plr, double height) {
        if (plr.getVehicle() != null) {
            plr.sendMessage("§7Kappas, sinähän istut jo!");
            return false;
        }
        ArmorStand as = (ArmorStand)plr.getWorld().spawnEntity(loc.subtract(0.0D, 2.0D - height, 0.0D), EntityType.ARMOR_STAND);
        as.setGravity(false);
        as.setInvulnerable(true);
        as.setVisible(false);
        as.setHealth(0.1);
        as.addPassenger((Entity)plr);
        return true;
    }
}
