package net.gahvila.selviytymisharpake.PlayerFeatures.Warps;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import net.gahvila.selviytymisharpake.PlayerFeatures.Addons.AddonManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Homes.HomeManager;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NewWarpMenu {
    private final WarpManager warpManager;


    public NewWarpMenu(WarpManager warpManager) {
        this.warpManager = warpManager;

    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(3, "§5§lWarpit");
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 5, Pane.Priority.LOWEST);

        ItemStack backgroundItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.displayName(toMiniMessage(""));
        backgroundItem.setItemMeta(backgroundItemMeta);

        background.addItem(new GuiItem(backgroundItem));

        background.setRepeat(true);

        gui.addPane(background);

        OutlinePane navigationPane = new OutlinePane(1, 1, 7, 3);

        gui.addPane(navigationPane);

        gui.update();
    }

    private void confirmMenu(Player player, ItemStack item, String addon) {
        ChestGui gui = new ChestGui(3, "§4§lVarmista osto");
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAICCC1",
                "111111111"
        );

        PatternPane pane = new PatternPane(0, 0, 9, 3, pattern);

        ItemStack accept = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta acceptMeta = accept.getItemMeta();
        acceptMeta.displayName(toMiniMessage("<green><b>Hyväksy"));
        accept.setItemMeta(acceptMeta);

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(toMiniMessage("<red><b>Hylkää"));
        cancel.setItemMeta(cancelMeta);

        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toMiniMessage(""));
        background.setItemMeta(backgroundMeta);

        pane.bindItem('1', new GuiItem(background));
        pane.bindItem('I', new GuiItem(item));

        /*
        pane.bindItem('A', new GuiItem(accept, event -> {
            player.closeInventory();

            if (addon.equals("home")) {
                int price = homeManager.getNextHomeCost(player);
                if (SelviytymisHarpake.getEconomy().getBalance(player) >= price) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 0.5F, 1F);
                    SelviytymisHarpake.getEconomy().withdrawPlayer(player, price);
                    homeManager.addAdditionalHomes(player);
                    player.sendMessage(toMiniMessage("<white>Ohhoh. Ostit lisäkodin hintaan <#85FF00>" + price +
                            "Ⓖ</#85FF00><white>. Sinulla on nyt <#85FF00>" + homeManager.getAllowedHomes(player) + " kotia</#85FF00> <white>yhteensä."));
                    player.closeInventory();
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                    player.sendMessage(toMiniMessage("Nyt kyllä loppu hilut kesken, tarviit <#85FF00>" + price + "Ⓖ</#85FF00> ostaaksesi tuon."));
                    showGUI(player);
                }
                return;
            }

            int price = addonManager.getPrice(addon);

            if (SelviytymisHarpake.getEconomy().getBalance(player) >= price) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 0.5F, 1F);
                SelviytymisHarpake.getEconomy().withdrawPlayer(player, price);
                player.sendMessage(toMiniMessage("<white>Ohhoh. Ostit lisäosan <#85FF00>" + addon + "</#85FF00> <white>onnistuneesti."));
                addonManager.setAddon(player, addon);
                player.closeInventory();
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                player.sendMessage(toMiniMessage("Nyt kyllä loppu hilut kesken, tarviit <#85FF00>" + price + "Ⓖ</#85FF00> ostaaksesi tuon."));
                player.closeInventory();
            }
        }));
         */

        pane.bindItem('C', new GuiItem(cancel, event -> {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
            showGUI(player);
        }));

        gui.addPane(pane);

        gui.update();
    }

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}