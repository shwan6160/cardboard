/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 */
package com.bergerkiller.bukkit.tc.attachments.control.sound;

import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundNumberBox;

public abstract class MapWidgetSoundVolumePitch
extends MapWidget {
    private static final int BOX_HEIGHT = 13;
    private static final int LEFT_TEXT_WIDTH = 12;
    private static final int RANDOM_ICON_WIDTH = 5;
    private final MapWidgetSoundNumberBox volumeBase = new MapWidgetSoundNumberBox(){

        @Override
        public String getAcceptedPropertyName() {
            return "Sound Base Volume";
        }

        @Override
        public void onValueChanged(double newValue) {
            MapWidgetSoundVolumePitch.this.onChanged();
        }
    }.setDefaultValue(1.0);
    private final MapWidgetSoundNumberBox volumeRandom = new MapWidgetSoundNumberBox(){

        @Override
        public String getAcceptedPropertyName() {
            return "Sound Random Volume";
        }

        @Override
        public void onValueChanged(double newValue) {
            MapWidgetSoundVolumePitch.this.onChanged();
        }
    }.setDefaultValue(0.0);
    private final MapWidgetSoundNumberBox speedBase = new MapWidgetSoundNumberBox(){

        @Override
        public String getAcceptedPropertyName() {
            return "Sound Base Speed";
        }

        @Override
        public void onValueChanged(double newValue) {
            MapWidgetSoundVolumePitch.this.onChanged();
        }
    }.setDefaultValue(1.0);
    private final MapWidgetSoundNumberBox speedRandom = new MapWidgetSoundNumberBox(){

        @Override
        public String getAcceptedPropertyName() {
            return "Sound Random Speed";
        }

        @Override
        public void onValueChanged(double newValue) {
            MapWidgetSoundVolumePitch.this.onChanged();
        }
    }.setDefaultValue(0.0);

    public abstract void onChanged();

    public MapWidgetSoundVolumePitch setInitialBaseVolume(double value) {
        this.volumeBase.setInitialValue(value);
        return this;
    }

    public MapWidgetSoundVolumePitch setInitialRandomVolume(double value) {
        this.volumeRandom.setInitialValue(value);
        return this;
    }

    public MapWidgetSoundVolumePitch setInitialBaseSpeed(double value) {
        this.speedBase.setInitialValue(value);
        return this;
    }

    public MapWidgetSoundVolumePitch setInitialRandomSpeed(double value) {
        this.speedRandom.setInitialValue(value);
        return this;
    }

    public double getBaseVolume() {
        return this.volumeBase.getValue();
    }

    public double getRandomVolume() {
        return this.volumeRandom.getValue();
    }

    public double getBaseSpeed() {
        return this.speedBase.getValue();
    }

    public double getRandomSpeed() {
        return this.speedRandom.getValue();
    }

    public void onAttached() {
        this.onBoundsChanged();
        this.addWidget(this.volumeBase);
        this.addWidget(this.volumeRandom);
        this.addWidget(this.speedBase);
        this.addWidget(this.speedRandom);
    }

    private int calcBoxWidth() {
        return (this.getWidth() - 12 - 5) / 2;
    }

    public void onBoundsChanged() {
        int boxWidth = this.calcBoxWidth();
        int randomBoxX = 12 + boxWidth + 5;
        this.volumeBase.setBounds(12, 0, boxWidth, 13);
        this.volumeRandom.setBounds(randomBoxX, 0, boxWidth, 13);
        this.speedBase.setBounds(12, this.getHeight() - 13, boxWidth, 13);
        this.speedRandom.setBounds(randomBoxX, this.getHeight() - 13, boxWidth, 13);
    }

    public void onDraw() {
        int boxWidth = this.calcBoxWidth();
        this.view.draw(MapFont.TINY, 0, 4, (byte)18, (CharSequence)"Vol");
        this.view.draw(MapFont.TINY, 0, this.getHeight() - 9, (byte)18, (CharSequence)"Spd");
        this.view.draw(MapFont.TINY, 12 + boxWidth + 1, 4, (byte)18, (CharSequence)"\u00f1");
        this.view.draw(MapFont.TINY, 12 + boxWidth + 1, this.getHeight() - 9, (byte)18, (CharSequence)"\u00f1");
    }
}

