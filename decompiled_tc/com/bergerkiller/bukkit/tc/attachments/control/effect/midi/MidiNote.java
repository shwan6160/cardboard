/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect.midi;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.effect.midi.MidiChartParameters;

public final class MidiNote
implements Comparable<MidiNote> {
    private final MidiChartParameters chartParams;
    private final EffectLoop.Time timestamp;
    private final Attachment.EffectAttachment.EffectOptions options;
    private final int timeStepIndex;
    final long timeStepTimestampNanos;
    private final int pitchClass;
    private final Attachment.EffectAttachment.EffectOptions optionsAdjusted;

    public MidiNote(MidiChartParameters chartParams, double timestampSecs, Attachment.EffectAttachment.EffectOptions options) {
        this(chartParams, EffectLoop.Time.seconds(timestampSecs), options);
    }

    public MidiNote(MidiChartParameters chartParams, EffectLoop.Time timestamp, Attachment.EffectAttachment.EffectOptions options) {
        this.chartParams = chartParams;
        this.timestamp = timestamp;
        this.options = options;
        this.timeStepIndex = chartParams.getTimeStepIndex(timestamp);
        this.timeStepTimestampNanos = chartParams.getTimestampNanos(this.timeStepIndex);
        this.pitchClass = chartParams.getPitchClass(options.speed());
        this.optionsAdjusted = options.withSpeed(chartParams.getPitch(this.pitchClass));
    }

    public MidiNote withChartParameters(MidiChartParameters chartParams) {
        if (this.chartParams.equals(chartParams)) {
            return this;
        }
        return new MidiNote(chartParams, this.timestamp.adjustBPM(this.chartParams.bpm(), chartParams.bpm()), this.options);
    }

    public MidiNote withTimeShift(int numTimeSteps) {
        if (numTimeSteps == 0) {
            return this;
        }
        return new MidiNote(this.chartParams, this.timestamp.add(this.chartParams.timeStep(), numTimeSteps), this.options);
    }

    public MidiNote withPitchShift(int numPitchClasses) {
        if (numPitchClasses == 0) {
            return this;
        }
        return new MidiNote(this.chartParams, this.timestamp, this.options.withSpeed(this.chartParams.getPitch(this.pitchClass + numPitchClasses)));
    }

    public EffectLoop.Time timestamp() {
        return this.timestamp;
    }

    public int timeStepIndex() {
        return this.timeStepIndex;
    }

    public int pitchClass() {
        return this.pitchClass;
    }

    public Attachment.EffectAttachment.EffectOptions options() {
        return this.options;
    }

    public void play(Attachment.EffectSink effectSink) {
        effectSink.playEffect(this.optionsAdjusted);
    }

    @Override
    public int compareTo(MidiNote note) {
        int comp = Integer.compare(this.timeStepIndex, note.timeStepIndex);
        if (comp == 0) {
            comp = Integer.compare(this.pitchClass, note.pitchClass);
        }
        return comp;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("t=").append(this.timestamp().seconds).append(" s=").append(this.options.speed());
        if (this.options.volume() != 1.0) {
            str.append(" v=").append(this.options.volume());
        }
        return str.toString();
    }

    public static MidiNote fromString(MidiChartParameters chartParams, String noteStr) {
        int startIndex = 0;
        boolean done = false;
        double timestamp = Double.NaN;
        double speed = Double.NaN;
        double volume = 1.0;
        do {
            String entry;
            int endIndex;
            if ((endIndex = noteStr.indexOf(32, startIndex)) == -1) {
                endIndex = noteStr.length();
                done = true;
            }
            if ((entry = noteStr.substring(startIndex, endIndex)).startsWith("t=")) {
                timestamp = ParseUtil.parseDouble((String)entry.substring(2), (double)timestamp);
            } else if (entry.startsWith("s=")) {
                speed = ParseUtil.parseDouble((String)entry.substring(2), (double)speed);
            } else if (entry.startsWith("v=")) {
                volume = ParseUtil.parseDouble((String)entry.substring(2), (double)volume);
            }
            startIndex = endIndex + 1;
        } while (!done);
        if (Double.isNaN(timestamp) || Double.isNaN(speed)) {
            return null;
        }
        return new MidiNote(chartParams, timestamp, Attachment.EffectAttachment.EffectOptions.of(volume, speed));
    }
}

