/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.Logging;

public class StopWatch {
    public static final StopWatch instance = new StopWatch();
    private long prevtime;
    private long prevdur;

    public StopWatch start() {
        this.prevtime = System.nanoTime();
        return this;
    }

    public StopWatch clear() {
        this.prevtime = 0L;
        this.prevdur = 0L;
        return this;
    }

    public double get() {
        return (double)this.prevdur / 1000000.0;
    }

    public double get(int scale) {
        return (double)this.prevdur / 1000000.0 / (double)scale;
    }

    public StopWatch set(long elapsednanotime, double strength) {
        this.prevdur = elapsednanotime = (long)((double)elapsednanotime + (1.0 - strength) * (double)(this.prevdur - elapsednanotime));
        return this;
    }

    public StopWatch set(long elapsednanotime) {
        return this.set(elapsednanotime, 1.0);
    }

    public StopWatch next() {
        return this.next(1.0);
    }

    public StopWatch stop() {
        return this.stop(1.0);
    }

    public StopWatch next(double strength) {
        return this.set(this.prevdur - this.prevtime + System.nanoTime(), strength).start();
    }

    public StopWatch stop(double strength) {
        return this.set(System.nanoTime() - this.prevtime, strength).start();
    }

    public StopWatch log(String name) {
        Logging.LOGGER.info(name + ": " + this.get() + " ms");
        return this;
    }
}

