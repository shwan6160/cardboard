/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.BuildableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ScopedComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface KeybindComponent
extends BuildableComponent<KeybindComponent, Builder>,
ScopedComponent<KeybindComponent> {
    @NotNull
    public String keybind();

    @Override
    @Contract(pure=true)
    @NotNull
    public KeybindComponent keybind(@NotNull String var1);

    @Override
    @Contract(pure=true)
    @NotNull
    default public KeybindComponent keybind(@NotNull KeybindLike keybind) {
        return this.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind());
    }

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("keybind", this.keybind())), BuildableComponent.super.examinableProperties());
    }

    public static interface KeybindLike {
        @NotNull
        public String asKeybind();
    }

    public static interface Builder
    extends ComponentBuilder<KeybindComponent, Builder> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder keybind(@NotNull String var1);

        @Contract(pure=true)
        @NotNull
        default public Builder keybind(@NotNull KeybindLike keybind) {
            return this.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind());
        }
    }
}

