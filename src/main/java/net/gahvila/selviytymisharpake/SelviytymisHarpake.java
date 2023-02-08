package net.gahvila.selviytymisharpake;

import net.gahvila.selviytymisharpake.Chat.ChatRange.ChatRangeCommand;
import net.gahvila.selviytymisharpake.Chat.ChatRange.ChatRangeEvents;
import net.gahvila.selviytymisharpake.NewSeason.EndBlocker.PortalEnterEvent;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonCommand;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Addons.CraftCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Addons.EnderchestCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.Addons.FeedCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackCommand;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackListener;
import net.gahvila.selviytymisharpake.PlayerFeatures.Commands.*;
import net.gahvila.selviytymisharpake.PlayerFeatures.Commands.RTP.RandomTPCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.Events.*;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.AddHomes;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.DelHomeCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.SetHomeCMD;
import net.gahvila.selviytymisharpake.PlayerFeatures.OnTabComplete;
import net.gahvila.selviytymisharpake.PlayerFeatures.Sit;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnProtection;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.VoidTP;
import net.gahvila.selviytymisharpake.PlayerWarps.*;
import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.Listeners.WarpMenuListener;
import net.gahvila.selviytymisharpake.PlayerWarps.MenuSystem.PlayerMenuUtility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;

public final class SelviytymisHarpake extends JavaPlugin implements Listener {
    public static SelviytymisHarpake instance;
    private PluginManager pluginManager;
    private static Economy econ = null;


    private static SelviytymisHarpake plugin;

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {



        //WORLD LOADING
        EmptyChunkGenerator.createWorld();

        World overworld = getServer().getWorld("world");
        overworld.getWorldBorder().setSize(30000);
        overworld.setDifficulty(Difficulty.HARD);

        World nether = getServer().getWorld("world_nether");
        nether.getWorldBorder().setSize(7500);
        nether.setDifficulty(Difficulty.HARD);

        World end = getServer().getWorld("world_the_end");
        end.getWorldBorder().setSize(12500);
        end.setDifficulty(Difficulty.HARD);

        World spawn = getServer().getWorld("spawn");
        spawn.getWorldBorder().setSize(250);
        spawn.setDifficulty(Difficulty.PEACEFUL);
        Objects.requireNonNull(Bukkit.getWorld("spawn")).setTime(Objects.requireNonNull(Bukkit.getWorld("world")).getTime());

        //CONFIG
        getConfig().options().copyDefaults();

        pluginManager = Bukkit.getPluginManager();
        instance = this;
        getCommand("spawn").setExecutor(new SpawnCMD());
        getCommand("tpaccept").setExecutor(new TPACMD());
        getCommand("tpadeny").setExecutor(new TPACMD());
        getCommand("tpa").setExecutor(new TPACMD());
        getCommand("rtp").setExecutor(new RandomTPCMD());
        //HOME
        getCommand("addhome").setExecutor(new AddHomes());
        getCommand("sethome").setExecutor(new SetHomeCMD());
        getCommand("delhome").setExecutor(new DelHomeCMD());
        getCommand("home").setExecutor(new HomeCMD());
        getCommand("home").setTabCompleter(new OnTabComplete());
        getCommand("delhome").setTabCompleter(new OnTabComplete());
        getCommand("sethome").setTabCompleter(new OnTabComplete());
        Bukkit.getPluginManager().registerEvents(new HomeCMD(), this);


        saveDefaultConfig();
        getCommand("back").setExecutor(new BackCommand());
        getCommand("selviytymishärpäke").setExecutor(new MainCMD());
        getCommand("säännöt").setExecutor(new RulesCMD());
        //addons
        getCommand("feed").setExecutor(new FeedCMD());
        getCommand("addon").setExecutor(new AddonCommand());
        getCommand("enderchest").setExecutor(new EnderchestCMD());
        getCommand("craft").setExecutor(new CraftCMD());
        getCommand("addon").setTabCompleter(new OnTabComplete());

        getCommand("sit").setExecutor(new Sit());
        getCommand("puhu").setExecutor(new ChatRangeCommand());
        getCommand("komennot").setExecutor(new UsefulCommandsCMD());

        getCommand("warp").setTabCompleter(new OnTabComplete());
        getCommand("delwarp").setTabCompleter(new OnTabComplete());
        getCommand("rtp").setTabCompleter(new OnTabComplete());

        getCommand("buywarp").setExecutor(new BuyWarpCMD());
        getCommand("setwarp").setExecutor(new SetWarpCMD());
        getCommand("warp").setExecutor(new WarpsCMD());
        getCommand("delwarp").setExecutor(new DelWarpCMD());
        getCommand("editwarp").setExecutor(new EditWarpCMD());

        registerListeners(new PlayerDeath(), new JoinEvent(), new QuitEvent(), new PortalEnterEvent(), new BackListener(), new Sit(), new ChatRangeEvents(), new ChunkUnload(), new SpawnProtection(), new WarpEvents(), new WarpMenuListener(), new VoidTP());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        if (!setupEconomy() ) {
            getServer().shutdown();
            return;
        }

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
}
