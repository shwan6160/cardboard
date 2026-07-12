/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.utils.LauncherConfig;

public abstract class LaunchFunction {
    protected double vstart;
    protected double vend;
    protected double vmin = 0.0;
    protected double vmax = Double.MAX_VALUE;
    protected int totalTime = 1;
    protected double dfactor = 1.0;

    public final double getMinimumVelocity() {
        return this.vmin;
    }

    public final void setMinimumVelocity(double minimumVelocity) {
        this.vmin = minimumVelocity;
        if (this.vstart < this.vmin) {
            this.vstart = this.vmin;
        }
        if (this.vend < this.vmin) {
            this.vend = this.vmin;
        }
    }

    public final double getMaximumVelocity() {
        return this.vmax;
    }

    public final void setMaximumVelocity(double maximumVelocity) {
        this.vmax = maximumVelocity;
        if (this.vstart > this.vmax) {
            this.vstart = this.vmax;
        }
        if (this.vend > this.vmax) {
            this.vend = this.vmax;
        }
    }

    public final void setVelocityRange(double startVelocity, double endVelocity) {
        this.setStartVelocity(startVelocity);
        this.setEndVelocity(endVelocity);
    }

    public final void setStartVelocity(double startVelocity) {
        this.vstart = startVelocity;
        if (this.vstart < this.vmin) {
            this.vstart = this.vmin;
        } else if (this.vstart > this.vmax) {
            this.vstart = this.vmax;
        }
    }

    public final void setEndVelocity(double endVelocity) {
        this.vend = endVelocity;
        if (this.vend < this.vmin) {
            this.vend = this.vmin;
        } else if (this.vend > this.vmax) {
            this.vend = this.vmax;
        }
    }

    public final double getStartVelocity() {
        return this.vstart;
    }

    public final double getEndVelocity() {
        return this.vend;
    }

    public final void configure(LauncherConfig config) {
        if (config.hasAcceleration()) {
            this.setAcceleration(config.getAcceleration());
        } else if (config.hasDuration()) {
            this.setTotalTime(config.getDuration());
        } else if (config.hasDistance()) {
            this.setTotalDistance(config.getDistance());
        } else {
            this.setInstantaneous();
        }
    }

    public final void setAcceleration(double acceleration) {
        double velocityDiff = Math.abs(this.vend - this.vstart);
        if (acceleration <= 0.0 || acceleration >= velocityDiff) {
            this.setInstantaneous();
            return;
        }
        this.setTotalTime(MathUtil.floor((double)(velocityDiff / acceleration)));
    }

    public final void setTotalDistance(double distance) {
        if (distance <= 0.0) {
            this.setInstantaneous();
            return;
        }
        for (int time = 0; time < 10000; ++time) {
            this.setTotalTime(time);
            double currDistance = this.getDistance(time);
            if (!(currDistance >= distance)) continue;
            this.dfactor = distance / currDistance;
            break;
        }
    }

    public final double getTotalDistance() {
        return this.getDistance(this.totalTime);
    }

    public final boolean isInstantaneous() {
        return this.totalTime == 0;
    }

    public final void setInstantaneous() {
        this.totalTime = 0;
        this.dfactor = 0.0;
    }

    public final void setTotalTime(int ticks) {
        this.totalTime = ticks;
        this.dfactor = 1.0;
    }

    public final int getTotalTime() {
        return this.totalTime;
    }

    public double getDistance(int tick) {
        if (tick < 0) {
            tick = 0;
        } else if (tick >= this.totalTime) {
            tick = this.totalTime;
        }
        return this.calculateDistance(tick) * this.dfactor;
    }

    public String toString() {
        return "{" + this.getClass().getSimpleName() + " vstart=" + this.vstart + " vend=" + this.vend + " time=" + this.getTotalTime() + " distance=" + this.getTotalDistance() + "}";
    }

    protected abstract double calculateDistance(int var1);

    public static class Bezier
    extends LaunchFunction {
        @Override
        protected double calculateDistance(int ticks) {
            double d1 = (double)ticks / (double)this.totalTime;
            double d2 = d1 * d1;
            double d3 = d1 * d2;
            double d4 = d2 * d2;
            double a = 0.0;
            a += d3 / 3.0 * (double)this.totalTime;
            a += d2 / 2.0;
            double b = 0.0;
            b += d4 / 4.0 * (double)this.totalTime;
            b += d3 / 2.0;
            return this.vstart * (double)(ticks + 1) + (this.vend - this.vstart) * (3.0 * (a += d1 / 6.0 / (double)this.totalTime) - 2.0 * (b += d2 / 4.0 / (double)this.totalTime));
        }
    }

    public static class Linear
    extends LaunchFunction {
        @Override
        protected double calculateDistance(int ticks) {
            double m = (this.vend - this.vstart) / (double)this.totalTime;
            return (double)(ticks + 1) * (this.vstart + 0.5 * m * (double)ticks);
        }
    }
}

