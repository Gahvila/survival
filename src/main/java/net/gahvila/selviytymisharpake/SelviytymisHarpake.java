package net.gahvila.selviytymisharpake;

import de.leonhard.storage.Json;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonCommands;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Menu.AddonMenuEvents;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackCommand;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.ChatRange;
import net.gahvila.selviytymisharpake.NewSeason.EndBlocker.PortalEnterEvent;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackListener;
import net.gahvila.selviytymisharpake.PlayerFeatures.Commands.MainCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.Commands.SpawnCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.Commands.TPACMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.Events.*;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.*;
import net.gahvila.selviytymisharpake.PlayerFeatures.VehicleBuffs.MinecartBuff;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import net.gahvila.selviytymisharpake.PlayerFeatures.VehicleBuffs.RidableBuff;
import net.gahvila.selviytymisharpake.PlayerWarps.*;
import net.gahvila.selviytymisharpake.Resurssinether.RNPortalDisabler;
import net.gahvila.selviytymisharpake.Resurssinether.ResourceNetherCMD;
import net.gahvila.selviytymisharpake.Resurssinether.ResurssinetherReset;
import net.gahvila.selviytymisharpake.Utils.MenuListener;
import net.gahvila.selviytymisharpake.Utils.PlayerMenuUtility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public final class SelviytymisHarpake extends JavaPlugin implements Listener {
    public static SelviytymisHarpake instance;
    private PluginManager pluginManager;
    private static Economy econ = null;
    private SelviytymisHarpake plugin;

    private AddonManager addonManager;
    private BackManager backManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private ResurssinetherReset resurssinetherReset;

    private PlayerMenuUtility playerMenuUtility;



    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        //WORLD LOADING
        EmptyChunkGenerator.createWorld();

        World overworld = getServer().getWorld("world");
        overworld.getWorldBorder().setSize(20000);
        overworld.setDifficulty(Difficulty.HARD);

        World nether = getServer().getWorld("world_nether");
        nether.getWorldBorder().setSize(2500);
        nether.setDifficulty(Difficulty.HARD);

        World end = getServer().getWorld("world_the_end");
        end.getWorldBorder().setSize(1000000);
        end.setDifficulty(Difficulty.HARD);

        World spawn = getServer().getWorld("spawn");
        spawn.getWorldBorder().setSize(602);
        spawn.setDifficulty(Difficulty.PEACEFUL);


        WorldCreator resurssinethercreator = new WorldCreator("resurssinether");

        resurssinethercreator.environment(World.Environment.NETHER);
        resurssinethercreator.type(WorldType.NORMAL);
        resurssinethercreator.createWorld();

        World resurssinether = getServer().getWorld("resurssinether");
        resurssinether.getWorldBorder().setSize(2000);
        resurssinether.setDifficulty(Difficulty.HARD);

        //CONFIG
        getConfig().options().copyDefaults();


        // initialize managers for handling various functionalities
        instance = this;

        pluginManager = Bukkit.getPluginManager();
        backManager = new BackManager();
        addonManager = new AddonManager();
        homeManager = new HomeManager();
        warpManager = new WarpManager();
        resurssinetherReset = new ResurssinetherReset(homeManager, instance);

        //schedule resource-nether reset
        ResurssinetherReset resurssinetherReset = new ResurssinetherReset(homeManager, instance);
        resurssinetherReset.schedule();

        //schedule ridable swimming
        RidableBuff ridableBuff = new RidableBuff();
        ridableBuff.ridableBuffScheduler();

        // Command
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false).silentLogs(true));

        AddonCommands addonCommands = new AddonCommands(addonManager);
        addonCommands.registerCommands();

        BackCommand backCommand = new BackCommand(backManager);
        backCommand.registerCommands();

        MainCMD mainCMD = new MainCMD();
        mainCMD.registerCommands();

        ChatRange chatRange = new ChatRange();
        chatRange.registerCommands();

        SpawnCMD spawnCMD = new SpawnCMD();
        spawnCMD.registerCommands();

        TPACMD tpacmd = new TPACMD();
        tpacmd.registerCommands();

        HomeCommands homeCommands = new HomeCommands(homeManager);
        homeCommands.registerCommands();

        WarpCommands warpCommands = new WarpCommands(warpManager);
        warpCommands.registerCommands();

        ResourceNetherCMD resourceNetherCMD = new ResourceNetherCMD();
        resourceNetherCMD.registerCommands();


        saveDefaultConfig();

        registerListeners(new PlayerDeath(), new JoinEvent(), new QuitEvent(), new PortalEnterEvent(), new BackListener(backManager), new ChatRange(), new ChunkUnload(),
                new WarpEvents(warpManager), new MenuListener(), new AddonMenuEvents(addonManager), new RNPortalDisabler(), new ExplodeEvent(), new MinecartBuff());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (!setupEconomy() ) {
            getServer().shutdown();
            return;
        }
    }

    //main class helpers
    private void registerListeners(Listener...listeners){
        for(Listener listener : listeners){
            pluginManager.registerEvents(listener, this);
        }
    }

    //getters
    public static PlayerMenuUtility getPlayerMenuUtility(Player p) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(p))) { //See if the player has a playermenuutility "saved" for them

            //This player doesn't. Make one for them add it to the hashmap
            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(p); //Return the object by using the provided player
        }
    }

    public SelviytymisHarpake getPlugin() {
        return plugin;
    }

    public ResurssinetherReset getResurssinetherReset() {
        return resurssinetherReset;
    }

    public static Economy getEconomy() {
        return econ;
    }

    //setup
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


}
