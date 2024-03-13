package net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop;

import dev.jorel.commandapi.CommandAPICommand;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.CooldownManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Menu.AddonMainMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class AddonCommands {

    private final AddonManager addonManager;


    public AddonCommands(AddonManager addonManager) {
        this.addonManager = addonManager;
    }

    private final CooldownManager cooldownManager = new CooldownManager();

    public void registerCommands() {
        new CommandAPICommand("addon")
                .executesPlayer((p, args) -> {
                    AddonMainMenu.openGui(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_LLAMA_SWAG, 2F, 1F);
                })
                .register();
        new CommandAPICommand("craft")
                .executesPlayer((p, args) -> {
                    if (addonManager.getCraft(p)){
                        p.openWorkbench(null, true);
                        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 2F, 1F);
                    }else{
                        Component message = toMiniMessage("<white>Käytä</white> <yellow>/addon</yellow> <white>komentoa saadaksesi oikeudet tähän.");
                        p.sendMessage(message);
                    }
                })
                .register();
        new CommandAPICommand("ec")
                .withAliases("enderchest")
                .executesPlayer((p, args) -> {
                    if (addonManager.getEnderchest(p)){
                        p.openInventory(p.getEnderChest());
                        p.sendMessage("§fSinun äärilaatikko avattiin!");
                        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 2F, 1F);
                    }else{
                        Component message = toMiniMessage("<white>Käytä</white> <yellow>/addon</yellow> <white>komentoa saadaksesi oikeudet tähän.");
                        p.sendMessage(message);
                    }
                })
                .register();
        new CommandAPICommand("feed")
                .executesPlayer((p, args) -> {
                    if (addonManager.getFeed(p)){
                        //Get the amount of milliseconds that have passed since the feature was last used.
                        long timeLeft = System.currentTimeMillis() - cooldownManager.getCooldown(p.getUniqueId());
                        if(TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= CooldownManager.DEFAULT_COOLDOWN){
                            p.setFoodLevel(20);
                            p.setSaturation(20F);
                            p.sendMessage(toMiniMessage("<white>Täytit ruokapalkkisi. Voit käyttää komentoa uudelleen </white><yellow>yhden minuutin</yellow> <white>kuluttua."));
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 2F, 1F);
                            cooldownManager.setCooldown(p.getUniqueId(), System.currentTimeMillis());
                        }else{
                            String secondsleft = String.valueOf((CooldownManager.DEFAULT_COOLDOWN - TimeUnit.MILLISECONDS.toSeconds(timeLeft)));
                            p.sendMessage(toMiniMessage("<yellow>" + secondsleft + " sekuntia</yellow><white> kunnes voit käyttää komentoa uudelleen."));
                        }
                    }else{
                        p.sendMessage(toMiniMessage("<white>Käytä</white> <yellow>/addon</yellow> <white>komentoa saadaksesi oikeudet tähän."));
                    }
                })
                .register();
        new CommandAPICommand("kauppa")
                .executesPlayer((p, args) -> {
                    if (addonManager.getShop(p)){
                        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                        Bukkit.dispatchCommand(console, "shop open menu kauppa " + p.getName());
                    }else{
                        p.sendMessage(toMiniMessage("<white>Käytä </white><yellow>/addon</yellow> <white>komentoa saadaksesi oikeudet tähän.</white>"));
                    }
                })
                .register();

    }

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }
}