package net.gahvila.survival.WeatherVote;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class WeatherVoteManager {

    /**
     * Voting
     */
    public boolean areWeVoting = false;

    public ArrayList<Player> allVoters = new ArrayList<>();
    public ArrayList<Player> stormVoters = new ArrayList<>();
    public ArrayList<Player> clearVoters = new ArrayList<>();


    public void startWeatherVote(Player player) {
        if (areWeVoting || getCooldown() != 0) {
            player.sendMessage("Sään äänestystä ei voi aloittaa.");
            return;
        }

        startCooldown();

        Bukkit.broadcast(toMM("<väri>" + player.getName() + " aloitti sään äänestyksen.<br>" +
                "Klikkaa viestiä äänestääksesi sään vaihdossa.<br>" +
                "Äänestys kestää 15 sekuntia.<br>" +
                "Voit myös äänestää komennolla /sää.")
                .hoverEvent(HoverEvent.showText(toMM("Klikkaa äänestääksesi")))
                .clickEvent(ClickEvent.runCommand("/weathervote"))
        );

        allVoters.addAll(Bukkit.getOnlinePlayers());
    }

    public void voteWeather(Player player, boolean isStorm) {
        if (!allVoters.contains(player)) {
            player.sendMessage("Et voi äänestää koska liityit palvelimelle liian myöhään.");
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
        latestCooldown = TimeUnit.SECONDS.toMillis(System.currentTimeMillis());
    }

    public Long getCooldown() {
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - latestCooldown;

        if (timePassed < TimeUnit.SECONDS.toMillis(DEFAULT_COOLDOWN)) {
            return (TimeUnit.MILLISECONDS.toSeconds(TimeUnit.SECONDS.toMillis(DEFAULT_COOLDOWN) - timePassed));
        } else {
            return 0L;
        }
    }
}