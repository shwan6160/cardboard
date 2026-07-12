/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.effect.MidiScheduledEffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.effect.ScheduledEffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.effect.midi.MidiChart;
import com.bergerkiller.bukkit.tc.attachments.control.effect.midi.MidiChartParameters;
import com.bergerkiller.bukkit.tc.attachments.control.effect.midi.MidiNote;
import com.bergerkiller.bukkit.tc.attachments.control.effect.midi.MidiTimeSignature;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetTooltip;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MidiChartDialog
extends MapWidgetMenu {
    private static final MapTexture MIDI_BUTTON_ICONS = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/midi_buttons.png");
    private MidiChart chart = MidiChart.empty();
    private MidiChart selection = MidiChart.empty(this.chart.getParameters());
    private MidiChart pattern = MidiChart.empty(this.chart.getParameters());
    private EffectLoop.RunMode midiRunMode = EffectLoop.RunMode.ASYNCHRONOUS;
    private Mode mode = Mode.NOTE;
    private TopMenuButton btnModeNote;
    private TopMenuButton btnModeSelect;
    private TopMenuButton btnModePattern;
    private TopMenuButton prevSelectedButton = null;
    private MidiPianoRollWidget pianoRoll;
    private EffectLoop.Time duration = null;
    private volatile int previewCtr = 0;
    private volatile EffectLoop.Time currentPreviewTime = null;

    public MidiChartDialog() {
        this.setPositionAbsolute(true);
        this.setBounds(5, 5, 118, 116);
        this.setBackgroundColor(MapColorPalette.getColor((int)16, (int)16, (int)128));
    }

    public abstract void onChartChanged(MidiChart var1);

    public abstract Attachment.EffectSink getEffectSink();

    public MidiChartDialog setChart(MidiChart chart) {
        this.chart = chart;
        this.selection = this.selection.withChartParameters(chart.getParameters());
        this.pattern = this.pattern.withChartParameters(chart.getParameters());
        this.stopPreview();
        if (this.pianoRoll != null) {
            this.pianoRoll.invalidate();
        }
        return this;
    }

    public MidiChartDialog setDuration(EffectLoop.Time duration) {
        this.duration = duration;
        return this;
    }

    public MidiChartDialog setMidiRunMode(EffectLoop.RunMode runMode) {
        this.midiRunMode = runMode;
        return this;
    }

    public void setMode(Mode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            this.applyMode();
        }
    }

    private void applyMode() {
        if (this.getDisplay() != null) {
            this.btnModeNote.setSelected(this.mode == Mode.NOTE);
            this.btnModeSelect.setSelected(this.mode == Mode.SELECT);
            this.btnModePattern.setSelected(this.mode == Mode.PATTERN);
            this.pianoRoll.invalidate();
            this.mode.select(this);
        }
    }

    private void setNoteSelect() {
        if (this.selection.isEmpty()) {
            this.selection.addNoteOnBar(0, 0, 1.0);
        } else {
            while (this.selection.getNotes().size() > 1) {
                this.selection.removeNote(this.selection.getNotes().get(this.selection.getNotes().size() - 1));
            }
        }
        this.pianoRoll.scrollToSelection();
    }

    private void setPatternSelect() {
        if (this.pattern.isEmpty()) {
            this.setNoteSelect();
        } else {
            this.selection.clearNotes();
            this.selection.addChartNotes(this.pattern);
            this.pianoRoll.scrollToSelection();
        }
    }

    private void exitPianoRoll() {
        if (this.prevSelectedButton == null) {
            this.prevSelectedButton = this.btnModeNote;
        }
        this.prevSelectedButton.focus();
    }

    private void stopPreview() {
        ++this.previewCtr;
    }

    private void preview(MidiChart chart, boolean shiftToStart, boolean ignoreDuration) {
        this.stopPreview();
        if (chart.isEmpty()) {
            return;
        }
        chart = chart.clone();
        EffectLoop.Time shifted = shiftToStart ? chart.timeShiftToStart() : EffectLoop.Time.ZERO;
        MidiScheduledEffectLoop midiEffectLoop = new MidiScheduledEffectLoop();
        int previewId = this.previewCtr;
        midiEffectLoop.setChart(chart);
        midiEffectLoop.setEffectSink(this.getEffectSink());
        ScheduledEffectLoop.SequentialEffectLoop effectLoop = midiEffectLoop.asEffectLoop();
        TrainCarts.plugin.createEffectLoopPlayer().play(effectLoop.withAdvance((base, dt, duration, loop) -> {
            if (this.duration != null && !ignoreDuration) {
                duration = this.duration;
            }
            if (previewId != this.previewCtr || !base.advance(dt, duration, loop)) {
                this.currentPreviewTime = null;
                return false;
            }
            this.currentPreviewTime = EffectLoop.Time.nanos(shifted.nanos + effectLoop.nanosElapsed());
            return true;
        }), this.midiRunMode);
    }

    @Override
    public void onAttached() {
        this.btnModeNote = (this.addWidget(new TopMenuButton(MIDI_BUTTON_ICONS.getView(0, 0, 12, 12).clone()){

            @Override
            public void onClick() {
                MidiChartDialog.this.setMode(Mode.NOTE);
                MidiChartDialog.this.pianoRoll.activate();
            }
        })).setTooltip("Place notes");
        this.btnModeNote.setPosition(5, 5);
        this.btnModeSelect = (this.addWidget(new TopMenuButton(MIDI_BUTTON_ICONS.getView(12, 0, 12, 12).clone()){

            @Override
            public void onClick() {
                MidiChartDialog.this.setMode(Mode.SELECT);
                MidiChartDialog.this.pianoRoll.activate();
            }
        })).setTooltip("Select note pattern");
        this.btnModeSelect.setPosition(18, 5);
        this.btnModePattern = (this.addWidget(new TopMenuButton(MIDI_BUTTON_ICONS.getView(24, 0, 12, 12).clone()){

            @Override
            public void onClick() {
                MidiChartDialog.this.setMode(Mode.PATTERN);
                MidiChartDialog.this.pianoRoll.activate();
            }
        })).setTooltip("Place note pattern");
        this.btnModePattern.setPosition(31, 5);
        (this.addWidget(new TopMenuButton(MIDI_BUTTON_ICONS.getView(36, 0, 12, 12).clone()){

            @Override
            public void onClick() {
                MidiChartDialog.this.addWidget((MapWidget)new ConfirmClearDialog(){

                    @Override
                    public void onConfirmClear() {
                        MidiChartDialog.this.chart.clearNotes();
                        MidiChartDialog.this.selection.clearNotes();
                        MidiChartDialog.this.pattern.clearNotes();
                        MidiChartDialog.this.pianoRoll.invalidate();
                        MidiChartDialog.this.mode.select(MidiChartDialog.this);
                        MidiChartDialog.this.stopPreview();
                        MidiChartDialog.this.onChartChanged(MidiChartDialog.this.chart);
                    }
                });
            }
        })).setTooltip("Clear chart").setPosition(46, 5);
        (this.addWidget(new TopMenuButton(MIDI_BUTTON_ICONS.getView(48, 0, 12, 12).clone()){
            private boolean playing;
            {
                this.playing = false;
            }

            private void setPlaying(boolean newPlaying) {
                if (this.playing != newPlaying) {
                    this.playing = newPlaying;
                    if (newPlaying) {
                        this.setIcon(MIDI_BUTTON_ICONS.getView(60, 0, 12, 12).clone());
                        this.setTooltip("Stop playing chart");
                    } else {
                        this.setIcon(MIDI_BUTTON_ICONS.getView(48, 0, 12, 12).clone());
                        this.setTooltip("Play chart");
                    }
                }
            }

            @Override
            public void onClick() {
                if (this.playing) {
                    MidiChartDialog.this.stopPreview();
                    this.setPlaying(false);
                } else {
                    MidiChartDialog.this.preview(MidiChartDialog.this.chart, false, false);
                    this.setPlaying(true);
                }
            }

            public void onTick() {
                super.onTick();
                this.setPlaying(MidiChartDialog.this.currentPreviewTime != null);
            }
        })).setTooltip("Play chart").setPosition(this.getWidth() - 30, 5);
        (this.addWidget(new TopMenuButton(MIDI_BUTTON_ICONS.getView(72, 0, 12, 12).clone()){

            @Override
            public void onClick() {
                (MidiChartDialog.this.addWidget((MapWidget)new ChartSettingsDialog(){

                    @Override
                    public void onParamsChanged(MidiChartParameters params) {
                        MidiChartDialog.this.setChart(MidiChartDialog.this.chart.withChartParameters(params));
                        MidiChartDialog.this.onChartChanged(MidiChartDialog.this.chart);
                    }
                })).setParams(MidiChartDialog.this.chart.getParameters());
            }
        })).setTooltip("Chart settings").setPosition(this.getWidth() - 17, 5);
        this.pianoRoll = (MidiPianoRollWidget)this.addWidget(new MidiPianoRollWidget());
        this.pianoRoll.setBounds(5, 19, this.getWidth() - 10, this.getHeight() - 24);
        this.applyMode();
        super.onAttached();
    }

    public void onDetached() {
        this.stopPreview();
        super.onDetached();
    }

    private abstract class TopMenuButton
    extends MapWidget {
        private MapTexture icon;
        private boolean selected = false;
        private boolean buttonDown = false;
        private final MapWidgetTooltip tooltip = new MapWidgetTooltip();

        public TopMenuButton(MapTexture icon) {
            this.icon = icon;
            this.setSize(icon.getWidth(), icon.getHeight());
            this.setFocusable(true);
        }

        public abstract void onClick();

        public TopMenuButton setTooltip(String text) {
            this.tooltip.setText(text);
            return this;
        }

        public TopMenuButton setSelected(boolean selected) {
            if (this.selected != selected) {
                this.selected = selected;
                this.invalidate();
            }
            return this;
        }

        public TopMenuButton setIcon(MapTexture icon) {
            this.icon = icon;
            this.invalidate();
            return this;
        }

        public void onFocus() {
            this.addWidget(this.tooltip);
            MidiChartDialog.this.prevSelectedButton = this;
        }

        public void onBlur() {
            this.removeWidget(this.tooltip);
            this.buttonDown = false;
        }

        public void onDraw() {
            byte bottomRim;
            byte background;
            byte topRim;
            byte edgeColor;
            if (this.isFocused()) {
                edgeColor = 119;
                topRim = MapColorPalette.getColor((int)216, (int)76, (int)178);
                background = MapColorPalette.getColor((int)186, (int)65, (int)153);
                bottomRim = MapColorPalette.getColor((int)152, (int)53, (int)125);
            } else {
                edgeColor = 119;
                topRim = MapColorPalette.getColor((int)142, (int)109, (int)208);
                background = MapColorPalette.getColor((int)116, (int)89, (int)170);
                bottomRim = MapColorPalette.getColor((int)97, (int)63, (int)148);
            }
            if (this.selected || this.buttonDown) {
                byte b = topRim;
                topRim = bottomRim;
                bottomRim = b;
            }
            this.view.fillRectangle(2, 2, this.getWidth() - 4, this.getHeight() - 4, background);
            this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), edgeColor);
            this.view.drawLine(1, 1, this.getWidth() - 2, 1, topRim);
            this.view.drawLine(1, 2, 1, this.getHeight() - 3, topRim);
            this.view.drawLine(this.getWidth() - 2, 2, this.getWidth() - 2, this.getHeight() - 3, bottomRim);
            this.view.drawLine(1, this.getHeight() - 2, this.getWidth() - 2, this.getHeight() - 2, bottomRim);
            this.view.draw((MapCanvas)this.icon, 0, 0);
        }

        public void onKeyPressed(MapKeyEvent event) {
            if (event.getKey() == MapPlayerInput.Key.ENTER) {
                if (!this.buttonDown) {
                    this.buttonDown = true;
                    this.invalidate();
                    this.onClick();
                }
            } else {
                super.onKeyPressed(event);
            }
        }

        public void onKeyReleased(MapKeyEvent event) {
            if (event.getKey() == MapPlayerInput.Key.ENTER) {
                if (this.buttonDown) {
                    this.buttonDown = false;
                    this.invalidate();
                }
            } else {
                super.onKeyReleased(event);
            }
        }
    }

    public static enum Mode {
        NOTE(dialog -> {
            ((MidiChartDialog)dialog).setNoteSelect();
            ((MidiChartDialog)dialog).pattern.clearNotes();
        }, dialog -> {
            if (((MidiChartDialog)dialog).chart.toggleChartNotes(((MidiChartDialog)dialog).selection)) {
                ((MidiChartDialog)dialog).preview(((MidiChartDialog)dialog).selection, true, true);
            }
        }),
        SELECT(rec$ -> MidiChartDialog.access$1100(rec$), dialog -> {
            if (((MidiChartDialog)dialog).pattern.toggleChartNotes(((MidiChartDialog)dialog).selection)) {
                ((MidiChartDialog)dialog).preview(((MidiChartDialog)dialog).pattern, true, true);
            } else {
                ((MidiChartDialog)dialog).stopPreview();
            }
        }),
        PATTERN(rec$ -> MidiChartDialog.access$1200(rec$), dialog -> {
            if (((MidiChartDialog)dialog).chart.toggleChartNotes(((MidiChartDialog)dialog).selection)) {
                ((MidiChartDialog)dialog).preview(((MidiChartDialog)dialog).selection, true, true);
            } else {
                ((MidiChartDialog)dialog).stopPreview();
            }
        });

        private final Consumer<MidiChartDialog> selectAction;
        private final Consumer<MidiChartDialog> activateAction;

        private Mode(Consumer<MidiChartDialog> selectAction, Consumer<MidiChartDialog> activateAction) {
            this.selectAction = selectAction;
            this.activateAction = activateAction;
        }

        public void select(MidiChartDialog dialog) {
            this.selectAction.accept(dialog);
        }

        public void activate(MidiChartDialog dialog) {
            this.activateAction.accept(dialog);
        }
    }

    private class MidiPianoRollWidget
    extends MapWidget {
        private int startPitchClass = 0;
        private int startTimeStepIndex = 0;
        private int playVerticalLineX = -1;

        public MidiPianoRollWidget() {
            this.setFocusable(true);
        }

        public void setStartTimeStepIndex(int startTimeStepIndex) {
            if (this.startTimeStepIndex != startTimeStepIndex) {
                this.startTimeStepIndex = startTimeStepIndex;
                this.invalidate();
            }
        }

        public void setStartPitchClass(int startPitchClass) {
            if (this.startPitchClass != startPitchClass) {
                this.startPitchClass = startPitchClass;
                this.invalidate();
            }
        }

        public int getNumTimeSteps() {
            return (this.getWidth() - 6) / 3;
        }

        public int getNumPitchClassesFromMiddle() {
            return this.getHeight() / 4;
        }

        public void scrollToSelection() {
            MidiChart.Bounds bounds = MidiChartDialog.this.selection.getBounds();
            if (!bounds.isEmpty()) {
                int spacing;
                int safeSpacing = Math.min(2, (this.getNumTimeSteps() - bounds.getNumTimeSteps()) / 2);
                if (safeSpacing <= 0) {
                    this.setStartTimeStepIndex(bounds.minTimeStepIndex());
                } else {
                    spacing = this.startTimeStepIndex - bounds.minTimeStepIndex() + safeSpacing;
                    if (spacing > 0) {
                        this.setStartTimeStepIndex(Math.max(0, this.startTimeStepIndex - spacing));
                    } else {
                        spacing = bounds.maxTimeStepIndex() - (this.startTimeStepIndex + this.getNumTimeSteps() - 1) + safeSpacing;
                        if (spacing > 0) {
                            this.setStartTimeStepIndex(this.startTimeStepIndex + spacing);
                        }
                    }
                }
                safeSpacing = 2;
                if (safeSpacing <= 0) {
                    this.setStartPitchClass((bounds.minPitchClass() + bounds.maxPitchClass()) / 2);
                } else {
                    spacing = this.startPitchClass - bounds.minPitchClass() - this.getNumPitchClassesFromMiddle() + 1 + safeSpacing;
                    if (spacing > 0) {
                        this.setStartPitchClass(this.startPitchClass - spacing);
                    } else {
                        spacing = bounds.maxPitchClass() - this.startPitchClass - this.getNumPitchClassesFromMiddle() + safeSpacing;
                        if (spacing > 0) {
                            this.setStartPitchClass(this.startPitchClass + spacing);
                        }
                    }
                }
            }
        }

        public void timeShiftSelection(int numTimeSteps) {
            if (numTimeSteps < 0 && MidiChartDialog.this.selection.getBounds().minTimeStepIndex() <= 0) {
                return;
            }
            MidiChartDialog.this.selection.timeShift(numTimeSteps);
            if (MidiChartDialog.this.mode == Mode.PATTERN) {
                MidiChartDialog.this.pattern.timeShift(numTimeSteps);
            }
            this.scrollToSelection();
            this.invalidate();
        }

        public void pitchShiftSelection(int numPitchClasses) {
            MidiChartDialog.this.selection.pitchShift(numPitchClasses);
            if (MidiChartDialog.this.mode == Mode.PATTERN) {
                MidiChartDialog.this.pattern.pitchShift(numPitchClasses);
            }
            this.scrollToSelection();
            this.invalidate();
        }

        public void onTick() {
            int newVerticalLineX = this.calcChartXFromTime(MidiChartDialog.this.currentPreviewTime);
            if (newVerticalLineX != this.playVerticalLineX) {
                this.playVerticalLineX = newVerticalLineX;
                this.invalidate();
            }
        }

        private int calcChartXFromTime(EffectLoop.Time time) {
            if (time == null) {
                return -1;
            }
            long elapsed = time.nanos - MidiChartDialog.this.chart.getParameters().getTimestampNanos(this.startTimeStepIndex);
            if (elapsed < 0L) {
                return -1;
            }
            int xPos = 7 + (int)(3L * elapsed / ((MidiChartDialog)MidiChartDialog.this).chart.getParameters().timeStep().nanos);
            if (xPos >= this.getWidth()) {
                return -1;
            }
            return xPos;
        }

        public void onDraw() {
            int durationVerticalLineX;
            int numPitchValues = this.getHeight() / 4 + 1;
            int baseY = this.getHeight() / 2;
            boolean active = this.isActivated();
            HashSet<Integer> selectedPitchClasses = new HashSet<Integer>();
            if (active) {
                for (MidiNote selectedNote : MidiChartDialog.this.selection.getNotes()) {
                    selectedPitchClasses.add(selectedNote.pitchClass());
                }
            }
            MidiTimeSignature signature = MidiChartDialog.this.chart.getParameters().timeSignature();
            for (int i = -numPitchValues; i <= numPitchValues; ++i) {
                int pitch = i + this.startPitchClass;
                PianoRendering.PianoKey key = pitch == 0 ? PianoRendering.BLACK_KEY_BASE : PianoRendering.PIANO_KEYS[Math.floorMod(pitch, 12)];
                key.draw(this.view, baseY - i * 2, this.getWidth(), selectedPitchClasses.contains(pitch), timeStepIndex -> {
                    int adjTimeStepIndex = timeStepIndex + this.startTimeStepIndex;
                    if (adjTimeStepIndex % signature.notesPerMeasure() == 0) {
                        return TimeSeparator.MEASURE;
                    }
                    if (adjTimeStepIndex % signature.noteValue() == 0) {
                        return TimeSeparator.BEAT;
                    }
                    return TimeSeparator.NOTE;
                });
            }
            if (active) {
                this.drawAllNotes(MidiChartDialog.this.selection, note -> {
                    if (MidiChartDialog.this.chart.containsNote((MidiNote)note)) {
                        return PianoRendering.NOTE_NONE;
                    }
                    if (MidiChartDialog.this.mode == Mode.SELECT && MidiChartDialog.this.pattern.containsNote((MidiNote)note)) {
                        return PianoRendering.NOTE_PATTERN_SELECTED;
                    }
                    return PianoRendering.NOTE_INACTIVE;
                });
            }
            if (MidiChartDialog.this.mode == Mode.SELECT || MidiChartDialog.this.mode == Mode.PATTERN) {
                this.drawAllNotes(MidiChartDialog.this.pattern, note -> {
                    if (MidiChartDialog.this.chart.containsNote((MidiNote)note)) {
                        return PianoRendering.NOTE_NONE;
                    }
                    if (active && MidiChartDialog.this.selection.containsNote((MidiNote)note)) {
                        return PianoRendering.NOTE_NONE;
                    }
                    return PianoRendering.NOTE_PATTERN_DEFAULT;
                });
            }
            this.drawAllNotes(MidiChartDialog.this.chart, note -> {
                if (active) {
                    if (MidiChartDialog.this.mode == Mode.SELECT && MidiChartDialog.this.pattern.containsNote((MidiNote)note)) {
                        if (MidiChartDialog.this.selection.containsNote((MidiNote)note)) {
                            return PianoRendering.NOTE_PATTERN_SELECTED;
                        }
                        return PianoRendering.NOTE_PATTERN_OVERLAP;
                    }
                    if (MidiChartDialog.this.selection.containsNote((MidiNote)note)) {
                        return PianoRendering.NOTE_SELECTED;
                    }
                }
                return PianoRendering.NOTE_DEFAULT;
            });
            if (this.playVerticalLineX >= 0) {
                this.view.drawLine(this.playVerticalLineX, 0, this.playVerticalLineX, this.getHeight() - 1, (byte)18);
            }
            if (MidiChartDialog.this.duration != null && (durationVerticalLineX = this.calcChartXFromTime(MidiChartDialog.this.duration)) >= 0) {
                this.view.drawLine(durationVerticalLineX, 0, durationVerticalLineX, this.getHeight() - 1, (byte)34);
            }
        }

        private void drawAllNotes(MidiChart chart, Function<MidiNote, NoteColors> colorsFunc) {
            int numTimeSteps = this.getNumTimeSteps();
            for (MidiNote note : chart.getChartVisibleNotes(this.startTimeStepIndex, numTimeSteps)) {
                this.drawNote(note, colorsFunc);
            }
        }

        private void drawNote(MidiNote note, Function<MidiNote, NoteColors> colorsFunc) {
            NoteColors colors;
            int baseY = this.getHeight() / 2;
            int noteX = 7 + (note.timeStepIndex() - this.startTimeStepIndex) * 3;
            int noteY = baseY - (note.pitchClass() - this.startPitchClass) * 2;
            if (noteY >= -1 && noteY < this.getHeight() && (colors = colorsFunc.apply(note)) != PianoRendering.NOTE_NONE) {
                colors.draw(this.view, noteX, noteY);
            }
        }

        public void onFocus() {
            this.activate();
        }

        public void onKeyPressed(MapKeyEvent event) {
            if (event.getKey() == MapPlayerInput.Key.LEFT) {
                this.timeShiftSelection(-1);
            } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
                this.timeShiftSelection(1);
            } else if (event.getKey() == MapPlayerInput.Key.UP) {
                this.pitchShiftSelection(1);
            } else if (event.getKey() == MapPlayerInput.Key.DOWN) {
                this.pitchShiftSelection(-1);
            } else if (event.getKey() == MapPlayerInput.Key.ENTER) {
                MidiChartDialog.this.mode.activate(MidiChartDialog.this);
                this.invalidate();
                MidiChartDialog.this.onChartChanged(MidiChartDialog.this.chart);
            } else if (event.getKey() == MapPlayerInput.Key.BACK) {
                MidiChartDialog.this.exitPianoRoll();
            }
        }
    }

    @FunctionalInterface
    private static interface FindTimeSeparatorFunc {
        public TimeSeparator find(int var1);
    }

    private static enum TimeSeparator {
        BACKGROUND,
        NOTE,
        BEAT,
        MEASURE;

    }

    public static class NoteColors {
        public final byte TOP;
        public final byte BTM;

        private NoteColors(Builder builder) {
            this.TOP = builder.TOP;
            this.BTM = builder.BTM;
        }

        public void draw(MapCanvas view, int x, int y) {
            view.writePixel(x, y, this.TOP);
            view.writePixel(x + 1, y, this.TOP);
            view.writePixel(x, y + 1, this.BTM);
            view.writePixel(x + 1, y + 1, this.BTM);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            public byte TOP = 0;
            public byte BTM = 0;

            public Builder top(int r, int g, int b) {
                this.TOP = MapColorPalette.getColor((int)r, (int)g, (int)b);
                return this;
            }

            public Builder btm(int r, int g, int b) {
                this.BTM = MapColorPalette.getColor((int)r, (int)g, (int)b);
                return this;
            }

            public NoteColors build() {
                return new NoteColors(this);
            }
        }
    }

    private static final class PianoRendering {
        public static final PianoKeyColors COLORS_BLACK_KEY_IDLE = PianoKeyColors.builder().key_top(27, 40, 54).key_btm(13, 13, 13).grid_bg(27, 40, 54).grid_note(69, 75, 95).grid_beat(13, 13, 13).grid_measure(152, 108, 72).build();
        public static final PianoKeyColors COLORS_BLACK_KEY_PRESSED = PianoKeyColors.builder().key_top(25, 57, 112).key_btm(36, 82, 159).grid_bg(25, 57, 112).grid_note(78, 77, 160).grid_beat(15, 13, 48).grid_measure(119, 93, 96).build();
        public static final PianoKeyColors COLORS_BLACK_BASE_KEY_IDLE = PianoKeyColors.builder().key_top(64, 43, 53).key_btm(48, 32, 40).grid_bg(48, 32, 40).grid_note(135, 84, 84).grid_beat(25, 25, 25).grid_measure(125, 53, 36).build();
        public static final PianoKeyColors COLORS_BLACK_BASE_KEY_PRESSED = PianoKeyColors.builder().key_top(94, 40, 27).key_btm(135, 67, 39).grid_bg(94, 40, 27).grid_note(138, 108, 112).grid_beat(48, 32, 40).grid_measure(186, 132, 88).build();
        public static final PianoKeyColors COLORS_WHITE_KEY_IDLE = PianoKeyColors.builder().key_top(220, 220, 220).key_btm(255, 255, 255).grid_bg(38, 63, 75).grid_note(84, 92, 116).grid_beat(18, 21, 30).grid_measure(153, 127, 76).build();
        public static final PianoKeyColors COLORS_WHITE_KEY_PRESSED = PianoKeyColors.builder().key_top(25, 93, 131).key_btm(44, 109, 186).grid_bg(25, 93, 131).grid_note(44, 109, 186).grid_beat(32, 42, 100).grid_measure(150, 154, 64).build();
        public static final PianoKey BLACK_KEY = new BlackPianoKey(COLORS_BLACK_KEY_IDLE, COLORS_BLACK_KEY_PRESSED);
        public static final PianoKey BLACK_KEY_BASE = new BlackPianoKey(COLORS_BLACK_BASE_KEY_IDLE, COLORS_BLACK_BASE_KEY_PRESSED);
        public static final PianoKey WHITE_KEY = new PianoKey(COLORS_WHITE_KEY_IDLE, COLORS_WHITE_KEY_PRESSED){

            @Override
            public void drawKey(MapCanvas view, int y, PianoKeyColors colors) {
                view.drawLine(4, y - 1, 5, y - 1, colors.KEY_TOP);
                view.fillRectangle(0, y, 6, 2, colors.KEY_BTM);
                view.drawLine(4, y + 2, 5, y + 2, colors.KEY_BTM);
            }
        };
        public static final PianoKey WHITE_KEY_HALF_TOP = new PianoKey(COLORS_WHITE_KEY_IDLE, COLORS_WHITE_KEY_PRESSED){

            @Override
            public void drawKey(MapCanvas view, int y, PianoKeyColors colors) {
                view.drawLine(4, y - 1, 5, y - 1, colors.KEY_TOP);
                view.fillRectangle(0, y, 6, 2, colors.KEY_BTM);
            }
        };
        public static final PianoKey WHITE_KEY_HALF_BTM = new PianoKey(COLORS_WHITE_KEY_IDLE, COLORS_WHITE_KEY_PRESSED){

            @Override
            public void drawKey(MapCanvas view, int y, PianoKeyColors colors) {
                view.drawLine(0, y, 5, y, colors.KEY_TOP);
                view.drawLine(0, y + 1, 5, y + 1, colors.KEY_BTM);
                view.drawLine(4, y + 2, 5, y + 2, colors.KEY_BTM);
            }
        };
        public static final PianoKey[] PIANO_KEYS = new PianoKey[]{BLACK_KEY, WHITE_KEY, BLACK_KEY, WHITE_KEY, BLACK_KEY, WHITE_KEY_HALF_BTM, WHITE_KEY_HALF_TOP, BLACK_KEY, WHITE_KEY, BLACK_KEY, WHITE_KEY_HALF_BTM, WHITE_KEY_HALF_TOP};
        public static final NoteColors NOTE_NONE = NoteColors.builder().build();
        public static final NoteColors NOTE_DEFAULT = NoteColors.builder().top(255, 64, 64).btm(220, 55, 55).build();
        public static final NoteColors NOTE_SELECTED = NoteColors.builder().top(213, 219, 92).btm(183, 188, 79).build();
        public static final NoteColors NOTE_INACTIVE = NoteColors.builder().top(211, 217, 220).btm(199, 199, 199).build();
        public static final NoteColors NOTE_PATTERN_DEFAULT = NoteColors.builder().top(54, 168, 176).btm(36, 161, 161).build();
        public static final NoteColors NOTE_PATTERN_SELECTED = NoteColors.builder().top(77, 238, 250).btm(66, 205, 215).build();
        public static final NoteColors NOTE_PATTERN_OVERLAP = NoteColors.builder().top(25, 204, 127).btm(56, 178, 127).build();

        private PianoRendering() {
        }

        private static class PianoKeyColors {
            public final byte KEY_TOP;
            public final byte KEY_BTM;
            public final byte[] GRID_COLORS;

            private PianoKeyColors(Builder builder) {
                this.KEY_TOP = builder.KEY_TOP;
                this.KEY_BTM = builder.KEY_BTM;
                this.GRID_COLORS = new byte[]{builder.GRID_BG, builder.GRID_NOTE, builder.GRID_BEAT, builder.GRID_MEASURE};
            }

            public byte getGridColor(TimeSeparator sep) {
                return this.GRID_COLORS[sep.ordinal()];
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {
                public byte KEY_TOP;
                public byte KEY_BTM;
                public byte GRID_BG;
                public byte GRID_NOTE;
                public byte GRID_BEAT;
                public byte GRID_MEASURE;

                public Builder key_top(int r, int g, int b) {
                    this.KEY_TOP = MapColorPalette.getColor((int)r, (int)g, (int)b);
                    return this;
                }

                public Builder key_btm(int r, int g, int b) {
                    this.KEY_BTM = MapColorPalette.getColor((int)r, (int)g, (int)b);
                    return this;
                }

                public Builder grid_bg(int r, int g, int b) {
                    this.GRID_BG = MapColorPalette.getColor((int)r, (int)g, (int)b);
                    return this;
                }

                public Builder grid_note(int r, int g, int b) {
                    this.GRID_NOTE = MapColorPalette.getColor((int)r, (int)g, (int)b);
                    return this;
                }

                public Builder grid_beat(int r, int g, int b) {
                    this.GRID_BEAT = MapColorPalette.getColor((int)r, (int)g, (int)b);
                    return this;
                }

                public Builder grid_measure(int r, int g, int b) {
                    this.GRID_MEASURE = MapColorPalette.getColor((int)r, (int)g, (int)b);
                    return this;
                }

                public PianoKeyColors build() {
                    return new PianoKeyColors(this);
                }
            }
        }

        private static class BlackPianoKey
        extends PianoKey {
            public BlackPianoKey(PianoKeyColors colors_idle, PianoKeyColors colors_pressed) {
                super(colors_idle, colors_pressed);
            }

            @Override
            public void drawKey(MapCanvas view, int y, PianoKeyColors colors) {
                view.drawLine(0, y, 3, y, colors.KEY_TOP);
                view.drawLine(0, y + 1, 3, y + 1, colors.KEY_BTM);
            }
        }

        private static abstract class PianoKey {
            private final PianoKeyColors colors_idle;
            private final PianoKeyColors colors_pressed;

            public PianoKey(PianoKeyColors colors_idle, PianoKeyColors colors_pressed) {
                this.colors_idle = colors_idle;
                this.colors_pressed = colors_pressed;
            }

            public abstract void drawKey(MapCanvas var1, int var2, PianoKeyColors var3);

            public final void draw(MapCanvas view, int y, int w, boolean pressed, FindTimeSeparatorFunc timeSepFunc) {
                PianoKeyColors colors = pressed ? this.colors_pressed : this.colors_idle;
                this.drawKey(view, y, colors);
                byte bgColor = colors.getGridColor(TimeSeparator.BACKGROUND);
                view.drawLine(6, y, w - 1, y, bgColor);
                view.drawLine(6, y + 1, w - 1, y + 1, bgColor);
                int timeStepIndex = 0;
                for (int x = 6; x < w; x += 3) {
                    byte bgColor2 = colors.getGridColor(timeSepFunc.find(timeStepIndex));
                    view.writePixel(x, y, bgColor2);
                    view.writePixel(x, y + 1, bgColor2);
                    ++timeStepIndex;
                }
            }
        }
    }

    private static abstract class ConfirmClearDialog
    extends MapWidgetMenu {
        public ConfirmClearDialog() {
            this.setBounds(10, 22, 98, 58);
            this.setBackgroundColor(MapColorPalette.getColor((int)135, (int)33, (int)33));
        }

        public abstract void onConfirmClear();

        @Override
        public void onAttached() {
            super.onAttached();
            this.addWidget(new MapWidgetText().setText("Are you sure you\nwant to clear\nthis chart?").setBounds(5, 5, 80, 30));
            this.addWidget(new MapWidgetButton(){

                public void onActivate() {
                    this.close();
                }
            }.setText("No").setBounds(10, 40, 36, 13));
            this.addWidget(new MapWidgetButton(){

                public void onActivate() {
                    this.close();
                    this.onConfirmClear();
                }
            }.setText("Yes").setBounds(52, 40, 36, 13));
        }
    }

    private static abstract class ChartSettingsDialog
    extends MapWidgetMenu {
        private MidiChartParameters params = MidiChartParameters.DEFAULT;

        public ChartSettingsDialog() {
            this.setBounds(10, 22, 98, 88);
            this.setBackgroundColor(MapColorPalette.getColor((int)138, (int)152, (int)180));
        }

        public abstract void onParamsChanged(MidiChartParameters var1);

        public MidiChartParameters getParams() {
            return this.params;
        }

        public ChartSettingsDialog setParams(MidiChartParameters params) {
            this.params = params;
            this.invalidate();
            return this;
        }

        @Override
        public void onAttached() {
            super.onAttached();
            int num_x_offset = 40;
            int y_pos = 12;
            MapWidgetText label = new MapWidgetText();
            label.setFont(MapFont.TINY);
            label.setText("- Time Signature -");
            label.setPosition(15, 5);
            label.setColor(MapColorPalette.getColor((int)115, (int)108, (int)18));
            this.addWidget((MapWidget)label);
            this.addWidget(new MapWidgetNumberBox(){

                @Override
                public void onAttached() {
                    this.setRange(1.0, 16.0);
                    this.setIncrement(1.0);
                    this.setInitialValue(params.timeSignature().beatsPerMeasure());
                    super.onAttached();
                }

                @Override
                public void onResetValue() {
                    this.setValue(4.0);
                }

                @Override
                public void onValueChangeEnd() {
                    params = params.withTimeSignature(MidiTimeSignature.of((int)this.getValue(), params.timeSignature().noteValue()));
                    this.onParamsChanged(params);
                }
            }.setBounds(40, y_pos, this.getWidth() - 40, 13));
            this.addLabel(5, y_pos + 1, "Beats per");
            this.addLabel(5, y_pos + 7, "measure");
            (this.addWidget(new MapWidgetNumberBox(){

                @Override
                public void onAttached() {
                    this.setRange(1.0, 16.0);
                    this.setIncrement(1.0);
                    this.setTextPrefix("1/");
                    this.setInitialValue(params.timeSignature().noteValue());
                    super.onAttached();
                }

                @Override
                public void onResetValue() {
                    this.setValue(4.0);
                }

                @Override
                public void onValueChangeEnd() {
                    params = params.withTimeSignature(MidiTimeSignature.of(params.timeSignature().beatsPerMeasure(), (int)this.getValue()));
                    this.onParamsChanged(params);
                }
            })).setBounds(40, y_pos += 16, this.getWidth() - 40, 13);
            this.addLabel(5, y_pos + 4, "Note value");
            (this.addWidget(new MapWidgetNumberBox(){

                @Override
                public void onAttached() {
                    this.setRange(1.0, 10000.0);
                    this.setIncrement(1.0);
                    this.setInitialValue(params.bpm());
                    super.onAttached();
                }

                @Override
                public void onResetValue() {
                    this.setValue(120.0);
                }

                @Override
                public void onValueChangeEnd() {
                    params = params.withBPM((int)this.getValue());
                    this.onParamsChanged(params);
                }
            })).setBounds(40, y_pos += 20, this.getWidth() - 40, 13);
            this.addLabel(5, y_pos + 1, "Beats per");
            this.addLabel(5, y_pos + 7, "minute");
            (this.addWidget(new MapWidgetNumberBox(){

                @Override
                public void onAttached() {
                    this.setRange(1.0, 192.0);
                    this.setIncrement(1.0);
                    this.setInitialValue(params.pitchClasses());
                    super.onAttached();
                }

                @Override
                public void onResetValue() {
                    this.setValue(12.0);
                }

                @Override
                public void onValueChangeEnd() {
                    params = params.withPitchClasses((int)this.getValue());
                    this.onParamsChanged(params);
                }
            })).setBounds(40, y_pos += 17, this.getWidth() - 40, 13);
            this.addLabel(5, y_pos + 1, "Pitch");
            this.addLabel(5, y_pos + 7, "classes");
        }
    }
}

