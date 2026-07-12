/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect;

import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import java.util.concurrent.atomic.AtomicBoolean;

public class DelayedEffectTask
implements EffectLoop {
    private final EffectLoop.Time delay;
    private final Runnable task;
    private final AtomicBoolean hasRun;
    private long elapsed;

    public DelayedEffectTask(EffectLoop.Time delay, Runnable task) {
        if (delay == null) {
            throw new IllegalArgumentException("Delay is null");
        }
        if (task == null) {
            throw new IllegalArgumentException("Task is null");
        }
        this.delay = delay;
        this.task = task;
        this.hasRun = new AtomicBoolean(false);
        this.elapsed = 0L;
    }

    public void runNow() {
        if (this.hasRun.compareAndSet(false, true)) {
            this.task.run();
        }
        this.elapsed = this.delay.nanos;
    }

    public void cancel() {
        this.elapsed = this.delay.nanos;
        this.hasRun.set(true);
    }

    @Override
    public boolean advance(EffectLoop.Time dt, EffectLoop.Time duration, boolean loop) {
        long newElapsed = this.elapsed + dt.nanos;
        if (newElapsed >= this.delay.nanos) {
            this.runNow();
            return false;
        }
        this.elapsed = newElapsed;
        return true;
    }

    @Override
    public void resetToBeginning() {
        this.elapsed = 0L;
        this.hasRun.set(false);
    }
}

