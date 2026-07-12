/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.NBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.NBTComponentBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ScopedComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.stream.Stream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface EntityNBTComponent
extends NBTComponent<EntityNBTComponent, Builder>,
ScopedComponent<EntityNBTComponent> {
    @NotNull
    public String selector();

    @Contract(pure=true)
    @NotNull
    public EntityNBTComponent selector(@NotNull String var1);

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("selector", this.selector())), NBTComponent.super.examinableProperties());
    }

    public static interface Builder
    extends NBTComponentBuilder<EntityNBTComponent, Builder> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder selector(@NotNull String var1);
    }
}

