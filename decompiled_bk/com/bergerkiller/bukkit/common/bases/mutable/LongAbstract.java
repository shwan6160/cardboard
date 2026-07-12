/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class LongAbstract {
    public abstract long get();

    public abstract LongAbstract set(long var1);

    public long squared() {
        long value = this.get();
        return value * value;
    }

    public long abs() {
        return Math.abs(this.get());
    }

    public LongAbstract setZero() {
        return this.set(0L);
    }

    public LongAbstract clamp(long limit) {
        return this.set(this.getClamped(limit));
    }

    public LongAbstract clamp(long min, long max) {
        return this.set(this.getClamped(min, max));
    }

    public long getClamped(long limit) {
        return MathUtil.clamp(this.get(), limit);
    }

    public long getClamped(long min, long max) {
        return MathUtil.clamp(this.get(), min, max);
    }

    public LongAbstract add(long value) {
        return this.set(this.get() + value);
    }

    public LongAbstract subtract(long value) {
        return this.set(this.get() - value);
    }

    public LongAbstract multiply(long value) {
        return this.set(this.get() * value);
    }

    public LongAbstract divide(long value) {
        return this.set(this.get() / value);
    }

    public boolean isMod(long modulus) {
        return this.get() % modulus == 0L;
    }

    public boolean equals(long value) {
        return this.get() == value;
    }

    public boolean equals(Object value) {
        if (value instanceof Number) {
            return this.equals(((Number)value).intValue());
        }
        if (value instanceof LongAbstract) {
            return this.equals(((LongAbstract)value).get());
        }
        return false;
    }

    public String toString() {
        return Long.toString(this.get());
    }
}

