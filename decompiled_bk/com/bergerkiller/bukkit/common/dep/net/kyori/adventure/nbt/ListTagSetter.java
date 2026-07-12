/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTag;
import org.jetbrains.annotations.NotNull;

public interface ListTagSetter<R, T extends BinaryTag> {
    @NotNull
    public R add(T var1);

    @NotNull
    public R add(Iterable<? extends T> var1);
}

