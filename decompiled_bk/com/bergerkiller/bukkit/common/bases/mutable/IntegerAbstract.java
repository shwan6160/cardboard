/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class IntegerAbstract {
    public abstract int get();

    public abstract IntegerAbstract set(int var1);

    public int squared() {
        int value = this.get();
        return value * value;
    }

    public int abs() {
        return Math.abs(this.get());
    }

    public IntegerAbstract setZero() {
        return this.set(0);
    }

    public int chunk() {
        return MathUtil.toChunk(this.get());
    }

    public IntegerAbstract clamp(int limit) {
        return this.set(this.getClamped(limit));
    }

    public IntegerAbstract clamp(int min, int max) {
        return this.set(this.getClamped(min, max));
    }

    public int getClamped(int limit) {
        return MathUtil.clamp(this.get(), limit);
    }

    public int getClamped(int min, int max) {
        return MathUtil.clamp(this.get(), min, max);
    }

    public IntegerAbstract add(int value) {
        return this.set(this.get() + value);
    }

    public IntegerAbstract subtract(int value) {
        return this.set(this.get() - value);
    }

    public IntegerAbstract multiply(int value) {
        return this.set(this.get() * value);
    }

    public IntegerAbstract divide(int value) {
        return this.set(this.get() / value);
    }

    public boolean isMod(int modulus) {
        return this.get() % modulus == 0;
    }

    public boolean equals(int value) {
        return this.get() == value;
    }

    public boolean equals(Object value) {
        if (value instanceof Number) {
            return this.equals(((Number)value).intValue());
        }
        if (value instanceof IntegerAbstract) {
            return this.equals(((IntegerAbstract)value).get());
        }
        return false;
    }

    public String toString() {
        return Integer.toString(this.get());
    }
}

