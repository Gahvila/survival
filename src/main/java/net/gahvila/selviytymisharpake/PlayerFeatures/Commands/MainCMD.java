package net.gahvila.selviytymisharpake.PlayerFeatures.Commands;

import dev.jorel.commandapi.CommandAPICommand;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.Arrays;

import static net.gahvila.selviytymisharpake.SelviytymisHarpake.instance;

public class MainCMD {

    public void registerCommands() {
        new CommandAPICommand("selviytymishärpäke")
                .withPermission("sh.admin")
                .withSubcommand(new CommandAPICommand("reload")
                        .executes((sender, args) -> {
                            instance.reloadConfig();
                            sender.sendMessage("ladattu uusiks toivotaan että servu ei lahonnut");
                        }))
                .withSubcommand(new CommandAPICommand("resetnether")
                        .executes((sender, args) -> {
                            sender.sendMessage("nether reset alotettu toivottavasti");
                            instance.getResurssinetherReset().performNetherReset();
                        }))
                .register();

    }
}
