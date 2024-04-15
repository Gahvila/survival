package net.gahvila.selviytymisharpake.PlayerFeatures.Warps;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.font.util.Font;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.DropperGui;
import com.github.stefvanschie.inventoryframework.pane.*;
import com.github.stefvanschie.inventoryframework.pane.component.Label;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Float.MAX_VALUE;
import static net.gahvila.selviytymisharpake.Utils.MiniMessageUtils.toMM;
import static net.gahvila.selviytymisharpake.Utils.MiniMessageUtils.toUndecoratedMM;

public class WarpMenu {
    private final WarpManager warpManager;

    public WarpMenu(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(5, ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Warpit")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "111AAA111"
        );
        PatternPane border = new PatternPane(0, 0, 9, 5, Pane.Priority.LOWEST, pattern);
        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));
        gui.addPane(border);

        PaginatedPane pages = new PaginatedPane(1, 1, 7, 3);
        List<ItemStack> items = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(SelviytymisHarpake.instance, "selviytymisharpake");
        for (Warp warp : warpManager.getWarps()) {
            Date currentTime = new Date(warp.getCreationDate());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);

            ItemStack item = new ItemStack(warp.getCustomItem());
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, warp.getName());
            meta.displayName(Component.text(warp.getName()));
            meta.lore(List.of(toUndecoratedMM("<white>Omistaja: <yellow>" + warp.getOwnerName()),
                    toUndecoratedMM("<white>Käyttökerrat: <yellow>" + warp.getUses()),
                    toUndecoratedMM("<white>Hinta: <yellow>" + warp.getPrice() + "Ⓖ"),
                    toUndecoratedMM("<gray><i>" + dateString)));
            item.setItemMeta(meta);
            items.add(item);
        }
        pages.populateWithItemStacks(items);
        gui.addPane(pages);

        pages.setOnClick(event -> {
            if (event.getCurrentItem() == null) return;
            Optional<Warp> warp = warpManager.getWarp(event.getCurrentItem().getItemMeta().getPersistentDataContainer()
                    .get(key, PersistentDataType.STRING));
            if (warp.get().getPrice() == 0){
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, MAX_VALUE, 1F);
                player.closeInventory();
                player.teleportAsync(warp.get().getLocation());
                player.sendMessage(toMM("Sinut teleportattiin warppiin <#85FF00>" + warp.get().getName() + "</#85FF00>."));
                Bukkit.getServer().getScheduler().runTaskLater(SelviytymisHarpake.instance, new Runnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, MAX_VALUE, 1F);
                    }
                }, 5);
                if (!player.getName().equals(warp.get().getOwnerName())){
                    warpManager.addUses(warp.get());
                }
            }else {
                confirmMenu(player, event.getCurrentItem(), warp.get());
            }
        });


        StaticPane navigationPane = new StaticPane(0, 4, 9, 1);

        ItemStack ownWarps = new ItemStack(Material.OAK_SIGN);
        ItemMeta ownWarpsMeta = ownWarps.getItemMeta();
        ownWarpsMeta.displayName(toUndecoratedMM("<b>Omat warpit<b>"));
        ownWarps.setItemMeta(ownWarpsMeta);
        navigationPane.addItem(new GuiItem(ownWarps, event -> {
            if (warpManager.getOwnedWarps(player.getUniqueId()).isEmpty()){
                player.sendMessage("Sinulla ei ole warppeja.");
                return;
            }
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
            showOwnWarps(player);

        }), 1, 0);

        ItemStack previous = new ItemStack(Material.MANGROVE_BUTTON);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(toUndecoratedMM("<b>Takaisin"));
        previous.setItemMeta(previousMeta);
        navigationPane.addItem(new GuiItem(previous, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);

                gui.update();
            }
        }), 3, 0);
        ItemStack sorting = new ItemStack(Material.NETHER_STAR);
        ItemMeta sortingMeta = sorting.getItemMeta();
        sortingMeta.displayName(toUndecoratedMM("<white><b>Järjestys"));
        sortingMeta.lore(List.of(toUndecoratedMM("<white>Järjestäminen on tulossa pian.")));
        sorting.setItemMeta(sortingMeta);
        navigationPane.addItem(new GuiItem(sorting, event -> {
            player.sendMessage("Tulossa pian.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
        }), 4, 0);
        ItemStack next = new ItemStack(Material.WARPED_BUTTON);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(toUndecoratedMM("<b>Seuraava"));
        next.setItemMeta(nextMeta);
        navigationPane.addItem(new GuiItem(next, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                gui.update();
            }
        }), 5, 0);
        gui.addPane(navigationPane);

        gui.update();
    }

    private void confirmMenu(Player player, ItemStack item, Warp warp) {
        ChestGui gui = new ChestGui(3, ComponentHolder.of(toUndecoratedMM("<dark_red><b>Varmista osto")));
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
        acceptMeta.displayName(toUndecoratedMM("<green><b>Hyväksy"));
        accept.setItemMeta(acceptMeta);

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(toUndecoratedMM("<red><b>Hylkää"));
        cancel.setItemMeta(cancelMeta);

        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        background.setItemMeta(backgroundMeta);

        pane.bindItem('1', new GuiItem(background));
        pane.bindItem('I', new GuiItem(item));

        pane.bindItem('A', new GuiItem(accept, event -> {
            player.closeInventory();
            int price = warp.getPrice();

            if (SelviytymisHarpake.getEconomy().getBalance(player) >= price) {
                SelviytymisHarpake.getEconomy().withdrawPlayer(player, price);
                player.sendMessage(toMM("Tililtäsi veloitettiin <#85FF00>" + price + "Ⓖ</#85FF00>."));
                player.closeInventory();
                player.teleportAsync(warp.getLocation());
                player.sendMessage(toMM("Sinut teleportattiin warppiin <#85FF00>" + warp.getName() + "</#85FF00>."));

                if (!player.getName().equals(warp.getOwnerName())){
                    warpManager.addUses(warp);
                }
                //
                UUID ownerUUID = warp.getOwner();
                if (Bukkit.getPlayer(ownerUUID) == null){
                    warpManager.addMoneyToQueue(ownerUUID.toString(), price);
                }else{
                    Player owner = Bukkit.getPlayer(ownerUUID);
                    SelviytymisHarpake.getEconomy().depositPlayer(owner, price);
                    owner.sendMessage(toMM("Sinun maksullista warppia käytettiin, sait <#85FF00>" + price + "Ⓖ</#85FF00>."));
                }
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
                player.sendMessage(toMM("Nyt kyllä loppu hilut kesken, tarviit <#85FF00>" + price + "Ⓖ</#85FF00> käyttääksesi tätä warppia."));
                player.closeInventory();
            }
        }));

        pane.bindItem('C', new GuiItem(cancel, event -> {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
            showGUI(player);
        }));

        gui.addPane(pane);

        gui.update();
    }

    public void showOwnWarps(Player player) {
        ChestGui gui = new ChestGui(5, ComponentHolder.of(toUndecoratedMM("<dark_green><b>Omat warpit")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "111AAA111"
        );
        PatternPane border = new PatternPane(0, 0, 9, 5, Pane.Priority.LOWEST, pattern);
        ItemStack background = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));
        gui.addPane(border);

        PaginatedPane pages = new PaginatedPane(1, 1, 7, 3);
        List<ItemStack> items = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(SelviytymisHarpake.instance, "selviytymisharpake");
        for (Warp warp : warpManager.getOwnedWarps(player.getUniqueId())) {
            Date currentTime = new Date(warp.getCreationDate());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);

            ItemStack item = new ItemStack(warp.getCustomItem());
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, warp.getName());
            meta.displayName(Component.text(warp.getName()));
            meta.lore(List.of(toUndecoratedMM("<green><b>Klikkaa muokataksesi"),
                    toUndecoratedMM("<white>Käyttökerrat: <yellow>" + warp.getUses()),
                    toUndecoratedMM("<white>Hinta: <yellow>" + warp.getPrice() + "Ⓖ"),
                    toUndecoratedMM("<gray><i>" + dateString)));
            item.setItemMeta(meta);
            items.add(item);
        }
        pages.populateWithItemStacks(items);
        gui.addPane(pages);

        pages.setOnClick(event -> {
            if (event.getCurrentItem() == null) return;
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, MAX_VALUE, 1F);
            Optional<Warp> warp = warpManager.getWarp(event.getCurrentItem().getItemMeta().getPersistentDataContainer()
                    .get(key, PersistentDataType.STRING));
            showWarpEditMenu(player, warp.get());
        });


        StaticPane navigationPane = new StaticPane(0, 4, 9, 1);

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(toUndecoratedMM("<white><b>Peruuta<b>"));
        back.setItemMeta(backMeta);
        navigationPane.addItem(new GuiItem(back, event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);
            showGUI(player);

        }), 1, 0);

        ItemStack previous = new ItemStack(Material.MANGROVE_BUTTON);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(toUndecoratedMM("<b>Takaisin"));
        previous.setItemMeta(previousMeta);
        navigationPane.addItem(new GuiItem(previous, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);

                gui.update();
            }
        }), 3, 0);
        ItemStack sorting = new ItemStack(Material.NETHER_STAR);
        ItemMeta sortingMeta = sorting.getItemMeta();
        sortingMeta.displayName(toUndecoratedMM("<white><b>Järjestys"));
        sortingMeta.lore(List.of(toUndecoratedMM("<white>Järjestäminen on tulossa pian.")));
        sorting.setItemMeta(sortingMeta);
        navigationPane.addItem(new GuiItem(sorting, event -> {
            player.sendMessage("Tulossa pian.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
        }), 4, 0);
        ItemStack next = new ItemStack(Material.WARPED_BUTTON);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(toUndecoratedMM("<b>Seuraava"));
        next.setItemMeta(nextMeta);
        navigationPane.addItem(new GuiItem(next, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                gui.update();
            }
        }), 5, 0);
        gui.addPane(navigationPane);

        gui.update();
    }

    public void showWarpEditMenu(Player player, Warp warp) {
        ChestGui gui = new ChestGui(5, ComponentHolder.of(toUndecoratedMM("<dark_green><b>Warp:</b> " + warp.getName())));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "111111111"
        );
        PatternPane border = new PatternPane(0, 0, 9, 5, Pane.Priority.LOWEST, pattern);
        ItemStack background = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));
        gui.addPane(border);

        StaticPane settingPane = new StaticPane(2, 2, 5, 1);

        ItemStack nameEdit = new ItemStack(Material.ANVIL);
        ItemMeta nameEditMeta = nameEdit.getItemMeta();
        nameEditMeta.displayName(toUndecoratedMM("<white><b>Muokkaa nimeä</b>"));
        nameEditMeta.lore(List.of(toUndecoratedMM("<white>Nyt: <#85FF00>" + warp.getName())));
        nameEdit.setItemMeta(nameEditMeta);

        settingPane.addItem(new GuiItem(nameEdit, event -> {
            player.playSound(player.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0F, 0.8F);
            showNameChangeMenu(player, warp);
        }), 0, 0);

        ItemStack itemEdit = new ItemStack(warp.getCustomItem());
        ItemMeta itemEditMeta = itemEdit.getItemMeta();
        itemEditMeta.displayName(toUndecoratedMM("<white><b>Muokkaa materiaalia</b>"));
        itemEditMeta.lore(List.of(toUndecoratedMM("<white>Nyt: <#85FF00><lang:" + warp.getCustomItem().getItemTranslationKey() + ">")));
        itemEdit.setItemMeta(itemEditMeta);

        settingPane.addItem(new GuiItem(itemEdit, event -> {
            player.playSound(player.getLocation(), Sound.ITEM_SPYGLASS_USE, 0.8F, 0.8F);
            showItemChangeMenu(player, warp);
        }), 1, 0);

        ItemStack priceEdit = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta priceEditMeta = priceEdit.getItemMeta();
        priceEditMeta.displayName(toUndecoratedMM("<white><b>Muokkaa hintaa</b>"));
        priceEditMeta.lore(List.of(toUndecoratedMM("<white>Nyt: <#85FF00>" + warp.getPrice() + "Ⓖ")));
        priceEdit.setItemMeta(priceEditMeta);

        settingPane.addItem(new GuiItem(priceEdit, event -> {
            player.playSound(player.getLocation(), Sound.ITEM_SPYGLASS_USE, 0.8F, 0.8F);
            showPriceChangeMenu(player, warp);
        }), 2, 0);

        ItemStack delete = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.displayName(toUndecoratedMM("<red><b>Poista warp</b>"));
        deleteMeta.lore(List.of(toUndecoratedMM("<white>Klikkaamalla warppi poistuu heti.</white>")));
        delete.setItemMeta(deleteMeta);

        settingPane.addItem(new GuiItem(delete, event -> {
            player.performCommand("delwarp " + warp.getName());
            player.playSound(player.getLocation(), Sound.ITEM_BUNDLE_REMOVE_ONE, 1.0F, 0.8F);
            if (warpManager.getOwnedWarps(player.getUniqueId()).isEmpty()){
                showGUI(player);
            }else {
                showOwnWarps(player);
            }
        }), 4, 0);

        StaticPane navigationPane = new StaticPane(0, 4, 9, 1);

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(toUndecoratedMM("<white><b>Peruuta<b>"));
        back.setItemMeta(backMeta);
        navigationPane.addItem(new GuiItem(back, event -> {
            showOwnWarps(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, MAX_VALUE, 1F);

        }), 1, 0);

        gui.addPane(navigationPane);
        gui.addPane(settingPane);

        gui.update();
    }

    public void showNameChangeMenu(Player player, Warp warp) {
        AnvilGui gui = new AnvilGui(ComponentHolder.of(toUndecoratedMM("<dark_green><b>Syötä nimi...")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        //
        ItemStack itemStack1 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta itemMeta1 = itemStack1.getItemMeta();
        itemMeta1.displayName(Component.text(warp.getName()));
        itemStack1.setItemMeta(itemMeta1);

        GuiItem item1 = new GuiItem(itemStack1, event -> event.setCancelled(true));
        StaticPane pane = new StaticPane(0, 0, 1, 1);
        pane.addItem(item1, 0, 0);
        gui.getFirstItemComponent().addPane(pane);

        ItemStack done = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta doneMeta = done.getItemMeta();
        doneMeta.displayName(toUndecoratedMM("<green>Valmis"));
        done.setItemMeta(doneMeta);

        GuiItem doneItem = new GuiItem(done, event -> {
            if (gui.getRenameText().matches("[\\p{L}\\p{N}]+") && gui.getRenameText().length() <= 16) {
                player.sendMessage("Vaihdettu nimi: " + gui.getRenameText());
                warpManager.editWarpName(warp, gui.getRenameText());
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, MAX_VALUE, 1F);
                showWarpEditMenu(player, warp);
            } else {
                player.sendMessage("Nimi voi sisältää vain aakkosia ja numeroita, ja se voi olla maks. 16 kirjainta pitkä.");
            }
        });

        StaticPane pane3 = new StaticPane(0, 0, 1, 1);
        pane3.addItem(doneItem, 0, 0);
        gui.getResultComponent().addPane(pane3);

        gui.update();
    }
    public void showItemChangeMenu(Player player, Warp warp) {
        ChestGui gui = new ChestGui(5, ComponentHolder.of(toUndecoratedMM("<dark_green><b>Valitse materiaali...")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "111AAA111"
        );
        PatternPane border = new PatternPane(0, 0, 9, 5, Pane.Priority.LOWEST, pattern);
        ItemStack background = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));
        gui.addPane(border);

        PaginatedPane pages = new PaginatedPane(1, 1, 7, 3);
        ArrayList<ItemStack> items = new ArrayList<>();
        World world = warp.getLocation().getWorld();
        for (Material material : Material.values()) {
            if (material.isLegacy() || material == Material.AIR || !material.isEnabledByFeature(world)) {
                continue;
            }
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(toUndecoratedMM("<#85FF00><lang:" + material.getItemTranslationKey() + "></#85FF00>"));
            meta.lore(List.of(toUndecoratedMM("<white>Klikkaa valitaksesi</white>")));
            item.setItemMeta(meta);
            items.add(item);
        }
        pages.populateWithItemStacks(items);
        gui.addPane(pages);

        pages.setOnClick(event -> {
            if (event.getCurrentItem() == null) return;
            player.sendMessage("Warpin uusi itemi asetettu.");
            warpManager.editWarpItem(warp, event.getCurrentItem().getType());
            player.playSound(player.getLocation(), Sound.BLOCK_BEEHIVE_ENTER, 1.1F, 0.7F);
            showWarpEditMenu(player, warp);
        });


        StaticPane navigationPane = new StaticPane(0, 4, 9, 1);

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(toUndecoratedMM("<white><b>Peruuta<b>"));
        back.setItemMeta(backMeta);
        navigationPane.addItem(new GuiItem(back, event -> {
            showWarpEditMenu(player, warp);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, MAX_VALUE, 1F);

        }), 1, 0);

        ItemStack previous = new ItemStack(Material.MANGROVE_BUTTON);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(toUndecoratedMM("<b>Takaisin"));
        previous.setItemMeta(previousMeta);
        navigationPane.addItem(new GuiItem(previous, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);

                gui.update();
            }
        }), 3, 0);
        ItemStack next = new ItemStack(Material.WARPED_BUTTON);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(toUndecoratedMM("<b>Seuraava"));
        next.setItemMeta(nextMeta);
        navigationPane.addItem(new GuiItem(next, event -> {
            if (pages.getPage() < pages.getPages() - 8) {
                pages.setPage(pages.getPage() + 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                gui.update();
            }
        }), 5, 0);
        gui.addPane(navigationPane);

        gui.update();
    }

    public void showPriceChangeMenu(Player player, Warp warp) {
        ChestGui gui = new ChestGui(5, ComponentHolder.of(toUndecoratedMM("<dark_green><b>Syötä hinta...")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "111AAA111"
        );
        PatternPane border = new PatternPane(0, 0, 9, 5, Pane.Priority.LOWEST, pattern);
        ItemStack background = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));
        gui.addPane(border);

        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder secretBuilder = new StringBuilder();
        Pattern pattern1 = new Pattern(
                "123",
                "456",
                "789",
                "N0N"
        );

        PatternPane numberPane = new PatternPane(3, 1, 3, 4, pattern1);
        ItemStack item = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(toUndecoratedMM("<#85FF00><b>" + "?")); // Set a placeholder display name

        for (int i = 1; i <= 9; i++) {
            String numberString = String.valueOf(i);
            char character = numberString.charAt(0); // Get first character (character code)
            itemMeta.displayName(toUndecoratedMM("<#85FF00><b>" + numberString)); // Set display name with string
            item.setItemMeta(itemMeta);
            item.setAmount(i);
            int finalI = i;
            numberPane.bindItem(character, new GuiItem(item.clone(), event -> {
                secretBuilder.append(finalI);
                if (stringBuilder.length() < 3) {
                    stringBuilder.append(finalI);
                    player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.8F, 0.8F);
                    gui.setTitle(ComponentHolder.of(toUndecoratedMM("<dark_green><b>Hinta: " + stringBuilder + "</b>Ⓖ")));
                    gui.update();
                } else {
                    player.sendMessage("Warpin hinta voi olla korkeintaan 3 lukua pitkä (max 999).");
                }
            }));
        }
        itemMeta.displayName(toUndecoratedMM("<#85FF00><b>0")); // Set a placeholder display name
        item.setItemMeta(itemMeta);
        item.setAmount(1);
        numberPane.bindItem('0', new GuiItem(item.clone(), event -> {
            secretBuilder.append(0);
            if (stringBuilder.length() < 3) {
                stringBuilder.append(0);
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.8F, 0.8F);
                gui.setTitle(ComponentHolder.of(toUndecoratedMM("<dark_green><b>Hinta: " + stringBuilder + "</b>Ⓖ")));
                gui.update();
            } else {
                player.sendMessage("Warpin hinta voi olla korkeintaan 3 lukua pitkä (max 999).");
            }
        }));

        StaticPane navigationPane = new StaticPane(0, 4, 9, 1);

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(toUndecoratedMM("<white><b>Peruuta<b>"));
        back.setItemMeta(backMeta);
        navigationPane.addItem(new GuiItem(back, event -> {
            showWarpEditMenu(player, warp);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, MAX_VALUE, 1F);
            gui.update();

        }), 1, 0);
        ItemStack remove = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta removeMeta = remove.getItemMeta();
        removeMeta.displayName(toUndecoratedMM("<red><b>-<b>"));
        remove.setItemMeta(removeMeta);
        navigationPane.addItem(new GuiItem(remove, event -> {
            secretBuilder.append("B");
            if (!stringBuilder.isEmpty()) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.8F, 0.8F);
                if (stringBuilder.isEmpty()){
                    gui.setTitle(ComponentHolder.of(toMM("<dark_green><b>Syötä hinta...")));
                } else {
                    gui.setTitle(ComponentHolder.of(toMM("<dark_green><b>Hinta: " + stringBuilder + "</b>Ⓖ")));
                }
                gui.update();
            }
        }), 3, 0);
        ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.displayName(toUndecoratedMM("<green><b>Valmis<b>"));
        confirm.setItemMeta(confirmMeta);
        navigationPane.addItem(new GuiItem(confirm, event -> {
            secretBuilder.append("A");
            if (secretBuilder.toString().equals("22884646BA")){
                if (!warpManager.getHasDoneSecret(player)) {
                    warpManager.setHasDoneSecret(player);
                    player.sendMessage("Ohhoh! Painoit konami koodin, sait 400Ⓖ. ");
                    SelviytymisHarpake.getEconomy().depositPlayer(player, 400);
                    player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, MAX_VALUE, 1F);
                    player.closeInventory();
                }
                return;
            }
            if (!stringBuilder.isEmpty()) {
                warpManager.updateWarpPrice(player, warp, Integer.parseInt(stringBuilder.toString()));
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, MAX_VALUE, 1F);
                showWarpEditMenu(player, warp);
            }
        }), 5, 0);

        gui.addPane(numberPane);
        gui.addPane(navigationPane);
        gui.update();
    }
}