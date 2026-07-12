/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.GlobalTranslator;
import java.util.Locale;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;

final class ComponentHelper {
    public static final Pattern SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[^\\s\\w\\-]");

    private ComponentHelper() {
    }

    public static @NonNull Component highlight(@NonNull Component component, @NonNull TextColor highlightColor) {
        return component.replaceText(config -> {
            config.match(SPECIAL_CHARACTERS_PATTERN);
            config.replacement(match -> match.color(highlightColor));
        });
    }

    public static @NonNull Component repeat(@NonNull Component component, int repetitions) {
        TextComponent.Builder builder = Component.text();
        for (int i = 0; i < repetitions; ++i) {
            builder.append(component);
        }
        return builder.build();
    }

    public static int length(@NonNull Component component) {
        int length = 0;
        if (component instanceof TextComponent) {
            length += ((TextComponent)component).content().length();
        }
        Component translated = GlobalTranslator.render(component, Locale.getDefault());
        for (Component child : translated.children()) {
            length += ComponentHelper.length(child);
        }
        return length;
    }
}

