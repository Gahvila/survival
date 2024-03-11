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
import net.gahvila.selviytymisharpake.PlayerWarps.*;
import net.gahvila.selviytymisharpake.Resurssinether.RNPortalDisabler;
import net.gahvila.selviytymisharpake.Resurssinether.ResourceNetherCMD;
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
    private static SelviytymisHarpake plugin;

    private AddonManager addonManager;
    private BackManager backManager;
    private HomeManager homeManager;
    private WarpManager warpManager;

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


        //schedule resource-nether reset
        schedule();

        //schedule ridable swimming
        ridableBuffScheduler();

        //CONFIG
        getConfig().options().copyDefaults();

        pluginManager = Bukkit.getPluginManager();
        instance = this;

        backManager = new BackManager();
        addonManager = new AddonManager();
        homeManager = new HomeManager();
        warpManager = new WarpManager();

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

        /*
        getCommand("warp").setTabCompleter(new OnTabComplete());
        getCommand("delwarp").setTabCompleter(new OnTabComplete());

        getCommand("buywarp").setExecutor(new BuyWarpCMD());
        getCommand("setwarp").setExecutor(new SetWarpCMD());
        getCommand("warp").setExecutor(new WarpsCMD());
        getCommand("delwarp").setExecutor(new DelWarpCMD());
        getCommand("editwarp").setExecutor(new EditWarpCMD());

         */

        registerListeners(new PlayerDeath(), new JoinEvent(), new QuitEvent(), new PortalEnterEvent(), new BackListener(backManager), new ChatRange(), new ChunkUnload(),
                new WarpEvents(warpManager), new MenuListener(), new AddonMenuEvents(addonManager), new RNPortalDisabler(), new ExplodeEvent(), new MinecartBuff());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (!setupEconomy() ) {
            getServer().shutdown();
            return;
        }
    }

    public void schedule() {
        // get the time for the first day of the next month
        ZonedDateTime nextTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusMonths(1L).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        // get the difference of time from now until the 1st day of the next month. multiply by 20 to convert from seconds to ticks.
        long delay = Duration.between(ZonedDateTime.now(), nextTime).getSeconds() * 20;


        getServer().getScheduler().runTaskLater(this, () -> {
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
        getServer().getScheduler().runTaskLater(this, () -> {
            Json warpData = new Json("netherdata.json", instance.getDataFolder() + "/data/");
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

    public static SelviytymisHarpake getPlugin() {
        return plugin;
    }


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

    public static Economy getEconomy() {
        return econ;
    }

    private void registerListeners(Listener...listeners){
        for(Listener listener : listeners){
            pluginManager.registerEvents(listener, this);
        }
    }

    public void ridableBuffScheduler() {
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                if (player.getVehicle() != null && player.getVehicle() instanceof LivingEntity
                        && !(player.getVehicle() instanceof Player)) {
                    LivingEntity livingEntity = (LivingEntity) player.getVehicle();

                    if (isInLiquid(livingEntity)) {
                        if (hasLand(livingEntity)) {
                            jump(livingEntity);
                        } else {
                            swim(livingEntity);
                        }
                    }
                }
            }
        }, 0L, 1L);

    }

    public void jump(LivingEntity livingEntity) {
        livingEntity.setVelocity(livingEntity.getVelocity().setY(0.20));
    }

    public void swim(LivingEntity livingEntity) {
        livingEntity.setVelocity(livingEntity.getVelocity().setY(0.10));
    }

    public boolean hasLand(LivingEntity livingEntity) {
        return livingEntity.getEyeLocation().add(livingEntity.getLocation().getDirection())
                .getBlock().getType() != Material.WATER;
    }

    public boolean isInLiquid(LivingEntity livingEntity) {
        Block block = livingEntity.getLocation().clone().add(0, 1, 0).getBlock();

        return block.getType() == Material.WATER || block.getType() == Material.LAVA;
    }

}
