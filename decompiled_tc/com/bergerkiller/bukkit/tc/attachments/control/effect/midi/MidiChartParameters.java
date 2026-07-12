/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect.midi;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.effect.midi.MidiTimeSignature;

public final class MidiChartParameters {
    public static final MidiChartParameters DEFAULT = MidiChartParameters.chromatic(MidiTimeSignature.COMMON, 120);
    private static final double LOG2 = 0.6931471805599453;
    private final MidiTimeSignature timeSignature;
    private final int bpm;
    private final EffectLoop.Time timeStep;
    private final int pitchClasses;
    private final double pitchClassesInv;
    private final double pitchClassesDivLog2;

    public static MidiChartParameters chromatic(MidiTimeSignature timeSignature, int bpm) {
        return MidiChartParameters.of(timeSignature, bpm, 12);
    }

    public static MidiChartParameters of(MidiTimeSignature timeSignature, int bpm, int pitchClasses) {
        return new MidiChartParameters(timeSignature, bpm, pitchClasses);
    }

    private MidiChartParameters(MidiTimeSignature timeSignature, int bpm, int pitchClasses) {
        if (timeSignature == null) {
            throw new IllegalArgumentException("Null time signature");
        }
        if (bpm < 1) {
            throw new IllegalArgumentException("Beats per minute must be at least 1");
        }
        if (bpm > 60000) {
            throw new IllegalArgumentException("Beats per minute must be no more than 60000");
        }
        if (pitchClasses <= 0) {
            throw new IllegalArgumentException("Number of pitch classes must be at least 1");
        }
        this.timeSignature = timeSignature;
        this.bpm = bpm;
        this.timeStep = EffectLoop.Time.seconds(60.0 / (double)(bpm * timeSignature.noteValue()));
        this.pitchClasses = pitchClasses;
        this.pitchClassesInv = 1.0 / (double)pitchClasses;
        this.pitchClassesDivLog2 = (double)pitchClasses / 0.6931471805599453;
    }

    public EffectLoop.Time timeStep() {
        return this.timeStep;
    }

    public MidiTimeSignature timeSignature() {
        return this.timeSignature;
    }

    public MidiChartParameters withTimeSignature(MidiTimeSignature signature) {
        return new MidiChartParameters(signature, this.bpm, this.pitchClasses);
    }

    public MidiChartParameters withBPM(int bpm) {
        return new MidiChartParameters(this.timeSignature, bpm, this.pitchClasses);
    }

    public MidiChartParameters withPitchClasses(int numPitchClasses) {
        return new MidiChartParameters(this.timeSignature, this.bpm, numPitchClasses);
    }

    public int bpm() {
        return this.bpm;
    }

    public int getTimeStepIndex(double timestamp) {
        return this.getTimeStepIndex(EffectLoop.Time.seconds(timestamp));
    }

    public int getTimeStepIndex(EffectLoop.Time timestamp) {
        return timestamp.roundDiv(this.timeStep);
    }

    public double getTimestamp(int timeStepIndex) {
        return (double)timeStepIndex * this.timeStep.seconds;
    }

    public long getTimestampNanos(int timeStepIndex) {
        return (long)timeStepIndex * this.timeStep.nanos;
    }

    public int pitchClasses() {
        return this.pitchClasses;
    }

    public int getPitchClass(double speed) {
        return (int)Math.round(this.pitchClassesDivLog2 * Math.log(speed));
    }

    public double getPitch(int pitchClass) {
        return Math.pow(2.0, this.pitchClassesInv * (double)pitchClass);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MidiChartParameters) {
            MidiChartParameters other = (MidiChartParameters)o;
            return this.bpm == other.bpm && this.timeSignature.equals(other.timeSignature) && this.pitchClasses == other.pitchClasses;
        }
        return false;
    }

    public void toYaml(ConfigurationNode config) {
        config.set("timeSignature", (Object)this.timeSignature().toString());
        config.set("bpm", (Object)this.bpm());
        config.set("pitchClasses", (Object)this.pitchClasses());
    }

    public static MidiChartParameters fromYaml(ConfigurationNode config) {
        MidiTimeSignature timeSignature = MidiTimeSignature.fromString((String)config.getOrDefault("timeSignature", (Object)""), DEFAULT.timeSignature());
        int bpm = (Integer)config.getOrDefault("bpm", (Object)DEFAULT.bpm());
        int pitchClasses = (Integer)config.getOrDefault("pitchClasses", (Object)DEFAULT.pitchClasses());
        return MidiChartParameters.of(timeSignature, bpm, pitchClasses);
    }
}

