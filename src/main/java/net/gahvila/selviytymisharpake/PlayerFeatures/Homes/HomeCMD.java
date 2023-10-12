package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static org.bukkit.Bukkit.getServer;

public class HomeCMD implements CommandExecutor, Listener {

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

            // IF NO PERM
            if (perm == 0) {
                p.sendMessage(ChatColor.RED + "Sinulla ei ole oikeutta suorittaa tuota komentoa!");
                return true;
            }

            //IF PERM
            if (args[0].equals("sänky")){
                if (p.getBedSpawnLocation() != null){
                    p.teleportAsync(p.getBedSpawnLocation());
                    p.setWalkSpeed(0.2F);
                }else{
                    p.sendMessage("§a§lSurvival §8> §fSinulla ei ole sänkyä asetettuna.");
                }
            }else {
                if (HomeManager.getHomes(p).contains(args[0])) {
                    p.teleportAsync(HomeManager.getHome(p, args[0]));
                    p.setWalkSpeed(0.2F);
                } else {
                    p.sendMessage("§a§lSurvival §8> §fSinulla ei ole kotia nimellä §e" + args[0] + "§f!");
                }
            }
        } else if (args.length == 0) {
            if (HomeManager.getHomes(p) == null){
                p.sendMessage(ChatColor.GRAY + "Sinulla ei ole yhtäkään kotia asetettu. Voit asettaa kodin sijaintiisi komennolla §e/sethome§7.");
                return true;
            }
            if (HomeManager.getHomes(p).isEmpty()) {
                p.sendMessage(ChatColor.GRAY + "Sinulla ei ole yhtäkään kotia asetettu. Voit asettaa kodin sijaintiisi komennolla §e/sethome§7.");
                return true;
            }
            new HomeMenu(SelviytymisHarpake.getPlayerMenuUtility(p)).open();
            return true;

        }
        return false;
    }


    @EventHandler
    public void invClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase("§6§lKodit")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            if (e.getCurrentItem().getType() == Material.GREEN_BED) {
                p.teleportAsync(HomeManager.getHome(p, e.getCurrentItem().getItemMeta().getDisplayName()));
                p.setWalkSpeed(0.2F);
                for (Player other : getServer().getOnlinePlayers()) {
                    other.showPlayer(p);
                }
            }
        }
    }
}
