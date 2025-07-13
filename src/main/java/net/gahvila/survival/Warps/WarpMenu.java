package net.gahvila.survival.Warps;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Single;
import net.gahvila.inventoryframework.adventuresupport.ComponentHolder;
import net.gahvila.inventoryframework.gui.GuiItem;
import net.gahvila.inventoryframework.gui.type.AnvilGui;
import net.gahvila.inventoryframework.gui.type.ChestGui;
import net.gahvila.inventoryframework.pane.PaginatedPane;
import net.gahvila.inventoryframework.pane.Pane;
import net.gahvila.inventoryframework.pane.PatternPane;
import net.gahvila.inventoryframework.pane.StaticPane;
import net.gahvila.inventoryframework.pane.util.Pattern;
import net.gahvila.survival.survival;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Float.MAX_VALUE;
import static net.gahvila.gahvilacore.GahvilaCore.instance;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toUndecoratedMM;

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
        NamespacedKey key = new NamespacedKey(survival.instance, "gahvilasurvival");
        for (Warp warp : warpManager.getWarps(Optional.of(player))) {
            Date currentTime = new Date(warp.getCreationDate());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);

            ItemStack item = new ItemStack(warp.getCustomItem());
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, warp.getName());
            meta.displayName(toUndecoratedMM("<" + warp.getColor().getColor() + ">" + warp.getName()));
            meta.lore(List.of(toUndecoratedMM("<white>Omistaja: <yellow>" + warp.getOwnerName()),
                    toUndecoratedMM("<white>Käyttökerrat: <yellow>" + warp.getUses()),
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
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, MAX_VALUE, 1F);
            player.closeInventory();
            player.teleportAsync(warp.get().getLocation());
            player.sendMessage(toMM("Sinut teleportattiin warppiin <#85FF00>" + warp.get().getName() + "</#85FF00>."));
            Bukkit.getServer().getScheduler().runTaskLater(survival.instance, new Runnable() {
                @Override
                public void run() {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, MAX_VALUE, 1F);
                }
            }, 5);
            if (!player.getName().equals(warp.get().getOwnerName())){
                warpManager.addUses(warp.get());
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
        sortingMeta.displayName(toUndecoratedMM("<white><b>Järjestys: " + warpManager.getSorting(player.getUniqueId()).getDisplayName()));
        sorting.setItemMeta(sortingMeta);
        navigationPane.addItem(new GuiItem(sorting, event -> {
            warpManager.changeSorting(player);
            player.playSound(player.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 0.8F, 1F);
            player.closeInventory();
            showGUI(player);
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
        NamespacedKey key = new NamespacedKey(survival.instance, "gahvilasurvival");
        for (Warp warp : warpManager.getOwnedWarps(player.getUniqueId())) {
            Date currentTime = new Date(warp.getCreationDate());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);

            ItemStack item = new ItemStack(warp.getCustomItem());
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, warp.getName());
            meta.displayName(toUndecoratedMM("<" + warp.getColor().getColor() + ">" + warp.getName()));
            meta.lore(List.of(toUndecoratedMM("<green><b>Klikkaa muokataksesi"),
                    toUndecoratedMM("<white>Käyttökerrat: <yellow>" + warp.getUses()),
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
        sortingMeta.displayName(toUndecoratedMM("<white><b>Järjestys: " + warpManager.getSorting(player.getUniqueId()).getDisplayName()));
        sorting.setItemMeta(sortingMeta);
        navigationPane.addItem(new GuiItem(sorting, event -> {
            warpManager.changeSorting(player);
            player.playSound(player.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 0.8F, 1F);
            player.closeInventory();
            showGUI(player);
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
            player.showDialog(createNameChangeDialog(player, warp));
        }), 0, 0);

        ItemStack colorEdit = new ItemStack(Material.LIME_WOOL);
        ItemMeta colorEditMeta = colorEdit.getItemMeta();
        colorEditMeta.displayName(toUndecoratedMM("<white><b>Muokkaa väriä</b>"));
        colorEditMeta.lore(List.of(toUndecoratedMM("<white>Nyt: <" + warp.getColor().getColor() + ">" + warp.getColor().getDisplayName())));
        colorEdit.setItemMeta(colorEditMeta);

        settingPane.addItem(new GuiItem(colorEdit, event -> {
            player.playSound(player.getLocation(), Sound.BLOCK_WOOL_PLACE, 1.0F, 0.8F);
            showColorChangeMenu(player, warp);
        }), 1, 0);

        ItemStack itemEdit = new ItemStack(warp.getCustomItem());
        ItemMeta itemEditMeta = itemEdit.getItemMeta();
        itemEditMeta.displayName(toUndecoratedMM("<white><b>Muokkaa materiaalia</b>"));
        itemEditMeta.lore(List.of(toUndecoratedMM("<white>Nyt: <#85FF00><lang:" + warp.getCustomItem().getItemTranslationKey() + ">")));
        itemEdit.setItemMeta(itemEditMeta);

        settingPane.addItem(new GuiItem(itemEdit, event -> {
            player.playSound(player.getLocation(), Sound.ITEM_SPYGLASS_USE, 0.8F, 0.8F);
            showItemChangeMenu(player, warp);
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

    private Dialog createNameChangeDialog(Player player, Warp warp) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Syötä nimi...</b>"))
                        .body(Arrays.asList(
                                DialogBody.plainMessage(toMM("Olet muokkaamassa warpin nimeä.")),
                                DialogBody.plainMessage(toMM("<white>Nimi voi sisältää vain aakkosia ja numeroita, ja se voi olla maks. 16 kirjainta pitkä."))
                        ))
                        .inputs(Arrays.asList(
                                DialogInput.text("warpName", Component.text("Warpin uusi nimi"))
                                        .initial(warp.getName())
                                        .maxLength(16)
                                        .build()
                        ))
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.builder(Component.text("Muokkaa warpin nimi"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            String newName = response.getText("warpName");
                                            if (newName.matches("[\\p{L}\\p{N}]+") && newName.length() <= 16) {
                                                player.sendMessage("Vaihdettu nimi: " + newName);
                                                warpManager.editWarpName(warp, newName);
                                                showWarpEditMenu(player, warp);
                                            } else {
                                                player.sendMessage("Nimi voi sisältää vain aakkosia ja numeroita, ja se voi olla maks. 16 kirjainta pitkä.");
                                            }
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build(),
                        ActionButton.builder(Component.text("Peruuta"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            showWarpEditMenu(player, warp);
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build()

                )));
    }

    public void showColorChangeMenu(Player player, Warp warp) {
        ChestGui gui = new ChestGui(5, ComponentHolder.of(toUndecoratedMM("<dark_green><b>Valitse väri...")));
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
        NamespacedKey key = new NamespacedKey(instance, "color");
        for (Single single : Single.values()) {
            if (player.hasPermission(single.getPermissionNode())) {
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, single.toString());
                meta.displayName(toUndecoratedMM("<" + single.getColor() + ">" + single.getDisplayName()));
                item.setItemMeta(meta);
                items.add(item);
            }
        }
        pages.populateWithItemStacks(items);
        gui.addPane(pages);

        pages.setOnClick(event -> {
            if (event.getCurrentItem() == null) return;
            String data = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            warpManager.editWarpColor(warp, Single.valueOf(data));
            player.playSound(player.getLocation(), Sound.BLOCK_BEEHIVE_ENTER, 1.1F, 0.7F);
            player.sendMessage("Warpin uusi väri asetettu.");
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
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                gui.update();
            }
        }), 5, 0);
        gui.addPane(navigationPane);

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
            if (material.isLegacy() || material == Material.AIR ||
                    !material.isItem() || !world.isEnabled(material.asItemType())) continue;

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
}