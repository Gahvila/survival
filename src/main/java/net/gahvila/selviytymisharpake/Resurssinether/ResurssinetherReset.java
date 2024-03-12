package net.gahvila.selviytymisharpake.Resurssinether;

import de.leonhard.storage.Json;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class ResurssinetherReset {

    private final HomeManager homeManager;
    private final SelviytymisHarpake plugin;

    public ResurssinetherReset(HomeManager homeManager, SelviytymisHarpake plugin) {
        this.homeManager = homeManager;
        this.plugin = plugin;
    }

    public void schedule() {
        // get the time for the first day of the next month
        ZonedDateTime nextTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusMonths(1L).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        // get the difference of time from now until the 1st day of the next month. multiply by 20 to convert from seconds to ticks.
        long delay = Duration.between(ZonedDateTime.now(), nextTime).getSeconds() * 20;


        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            performNetherReset();
            schedule();
        }, delay);
    }

    public boolean deleteWorld(File path) {
        if(path.exists()) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }
    public void performNetherReset() {
        Bukkit.broadcastMessage("§c§lResurssinetherin nollaus on aloitettu!!");
        for (Player p : Bukkit.getOnlinePlayers()){
            if (p.getWorld().getName().equals("resurssinether")){
                SpawnTeleport.teleportSpawn(p);
                p.sendMessage("Sinut teleportattiin spawnille, koska olit resurssinetherissä, ja sen nollaus aloitettiin.");
            }
        }
        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            Json warpData = new Json("netherdata.json", plugin.getDataFolder() + "/data/");
            warpData.set("generation", true);

            Bukkit.broadcastMessage("§7Poistetaan resurssinether muistista...");
            Bukkit.unloadWorld("resurssinether", false);

            Bukkit.broadcastMessage("§7Poistetaan kaikki kodit resurssinetherissä...");
            homeManager.deleteHomesInWorld("resurssinether");

            Bukkit.broadcastMessage("§7Poistetaan resurssinetherin kartta...");
            deleteWorld(new File("resurssinether/DIM-1"));
            File leveldat = new File("resurssinether/level.dat");
            leveldat.delete();


            Bukkit.broadcastMessage("§7Luodaan uutta karttaa...");
            WorldCreator resurssinethercreator = new WorldCreator("resurssinether");
            resurssinethercreator.environment(World.Environment.NETHER);
            resurssinethercreator.type(WorldType.NORMAL);
            resurssinethercreator.createWorld();
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, "rtp-admin reload");
            Bukkit.dispatchCommand(console, "chunky start resurssinether square 0 0 500 500 concentric");
            Bukkit.broadcastMessage("§c§lResurssinether on nollattu onnistuneesti. Tervetuloa pelailemaan!");
            warpData.set("generation", false);
        }, 20);
    }
}
