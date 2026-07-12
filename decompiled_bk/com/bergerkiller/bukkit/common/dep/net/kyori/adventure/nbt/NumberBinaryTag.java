/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagType;
import org.jetbrains.annotations.NotNull;

public interface NumberBinaryTag
extends BinaryTag {
    @NotNull
    public BinaryTagType<? extends NumberBinaryTag> type();

    public byte byteValue();

    public double doubleValue();

    public float floatValue();

    public int intValue();

    public long longValue();

    public short shortValue();

    @NotNull
    public Number numberValue();
}

