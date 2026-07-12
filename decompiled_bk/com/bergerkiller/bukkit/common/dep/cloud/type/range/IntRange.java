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
public interface IntRange
extends Range<Integer> {
    public int minInt();

    public int maxInt();

    @Override
    default public @NonNull Integer min() {
        return this.minInt();
    }

    @Override
    default public @NonNull Integer max() {
        return this.maxInt();
    }
}

