/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect;

import com.bergerkiller.bukkit.tc.attachments.control.effect.DelayedEffectTask;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoopAdvanceModifier;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoopGroup;
import java.util.Collection;
import java.util.function.Predicate;

public interface EffectLoop {
    public static final EffectLoop NONE = new EffectLoop(){

        @Override
        public boolean advance(Time dt, Time duration, boolean loop) {
            return false;
        }

        @Override
        public void resetToBeginning() {
        }
    };

    public boolean advance(Time var1, Time var2, boolean var3);

    default public void resetToBeginning() {
    }

    default public EffectLoop withAdvance(AdvanceModifier modifier) {
        return new EffectLoopAdvanceModifier(this, modifier);
    }

    default public EffectLoop withConditionalAdvance(Predicate<EffectLoop> check) {
        return this.withAdvance((base, dt, duration, loop) -> check.test(base) && base.advance(dt, duration, loop));
    }

    default public EffectLoop withSpeed(double speed) {
        if (speed < 1.0E-8) {
            return NONE;
        }
        if (speed == 1.0) {
            return this;
        }
        return this.withAdvance((base, dt, duration, loop) -> base.advance(dt.multiply(speed), duration.multiply(speed), loop));
    }

    public static EffectLoop group(Collection<EffectLoop> effectLoops) {
        return new EffectLoopGroup(effectLoops);
    }

    @FunctionalInterface
    public static interface AdvanceModifier {
        public boolean advance(EffectLoop var1, Time var2, Time var3, boolean var4);
    }

    public static class Time {
        public static final Time ZERO = new Time(0L){

            @Override
            public Time multiply(double factor) {
                return this;
            }
        };
        public static final Time ONE_TICK = Time.nanos(50000000L);
        public static final Time NEVER = new Time(Double.MAX_VALUE, Long.MAX_VALUE);
        public final double seconds;
        public final long nanos;

        public static Time seconds(double seconds) {
            return new Time(seconds);
        }

        public static Time nanos(long nanoSeconds) {
            return new Time(nanoSeconds);
        }

        private Time(double seconds) {
            this(seconds, (long)(seconds * 1.0E9));
        }

        private Time(long nanos) {
            this((double)nanos / 1.0E9, nanos);
        }

        private Time(double seconds, long nanos) {
            this.seconds = seconds;
            this.nanos = nanos;
        }

        public boolean isZero() {
            return this.nanos == 0L;
        }

        public boolean isNever() {
            return this.nanos == Long.MAX_VALUE;
        }

        public Time multiply(double factor) {
            return Time.seconds(this.seconds * factor);
        }

        public Time multiply(int factor) {
            return Time.nanos(this.nanos * (long)factor);
        }

        public Time add(Time step, int count) {
            if (count == 0) {
                return this;
            }
            return Time.nanos(this.nanos + step.nanos * (long)count);
        }

        public static long secondsToNanos(double seconds) {
            return (long)(seconds * 1.0E9);
        }

        public int roundDiv(Time divisor) {
            if (divisor.isZero()) {
                throw new ArithmeticException("Divisor is zero");
            }
            long division = this.nanos / divisor.nanos;
            long remainder = Math.abs(this.nanos % divisor.nanos);
            if (2L * remainder >= Math.abs(divisor.nanos)) {
                division += this.nanos < 0L ^ divisor.nanos < 0L ? -1L : 1L;
            }
            return (int)division;
        }

        public Time adjustBPM(int fromBPM, int toBPM) {
            if (fromBPM == toBPM) {
                return this;
            }
            return Time.nanos(this.nanos * (long)fromBPM / (long)toBPM);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Time) {
                return ((Time)o).nanos == this.nanos;
            }
            return false;
        }

        public String toString() {
            if (this.isNever()) {
                return "Time{NEVER}";
            }
            if (this.isZero()) {
                return "Time{ZERO}";
            }
            return String.format("Time{%.3f seconds, %d nanos}", this.seconds, this.nanos);
        }
    }

    @FunctionalInterface
    public static interface Player {
        public void play(EffectLoop var1, RunMode var2);

        default public void play(EffectLoop loop) {
            this.play(loop, RunMode.ASYNCHRONOUS);
        }

        default public DelayedEffectTask scheduleTask(Time delay, Runnable task) {
            return this.scheduleTask(delay, task, RunMode.ASYNCHRONOUS);
        }

        default public DelayedEffectTask scheduleTask(Time delay, Runnable task, RunMode runMode) {
            DelayedEffectTask delayedTask = new DelayedEffectTask(delay, task);
            this.play(delayedTask, runMode);
            return delayedTask;
        }
    }

    public static enum RunMode {
        SYNCHRONOUS,
        ASYNCHRONOUS;

    }
}

