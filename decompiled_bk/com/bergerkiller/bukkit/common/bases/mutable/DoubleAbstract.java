/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class DoubleAbstract {
    public abstract double get();

    public abstract DoubleAbstract set(double var1);

    public double squared() {
        double value = this.get();
        return value * value;
    }

    public double abs() {
        return Math.abs(this.get());
    }

    public int block() {
        return this.getFloor();
    }

    public DoubleAbstract setZero() {
        return this.set(0.0);
    }

    public int getFloor() {
        return MathUtil.floor(this.get());
    }

    public int chunk() {
        return MathUtil.toChunk(this.get());
    }

    public DoubleAbstract clamp(double limit) {
        return this.set(this.getClamped(limit));
    }

    public DoubleAbstract clamp(double min, double max) {
        return this.set(this.getClamped(min, max));
    }

    public double getClamped(double limit) {
        return MathUtil.clamp(this.get(), limit);
    }

    public double getClamped(double min, double max) {
        return MathUtil.clamp(this.get(), min, max);
    }

    public DoubleAbstract add(double value) {
        return this.set(this.get() + value);
    }

    public DoubleAbstract subtract(double value) {
        return this.set(this.get() - value);
    }

    public DoubleAbstract multiply(double value) {
        return this.set(this.get() * value);
    }

    public DoubleAbstract divide(double value) {
        return this.set(this.get() / value);
    }

    public DoubleAbstract fixNaN() {
        return this.set(MathUtil.fixNaN(this.get()));
    }

    public DoubleAbstract fixNaN(double def) {
        return this.set(MathUtil.fixNaN(this.get(), def));
    }

    public boolean equals(double value) {
        return this.get() == value;
    }

    public boolean equals(Object value) {
        if (value instanceof Number) {
            return this.equals(((Number)value).doubleValue());
        }
        if (value instanceof DoubleAbstract) {
            return this.equals(((DoubleAbstract)value).get());
        }
        return false;
    }

    public String toString() {
        return Double.toString(this.get());
    }
}

