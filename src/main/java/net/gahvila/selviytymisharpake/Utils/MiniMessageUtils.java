package net.gahvila.selviytymisharpake.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class MiniMessageUtils {

    public static @NotNull Component toMM(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }


    public static @NotNull Component toUndecoratedMM(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
