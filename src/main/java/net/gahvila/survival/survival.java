package net.gahvila.survival;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import net.crashcraft.crashclaim.CrashClaim;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import net.gahvila.gahvilacore.Teleport.TeleportManager;
import net.gahvila.survival.DailyRTP.DrtpCommand;
import net.gahvila.survival.DailyRTP.DrtpManager;
import net.gahvila.survival.DailyRTP.integration.ClaimBlockListener;
import net.gahvila.survival.Features.ElytraDisabler.ElytraReplacer;
import net.gahvila.survival.Events.JoinEvent;
import net.gahvila.survival.Events.PlayerDeath;
import net.gahvila.survival.Events.QuitEvent;
import net.gahvila.survival.Features.NoPunchDamage;
import net.gahvila.survival.Homes.HomeCommands;
import net.gahvila.survival.Homes.HomeEvents;
import net.gahvila.survival.Homes.HomeManager;
import net.gahvila.survival.Homes.HomeMenu;
import net.gahvila.survival.Features.ElytraDisabler.ElytraDebuff;
import net.gahvila.survival.Pets.Pets;
import net.gahvila.survival.Warps.WarpApplications.WarpApplication;
import net.gahvila.survival.Warps.WarpApplications.WarpApplicationManager;
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
    private DrtpManager drtpManager;
    private CrashClaim crashClaim;


    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        //load worlds
        setupWorlds();

        // initialize managers for handling various functionalities
        instance = this;

        pluginManager = Bukkit.getPluginManager();
        PlaytimeManager playtimeManager = GahvilaCore.instance.getPlaytimeManager();
        homeManager = new HomeManager(playtimeManager);
        TeleportManager teleportManager = new TeleportManager();
        WarpManager warpManager = new WarpManager(playtimeManager);
        WarpApplicationManager warpApplicationManager = new WarpApplicationManager(warpManager);
        WarpApplication warpApplication = new WarpApplication(warpApplicationManager);
        WarpMenu warpMenu = new WarpMenu(warpManager);
        HomeMenu homeMenu = new HomeMenu(homeManager);

        // Commands
        CommandAPI.onLoad(new CommandAPIPaperConfig(this).verboseOutput(false).silentLogs(true));

        MainCommand mainCommand = new MainCommand(teleportManager);
        mainCommand.registerCommands();

        Pets pets = new Pets();
        pets.registerCommands();

        HomeCommands homeCommands = new HomeCommands(homeManager, homeMenu);
        homeCommands.registerCommands();

        WarpCommands warpCommands = new WarpCommands(warpManager, warpMenu, warpApplication, warpApplicationManager);
        warpCommands.registerCommands();

        //weathervote
        WeatherVoteManager weatherVoteManager = new WeatherVoteManager();
        WeatherVoteCommand weatherVoteCommand = new WeatherVoteCommand(weatherVoteManager);
        weatherVoteCommand.registerCommands();
        registerListeners(new WeatherVoteListener());


        //register events
        registerListeners(new PlayerDeath(teleportManager), new JoinEvent(), new QuitEvent(),
                new WarpEvents(warpManager), new Pets(), new HomeEvents(homeManager), new ElytraDebuff(), new ElytraReplacer(), new NoPunchDamage());

        //fix reload argh
        for (Player player : Bukkit.getOnlinePlayers()) {
            homeManager.putHomeIntoCache(player.getUniqueId());
        }
        warpManager.loadWarps();

        //daily rtp
        crashClaim = CrashClaim.getPlugin();
        drtpManager = new DrtpManager();
        DrtpCommand drtpCommand = new DrtpCommand(drtpManager);
        drtpCommand.register(this);
        getServer().getPluginManager().registerEvents(new ClaimBlockListener(drtpManager), this);
    }

    @Override
    public void onDisable() {
        if (drtpManager != null) {
            drtpManager.saveData();
        }
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

    public CrashClaim getCrashClaim() {
        return crashClaim;
    }

    public void setupWorlds() {
        World overworld = getServer().getWorld("world");
        overworld.getWorldBorder().setSize(30000000);
        overworld.setDifficulty(Difficulty.HARD);

        World nether = getServer().getWorld("world_nether");
        nether.getWorldBorder().setSize(3750000);
        nether.setDifficulty(Difficulty.HARD);

        World end = getServer().getWorld("world_the_end");
        end.getWorldBorder().setSize(30000000);
        end.setDifficulty(Difficulty.HARD);
    }
}
