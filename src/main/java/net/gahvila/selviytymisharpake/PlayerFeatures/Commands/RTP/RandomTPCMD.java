package net.gahvila.selviytymisharpake.PlayerFeatures.Commands.RTP;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

import static java.lang.Long.MAX_VALUE;

public class RandomTPCMD implements CommandExecutor {
    private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();
    private int cooldowntime = 10;
    @Override

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!cooldown.containsKey(player.getUniqueId())) {
                if (args.length == 0) {
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                    Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> cooldown.remove(player.getUniqueId()), 200);
                    //Safe Location that has been generated
                    Location randomLocation = TeleportUtils.findSafeLocation(player, 1);
                    //Teleport player
                    randomLocation.setY(randomLocation.getBlockY() + 3);
                    player.teleportAsync(randomLocation);

                } else if (args.length == 1) {
                    String s = args[0];
                    Player p = (Player) sender;
                    switch (s.toLowerCase()) {
                        case "spawn":
                            cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                            Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> cooldown.remove(player.getUniqueId()), 200);
                            //Safe Location that has been generated
                            Location randomLocation1 = TeleportUtils.findSafeLocation(player, 0);
                            //Teleport player
                            randomLocation1.setY(randomLocation1.getBlockY() + 3);
                            player.teleportAsync(randomLocation1);
                            break;
                        case "normaali":
                            cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                            Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> cooldown.remove(player.getUniqueId()), 200);
                            //Safe Location that has been generated
                            Location randomLocation2 = TeleportUtils.findSafeLocation(player, 1);
                            //Teleport player
                            randomLocation2.setY(randomLocation2.getBlockY() + 3);
                            player.teleportAsync(randomLocation2);
                            break;
                        default:
                            p.sendMessage("Syötä jokin kelvollinen argumentti: spawn, normaali, meret, vuoristot");
                            break;
                    }
                } else {
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                    Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> cooldown.remove(player.getUniqueId()), 200);
                    //Safe Location that has been generated
                    Location randomLocation1 = TeleportUtils.findSafeLocation(player, 1);
                    //Teleport player
                    randomLocation1.setY(randomLocation1.getBlockY() + 3);
                    player.teleportAsync(randomLocation1);
                }return true;

            } else {
                long secondsleft = ((cooldown.get(player.getUniqueId()) / 1000) + cooldowntime) - (System.currentTimeMillis() / 1000);
                player.sendMessage("Voit suorittaa komennon uudelleen §e" + secondsleft + " §fsekuntin päästä.");
            }
        }return true;
    }
}
