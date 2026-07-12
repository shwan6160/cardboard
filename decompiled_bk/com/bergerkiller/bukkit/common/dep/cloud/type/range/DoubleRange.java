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
public interface DoubleRange
extends Range<Double> {
    public double minDouble();

    public double maxDouble();

    @Override
    default public @NonNull Double min() {
        return this.minDouble();
    }

    @Override
    default public @NonNull Double max() {
        return this.maxDouble();
    }
}

