/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect;

import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;

public interface ScheduledEffectLoop {
    public static final ScheduledEffectLoop NONE = (prevNanos, currNanos) -> false;

    public boolean advance(long var1, long var3);

    default public SequentialEffectLoop asEffectLoop() {
        return this.asEffectLoop(null);
    }

    default public SequentialEffectLoop asEffectLoop(final EffectLoop.Time overrideDuration) {
        return new SequentialEffectLoop(){
            private long nanosElapsed = 0L;
            final /* synthetic */ ScheduledEffectLoop this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public boolean advance(EffectLoop.Time dt, EffectLoop.Time duration, boolean loop) {
                if (overrideDuration != null) {
                    duration = overrideDuration;
                }
                long prev_time_nanos = this.nanosElapsed;
                long curr_time_nanos = prev_time_nanos + dt.nanos;
                if (duration.isZero()) {
                    this.nanosElapsed = curr_time_nanos;
                    return this.this$0.advance(prev_time_nanos, curr_time_nanos);
                }
                if (curr_time_nanos <= duration.nanos) {
                    this.nanosElapsed = curr_time_nanos;
                    return this.this$0.advance(prev_time_nanos, curr_time_nanos) || loop;
                }
                if (loop) {
                    long remainder;
                    this.nanosElapsed = remainder = curr_time_nanos - duration.nanos;
                    this.this$0.advance(prev_time_nanos, duration.nanos);
                    this.this$0.advance(0L, remainder);
                    return true;
                }
                this.nanosElapsed = duration.nanos;
                this.this$0.advance(prev_time_nanos, duration.nanos);
                return false;
            }

            @Override
            public long nanosElapsed() {
                return this.nanosElapsed;
            }

            @Override
            public void resetToBeginning() {
                this.nanosElapsed = 0L;
            }
        };
    }

    public static interface SequentialEffectLoop
    extends EffectLoop {
        public long nanosElapsed();
    }
}

