package net.gahvila.survival.Homes;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.gahvila.survival.survival;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class HomeMenu {
    private final HomeManager homeManager;


    public HomeMenu(HomeManager homeManager) {
        this.homeManager = homeManager;

    }

    public void show(Player player) {
        player.showDialog(createHomesDialog(player));
    }

    public void showEdit(Player player, boolean openedFromHomesMenu) {
        player.showDialog(createEditHomesDialog(player, openedFromHomesMenu));
    }

    public void showSetHome(Player player, boolean openedFromHomesMenu) {
        player.showDialog(createSetHomeDialog(player, openedFromHomesMenu));
    }

    public void showDelhome(Player player, String home, boolean openedFromHomesMenu) {
        player.showDialog(createDelHomeDialog(player, home, openedFromHomesMenu));
    }

    public void showRenameHome(Player player, String home, boolean openedFromHomesMenu) {
        player.showDialog(createRenameHomeDialog(player, home, openedFromHomesMenu));
    }

    public void showHomesEditList(Player player, boolean renameOrDelete, boolean openedFromHomesMenu) {
        player.showDialog(createHomesEditListDialog(player, renameOrDelete, openedFromHomesMenu));
    }

    private Dialog createHomesDialog(Player player) {
        List<ActionButton> buttons = new ArrayList<>();

        buttons.add(ActionButton.builder(toMM("<u>Muokkaa"))
                .width(120)
                .tooltip(toMM("Aseta uusi koti nykyiseen sijaintiisi"))
                .action(DialogAction.customClick((response, audience) -> {
                            showEdit(player, true);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        buttons.add(ActionButton.builder(toMM("<u>Muokkaa"))
                .width(1)
                .action(DialogAction.customClick((response, audience) -> {
                            show(player);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());
        if (homeManager.getHomes(player.getUniqueId()).isEmpty()) {
            buttons.add(
                    ActionButton.builder(toMM("<red>Sinulla ei ole koteja asetettuna"))
                            .tooltip(toMM("Et ole asettanut kotia. Voit asettaa kodin yllä olevasta <u>Muokkaa</u> napista."))
                            .width(175)
                            .action(DialogAction.customClick((response, audience) -> {
                                        show(player);
                                    },
                                    ClickCallback.Options.builder().build()
                            ))
                            .build()
            );
        } else {
            for (String home : homeManager.getHomes(player.getUniqueId())) {
                    String homeName;
                    switch (homeManager.getHome(player.getUniqueId(), home).getWorld().getEnvironment()) {
                        case NETHER -> homeName = "<#ffdddd>" + home;
                        case THE_END ->
                                homeName = "<light_purple>" + home; //not possible to set a home in the end, but accounting for it anyway
                        default -> homeName = "<#ddffdd>" + home;
                    }
                    buttons.add(
                            ActionButton.builder(toMM(homeName))
                                    .tooltip(toMM(homeName))
                                    .width(120)
                                    .action(DialogAction.customClick((response, audience) -> {
                                                Location homeLocation = homeManager.getHome(player.getUniqueId(), home);
                                                if (homeLocation != null) {
                                                    player.teleportAsync(homeLocation);
                                                    player.sendMessage(toMM("<white>Sinut teleportattiin kotiin</white> <#85FF00>" + homeName + "</#85FF00>."));
                                                    Bukkit.getServer().getScheduler().runTaskLater(survival.instance, new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 0.5F, 1F);
                                                        }
                                                    }, 5);
                                                } else {
                                                    player.closeInventory();
                                                    player.sendMessage("Tuota kotia ei ole olemassa. Mitä duunaat?");
                                                }
                                            },
                                            ClickCallback.Options.builder().build()
                                    ))
                                    .build()
                    );
                }
            }

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Kodit</b>")).build())
                .type(DialogType.multiAction(
                        buttons,
                        ActionButton.builder(toMM("Sulje valikko")).build(),
                        1
                )));
    }

    private Dialog createEditHomesDialog(Player player, boolean openedFromHomesMenu) {
        List<ActionButton> buttons = new ArrayList<>();

        buttons.add(ActionButton.builder(toMM("Aseta uusi koti"))
                .width(120)
                .tooltip(toMM("Aseta uusi koti nykyiseen sijaintiisi"))
                .action(DialogAction.customClick((response, audience) -> {
                            showSetHome(player, openedFromHomesMenu);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        buttons.add(ActionButton.builder(toMM("Poista koti"))
                .width(120)
                .tooltip(toMM("Avaa listan, josta voit valita kodin minkä poistaa"))
                .action(DialogAction.customClick((response, audience) -> {
                            showHomesEditList(player, true, openedFromHomesMenu);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        buttons.add(ActionButton.builder(toMM("Uudelleennimeä koti"))
                .width(120)
                .tooltip(toMM("Nimeä koti uudelleen"))
                .action(DialogAction.customClick((response, audience) -> {
                            showHomesEditList(player, false, openedFromHomesMenu);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Kodit | Muokkaa</b>"))
                        .body(List.of(
                                DialogBody.plainMessage(toMM("Mitä tehdään?")),
                                DialogBody.plainMessage(toMM(""))

                        ))
                        .build())
                .type(DialogType.multiAction(
                        buttons,
                        ActionButton.builder(toMM(openedFromHomesMenu ? "Päävalikko" : "Sulje valikko"))
                                .action(DialogAction.customClick(
                                        (response, audience) -> {
                                            if (openedFromHomesMenu) {
                                                show(player);
                                            }
                                        },
                                        ClickCallback.Options.builder().build()
                                ))
                                .build(),
                        1
                )));
    }

    // boolean renameOrDelete: false=rename, true=delete
    private Dialog createHomesEditListDialog(Player player, boolean renameOrDelete, boolean openedFromHomesMenu) {
        List<ActionButton> buttons = new ArrayList<>();

        for (String home : homeManager.getHomes(player.getUniqueId())) {
            String homeName;
            switch (homeManager.getHome(player.getUniqueId(), home).getWorld().getEnvironment()) {
                case NETHER -> homeName = "<#ffdddd>" + home;
                case THE_END ->
                        homeName = "<light_purple>" + home; //not possible to set a home in the end, but accounting for it anyway
                default -> homeName = "<#ddffdd>" + home;
            }
            buttons.add(
                    ActionButton.builder(toMM(homeName))
                            .tooltip(toMM(homeName))
                            .width(120)
                            .action(DialogAction.customClick((response, audience) -> {
                                        if (renameOrDelete) {
                                            showDelhome(player, home, openedFromHomesMenu);
                                        } else {
                                            showRenameHome(player, home, openedFromHomesMenu);
                                        }
                                    },
                                    ClickCallback.Options.builder().build()
                            ))
                            .build()
            );
        }

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Kodit</b>"))
                        .body(List.of(
                                DialogBody.plainMessage(toMM("Mitä kotia haluat muokata?"))
                        ))
                        .build())
                .type(DialogType.multiAction(
                        buttons,
                        ActionButton.builder(toMM("Sulje valikko"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            showEdit(player, openedFromHomesMenu);
                                        },
                                        ClickCallback.Options.builder().build()))
                                .build(),
                        1
                )));
    }

    private Dialog createSetHomeDialog(Player player, boolean openedFromHomesMenu) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Kodit | Muokkaa</b>"))
                        .body(Arrays.asList(
                                DialogBody.plainMessage(toMM("Olet asettamassa kotia.")),
                                DialogBody.plainMessage(toMM("<white>Kodin nimi voi olla maksimissaan</white> <#85FF00>30 kirjainta</#85FF00> <white>pitkä, ja se voi sisältää vain <#85FF00>aakkosia</#85FF00><white>,</white> <#85FF00>numeroita<#85FF00/><white> ja seuraavia välimerkkejä: <#85FF00>! ? , . - _</#85FF00>.</white>")),
                                DialogBody.plainMessage(toMM("Komento asettaa kodin tarkasti siihen kohtaan missä seisot, mukaanlukien sen, minne suuntaan katsot."))
                        ))
                        .inputs(Arrays.asList(
                                DialogInput.text("homeName", Component.text("Kodin nimi"))
                                        .maxLength(30)
                                        .build()
                        ))
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.builder(Component.text("Aseta koti"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            player.performCommand("sethome " + response.getText("homeName"));
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build(),
                        ActionButton.builder(Component.text("Peruuta"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            showEdit(player, openedFromHomesMenu);
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build()

                )));
    }

    private Dialog createRenameHomeDialog(Player player, String oldName, boolean openedFromHomesMenu) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Kodit | Muokkaa</b>"))
                        .body(Arrays.asList(
                                DialogBody.plainMessage(toMM("Olet muokkaamassa kodin nimeä.")),
                                DialogBody.plainMessage(toMM("<white>Kodin nimi voi olla maksimissaan</white> <#85FF00>30 kirjainta</#85FF00> <white>pitkä, ja se voi sisältää vain <#85FF00>aakkosia</#85FF00><white>,</white> <#85FF00>numeroita<#85FF00/><white> ja seuraavia välimerkkejä: <#85FF00>! ? , . - _</#85FF00>.</white>"))
                        ))
                        .inputs(Arrays.asList(
                                DialogInput.text("homeName", Component.text("Kodin uusi nimi"))
                                        .initial(oldName)
                                        .maxLength(30)
                                        .build()
                        ))
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.builder(Component.text("Muokkaa kodin nimi"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            String newName = response.getText("homeName");
                                            if (homeManager.getHomes(player.getUniqueId()).contains(oldName) && !(homeManager.getHomes(player.getUniqueId()).contains(newName))) {
                                                homeManager.editHomeName(player.getUniqueId(), oldName, response.getText("homeName"));
                                                player.sendMessage(toMM("<white>Kodin <#85FF00>" + oldName + " <white>nimi muutettu: <#85FF00>" + newName));
                                            }
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build(),
                        ActionButton.builder(Component.text("Peruuta"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            showEdit(player, openedFromHomesMenu);
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build()

                )));
    }

    private Dialog createDelHomeDialog(Player player, String home, boolean openedFromHomesMenu) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Kodit | Muokkaa</b>"))
                        .body(Arrays.asList(
                                DialogBody.plainMessage(toMM("Olet Poistamassa kotia:")),
                                DialogBody.plainMessage(toMM(home)),
                                DialogBody.plainMessage(toMM("")),
                                DialogBody.plainMessage(toMM("Oletko varma?"))

                        ))
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.builder(Component.text("Poista koti"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            player.performCommand("delhome " + home);
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build(),
                        ActionButton.builder(Component.text("Peruuta"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            showEdit(player, openedFromHomesMenu);
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build()

                )));
    }
}