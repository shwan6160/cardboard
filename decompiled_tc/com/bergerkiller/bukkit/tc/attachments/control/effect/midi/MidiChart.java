/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect.midi;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.effect.midi.MidiChartParameters;
import com.bergerkiller.bukkit.tc.attachments.control.effect.midi.MidiNote;
import com.bergerkiller.bukkit.tc.attachments.control.effect.midi.MidiTimeSignature;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;

public final class MidiChart
implements Cloneable {
    private final MidiChartParameters chartParams;
    private final List<MidiNote> notes = new ArrayList<MidiNote>();
    private int lastIndex = 0;

    public MidiChart(MidiChartParameters chartParams) {
        this.chartParams = chartParams;
    }

    public MidiChartParameters getParameters() {
        return this.chartParams;
    }

    public MidiChart withChartParameters(MidiChartParameters chartParams) {
        MidiChart updated = new MidiChart(chartParams);
        for (MidiNote note : this.notes) {
            updated.addNote(note);
        }
        return updated;
    }

    public MidiChart withChartParameters(Function<MidiChartParameters, MidiChartParameters> chartParamsChanger) {
        return this.withChartParameters(chartParamsChanger.apply(this.getParameters()));
    }

    public boolean isEmpty() {
        return this.notes.isEmpty();
    }

    public List<MidiNote> getNotes() {
        return this.notes;
    }

    public Bounds getBounds() {
        if (this.isEmpty()) {
            return Bounds.EMPTY;
        }
        int minTimeStepIndex = this.notes.get(0).timeStepIndex();
        int maxTimeStepIndex = this.notes.get(this.notes.size() - 1).timeStepIndex();
        int minPitch = Integer.MAX_VALUE;
        int maxPitch = Integer.MIN_VALUE;
        for (MidiNote note : this.notes) {
            minPitch = Math.min(minPitch, note.pitchClass());
            maxPitch = Math.max(maxPitch, note.pitchClass());
        }
        return new Bounds(minTimeStepIndex, maxTimeStepIndex, minPitch, maxPitch);
    }

    public void clearNotes() {
        this.notes.clear();
    }

    public void timeShift(int numTimeSteps) {
        if (numTimeSteps != 0) {
            ListIterator<MidiNote> it = this.notes.listIterator();
            while (it.hasNext()) {
                it.set(it.next().withTimeShift(numTimeSteps));
            }
        }
    }

    public EffectLoop.Time timeShiftToStart() {
        if (!this.isEmpty()) {
            MidiNote firstNote = this.getNotes().get(0);
            EffectLoop.Time shifted = EffectLoop.Time.nanos(firstNote.timeStepTimestampNanos);
            this.timeShift(-firstNote.timeStepIndex());
            return shifted;
        }
        return EffectLoop.Time.ZERO;
    }

    public void pitchShift(int numPitchClasses) {
        if (numPitchClasses != 0) {
            ListIterator<MidiNote> it = this.notes.listIterator();
            while (it.hasNext()) {
                it.set(it.next().withPitchShift(numPitchClasses));
            }
        }
    }

    public boolean forNotesInRange(long prevNanos, long currNanos, Consumer<MidiNote> action) {
        int currIndex = this.lastIndex;
        List<MidiNote> notes = this.notes;
        int notesCount = notes.size();
        if (currIndex >= notesCount || notes.get((int)currIndex).timeStepTimestampNanos > prevNanos) {
            currIndex = 0;
        }
        while (currIndex < notesCount) {
            MidiNote n = notes.get(currIndex);
            if (n.timeStepTimestampNanos >= currNanos) break;
            if (n.timeStepTimestampNanos >= prevNanos) {
                action.accept(n);
            }
            ++currIndex;
        }
        this.lastIndex = currIndex;
        return currIndex < notesCount;
    }

    public List<MidiNote> getChartVisibleNotes(int startTimeStepIndex, int numTimeSteps) {
        ArrayList<MidiNote> result = new ArrayList<MidiNote>();
        for (MidiNote note : this.notes) {
            int offset = note.timeStepIndex() - startTimeStepIndex;
            if (offset < 0 || offset >= numTimeSteps) continue;
            result.add(note);
        }
        return result;
    }

    public MidiNote update(MidiNote note, Function<MidiNote, MidiNote> operation) {
        MidiNote updated = operation.apply(note);
        this.removeNote(note);
        this.addNoteDirect(updated);
        return updated;
    }

    public boolean containsNote(MidiNote note) {
        return Collections.binarySearch(this.notes, note) >= 0;
    }

    public boolean containsAllNotes(Collection<MidiNote> notes) {
        for (MidiNote note : notes) {
            if (this.containsNote(note)) continue;
            return false;
        }
        return true;
    }

    public void removeNote(MidiNote note) {
        int index = Collections.binarySearch(this.notes, note);
        if (index >= 0) {
            this.notes.remove(index);
        }
    }

    public MidiNote addNoteOnBar(int timeStepIndex, int pitchClass, double volume) {
        MidiNote note = new MidiNote(this.chartParams, this.chartParams.timeStep().multiply(timeStepIndex), Attachment.EffectAttachment.EffectOptions.of(volume, this.chartParams.getPitch(pitchClass)));
        this.addNoteDirect(note);
        return note;
    }

    public MidiNote addNote(double timestamp, double volume, double speed) {
        return this.addNote(timestamp, Attachment.EffectAttachment.EffectOptions.of(volume, speed));
    }

    public MidiNote addNote(double timestamp, Attachment.EffectAttachment.EffectOptions options) {
        MidiNote note = new MidiNote(this.chartParams, timestamp, options);
        this.addNoteDirect(note);
        return note;
    }

    public void addChartNotes(MidiChart chart) {
        if (chart.chartParams.equals(this.chartParams)) {
            chart.notes.forEach(this::addNoteDirect);
        } else {
            chart.notes.forEach(this::addNote);
        }
    }

    public void removeChartNotes(MidiChart chart) {
        if (chart.chartParams.equals(this.chartParams)) {
            chart.notes.forEach(this::removeNote);
        } else {
            chart.notes.forEach(n -> this.removeNote(n.withChartParameters(this.chartParams)));
        }
    }

    public boolean toggleChartNotes(MidiChart chart) {
        if (this.containsAllNotes(chart.getNotes())) {
            this.removeChartNotes(chart);
            return false;
        }
        this.addChartNotes(chart);
        return true;
    }

    public MidiNote addNote(MidiNote note) {
        note = note.withChartParameters(this.chartParams);
        this.addNoteDirect(note);
        return note;
    }

    private void addNoteDirect(MidiNote note) {
        int index = Collections.binarySearch(this.notes, note);
        if (index >= 0) {
            this.notes.set(index, note);
        } else {
            this.notes.add(-index - 1, note);
        }
    }

    public MidiChart clone() {
        MidiChart copy = new MidiChart(this.chartParams);
        copy.notes.addAll(this.notes);
        return copy;
    }

    public ConfigurationNode toYaml() {
        ConfigurationNode yaml = new ConfigurationNode();
        this.getParameters().toYaml(yaml);
        if (!this.isEmpty()) {
            List notesStr = yaml.getList("notes", String.class);
            for (MidiNote note : this.notes) {
                notesStr.add(note.toString());
            }
        }
        return yaml;
    }

    public static MidiChart fromYaml(ConfigurationNode config) {
        MidiChart chart = MidiChart.empty(MidiChartParameters.fromYaml(config));
        if (config.contains("notes")) {
            for (String noteStr : config.getList("notes", String.class)) {
                MidiNote note = MidiNote.fromString(chart.getParameters(), noteStr);
                if (note == null) continue;
                chart.addNoteDirect(note);
            }
        }
        return chart;
    }

    public static MidiChart empty() {
        return MidiChart.empty(MidiChartParameters.DEFAULT);
    }

    public static MidiChart empty(MidiChartParameters chartParams) {
        return new MidiChart(chartParams);
    }

    public static MidiChart bergersTune() {
        MidiChart chart = new MidiChart(MidiChartParameters.chromatic(MidiTimeSignature.COMMON, 150));
        chart.addNote(0.0, 1.0, 0.6);
        chart.addNote(0.1, 1.0, 0.8);
        chart.addNote(0.2, 1.0, 1.0);
        chart.addNote(0.3, 1.0, 1.2);
        chart.addNote(0.4, 1.0, 1.4);
        chart.addNote(0.5, 1.0, 1.6);
        chart.addNote(0.6, 1.0, 1.8);
        chart.addNote(0.7, 1.0, 2.0);
        chart.addNote(1.0, 1.0, 1.0);
        chart.addNote(1.2, 1.0, 1.2);
        chart.addNote(1.4, 1.0, 1.4);
        chart.addNote(1.6, 1.0, 1.4);
        chart.addNote(1.7, 1.0, 1.2);
        chart.addNote(1.8, 1.0, 1.0);
        chart.addNote(1.9, 1.0, 0.8);
        chart.addNote(2.0, 1.0, 0.9);
        chart.addNote(2.2, 1.0, 1.1);
        chart.addNote(2.4, 1.0, 0.5);
        chart.addNote(2.6, 1.0, 0.7);
        return chart;
    }

    public static class Bounds {
        public static final Bounds EMPTY = new Bounds(0, 0, 0, 0);
        private final int minTimeStepIndex;
        private final int maxTimeStepIndex;
        private final int minPitchClass;
        private final int maxPitchClass;

        public Bounds(int minTimeStepIndex, int maxTimeStepIndex, int minPitchClass, int maxPitchClass) {
            this.minTimeStepIndex = minTimeStepIndex;
            this.maxTimeStepIndex = maxTimeStepIndex;
            this.minPitchClass = minPitchClass;
            this.maxPitchClass = maxPitchClass;
        }

        public int minTimeStepIndex() {
            return this.minTimeStepIndex;
        }

        public int maxTimeStepIndex() {
            return this.maxTimeStepIndex;
        }

        public int getNumTimeSteps() {
            return this.maxTimeStepIndex - this.minTimeStepIndex + 1;
        }

        public int minPitchClass() {
            return this.minPitchClass;
        }

        public int maxPitchClass() {
            return this.maxPitchClass;
        }

        public int getNumPitchClasses() {
            return this.maxPitchClass - this.minPitchClass + 1;
        }

        public boolean isEmpty() {
            return this == EMPTY;
        }
    }
}

