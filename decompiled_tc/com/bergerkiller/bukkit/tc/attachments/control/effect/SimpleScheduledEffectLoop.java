/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect;

import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.effect.ScheduledEffectLoopBase;

public class SimpleScheduledEffectLoop
extends ScheduledEffectLoopBase {
    private long nanosDelay = 0L;

    public void setDelay(EffectLoop.Time delay) {
        this.nanosDelay = delay.nanos;
    }

    @Override
    public boolean advance(long prevNanos, long currNanos) {
        if (currNanos < this.nanosDelay) {
            return true;
        }
        if (prevNanos <= this.nanosDelay) {
            this.getEffectSink().playEffect(Attachment.EffectAttachment.EffectOptions.DEFAULT);
        }
        return false;
    }
}

