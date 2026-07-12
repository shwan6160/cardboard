/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.BuildableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ScopedComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.ObjectContents;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public interface ObjectComponent
extends BuildableComponent<ObjectComponent, Builder>,
ScopedComponent<ObjectComponent> {
    @NotNull
    public ObjectContents contents();

    @NotNull
    public ObjectComponent contents(@NotNull ObjectContents var1);

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.concat(Stream.of(ExaminableProperty.of("contents", this.contents())), BuildableComponent.super.examinableProperties());
    }

    public static interface Builder
    extends ComponentBuilder<ObjectComponent, Builder> {
        @NotNull
        public Builder contents(@NotNull ObjectContents var1);
    }
}

