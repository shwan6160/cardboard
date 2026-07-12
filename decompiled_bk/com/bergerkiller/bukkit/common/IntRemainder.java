/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.CircularInteger;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import java.util.Arrays;

public class IntRemainder {
    private double contained = 0.0;
    private final CircularInteger counter;
    private final int[] values;

    public IntRemainder(double initialvalue, int decimals) {
        if (decimals < 1) {
            throw new IllegalArgumentException("Decimal count needs to be higher than 0");
        }
        this.values = new int[10 * decimals];
        this.counter = new CircularInteger(this.values.length);
        this.set(initialvalue);
    }

    public void set(double value) {
        this.contained = value;
        int floor = MathUtil.floor(value);
        Arrays.fill(this.values, floor);
        floor = (int)((value - (double)floor) * (double)this.values.length);
        int i = 0;
        while (i < floor) {
            int n = i++;
            this.values[n] = this.values[n] + 1;
        }
    }

    public int next() {
        return this.values[this.counter.next()];
    }

    public double get() {
        return this.contained;
    }

    public int[] getValues() {
        return this.values;
    }
}

