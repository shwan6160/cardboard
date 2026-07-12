/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.ClickEvent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.HoverEventSource;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextDecoration;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.ARGBLike;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface StyleSetter<T extends StyleSetter<?>> {
    @NotNull
    public T font(@Nullable Key var1);

    @NotNull
    public T color(@Nullable TextColor var1);

    @NotNull
    public T colorIfAbsent(@Nullable TextColor var1);

    @NotNull
    public T shadowColor(@Nullable ARGBLike var1);

    @NotNull
    public T shadowColorIfAbsent(@Nullable ARGBLike var1);

    @NotNull
    default public T decorate(@NotNull TextDecoration decoration) {
        return this.decoration(decoration, TextDecoration.State.TRUE);
    }

    @NotNull
    default public T decorate(TextDecoration ... decorations) {
        EnumMap<TextDecoration, TextDecoration.State> map = new EnumMap<TextDecoration, TextDecoration.State>(TextDecoration.class);
        int length = decorations.length;
        for (int i = 0; i < length; ++i) {
            map.put(decorations[i], TextDecoration.State.TRUE);
        }
        return this.decorations(map);
    }

    @NotNull
    default public T decoration(@NotNull TextDecoration decoration, boolean flag) {
        return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
    }

    @NotNull
    public T decoration(@NotNull TextDecoration var1, @NotNull TextDecoration.State var2);

    @NotNull
    public T decorationIfAbsent(@NotNull TextDecoration var1, @NotNull TextDecoration.State var2);

    @NotNull
    public T decorations(@NotNull Map<TextDecoration, TextDecoration.State> var1);

    @NotNull
    default public T decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
        return this.decorations(decorations.stream().collect(Collectors.toMap(Function.identity(), decoration -> TextDecoration.State.byBoolean(flag))));
    }

    @NotNull
    public T clickEvent(@Nullable ClickEvent var1);

    @NotNull
    public T hoverEvent(@Nullable HoverEventSource<?> var1);

    @NotNull
    public T insertion(@Nullable String var1);
}

