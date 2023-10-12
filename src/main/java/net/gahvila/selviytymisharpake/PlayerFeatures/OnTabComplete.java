package net.gahvila.selviytymisharpake.PlayerFeatures;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import net.gahvila.selviytymisharpake.PlayerWarps.WarpManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Collections;
import java.util.List;

public class OnTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        Player player = (Player) sender;


        switch (command.getName()) {
            case "warp", "delwarp":
                if (WarpManager.getWarps().isEmpty()) {
                    return null;
                }
                for (int i = 0; i < WarpManager.getWarps().size(); i++) {
                }return WarpManager.getWarps();
            case "home", "delhome":
                if (HomeManager.getHomes(player).isEmpty()) {
                    return Collections.singletonList("");
                }
                for (int i = 0; i < HomeManager.getHomes(player).size(); i++) {
                }return HomeManager.getHomes(player);
            case "sethome":
                return Collections.singletonList("");
        }return null;

    }
}
