/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.attachments.control.sound;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundElement;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetArrow;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.attachments.ui.SetValueTarget;
import java.text.NumberFormat;
import org.bukkit.block.BlockFace;

public abstract class MapWidgetSoundNumberBox
extends MapWidgetSoundElement
implements SetValueTarget {
    private static final NumberFormat numberFormat = Util.createNumberFormat(1, 2);
    private final MapWidgetArrow leftArrow = new MapWidgetArrow(BlockFace.WEST);
    private final MapWidgetArrow rightArrow = new MapWidgetArrow(BlockFace.EAST);
    private double value = 0.0;
    private double defaultValue = 0.0;
    private double _incr = 0.01;
    private double _min = 0.0;
    private double _max = 100.0;
    private int _holdEnterProgress = 0;
    private int _holdEnterMaximum = 15;

    public abstract void onValueChanged(double var1);

    public MapWidgetSoundNumberBox setInitialValue(double value) {
        this.value = value;
        return this;
    }

    public MapWidgetSoundNumberBox setDefaultValue(double value) {
        this.defaultValue = value;
        return this;
    }

    public MapWidgetSoundNumberBox setIncrement(double increment) {
        this._incr = increment;
        return this;
    }

    public MapWidgetSoundNumberBox setRange(double min, double max) {
        this._min = min;
        this._max = max;
        return this;
    }

    public double getValue() {
        return this.value;
    }

    public MapWidgetSoundNumberBox setValue(double value) {
        if (this.value != (value = MathUtil.clamp((double)value, (double)this._min, (double)this._max))) {
            this.value = value;
            this.onValueChanged(value);
            this.invalidate();
        }
        return this;
    }

    private void addValue(double incr, int repeat) {
        this.setValue(MapWidgetNumberBox.scaledIncrease(this.getValue(), incr, repeat));
    }

    public boolean isHoldEnterResetComplete() {
        return this._holdEnterProgress == this._holdEnterMaximum;
    }

    public void onActivate() {
        super.onActivate();
        this._holdEnterProgress = this._holdEnterMaximum + 1;
        this.removeWidget(this.leftArrow);
        this.removeWidget(this.rightArrow);
        this.leftArrow.stopFocus();
        this.rightArrow.stopFocus();
        this.addWidget(this.leftArrow.setPosition(-this.leftArrow.getWidth() - 1, 1));
        this.addWidget(this.rightArrow.setPosition(this.getWidth() + 1, 1));
    }

    public void onDeactivate() {
        super.onDeactivate();
        this._holdEnterProgress = 0;
        this.removeWidget(this.leftArrow);
        this.removeWidget(this.rightArrow);
    }

    @Override
    public void onDraw() {
        super.onDraw();
        int holdEnterProgress = this._holdEnterProgress;
        if (holdEnterProgress > this._holdEnterMaximum) {
            holdEnterProgress = 0;
        }
        if (holdEnterProgress > 0) {
            int barWidth = this.getWidth() * holdEnterProgress / 2 / this._holdEnterMaximum;
            this.view.fillRectangle(2, 2, barWidth, this.getHeight() - 4, (byte)18);
            this.view.fillRectangle(this.getWidth() - barWidth - 2, 2, barWidth, this.getHeight() - 4, (byte)18);
        }
        this.view.draw(MapFont.MINECRAFT, 3, 3, (byte)34, (CharSequence)numberFormat.format(this.value));
    }

    public void onKey(MapKeyEvent event) {
        if (this.isActivated() && event.getKey() == MapPlayerInput.Key.ENTER) {
            if (this._holdEnterProgress <= this._holdEnterMaximum) {
                ++this._holdEnterProgress;
                if (this.isHoldEnterResetComplete()) {
                    this.setValue(this.defaultValue);
                }
                this.invalidate();
            }
        } else {
            super.onKey(event);
        }
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (this.isActivated()) {
            if (event.getKey() == MapPlayerInput.Key.LEFT) {
                this.addValue(-this._incr, event.getRepeat());
                this.leftArrow.sendFocus();
                return;
            }
            if (event.getKey() == MapPlayerInput.Key.RIGHT) {
                this.addValue(this._incr, event.getRepeat());
                this.rightArrow.sendFocus();
                return;
            }
            if (event.getKey() == MapPlayerInput.Key.ENTER) {
                if (event.getRepeat() == 15 && this._holdEnterProgress > this._holdEnterMaximum) {
                    this._holdEnterProgress = 1;
                }
                return;
            }
            if (event.getKey() == MapPlayerInput.Key.UP || event.getKey() == MapPlayerInput.Key.DOWN) {
                this.focus();
            }
        }
        super.onKeyPressed(event);
    }

    public void onKeyReleased(MapKeyEvent event) {
        if (this.isActivated()) {
            if (event.getKey() == MapPlayerInput.Key.LEFT) {
                this.leftArrow.stopFocus();
            } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
                this.rightArrow.stopFocus();
            } else if (event.getKey() == MapPlayerInput.Key.ENTER) {
                this._holdEnterProgress = 0;
                this.invalidate();
            }
        }
    }

    @Override
    public String getAcceptedPropertyName() {
        return "Numeric Value";
    }

    @Override
    public boolean acceptTextValue(String value) {
        return this.acceptTextValue(SetValueTarget.Operation.SET, value);
    }

    @Override
    public boolean acceptTextValue(SetValueTarget.Operation operation, String value) {
        return operation.perform(this::getValue, this::setValue, value);
    }
}

