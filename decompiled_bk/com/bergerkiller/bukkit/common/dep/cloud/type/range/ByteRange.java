/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.type.range;

import com.bergerkiller.bukkit.common.dep.cloud.type.range.Range;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@Value.Immutable
public interface ByteRange
extends Range<Byte> {
    public byte minByte();

    public byte maxByte();

    @Override
    default public @NonNull Byte min() {
        return this.minByte();
    }

    @Override
    default public @NonNull Byte max() {
        return this.maxByte();
    }
}

