/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect;

import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.effect.ScheduledEffectLoop;

public abstract class ScheduledEffectLoopBase
implements ScheduledEffectLoop {
    private Attachment.EffectSink effectSink = Attachment.EffectSink.DISABLED_EFFECT_SINK;

    public Attachment.EffectSink getEffectSink() {
        return this.effectSink;
    }

    public void setEffectSink(Attachment.EffectSink effectSink) {
        this.effectSink = effectSink;
    }
}

