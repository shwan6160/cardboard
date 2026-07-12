/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect.midi;

import com.bergerkiller.bukkit.common.utils.ParseUtil;

public class MidiTimeSignature {
    private final int beatsPerMeasure;
    private final int noteValue;
    private final int notesPerMeasure;
    public static final MidiTimeSignature COMMON = MidiTimeSignature.of(4, 4);

    public static MidiTimeSignature of(int beatsPerMeasure, int noteValue) {
        return new MidiTimeSignature(beatsPerMeasure, noteValue);
    }

    private MidiTimeSignature(int beatsPerMeasure, int noteValue) {
        if (beatsPerMeasure < 1) {
            throw new IllegalArgumentException("Invalid number of beats per measure: " + beatsPerMeasure);
        }
        if (noteValue < 1) {
            throw new IllegalArgumentException("Invalid note value: 1/" + noteValue);
        }
        this.beatsPerMeasure = beatsPerMeasure;
        this.noteValue = noteValue;
        this.notesPerMeasure = noteValue * beatsPerMeasure;
    }

    public int beatsPerMeasure() {
        return this.beatsPerMeasure;
    }

    public int notesPerMeasure() {
        return this.notesPerMeasure;
    }

    public int noteValue() {
        return this.noteValue;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MidiTimeSignature) {
            MidiTimeSignature other = (MidiTimeSignature)o;
            return this.beatsPerMeasure == other.beatsPerMeasure && this.noteValue == other.noteValue;
        }
        return false;
    }

    public String toString() {
        return this.beatsPerMeasure + "/" + this.noteValue;
    }

    public static MidiTimeSignature fromString(String signatureText, MidiTimeSignature defaultSig) {
        int sep;
        if (signatureText != null && (sep = signatureText.indexOf(47)) != -1) {
            String beatsPerMeasureStr = signatureText.substring(0, sep).trim();
            String noteValueStr = signatureText.substring(sep + 1).trim();
            return MidiTimeSignature.of(ParseUtil.parseInt((String)beatsPerMeasureStr, (int)defaultSig.beatsPerMeasure), ParseUtil.parseInt((String)noteValueStr, (int)defaultSig.noteValue));
        }
        return defaultSig;
    }
}

