package net.gahvila.survival.PlayerFeatures.Resurssinether;

import de.leonhard.storage.Json;
import dev.jorel.commandapi.CommandAPICommand;
import net.gahvila.survival.survival;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static net.gahvila.survival.survival.instance;
import static net.gahvila.survival.Utils.MiniMessageUtils.toMM;

public class ResourceNetherCMD  {

    private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();

    private int cooldowntime = 30;

    public static HashMap<Player, Integer> confirmation = new HashMap<Player, Integer>();

    public void registerCommands() {
        new CommandAPICommand("resurssinether")
                .executesPlayer((p, args) -> {
                    Json warpData = new Json("netherdata.json", instance.getDataFolder() + "/data/");
                    Boolean generation = warpData.getBoolean("generation");
                    if (!generation) {
                        if (!cooldown.containsKey(p.getUniqueId())) {
                            if (!confirmation.containsKey(p) || confirmation.get(p) == null) {
                                p.sendMessage(toMM("<br>" +
                                        "<red><b>ʀᴇsᴜʀssɪɴᴇᴛʜᴇʀ</b></red><br>" +
                                        "<white>Sinut teleportataan satunnaiseen sijaintiin resurssinetherissä." +
                                        "<br><br>" +
                                        "Sijainti voi olla vaarallinen, joten tarkista ympäristösi ennen kuin alat juoksemaan satunnaiseen suuntaan.<br>" +
                                        "<red>Oletko varma? Suorita komento uudelleen.</red>"));
                                confirmation.put(p, 1);
                                Bukkit.getScheduler().runTaskLater(survival.instance, () -> confirmation.remove(p), 300);
                            } else if (confirmation.get(p) == 1) {
                                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "forcertp " + p.getName() + " -c resurssinether");
                                confirmation.remove(p);
                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                Bukkit.getScheduler().runTaskLater(survival.instance, () -> cooldown.remove(p.getUniqueId()), 600);
                            }
                        } else {
                            long secondsleft = ((cooldown.get(p.getUniqueId()) / 1000) + cooldowntime) - (System.currentTimeMillis() / 1000);
                            p.sendMessage(toMM("Voit suorittaa tuon uudelleen <#85FF00>" + secondsleft + "</#85FF00> sekuntin päästä."));
                        }
                    }else {
                        p.sendMessage("Et voi mennä resurssinetheriin tällä hetkellä.");
                    }
                })

                .register();
    }
}