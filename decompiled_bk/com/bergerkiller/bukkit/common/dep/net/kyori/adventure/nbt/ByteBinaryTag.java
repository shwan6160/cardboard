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
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ByteBinaryTagImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.NumberBinaryTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface ByteBinaryTag
extends NumberBinaryTag {
    public static final ByteBinaryTag ZERO = new ByteBinaryTagImpl(0);
    public static final ByteBinaryTag ONE = new ByteBinaryTagImpl(1);

    @NotNull
    public static ByteBinaryTag byteBinaryTag(byte value) {
        if (value == 0) {
            return ZERO;
        }
        if (value == 1) {
            return ONE;
        }
        return new ByteBinaryTagImpl(value);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    public static ByteBinaryTag of(byte value) {
        return ByteBinaryTag.byteBinaryTag(value);
    }

    @NotNull
    default public BinaryTagType<ByteBinaryTag> type() {
        return BinaryTagTypes.BYTE;
    }

    public byte value();
}

