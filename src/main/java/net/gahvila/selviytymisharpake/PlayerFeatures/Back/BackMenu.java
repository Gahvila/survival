package net.gahvila.selviytymisharpake.PlayerFeatures.Back;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BackMenu {

    private final BackManager backManager;

    public BackMenu(BackManager backManager) {
        this.backManager = backManager;

    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(3, "§5§lLisäosat");
        gui.show(player);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);
        ItemStack backgroundItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.displayName(toMiniMessage(""));
        backgroundItem.setItemMeta(backgroundItemMeta);
        background.addItem(new GuiItem(backgroundItem));
        background.setRepeat(true);
        gui.addPane(background);

        StaticPane navigationPane = new StaticPane(3, 1, 3, 1);

        ItemStack deathItem = new ItemStack(Material.SKELETON_SKULL, 1);
        ItemMeta deathMeta = deathItem.getItemMeta();
        deathMeta.setDisplayName("§fKuolinsijainti");
        deathMeta.setLore(List.of("§fHinta: §e" + backManager.calculateDeathPrice(player) + "Ⓖ", "§fKoordinaatit: §e" + backManager.getXdeath(player) + " §8| §e" + backManager.getZdeath(player)));
        deathItem.setItemMeta(deathMeta);

        navigationPane.addItem(new GuiItem(deathItem, event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1F);
            confirmMenu(player, deathItem);
        }),0, 0);

        if (backManager.getBack(player) != null) {
            ItemStack back = new ItemStack(Material.MAP, 1);
            ItemMeta backmeta = back.getItemMeta();
            backmeta.setDisplayName("§fViimeisin sijainti");
            backmeta.setLore(List.of("§fKoordinaatit: §e" + backManager.getX(player) + " §8| §e" + backManager.getZ(player)));
            back.setItemMeta(backmeta);

            navigationPane.addItem(new GuiItem(back, event -> {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1F);
                Location previousLocation = backManager.getBack(player);

                if (previousLocation != null) {
                    player.teleportAsync(previousLocation);
                } else {
                    player.sendMessage("Sijaintia ei ole.");
                }
            }), 2, 0);

        }else {
            ItemStack back = new ItemStack(Material.BARRIER, 1);
            ItemMeta back1meta = back.getItemMeta();
            back1meta.setDisplayName("§fSijaintia ei ole");
            back.setItemMeta(back1meta);

            navigationPane.addItem(new GuiItem(back, event -> {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
            }), 2, 0);
        }



        gui.addPane(navigationPane);

        gui.update();
    }

    private void confirmMenu(Player player, ItemStack item) {
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

        pane.bindItem('A', new GuiItem(accept, event -> {
            player.closeInventory();
            double price = backManager.calculateDeathPrice(player);
            if (SelviytymisHarpake.getEconomy().getBalance(player) >= price) {
                SelviytymisHarpake.getEconomy().withdrawPlayer(player, price);
                player.sendMessage("Tililtäsi veloitettiin §e" + price + "Ⓖ§f.");
                player.closeInventory();
                player.teleportAsync(backManager.getDeath(player));
                player.sendMessage("Sinut teleportattiin sinun viimeisimpään kuolinpaikkaan§f.");
            }else{
                player.closeInventory();
                player.sendMessage("Sinulla ei ole tarpeeksi Ⓖ!");
            }
        }));

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
