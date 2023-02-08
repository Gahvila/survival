package net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Addons;

import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.CooldownManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FeedCMD implements CommandExecutor {

    private final CooldownManager cooldownManager = new CooldownManager();

    HashMap<UUID, Long> cooldown = new HashMap<>();



    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (AddonManager.getFeed(p)){
            //Get the amount of milliseconds that have passed since the feature was last used.
            long timeLeft = System.currentTimeMillis() - cooldownManager.getCooldown(p.getUniqueId());
            if(TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= CooldownManager.DEFAULT_COOLDOWN){
                p.setFoodLevel(20);
                p.setSaturation(20F);
                p.sendMessage("Täytit ruokapalkkisi. Voit käyttää komentoa uudelleen yhden minuutin kuluttua.");
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 2F, 1F);
                cooldownManager.setCooldown(p.getUniqueId(), System.currentTimeMillis());
            }else{
                String secondsleft = String.valueOf((TimeUnit.MILLISECONDS.toSeconds(timeLeft) - CooldownManager.DEFAULT_COOLDOWN));
                p.sendMessage("§e" + secondsleft.replace("-", "") + " sekuntia §fkunnes voit käyttää komentoa uudelleen..");
            }
        }else{
            p.sendMessage("§fKäytä §e/addon feed §fkomentoa saadaksesi oikeudet tähän.");
        }return true;
    }
}
