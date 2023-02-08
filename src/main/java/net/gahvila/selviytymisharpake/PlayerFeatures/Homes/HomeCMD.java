package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

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
                    p.teleport(p.getBedSpawnLocation());
                    p.setWalkSpeed(0.2F);
                }else{
                    p.sendMessage("§a§lSurvival §8> §fSinulla ei ole sänkyä asetettuna.");
                }
            }else {
                if (HomeManager.getHomes(p).contains(args[0])) {
                    p.teleport(HomeManager.getHome(p, args[0]));
                    p.setGameMode(GameMode.SURVIVAL);
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
            Inventory inv = Bukkit.createInventory(p, 9, "§6§lKodit");

            Bukkit.getScheduler().runTaskAsynchronously(SelviytymisHarpake.instance, new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < HomeManager.getHomes(p).size(); i++) {
                        int I = i;
                        Bukkit.getScheduler().runTask(SelviytymisHarpake.instance, new Runnable() {
                            @Override
                            public void run() {
                                ItemStack bed = new ItemStack(Material.GREEN_BED);
                                ItemMeta bedmeta = bed.getItemMeta();
                                bedmeta.setLore(Arrays.asList("§7Teleporttaa kotiisi klikkaamalla."));
                                bedmeta.setDisplayName(HomeManager.getHomes(p).get(I));
                                bed.setItemMeta(bedmeta);
                                inv.setItem(I, bed);
                                p.openInventory(inv);
                            }
                        });
                    }
                }
            });
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
                p.setGameMode(GameMode.SURVIVAL);
                p.setWalkSpeed(0.2F);
                for (Player other : getServer().getOnlinePlayers()) {
                    other.showPlayer(p);
                }
            }
        }
    }
}
