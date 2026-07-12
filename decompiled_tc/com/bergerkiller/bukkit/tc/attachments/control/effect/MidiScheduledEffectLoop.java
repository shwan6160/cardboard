/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect;

import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.effect.ScheduledEffectLoopBase;
import com.bergerkiller.bukkit.tc.attachments.control.effect.midi.MidiChart;

public class MidiScheduledEffectLoop
extends ScheduledEffectLoopBase {
    private MidiChart chart = MidiChart.empty();

    public MidiChart getChart() {
        return this.chart;
    }

    public void setChart(MidiChart chart) {
        this.chart = chart;
    }

    @Override
    public boolean advance(long prevNanos, long currNanos) {
        Attachment.EffectSink effectSink = this.getEffectSink();
        return this.chart.forNotesInRange(prevNanos, currNanos, n -> n.play(effectSink));
    }
}

