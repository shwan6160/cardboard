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
public interface LongRange
extends Range<Long> {
    public long minLong();

    public long maxLong();

    @Override
    default public @NonNull Long min() {
        return this.minLong();
    }

    @Override
    default public @NonNull Long max() {
        return this.maxLong();
    }
}

