package net.gahvila.selviytymisharpake.PlayerFeatures.Addons;

import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AddonCommands {

    private final AddonManager addonManager;
    private final AddonMenu addonMenu;

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    public static final long DEFAULT_COOLDOWN = 120;

    public AddonCommands(AddonManager addonManager, AddonMenu addonMenu) {
        this.addonManager = addonManager;
        this.addonMenu = addonMenu;
    }
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
                        Component message = toMM("<white>Käytä</white> <#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän.");
                        p.sendMessage(message);
                    }
                })
                .register();
        new CommandAPICommand("ec")
                .withAliases("enderchest")
                .executesPlayer((p, args) -> {
                    if (addonManager.getAddon(p, "enderchest")){
                        p.openInventory(p.getEnderChest());
                        p.sendMessage("Sinun äärilaatikko avattiin!");
                        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 2F, 1F);
                    }else{
                        Component message = toMM("<white>Käytä</white> <#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän.");
                        p.sendMessage(message);
                    }
                })
                .register();
        new CommandAPICommand("feed")
                .executesPlayer((p, args) -> {
                    if (addonManager.getAddon(p, "feed")){
                        //Get the amount of milliseconds that have passed since the feature was last used.
                        long timeLeft = System.currentTimeMillis() - getCooldown(p.getUniqueId());
                        if(TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= DEFAULT_COOLDOWN){
                            p.setFoodLevel(20);
                            p.setSaturation(20F);
                            p.sendMessage(toMM("<white>Täytit ruokapalkkisi. Voit käyttää komentoa uudelleen </white><#85FF00>kahden minuutin</#85FF00> <white>kuluttua."));
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 2F, 1F);
                            setCooldown(p.getUniqueId(), System.currentTimeMillis());
                        }else{
                            String secondsleft = String.valueOf((DEFAULT_COOLDOWN - TimeUnit.MILLISECONDS.toSeconds(timeLeft)));
                            p.sendMessage(toMM("<#85FF00>" + secondsleft + " sekuntia</#85FF00><white> kunnes voit käyttää komentoa uudelleen."));
                        }
                    }else{
                        p.sendMessage(toMM("<white>Käytä</white> <#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän."));
                    }
                })
                .register();
        new CommandAPICommand("kauppa")
                .executesPlayer((p, args) -> {
                    if (addonManager.getAddon(p, "shop")){
                        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                        Bukkit.dispatchCommand(console, "shop open menu kauppa " + p.getName());
                    }else{
                        p.sendMessage(toMM("<white>Käytä </white><#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän.</white>"));
                    }
                })
                .register();

    }

    public @NotNull Component toMM(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }

    public void setCooldown(UUID player, long time){
        if(time < 1) {
            cooldowns.remove(player);
        } else {
            cooldowns.put(player, time);
        }
    }

    public Long getCooldown(UUID player){
        return cooldowns.getOrDefault(player, 0L);
    }
}
