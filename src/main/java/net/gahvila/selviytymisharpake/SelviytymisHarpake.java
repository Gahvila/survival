package net.gahvila.selviytymisharpake;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.gahvila.selviytymisharpake.PlayerFeatures.Addons.AddonCommands;
import net.gahvila.selviytymisharpake.PlayerFeatures.Addons.AddonManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Addons.AddonMenu;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackCommand;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackListener;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackMenu;
import net.gahvila.selviytymisharpake.PlayerFeatures.Events.*;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeCommands;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeEvents;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeMenu;
import net.gahvila.selviytymisharpake.PlayerFeatures.Pets;
import net.gahvila.selviytymisharpake.PlayerFeatures.PlayerCommands.TpaCommands;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.VehicleBuffs.MinecartBuff;
import net.gahvila.selviytymisharpake.PlayerFeatures.VehicleBuffs.RidableBuff;
import net.gahvila.selviytymisharpake.PlayerFeatures.Warps.*;
import net.gahvila.selviytymisharpake.PluginCommands.MainCommand;
import net.gahvila.selviytymisharpake.Resurssinether.RNPortalDisabler;
import net.gahvila.selviytymisharpake.Resurssinether.ResourceNetherCMD;
import net.gahvila.selviytymisharpake.Resurssinether.ResurssinetherReset;
import net.gahvila.selviytymisharpake.Utils.EmptyChunkGenerator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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
    private AddonMenu addonMenu;
    private WarpMenu warpMenu;
    private HomeMenu homeMenu;
    private BackMenu backMenu;

    @Override
    public void onEnable() {
        //load worlds
        setupWorlds();

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
        addonMenu = new AddonMenu(addonManager, homeManager);
        warpMenu = new WarpMenu(warpManager);
        homeMenu = new HomeMenu(homeManager);
        backMenu = new BackMenu(backManager);



        RidableBuff ridableBuff = new RidableBuff();
        ridableBuff.ridableBuffScheduler();

        // Commands
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false).silentLogs(true));

        AddonCommands addonCommands = new AddonCommands(addonManager, addonMenu);
        addonCommands.registerCommands();

        BackCommand backCommand = new BackCommand(backManager, backMenu);
        backCommand.registerCommands();

        MainCommand mainCommand = new MainCommand();
        mainCommand.registerCommands();

        SpawnCMD spawnCMD = new SpawnCMD();
        spawnCMD.registerCommands();

        TpaCommands tpaCommands = new TpaCommands();
        tpaCommands.registerCommands();

        Pets pets = new Pets();
        pets.registerCommands();

        HomeCommands homeCommands = new HomeCommands(homeManager, homeMenu);
        homeCommands.registerCommands();

        WarpCommands warpCommands = new WarpCommands(warpManager, warpMenu);
        warpCommands.registerCommands();

        ResourceNetherCMD resourceNetherCMD = new ResourceNetherCMD();
        resourceNetherCMD.registerCommands();


        //register events
        registerListeners(new PlayerDeath(), new JoinEvent(), new QuitEvent(), new BackListener(backManager),
                new WarpEvents(warpManager), new RNPortalDisabler(), new ExplodeEvent(), new MinecartBuff(), new Pets(), new HomeEvents(homeManager));

        //fix reload argh
        for (Player player : Bukkit.getOnlinePlayers()) {
            homeManager.putHomeIntoRam(player.getUniqueId());
        }

        warpManager.loadWarps();
    }

    @Override
    public void onDisable() {
        homeManager.homes.clear();
        //unregister homes
        CommandAPI.unregister("buyhome");
        CommandAPI.unregister("delhome");
        CommandAPI.unregister("renamehome");
        CommandAPI.unregister("home");
        CommandAPI.unregister("sethome");
        //unregister warps
        CommandAPI.unregister("buywarp");
        CommandAPI.unregister("delwarp");
        CommandAPI.unregister("editwarp");
        CommandAPI.unregister("setwarp");
        CommandAPI.unregister("warp");
        //unregister back
        CommandAPI.unregister("back");
        CommandAPI.unregister("fback");
        //unregister tpa
        CommandAPI.unregister("tpacancel");
        CommandAPI.unregister("tpayes");
        CommandAPI.unregister("tpano");
        CommandAPI.unregister("tpahere");
        CommandAPI.unregister("tpa");
        CommandAPI.unregister("tpatoggle");
        //unregister misc.
        CommandAPI.unregister("selviytymishärpäke");
        CommandAPI.unregister("resurssinether");
        CommandAPI.unregister("spawn");
        CommandAPI.unregister("addon");
    }

    //main class helpers
    private void registerListeners(Listener...listeners){
        for(Listener listener : listeners){
            pluginManager.registerEvents(listener, this);
        }
    }

    //getters
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
