package net.gahvila.selviytymisharpake.Resurssinether;

import de.leonhard.storage.Json;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class ResurssinetherReset {

    private final HomeManager homeManager;
    private final SelviytymisHarpake plugin;

    public ResurssinetherReset(HomeManager homeManager, SelviytymisHarpake plugin) {
        this.homeManager = homeManager;
        this.plugin = plugin;
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
        Bukkit.broadcast(toMM("<red><b>Resurssinetherin nollaus on aloitettu!!"));
        for (Player p : Bukkit.getOnlinePlayers()){
            if (p.getWorld().getName().equals("resurssinether")){
                SpawnTeleport.teleportSpawn(p);
                p.sendMessage("Sinut teleportattiin spawnille, koska olit resurssinetherissä, ja sen nollaus aloitettiin.");
            }
        }
        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            Json warpData = new Json("netherdata.json", plugin.getDataFolder() + "/data/");
            warpData.set("generation", true);

            Bukkit.unloadWorld("resurssinether", false);
            homeManager.deleteHomesInWorld("resurssinether");

            deleteWorld(new File("resurssinether/DIM-1"));
            File leveldat = new File("resurssinether/level.dat");
            leveldat.delete();

            WorldCreator resurssinethercreator = new WorldCreator("resurssinether");
            resurssinethercreator.environment(World.Environment.NETHER);
            resurssinethercreator.type(WorldType.NORMAL);
            resurssinethercreator.createWorld();
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, "rtp-admin reload");
            Bukkit.dispatchCommand(console, "chunky start resurssinether square 0 0 500 500 concentric");
            Bukkit.broadcast(toMM("<red><b>Resurssinether on nollattu onnistuneesti. Tervetuloa pelailemaan!"));
            warpData.set("generation", false);
        }, 20);
    }

    public @NotNull Component toMM(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
