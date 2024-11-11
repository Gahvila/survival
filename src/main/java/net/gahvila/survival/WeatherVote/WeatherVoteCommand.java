package net.gahvila.survival.WeatherVote;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Location;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class WeatherVoteCommand {

    private final WeatherVoteManager manager;

    public WeatherVoteCommand(WeatherVoteManager manager) {
        this.manager = manager;
    }

    public void registerCommands() {
        new CommandAPICommand("weathervote")
                .withAliases("sää")
                .executesPlayer((player, args) -> {
                    manager.startWeatherVote(player);
                })
                .withSubcommand(new CommandAPICommand("clear")
                        .executesPlayer((player, args) -> {
                            manager.voteWeather(player, false);
                        }))
                .withSubcommand(new CommandAPICommand("storm")
                        .executesPlayer((player, args) -> {
                            manager.voteWeather(player, true);
                        }))
                .register();
    }
}
