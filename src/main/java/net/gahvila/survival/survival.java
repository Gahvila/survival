package net.gahvila.survival;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.crashcraft.crashclaim.CrashClaim;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import net.gahvila.gahvilacore.Teleport.TeleportManager;
import net.gahvila.survival.Back.BackCommand;
import net.gahvila.survival.Back.BackListener;
import net.gahvila.survival.Back.BackManager;
import net.gahvila.survival.ElytraDisabler.ElytraReplacer;
import net.gahvila.survival.Events.JoinEvent;
import net.gahvila.survival.Events.PlayerDeath;
import net.gahvila.survival.Events.QuitEvent;
import net.gahvila.survival.Homes.HomeCommands;
import net.gahvila.survival.Homes.HomeEvents;
import net.gahvila.survival.Homes.HomeManager;
import net.gahvila.survival.Homes.HomeMenu;
import net.gahvila.survival.ElytraDisabler.ElytraDebuff;
import net.gahvila.survival.Pets.Pets;
import net.gahvila.survival.Spawn.SpawnCMD;
import net.gahvila.survival.Movement.RidableBuff;
import net.gahvila.survival.Warps.WarpCommands;
import net.gahvila.survival.Warps.WarpEvents;
import net.gahvila.survival.Warps.WarpManager;
import net.gahvila.survival.Warps.WarpMenu;
import net.gahvila.survival.WeatherVote.WeatherVoteCommand;
import net.gahvila.survival.WeatherVote.WeatherVoteListener;
import net.gahvila.survival.WeatherVote.WeatherVoteManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class survival extends JavaPlugin implements Listener {
    public static survival instance;
    private PluginManager pluginManager;
    private survival plugin;
    private HomeManager homeManager;
    private CrashClaim crashClaim;


    @Override
    public void onEnable() {
        //load worlds
        setupWorlds();

        // initialize managers for handling various functionalities
        instance = this;

        pluginManager = Bukkit.getPluginManager();
        crashClaim = CrashClaim.getPlugin();
        PlaytimeManager playtimeManager = GahvilaCore.instance.getPlaytimeManager();
        homeManager = new HomeManager(playtimeManager);
        TeleportManager teleportManager = new TeleportManager();
        BackManager backManager = new BackManager();
        WarpManager warpManager = new WarpManager(playtimeManager);
        WarpMenu warpMenu = new WarpMenu(warpManager);
        HomeMenu homeMenu = new HomeMenu(homeManager);

        RidableBuff ridableBuff = new RidableBuff();
        ridableBuff.ridableBuffScheduler();

        // Commands
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false).silentLogs(true));

        BackCommand backCommand = new BackCommand(backManager);
        backCommand.registerCommands();

        MainCommand mainCommand = new MainCommand(teleportManager);
        mainCommand.registerCommands();

        SpawnCMD spawnCMD = new SpawnCMD(teleportManager);
        spawnCMD.registerCommands();

        Pets pets = new Pets();
        pets.registerCommands();

        HomeCommands homeCommands = new HomeCommands(homeManager, homeMenu);
        homeCommands.registerCommands();

        WarpCommands warpCommands = new WarpCommands(warpManager, warpMenu);
        warpCommands.registerCommands();

        //weathervote
        WeatherVoteManager weatherVoteManager = new WeatherVoteManager();
        WeatherVoteCommand weatherVoteCommand = new WeatherVoteCommand(weatherVoteManager);
        weatherVoteCommand.registerCommands();
        registerListeners(new WeatherVoteListener());


        //register events
        registerListeners(new PlayerDeath(teleportManager), new JoinEvent(teleportManager), new QuitEvent(), new BackListener(backManager),
                new WarpEvents(warpManager), new Pets(), new HomeEvents(homeManager), new ElytraDebuff(), new ElytraReplacer());

        //fix reload argh
        for (Player player : Bukkit.getOnlinePlayers()) {
            homeManager.putHomeIntoCache(player.getUniqueId());
        }
        warpManager.loadWarps();
    }

    @Override
    public void onDisable() {
        homeManager.homes.clear();
    }

    //main class helpers
    private void registerListeners(Listener...listeners){
        for(Listener listener : listeners){
            pluginManager.registerEvents(listener, this);
        }
    }

    //getters
    public survival getPlugin() {
        return plugin;
    }

    public void setupWorlds() {
        World overworld = getServer().getWorld("world");
        overworld.getWorldBorder().setSize(30000000);
        overworld.setDifficulty(Difficulty.HARD);

        World nether = getServer().getWorld("world_nether");
        nether.getWorldBorder().setSize(3750000);
        nether.setDifficulty(Difficulty.HARD);

        World end = getServer().getWorld("world_the_end");
        end.getWorldBorder().setSize(100000);
        end.setDifficulty(Difficulty.HARD);
    }
}
