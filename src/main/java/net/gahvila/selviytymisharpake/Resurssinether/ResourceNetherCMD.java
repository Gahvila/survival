package net.gahvila.selviytymisharpake.Resurssinether;

import de.leonhard.storage.Json;
import net.gahvila.selviytymisharpake.EmptyChunkGenerator;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class ResourceNetherCMD implements CommandExecutor  {

    private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();

    private int cooldowntime = 30;

    public static HashMap<Player, Integer> confirmation = new HashMap<Player, Integer>();
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Json warpData = new Json("netherdata.json", instance.getDataFolder() + "/data/");
        Boolean generation = warpData.getBoolean("generation");
        if (!generation) {
            if (!cooldown.containsKey(p.getUniqueId())) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("npcspawn")) {
                        if (!confirmation.containsKey(p) || confirmation.get(p) == null) {
                            p.sendMessage("");
                            p.sendMessage("§c§lʀᴇsᴜʀssɪɴᴇᴛʜᴇʀ");
                            p.sendMessage("§fSinut teleportataan satunnaiseen sijaintiin resurssinetherissä.");
                            p.sendMessage("");
                            p.sendMessage("§fSijainti voi olla vaarallinen, joten tarkista ympäristösi ennen kuin alat juoksemaan satunnaiseen suuntaan.");
                            p.sendMessage("§cOletko varma? Napsauta uudelleen.");
                            confirmation.put(p, 1);
                            Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> confirmation.remove(p), 300);
                            return true;
                        } else if (confirmation.get(p) == 1) {
                            String command = "forcertp " + sender.getName() + " -c resurssinether";
                            Bukkit.dispatchCommand(console, command);
                            confirmation.remove(p);
                            cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                            Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> cooldown.remove(p.getUniqueId()), 600);
                            return true;
                        }
                    }
                } else {
                    if (!confirmation.containsKey(p) || confirmation.get(p) == null) {
                        p.sendMessage("");
                        p.sendMessage("§c§lʀᴇsᴜʀssɪɴᴇᴛʜᴇʀ");
                        p.sendMessage("§fSinut teleportataan satunnaiseen sijaintiin resurssinetherissä.");
                        p.sendMessage("");
                        p.sendMessage("§fSijainti voi olla vaarallinen, joten tarkista ympäristösi ennen kuin alat juoksemaan satunnaiseen suuntaan.");
                        p.sendMessage("§cOletko varma? Suorita komento uudelleen.");
                        confirmation.put(p, 1);
                        Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> confirmation.remove(p), 300);
                        return true;
                    } else if (confirmation.get(p) == 1) {
                        String command = "forcertp " + sender.getName() + " -c resurssinether";
                        Bukkit.dispatchCommand(console, command);
                        confirmation.remove(p);
                        cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                        Bukkit.getScheduler().runTaskLater(SelviytymisHarpake.instance, () -> cooldown.remove(p.getUniqueId()), 600);
                        return true;
                    }
                }
            } else {
                long secondsleft = ((cooldown.get(p.getUniqueId()) / 1000) + cooldowntime) - (System.currentTimeMillis() / 1000);
                p.sendMessage("Voit suorittaa tuon uudelleen §e" + secondsleft + " §fsekuntin päästä.");
            }
        }else{
            p.sendMessage("Et voi mennä resurssinetheriin tällä hetkellä.");
        }return true;
    }
}
