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
public interface FloatRange
extends Range<Float> {
    public float minFloat();

    public float maxFloat();

    @Override
    default public @NonNull Float min() {
        return Float.valueOf(this.minFloat());
    }

    @Override
    default public @NonNull Float max() {
        return Float.valueOf(this.maxFloat());
    }
}

