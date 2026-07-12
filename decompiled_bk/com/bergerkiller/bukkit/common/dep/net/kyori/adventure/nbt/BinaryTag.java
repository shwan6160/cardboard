/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagType;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import org.jetbrains.annotations.NotNull;

public interface BinaryTag
extends BinaryTagLike,
Examinable {
    @NotNull
    public BinaryTagType<? extends BinaryTag> type();

    @Override
    @NotNull
    default public BinaryTag asBinaryTag() {
        return this;
    }
}

