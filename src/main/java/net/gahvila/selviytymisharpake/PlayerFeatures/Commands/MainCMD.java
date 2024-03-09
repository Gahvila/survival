package net.gahvila.selviytymisharpake.PlayerFeatures.Commands;

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

public class MainCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1) {
            if (!sender.hasPermission("sh.admin")) return true;
            if (args[0].equalsIgnoreCase("reload")) {
                SelviytymisHarpake.instance.reloadConfig();
                sender.sendMessage("Ya did it boeh!");
                return true;
            }
            if (args[0].equalsIgnoreCase("resetnether")) {
                SelviytymisHarpake.instance.performNetherReset();
                return true;
            }

            if (args[0].equalsIgnoreCase("boat1")) {
                Player player = (Player) sender;
                ItemStack pistonItem = new ItemStack(Material.PISTON);
                ItemMeta itemMeta = pistonItem.getItemMeta();

                // Set display name
                itemMeta.setDisplayName("§6§lVenemoottori"); // § is used for color codes
                itemMeta.setLore(Arrays.asList("§e2 §fsylinterin moottori"));
                // Create persistent data container
                PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

                // Store a value of 1 with a custom key (change "your_key" to a unique key)
                persistentDataContainer.set(new NamespacedKey(SelviytymisHarpake.instance, "power"), PersistentDataType.INTEGER, 1);

                // Apply the modified meta to the item
                pistonItem.setItemMeta(itemMeta);

                // Give the player the piston item
                player.getInventory().addItem(pistonItem);
                return true;
            }
            if (args[0].equalsIgnoreCase("boat2")) {
                Player player = (Player) sender;
                ItemStack pistonItem = new ItemStack(Material.PISTON);
                ItemMeta itemMeta = pistonItem.getItemMeta();

                // Set display name
                itemMeta.setDisplayName("§6§lVenemoottori"); // § is used for color codes
                itemMeta.setLore(Arrays.asList("§e3 §fsylinterin moottori"));
                // Create persistent data container
                PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

                // Store a value of 1 with a custom key (change "your_key" to a unique key)
                persistentDataContainer.set(new NamespacedKey(SelviytymisHarpake.instance, "power"), PersistentDataType.INTEGER, 2);

                // Apply the modified meta to the item
                pistonItem.setItemMeta(itemMeta);

                // Give the player the piston item
                player.getInventory().addItem(pistonItem);
                return true;
            }
            if (args[0].equalsIgnoreCase("boat3")) {
                Player player = (Player) sender;
                ItemStack pistonItem = new ItemStack(Material.PISTON);
                ItemMeta itemMeta = pistonItem.getItemMeta();

                // Set display name
                itemMeta.setDisplayName("§6§lVenemoottori"); // § is used for color codes
                itemMeta.setLore(Arrays.asList("§e4 §fsylinterin moottori"));
                // Create persistent data container
                PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

                // Store a value of 1 with a custom key (change "your_key" to a unique key)
                persistentDataContainer.set(new NamespacedKey(SelviytymisHarpake.instance, "power"), PersistentDataType.INTEGER, 3);

                // Apply the modified meta to the item
                pistonItem.setItemMeta(itemMeta);

                // Give the player the piston item
                player.getInventory().addItem(pistonItem);
                return true;
            }
        }return true;
    }
}
