/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.type.range;

import com.bergerkiller.bukkit.common.dep.cloud.type.range.ByteRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.ByteRangeImpl;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.DoubleRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.DoubleRangeImpl;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.FloatRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.FloatRangeImpl;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.IntRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.IntRangeImpl;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.LongRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.LongRangeImpl;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.ShortRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.ShortRangeImpl;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface Range<N extends Number> {
    public @NonNull N min();

    public @NonNull N max();

    public static @NonNull ByteRange byteRange(byte min, byte max) {
        return ByteRangeImpl.of(min, max);
    }

    public static @NonNull DoubleRange doubleRange(double min, double max) {
        return DoubleRangeImpl.of(min, max);
    }

    public static @NonNull FloatRange floatRange(float min, float max) {
        return FloatRangeImpl.of(min, max);
    }

    public static @NonNull IntRange intRange(int min, int max) {
        return IntRangeImpl.of(min, max);
    }

    public static @NonNull LongRange longRange(long min, long max) {
        return LongRangeImpl.of(min, max);
    }

    public static @NonNull ShortRange shortRange(short min, short max) {
        return ShortRangeImpl.of(min, max);
    }
}

