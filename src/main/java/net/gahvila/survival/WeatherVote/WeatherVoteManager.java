package net.gahvila.survival.WeatherVote;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.survival.survival.instance;

public class WeatherVoteManager {

    /**
     * Voting
     */
    public boolean areWeVoting = false;

    public ArrayList<Player> allVoters = new ArrayList<>();
    public ArrayList<Player> stormVoters = new ArrayList<>();
    public ArrayList<Player> clearVoters = new ArrayList<>();

    public void startWeatherVote(Player player) {
        if (areWeVoting) {
            player.sendMessage("Sään äänestystä ei voi aloittaa koska on jo äänestys.");
            return;
        }

        if (getCooldown() != 0) {
            player.sendRichMessage("Voit aloittaa uuden äänestyksen <#85FF00>" + getCooldown() + "s</#85FF00> kuluttua.");
            return;
        }

        areWeVoting = true;

        Bukkit.broadcast(toMM("""
                <br>
                <#85FF00>{playername}</#85FF00> <gray>aloitti sää-äänestyksen:</gray>
                <hover:show_text:'<yellow>Äänestä selkeää'><click:run_command:'/weathervote clear'><dark_gray>[ <yellow>☀ SELKEÄ</yellow> ]</dark_gray></click></hover> <gray>tai</gray> <hover:show_text:'<aqua>Äänestä myrskyä'><click:run_command:'/weathervote storm'><dark_gray>[ <aqua>⛈ MYRSKY</aqua> ]</dark_gray></click></hover> <gray>(15s)</gray>"""
                .replace("{playername}", player.getName())));

        allVoters.addAll(Bukkit.getOnlinePlayers());

        new BukkitRunnable() {
            int countdown = 15; //seconds

            @Override
            public void run() {
                if (countdown <= 0) {
                    concludeVote();
                    areWeVoting = false;
                    this.cancel();
                } else {
                    for (Player voter : allVoters) {
                        voter.sendActionBar(toMM("Äänestys päättyy <#85FF00>" + countdown + "s</#85FF00> kuluttua."));
                    }
                    countdown--;
                }
            }
        }.runTaskTimer(instance, 0, 20);
    }

    public void concludeVote() {
        int stormVotes = stormVoters.size();
        int clearVotes = clearVoters.size();

        String resultMessage;
        if (stormVotes > clearVotes) {
            resultMessage = "Äänestys päättyi: Myrsky voitti <#85FF00>" + stormVotes + "</#85FF00> - <#ff0000>" + clearVotes + "</#ff0000>";
            Bukkit.getWorld("world").setStorm(true);
        } else if (clearVotes > stormVotes) {
            resultMessage = "Äänestys päättyi: Selkeä voitti <#85FF00>" + clearVotes + "</#85FF00> - <#ff0000>" + stormVotes + "</#ff0000>";
            Bukkit.getWorld("world").setStorm(false);
        } else {
            resultMessage = "Äänestys päättyi tasan! Sää pysyy ennallaan.";
        }

        Bukkit.broadcast(toMM(resultMessage));
        for (Player voter : allVoters) {
            voter.sendActionBar(toMM(resultMessage));
        }
        startCooldown();

        // Reset lists for the next vote
        allVoters.clear();
        stormVoters.clear();
        clearVoters.clear();
    }

    public void voteWeather(Player player, boolean isStorm) {
        if (!allVoters.contains(player)) {
            player.sendMessage("Et voi äänestää tässä äänestyksessä.");
            return;
        }

        if (isStorm) {
            removeVote(player, clearVoters, "Sinun äänestys selkeästä evättiin.");
            addVote(player, stormVoters, "Äänestit myrskyn puolesta.");
        } else {
            removeVote(player, stormVoters, "Sinun äänestys myrskystä evättiin.");
            addVote(player, clearVoters, "Äänestit selkeän puolesta.");
        }
    }

    private void addVote(Player player, ArrayList<Player> voters, String message) {
        if (!voters.contains(player)) {
            voters.add(player);
            player.sendMessage(message);
        }
    }

    private void removeVote(Player player, ArrayList<Player> opposingVoters, String message) {
        if (opposingVoters.contains(player)) {
            opposingVoters.remove(player);
            player.sendMessage(message);
        }
    }


    /**
     * Cooldown
     */
    private long latestCooldown;

    public final long DEFAULT_COOLDOWN = 120; // seconds

    private void startCooldown() {
        latestCooldown = System.currentTimeMillis();
    }

    public Long getCooldown() {
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - latestCooldown;

        if (timePassed < TimeUnit.SECONDS.toMillis(DEFAULT_COOLDOWN)) {
            return TimeUnit.MILLISECONDS.toSeconds(TimeUnit.SECONDS.toMillis(DEFAULT_COOLDOWN) - timePassed);
        } else {
            return 0L;
        }
    }
}