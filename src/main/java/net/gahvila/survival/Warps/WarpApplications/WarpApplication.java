package net.gahvila.survival.Warps.WarpApplications;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.gahvila.survival.Messages.Message;
import net.gahvila.survival.Warps.WarpManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class WarpApplication {
    private final WarpApplicationManager warpApplicationManager;

    public WarpApplication(WarpApplicationManager warpApplicationManager) {
        this.warpApplicationManager = warpApplicationManager;
    }

    public void show(Player player) {
        player.showDialog(createWarpApplicationDialog(player));
    }

    private Dialog createWarpApplicationDialog(Player player) {
        Location location = player.getLocation();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Warphakemus</b>"))
                        .body(Arrays.asList(
                                DialogBody.plainMessage(toMM("Olet asettamassa uutta warppia.<br>" +
                                        "Warpin asettaminen vaatii palvelimen ylläpidon hyväksynnän.<br>" +
                                        "<u>Tahallisesti haitallisten hakemusten lähettäminen johtaa rangaistukseen.</u><br><br>" +
                                        "Warpin sijainti asetaan siihen kohtaan, missä seisoit avatessasi tämän valikon.<br>" +
                                        "Syötä tarpeelliset tiedot ja lähetä hakemus, jonka jälkeen se otetaan käsittelyyn."))
                        ))
                        .inputs(Arrays.asList(
                                DialogInput.text("warpName", Component.text("Warpin nimi"))
                                        .maxLength(30)
                                        .build()
                        ))
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.builder(Component.text("Lähetä hakemus"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            String warpName = response.getText("warpName");
                                            try {
                                                warpApplicationManager.sendWarpApplication(player, warpName, location);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build(),
                        ActionButton.builder(Component.text("Peruuta"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            player.sendRichMessage(Message.WARP_SETWARP_CANCELED.getText());
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build()

                )));
    }
}
