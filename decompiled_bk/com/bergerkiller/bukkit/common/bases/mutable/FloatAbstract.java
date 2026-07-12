/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class FloatAbstract {
    public abstract float get();

    public abstract FloatAbstract set(float var1);

    public float squared() {
        float value = this.get();
        return value * value;
    }

    public float abs() {
        return Math.abs(this.get());
    }

    public int block() {
        return this.getFloor();
    }

    public FloatAbstract setZero() {
        return this.set(0.0f);
    }

    public int getFloor() {
        return MathUtil.floor(this.get());
    }

    public int chunk() {
        return MathUtil.toChunk(this.get());
    }

    public FloatAbstract clamp(float limit) {
        return this.set(this.getClamped(limit));
    }

    public FloatAbstract clamp(float min, float max) {
        return this.set(this.getClamped(min, max));
    }

    public float getClamped(float limit) {
        return MathUtil.clamp(this.get(), limit);
    }

    public float getClamped(float min, float max) {
        return MathUtil.clamp(this.get(), min, max);
    }

    public FloatAbstract add(float value) {
        return this.set(this.get() + value);
    }

    public FloatAbstract subtract(float value) {
        return this.set(this.get() - value);
    }

    public FloatAbstract multiply(float value) {
        return this.set(this.get() * value);
    }

    public FloatAbstract divide(float value) {
        return this.set(this.get() / value);
    }

    public FloatAbstract fixNaN() {
        return this.set(MathUtil.fixNaN(this.get()));
    }

    public FloatAbstract fixNaN(float def) {
        return this.set(MathUtil.fixNaN(this.get(), def));
    }

    public boolean equals(float value) {
        return this.get() == value;
    }

    public boolean equals(Object value) {
        if (value instanceof Number) {
            return this.equals(((Number)value).doubleValue());
        }
        if (value instanceof FloatAbstract) {
            return this.equals(((FloatAbstract)value).get());
        }
        return false;
    }

    public String toString() {
        return Float.toString(this.get());
    }
}

