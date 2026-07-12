/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.BuildableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ScopedComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.stream.Stream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SelectorComponent
extends BuildableComponent<SelectorComponent, Builder>,
ScopedComponent<SelectorComponent> {
    @NotNull
    public String pattern();

    @Contract(pure=true)
    @NotNull
    public SelectorComponent pattern(@NotNull String var1);

    @Nullable
    public Component separator();

    @NotNull
    public SelectorComponent separator(@Nullable ComponentLike var1);

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("pattern", this.pattern()), ExaminableProperty.of("separator", this.separator())), BuildableComponent.super.examinableProperties());
    }

    public static interface Builder
    extends ComponentBuilder<SelectorComponent, Builder> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder pattern(@NotNull String var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder separator(@Nullable ComponentLike var1);
    }
}

