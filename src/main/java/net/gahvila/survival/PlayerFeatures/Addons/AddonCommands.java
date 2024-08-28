package net.gahvila.survival.PlayerFeatures.Addons;

import dev.jorel.commandapi.CommandAPICommand;
import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.permissions.PermissionRoute;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static net.gahvila.survival.Utils.MiniMessageUtils.toMM;

public class AddonCommands {
    protected CrashClaim crashClaim;
    private final AddonManager addonManager;
    private final AddonMenu addonMenu;

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    public static final long DEFAULT_COOLDOWN = 120;

    public AddonCommands(AddonManager addonManager, AddonMenu addonMenu, CrashClaim crashClaim) {
        this.crashClaim = crashClaim;
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
                    if (addonManager.getAddon(p, Addon.CRAFT)){
                        p.openWorkbench(null, true);
                        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 2F, 1F);
                    }else{
                        p.sendMessage(toMM("<white>Käytä</white> <#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän."));
                    }
                })
                .register();
        new CommandAPICommand("ec")
                .withAliases("enderchest")
                .executesPlayer((p, args) -> {
                    if (addonManager.getAddon(p, Addon.ENDERCHEST)){
                        p.openInventory(p.getEnderChest());
                        p.sendMessage("Sinun äärilaatikko avattiin!");
                        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 2F, 1F);
                    }else{
                        Component message = toMM("<white>Käytä</white> <#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän.");
                        p.sendMessage(message);
                    }
                })
                .register();
        new CommandAPICommand("kauppa")
                .executesPlayer((p, args) -> {
                    if (addonManager.getAddon(p, Addon.SHOP)){
                        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                        Bukkit.dispatchCommand(console, "shop open menu kauppa " + p.getName());
                    }else{
                        p.sendMessage(toMM("<white>Käytä </white><#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän.</white>"));
                    }
                })
                .register();
        new CommandAPICommand("fly")
                .executesPlayer((p, args) -> {
                    if (addonManager.getAddon(p, Addon.FLY)){
                        if (p.hasPermission("survival.fly.bypass")) {
                            if (p.getAllowFlight()) {
                                p.sendMessage(toMM("Lentotila: <red>pois päältä"));
                            } else {
                                p.sendMessage(toMM("Lentotila: <green>päällä"));
                            }
                            p.setAllowFlight(!p.getAllowFlight());
                        }
                        if (p.getLocation().getY() < 63) {
                            p.sendMessage("Voit käyttää tätä vain vedenpinnan yläpuolella.");
                            return;
                        }
                        if (crashClaim.getApi().getClaim(p.getLocation()) == null) {
                            p.sendMessage("Et ole suojauksessa.");
                            return;
                        }
                        if (!crashClaim.getApi().getPermissionHelper().hasPermission(p.getUniqueId(), p.getLocation(), PermissionRoute.BUILD)){
                            p.sendMessage("Sinulla ei ole tarpeeksi oikeuksia tässä suojauksessa. Tarvitset rakennusoikeudet.");
                            return;
                        }
                        if (p.getAllowFlight()) {
                            p.sendMessage(toMM("Lentotila: <red>pois päältä"));
                        } else {
                            p.sendMessage(toMM("Lentotila: <green>päällä"));
                        }
                        p.setAllowFlight(!p.getAllowFlight());
                        addonManager.flyScheduler(p);
                    }else{
                        p.sendMessage(toMM("<white>Käytä </white><#85FF00>/addon</#85FF00> <white>komentoa saadaksesi oikeudet tähän.</white>"));
                    }
                })
                .register();

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
