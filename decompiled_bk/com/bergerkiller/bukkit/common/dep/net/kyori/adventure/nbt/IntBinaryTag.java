/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagTypes;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.IntBinaryTagImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.NumberBinaryTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface IntBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static IntBinaryTag intBinaryTag(int value) {
        return new IntBinaryTagImpl(value);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    public static IntBinaryTag of(int value) {
        return new IntBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<IntBinaryTag> type() {
        return BinaryTagTypes.INT;
    }

    public int value();
}

