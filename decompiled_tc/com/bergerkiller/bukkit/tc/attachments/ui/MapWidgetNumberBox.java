/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapFont$Alignment
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetArrow;
import com.bergerkiller.bukkit.tc.attachments.ui.SetValueTarget;
import org.bukkit.block.BlockFace;

public class MapWidgetNumberBox
extends MapWidget
implements SetValueTarget {
    private double _value = 0.0;
    private double _min = Double.NEGATIVE_INFINITY;
    private double _max = Double.POSITIVE_INFINITY;
    private double _incr = 0.01;
    private int _changeRepeat = 0;
    private boolean _vertical = false;
    private boolean _alwaysFocused = false;
    private int _holdEnterProgress = 0;
    private int _holdEnterMaximum = 15;
    private String _textOverride = null;
    private String _textPrefix = "";
    private final MapWidgetArrow nav_decr = new MapWidgetArrow(BlockFace.WEST);
    private final MapWidgetArrow nav_incr = new MapWidgetArrow(BlockFace.EAST);

    public MapWidgetNumberBox() {
        this.setFocusable(true);
    }

    public MapWidgetNumberBox setVertical(boolean vertical) {
        if (this._vertical != vertical) {
            this._vertical = vertical;
            this.nav_decr.setDirection(vertical ? BlockFace.NORTH : BlockFace.WEST);
            this.nav_incr.setDirection(vertical ? BlockFace.SOUTH : BlockFace.EAST);
            this.onBoundsChanged();
        }
        return this;
    }

    public void setTextOverride(String text) {
        if (!LogicUtil.bothNullOrEqual((Object)this._textOverride, (Object)text)) {
            this._textOverride = text;
            this.invalidate();
        }
    }

    public void setTextPrefix(String textPrefix) {
        if (!LogicUtil.bothNullOrEqual((Object)this._textPrefix, (Object)textPrefix)) {
            this._textPrefix = textPrefix;
            this.invalidate();
        }
    }

    public void setAlwaysFocused(boolean always) {
        if (this._alwaysFocused != always) {
            this._alwaysFocused = always;
            this.invalidate();
            if (always) {
                this.nav_incr.setVisible(true);
                this.nav_decr.setVisible(true);
            } else {
                this.nav_incr.setVisible(this.isFocused());
                this.nav_decr.setVisible(this.isFocused());
            }
        }
    }

    public void setIncrement(double increment) {
        this._incr = increment;
    }

    public void setRange(double min, double max) {
        this._min = min;
        this._max = max;
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
        if (operation.perform(this::getValue, this::setValue, value)) {
            this.onValueChangeEnd();
            return true;
        }
        return false;
    }

    public void setInitialValue(double value) {
        if (value != this._value) {
            this.updateValue(value);
        }
    }

    public void setValue(double value) {
        if (value != this._value) {
            this.updateValue(value);
            this.onValueChanged();
        }
    }

    private void updateValue(double value) {
        this._value = value;
        if (this._value < this._min) {
            this._value = this._min;
        } else if (this._value > this._max) {
            this._value = this._max;
        }
        this.invalidate();
    }

    public double getValue() {
        return this._value;
    }

    public int getChangeRepeat() {
        return this._changeRepeat;
    }

    public void onAttached() {
        super.onAttached();
        this.nav_decr.setVisible(this._alwaysFocused);
        this.nav_incr.setVisible(this._alwaysFocused);
        this.nav_decr.setClipParent(this.isClipParent());
        this.nav_incr.setClipParent(this.isClipParent());
        this.addWidget(this.nav_decr);
        this.addWidget(this.nav_incr);
    }

    public void onBoundsChanged() {
        if (this._vertical) {
            int x_offset = this.getWidth() - this.nav_decr.getWidth() >> 1;
            this.nav_decr.setPosition(x_offset, this.getHeight() - this.nav_incr.getHeight());
            this.nav_incr.setPosition(x_offset, 0);
        } else {
            int y_offset = this.getHeight() - this.nav_decr.getHeight() >> 1;
            this.nav_decr.setPosition(0, y_offset);
            this.nav_incr.setPosition(this.getWidth() - this.nav_incr.getWidth(), y_offset);
        }
    }

    public void onResetValue() {
        this.setValue(0.0);
    }

    public void onResetSpecial(MapPlayerInput.Key key) {
        double newValue = -this.getValue();
        if (newValue >= this._min && newValue <= this._max) {
            this.setValue(newValue);
        }
    }

    private static double getExp(int repeat) {
        int a = repeat / 3;
        int b = repeat % 3;
        double f = b == 0 ? 1.0 : (b == 1 ? 2.0 : 5.0);
        return f * Math.pow(10.0, a);
    }

    public static double scaledIncrease(double value, double incr, int repeat) {
        value = (incr *= MapWidgetNumberBox.getExp(repeat / 50)) * (double)Math.round(value / incr);
        return value + incr;
    }

    private void addValue(double incr, int repeat) {
        this.setValue(MapWidgetNumberBox.scaledIncrease(this.getValue(), incr, repeat));
    }

    public void updateArrowFocus(boolean decreasing, boolean increasing) {
        if (decreasing) {
            this.nav_decr.sendFocus();
        } else {
            this.nav_decr.stopFocus();
        }
        if (increasing) {
            this.nav_incr.sendFocus();
        } else {
            this.nav_incr.stopFocus();
        }
    }

    public void stopArrowFocus(boolean increasing) {
        if (increasing) {
            this.nav_incr.stopFocus();
        } else {
            this.nav_decr.stopFocus();
        }
    }

    public boolean isHoldEnterResetComplete() {
        return this._holdEnterProgress == this._holdEnterMaximum;
    }

    public void onKey(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.ENTER) {
            if (this._holdEnterProgress <= this._holdEnterMaximum) {
                ++this._holdEnterProgress;
                if (this.isHoldEnterResetComplete()) {
                    this.activate();
                }
                this.invalidate();
            }
        } else {
            super.onKey(event);
        }
    }

    public void onKeyPressed(MapKeyEvent event) {
        this._changeRepeat = event.getRepeat();
        if (event.getKey() != MapPlayerInput.Key.ENTER) {
            if (this._holdEnterProgress > 0 && this._holdEnterProgress < this._holdEnterMaximum) {
                switch (event.getKey()) {
                    case UP: 
                    case DOWN: 
                    case LEFT: 
                    case RIGHT: {
                        this.onResetSpecial(event.getKey());
                        this._holdEnterProgress = this._holdEnterMaximum + 1;
                        this.invalidate();
                        this.onResetClickSound();
                        break;
                    }
                    default: {
                        super.onKeyPressed(event);
                        break;
                    }
                }
            } else if (this._vertical) {
                if (event.getKey() == MapPlayerInput.Key.DOWN) {
                    this.updateArrowFocus(true, false);
                    this.addValue(-this._incr, event.getRepeat());
                } else if (event.getKey() == MapPlayerInput.Key.UP) {
                    this.updateArrowFocus(false, true);
                    this.addValue(this._incr, event.getRepeat());
                } else {
                    super.onKeyPressed(event);
                }
            } else if (event.getKey() == MapPlayerInput.Key.LEFT) {
                this.updateArrowFocus(true, false);
                this.addValue(-this._incr, event.getRepeat());
            } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
                this.updateArrowFocus(false, true);
                this.addValue(this._incr, event.getRepeat());
            } else {
                super.onKeyPressed(event);
            }
        }
    }

    public void onKeyReleased(MapKeyEvent event) {
        super.onKeyReleased(event);
        if (event.getKey() == MapPlayerInput.Key.ENTER) {
            this._holdEnterProgress = 0;
            this.invalidate();
        } else if (this._vertical) {
            if (event.getKey() == MapPlayerInput.Key.DOWN) {
                this.stopArrowFocus(false);
                this.onValueChangeEnd();
            } else if (event.getKey() == MapPlayerInput.Key.UP) {
                this.stopArrowFocus(true);
                this.onValueChangeEnd();
            }
        } else if (event.getKey() == MapPlayerInput.Key.LEFT) {
            this.stopArrowFocus(false);
            this.onValueChangeEnd();
        } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
            this.stopArrowFocus(true);
            this.onValueChangeEnd();
        }
    }

    public void onFocus() {
        this.nav_decr.setVisible(true);
        this.nav_incr.setVisible(true);
    }

    public void onBlur() {
        this.nav_decr.setVisible(this._alwaysFocused);
        this.nav_incr.setVisible(this._alwaysFocused);
    }

    public String getValueText() {
        String s = this._incr == 1.0 ? Integer.toString((int)this.getValue()) : Util.stringifyNumberBoxValue(this.getValue());
        if (!this._textPrefix.isEmpty()) {
            s = this._textPrefix + s;
        }
        return s;
    }

    public void onDraw() {
        this.onDraw(this.isFocused());
    }

    protected void onDraw(boolean focused) {
        String text = this._textOverride != null ? this._textOverride : this.getValueText();
        int holdEnterProgress = this._holdEnterProgress;
        if (holdEnterProgress > this._holdEnterMaximum) {
            holdEnterProgress = 0;
        }
        if (this._vertical) {
            int offset = this.nav_decr.getHeight() + 1;
            MapWidgetButton.fillBackground((MapCanvas)this.view.getView(1, offset + 1, this.getWidth() - 2, this.getHeight() - 2 * offset - 2), (boolean)this.isEnabled(), (boolean)focused);
            this.view.drawRectangle(0, offset, this.getWidth(), this.getHeight() - 2 * offset, focused ? (byte)18 : 119);
            if (holdEnterProgress > 0) {
                int barHeight = (this.getHeight() - 2 * offset - 4) * holdEnterProgress / 2 / this._holdEnterMaximum;
                this.view.fillRectangle(2, 2 + offset, this.getWidth() - 4, barHeight, (byte)18);
                this.view.fillRectangle(2, this.getHeight() - offset - barHeight - 2, this.getWidth() - 4, barHeight, (byte)18);
            }
        } else {
            int offset = this.nav_decr.getWidth() + 1;
            MapWidgetButton.fillBackground((MapCanvas)this.view.getView(offset + 1, 1, this.getWidth() - 2 * offset - 2, this.getHeight() - 2), (boolean)this.isEnabled(), (boolean)focused);
            this.view.drawRectangle(offset, 0, this.getWidth() - 2 * offset, this.getHeight(), focused ? (byte)18 : 119);
            if (holdEnterProgress > 0) {
                int barWidth = (this.getWidth() - 2 * offset - 4) * holdEnterProgress / 2 / this._holdEnterMaximum;
                this.view.fillRectangle(2 + offset, 2, barWidth, this.getHeight() - 4, (byte)18);
                this.view.fillRectangle(this.getWidth() - offset - barWidth - 2, 2, barWidth, this.getHeight() - 4, (byte)18);
            }
        }
        this.view.setAlignment(MapFont.Alignment.MIDDLE);
        this.view.draw(MapFont.MINECRAFT, this.getWidth() / 2, (this.getHeight() - 7) / 2, (byte)34, (CharSequence)text);
    }

    public void onActivate() {
        this.onResetValue();
        this.onValueChangeEnd();
        this.onResetClickSound();
    }

    protected void onResetClickSound() {
        this.display.playSound(SoundEffect.CLICK);
    }

    public void onValueChanged() {
    }

    public void onValueChangeEnd() {
    }
}

