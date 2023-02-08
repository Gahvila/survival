package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCMD implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (args.length == 1) {
            int perm = 0;
            for (int i = 0; i < 25; i++) {
                if (p.hasPermission("survival.homes." + i)) {
                    if (i > perm) {
                        perm = i;
                    }
                }
            }

            //IF NO PERM
            if (perm == 0) {
                p.sendMessage("oof no perm nub");
                return true;
            }

            //IF PERM
            if (HomeManager.getHomes(p) == null) {
                p.sendTitle("§c" + args[0], "§7Sinulla ei ole kotia tuolla nimellä.", 1, 60, 1);
                return true;
            }
            if (HomeManager.getHomes(p).contains(args[0])) {
                Bukkit.getScheduler().runTaskAsynchronously(SelviytymisHarpake.instance, new Runnable() {
                    @Override
                    public void run() {
                        HomeManager.deleteHome(p, args[0]);
                    }
                });
                p.sendTitle("§c" + args[0], "§7Koti poistettu.", 1, 60, 1);
                return true;
            } else {
                p.sendTitle("§c" + args[0], "§7Sinulla ei ole kotia tuolla nimellä.", 1, 60, 1);
            }


        } else {
            p.sendMessage("Sinun täytyy kirjoittaa kodin nimi. /delhome (nimi)");
            return true;
        }
        return false;
    }
}
