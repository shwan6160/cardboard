/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import java.util.NoSuchElementException;

public class FromToCounter {
    private int start;
    private int end;
    private int i;
    private int incr;

    public FromToCounter() {
        this.disable();
    }

    public FromToCounter(int start, int end) {
        this.reset(start, end);
    }

    public boolean hasNext() {
        return this.i != this.end + this.incr;
    }

    public int next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("No next elements is available");
        }
        int value = this.i;
        this.i += this.incr;
        return value;
    }

    public int get() {
        if (this.i == this.start) {
            throw new NoSuchElementException("A call to next() is required before this can be used");
        }
        return this.i - this.incr;
    }

    public void disable() {
        this.end = 0;
        this.start = 0;
        this.i = 0;
        this.incr = 0;
    }

    public void reset(int start, int end) {
        this.start = start;
        this.end = end;
        this.incr = end >= start ? 1 : -1;
        this.reset();
    }

    public void reset() {
        this.i = this.start;
    }
}

