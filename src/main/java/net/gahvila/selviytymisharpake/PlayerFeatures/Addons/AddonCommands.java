package net.gahvila.selviytymisharpake.PlayerFeatures.Addons;

import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class AddonCommands {

    private final AddonManager addonManager;
    private final AddonMenu addonMenu;


    public AddonCommands(AddonManager addonManager, AddonMenu addonMenu) {
        this.addonManager = addonManager;
        this.addonMenu = addonMenu;
    }

    private final CooldownManager cooldownManager = new CooldownManager();

    public void registerCommands() {
        new CommandAPICommand("addon")
                .withAliases("lisäosat")
                .executesPlayer((p, args) -> {
                    addonMenu.showGUI(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_LLAMA_SWAG, 2F, 1F);
                })
                .register();
        new CommandAPICommand("craft")
                .executesPlayer((p, args) -> {
                    if (addonManager.getAddon(p, "craft")){
                        p.openWorkbench(null, true);
                        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 2F, 1F);
                    }else{
                        Component message = toMiniMessage("<white>Käytä</white> <#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän.");
                        p.sendMessage(message);
                    }
                })
                .register();
        new CommandAPICommand("ec")
                .withAliases("enderchest")
                .executesPlayer((p, args) -> {
                    if (addonManager.getAddon(p, "enderchest")){
                        p.openInventory(p.getEnderChest());
                        p.sendMessage("§fSinun äärilaatikko avattiin!");
                        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 2F, 1F);
                    }else{
                        Component message = toMiniMessage("<white>Käytä</white> <#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän.");
                        p.sendMessage(message);
                    }
                })
                .register();
        new CommandAPICommand("feed")
                .executesPlayer((p, args) -> {
                    if (addonManager.getAddon(p, "feed")){
                        //Get the amount of milliseconds that have passed since the feature was last used.
                        long timeLeft = System.currentTimeMillis() - cooldownManager.getCooldown(p.getUniqueId());
                        if(TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= CooldownManager.DEFAULT_COOLDOWN){
                            p.setFoodLevel(20);
                            p.setSaturation(20F);
                            p.sendMessage(toMiniMessage("<white>Täytit ruokapalkkisi. Voit käyttää komentoa uudelleen </white><#85FF00>kahden minuutin</#85FF00> <white>kuluttua."));
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 2F, 1F);
                            cooldownManager.setCooldown(p.getUniqueId(), System.currentTimeMillis());
                        }else{
                            String secondsleft = String.valueOf((CooldownManager.DEFAULT_COOLDOWN - TimeUnit.MILLISECONDS.toSeconds(timeLeft)));
                            p.sendMessage(toMiniMessage("<#85FF00>" + secondsleft + " sekuntia</#85FF00><white> kunnes voit käyttää komentoa uudelleen."));
                        }
                    }else{
                        p.sendMessage(toMiniMessage("<white>Käytä</white> <#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän."));
                    }
                })
                .register();
        new CommandAPICommand("kauppa")
                .executesPlayer((p, args) -> {
                    if (addonManager.getAddon(p, "shop")){
                        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                        Bukkit.dispatchCommand(console, "shop open menu kauppa " + p.getName());
                    }else{
                        p.sendMessage(toMiniMessage("<white>Käytä </white><#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän.</white>"));
                    }
                })
                .register();

    }

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }
}
