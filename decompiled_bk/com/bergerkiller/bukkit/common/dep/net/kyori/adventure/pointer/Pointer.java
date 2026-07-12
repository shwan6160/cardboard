/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.PointerImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public interface Pointer<V>
extends Examinable {
    @NotNull
    public static <V> Pointer<V> pointer(@NotNull Class<V> type, @NotNull Key key) {
        return new PointerImpl<V>(type, key);
    }

    @NotNull
    public Class<V> type();

    @NotNull
    public Key key();

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("type", this.type()), ExaminableProperty.of("key", this.key()));
    }
}

