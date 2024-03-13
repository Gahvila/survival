package net.gahvila.selviytymisharpake;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonCommands;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Menu.AddonMenuEvents;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackCommand;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackListener;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Events.ExplodeEvent;
import net.gahvila.selviytymisharpake.PlayerFeatures.Events.JoinEvent;
import net.gahvila.selviytymisharpake.PlayerFeatures.Events.PlayerDeath;
import net.gahvila.selviytymisharpake.PlayerFeatures.Events.QuitEvent;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeCommands;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.PlayerCommands.ChatRange;
import net.gahvila.selviytymisharpake.PlayerFeatures.PlayerCommands.TPACMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.VehicleBuffs.MinecartBuff;
import net.gahvila.selviytymisharpake.PlayerFeatures.VehicleBuffs.RidableBuff;
import net.gahvila.selviytymisharpake.PlayerFeatures.Warps.WarpCommands;
import net.gahvila.selviytymisharpake.PlayerFeatures.Warps.WarpEvents;
import net.gahvila.selviytymisharpake.PlayerFeatures.Warps.WarpManager;
import net.gahvila.selviytymisharpake.PluginCommands.MainCommand;
import net.gahvila.selviytymisharpake.Resurssinether.RNPortalDisabler;
import net.gahvila.selviytymisharpake.Resurssinether.ResourceNetherCMD;
import net.gahvila.selviytymisharpake.Resurssinether.ResurssinetherReset;
import net.gahvila.selviytymisharpake.Utils.EmptyChunkGenerator;
import net.gahvila.selviytymisharpake.Utils.MenuListener;
import net.gahvila.selviytymisharpake.Utils.PlayerMenuUtility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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
        //load worlds
        setupWorlds();

        //configuration
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //economy check
        if (!setupEconomy() ) {
            getServer().shutdown();
            return;
        }

        // initialize managers for handling various functionalities
        instance = this;

        pluginManager = Bukkit.getPluginManager();
        backManager = new BackManager();
        addonManager = new AddonManager();
        homeManager = new HomeManager();
        warpManager = new WarpManager();
        resurssinetherReset = new ResurssinetherReset(homeManager, instance);


        //scheduling
        ResurssinetherReset resurssinetherReset = new ResurssinetherReset(homeManager, instance);
        resurssinetherReset.schedule();

        RidableBuff ridableBuff = new RidableBuff();
        ridableBuff.ridableBuffScheduler();

        // Commands
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false).silentLogs(true));

        AddonCommands addonCommands = new AddonCommands(addonManager);
        addonCommands.registerCommands();

        BackCommand backCommand = new BackCommand(backManager);
        backCommand.registerCommands();

        MainCommand mainCommand = new MainCommand();
        mainCommand.registerCommands();

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


        //register events
        registerListeners(new PlayerDeath(), new JoinEvent(), new QuitEvent(), new BackListener(backManager), new ChatRange(), new WarpEvents(warpManager),
                new MenuListener(), new AddonMenuEvents(addonManager), new RNPortalDisabler(), new ExplodeEvent(), new MinecartBuff());
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

    public void setupWorlds() {
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
    }
}
