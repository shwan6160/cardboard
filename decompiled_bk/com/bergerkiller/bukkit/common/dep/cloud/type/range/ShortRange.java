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
public interface ShortRange
extends Range<Short> {
    public short minShort();

    public short maxShort();

    @Override
    default public @NonNull Short min() {
        return this.minShort();
    }

    @Override
    default public @NonNull Short max() {
        return this.maxShort();
    }
}

