/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import java.util.function.Consumer;
import org.bukkit.util.Vector;

public abstract class MapWidgetSizeBox
extends MapWidget {
    public final MapWidgetNumberBox x = (MapWidgetNumberBox)this.addWidget(new SizeNumberBox(){

        @Override
        public String getAcceptedPropertyName() {
            return "Size X-Axis";
        }

        @Override
        protected void onResetClickSound() {
            if (!MapWidgetSizeBox.this.isUniformFocused()) {
                super.onResetClickSound();
            }
        }
    });
    public final MapWidgetNumberBox y = (MapWidgetNumberBox)this.addWidget(new SizeNumberBox(){

        @Override
        public String getAcceptedPropertyName() {
            return "Size Y-Axis";
        }

        @Override
        protected void onResetClickSound() {
            if (!MapWidgetSizeBox.this.isUniformFocused()) {
                super.onResetClickSound();
            }
        }
    });
    public final MapWidgetNumberBox z = (MapWidgetNumberBox)this.addWidget(new SizeNumberBox(){

        @Override
        public String getAcceptedPropertyName() {
            return "Size Z-Axis";
        }

        @Override
        public void onKeyPressed(MapKeyEvent event) {
            if (event.getKey() == MapPlayerInput.Key.DOWN) {
                MapWidgetSizeBox.this.setUniformFocused(true);
            } else {
                super.onKeyPressed(event);
            }
        }
    });
    private Vector uniformFocusStart = new Vector();
    private boolean suppressSizeChanges = false;
    private boolean uniformFocusActive = false;
    private double defaultValue = 1.0;
    private boolean enableY = true;
    private int lastUniformResetTick = -1;

    public MapWidgetSizeBox() {
        this.setRetainChildWidgets(true);
        this.setRangeAndDefault(false, 1.0);
    }

    public boolean isUniformFocused() {
        return this.uniformFocusActive;
    }

    public void setUniformFocused(boolean focused) {
        if (this.uniformFocusActive == focused) {
            return;
        }
        this.uniformFocusActive = focused;
        if (focused) {
            this.setFocusable(true);
            this.focus();
            this.forAllAxis(a -> a.setAlwaysFocused(true));
            this.uniformFocusStart = new Vector(this.x.getValue(), this.y.getValue(), this.z.getValue());
        } else {
            this.forAllAxis(a -> {
                a.setAlwaysFocused(false);
                a.updateArrowFocus(false, false);
            });
            this.setFocusable(false);
        }
    }

    public MapWidgetSizeBox setYAxisEnabled(boolean enableY) {
        if (this.enableY != enableY) {
            this.enableY = enableY;
            if (enableY) {
                this.addWidget(this.y);
            } else {
                this.removeWidget(this.y);
            }
            this.onBoundsChanged();
        }
        return this;
    }

    public MapWidgetSizeBox setRangeAndDefault(boolean canBeNegative, double defaultValue) {
        double min = canBeNegative ? -1000.0 : 0.01;
        double max = 1000.0;
        this.x.setRange(min, max);
        this.y.setRange(min, max);
        this.z.setRange(min, max);
        this.defaultValue = defaultValue;
        return this;
    }

    public abstract void onSizeChanged();

    public void onUniformResetValue() {
        this.forAllAxis(a -> a.setValue(this.defaultValue));
    }

    public void setSize(double sx, double sy, double sz) {
        if (sx != this.x.getValue() || sy != this.y.getValue() || sz != this.z.getValue()) {
            this.setInitialSize(sx, sy, sz);
            this.onSizeChanged();
        }
    }

    public void setInitialSize(double sx, double sy, double sz) {
        this.x.setInitialValue(sx);
        this.y.setInitialValue(sy);
        this.z.setInitialValue(sz);
    }

    public MapWidgetSizeBox setTextOverride(String text) {
        this.forAllAxis(a -> a.setTextOverride(text));
        return this;
    }

    public void onBoundsChanged() {
        int selHeight = (this.getHeight() - 2) / (this.enableY ? 3 : 2);
        this.x.setBounds(0, 0, this.getWidth(), selHeight);
        if (this.enableY) {
            this.y.setBounds(0, (this.getHeight() - selHeight) / 2, this.getWidth(), selHeight);
        }
        this.z.setBounds(0, this.getHeight() - selHeight, this.getWidth(), selHeight);
    }

    private void increaseUniform(double increase, int repeat) {
        double absX = Math.abs(this.uniformFocusStart.getX());
        double absY = Math.abs(this.uniformFocusStart.getY());
        double absZ = Math.abs(this.uniformFocusStart.getZ());
        this.suppressSizeChanges = true;
        if (!(absX != 0.0 || absZ != 0.0 || this.enableY && absY != 0.0)) {
            double value = MapWidgetNumberBox.scaledIncrease(this.x.getValue(), increase, repeat);
            this.x.setValue(value);
            if (this.enableY) {
                this.y.setValue(value);
            }
            this.z.setValue(value);
        } else if (absX > absZ && (!this.enableY || absX > absY)) {
            this.scaleAxisByIncreasing(this.x, this.uniformFocusStart.getX(), increase, repeat);
        } else if (this.enableY && absY > absX && absY > absZ) {
            this.scaleAxisByIncreasing(this.y, this.uniformFocusStart.getY(), increase, repeat);
        } else {
            this.scaleAxisByIncreasing(this.z, this.uniformFocusStart.getZ(), increase, repeat);
        }
        this.suppressSizeChanges = false;
        this.onSizeChanged();
    }

    private void scaleAxisByIncreasing(MapWidgetNumberBox axis, double uniformStart, double increase, int repeat) {
        axis.setValue(MapWidgetNumberBox.scaledIncrease(axis.getValue(), increase, repeat));
        double scale = axis.getValue() / uniformStart;
        if (axis != this.x) {
            this.x.setValue(this.roundByIncrease(this.uniformFocusStart.getX() * scale, increase));
        }
        if (axis != this.y && this.enableY) {
            this.y.setValue(this.roundByIncrease(this.uniformFocusStart.getY() * scale, increase));
        }
        if (axis != this.z) {
            this.z.setValue(this.roundByIncrease(this.uniformFocusStart.getZ() * scale, increase));
        }
    }

    private double roundByIncrease(double value, double incr) {
        return incr * (double)Math.round(value / incr);
    }

    public void onKey(MapKeyEvent event) {
        if (!this.isUniformFocused() || event.getKey() != MapPlayerInput.Key.ENTER) {
            super.onKey(event);
            return;
        }
        this.forAllAxis(a -> a.onKey(event));
        if (this.x.isHoldEnterResetComplete()) {
            this.uniformFocusStart = new Vector(this.x.getValue(), this.y.getValue(), this.z.getValue());
        }
    }

    public void onKeyReleased(MapKeyEvent event) {
        if (!this.isUniformFocused()) {
            super.onKeyReleased(event);
            return;
        }
        if (event.getKey() == MapPlayerInput.Key.LEFT) {
            this.forAllAxis(a -> a.stopArrowFocus(false));
        } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
            this.forAllAxis(a -> a.stopArrowFocus(true));
        } else if (event.getKey() == MapPlayerInput.Key.ENTER) {
            this.forAllAxis(a -> a.onKeyReleased(event));
        }
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (!this.isUniformFocused()) {
            super.onKeyPressed(event);
            return;
        }
        if (event.getKey() == MapPlayerInput.Key.LEFT) {
            this.forAllAxis(a -> a.updateArrowFocus(true, false));
            this.increaseUniform(-0.01, event.getRepeat());
        } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
            this.forAllAxis(a -> a.updateArrowFocus(false, true));
            this.increaseUniform(0.01, event.getRepeat());
        } else if (event.getKey() == MapPlayerInput.Key.ENTER) {
            this.forAllAxis(a -> a.onKeyPressed(event));
            return;
        }
        super.onKeyPressed(event);
        if (!this.isFocused()) {
            this.setUniformFocused(false);
        } else if (event.getKey() == MapPlayerInput.Key.UP) {
            this.setUniformFocused(false);
            this.x.focus();
        }
    }

    private void forAllAxis(Consumer<MapWidgetNumberBox> action) {
        action.accept(this.x);
        if (this.enableY) {
            action.accept(this.y);
        }
        action.accept(this.z);
    }

    private class SizeNumberBox
    extends MapWidgetNumberBox {
        private SizeNumberBox() {
        }

        @Override
        public void onValueChanged() {
            if (!MapWidgetSizeBox.this.suppressSizeChanges) {
                MapWidgetSizeBox.this.onSizeChanged();
            }
        }

        @Override
        public void onResetValue() {
            if (MapWidgetSizeBox.this.isUniformFocused()) {
                if (MapWidgetSizeBox.this.lastUniformResetTick != CommonUtil.getServerTicks()) {
                    MapWidgetSizeBox.this.lastUniformResetTick = CommonUtil.getServerTicks();
                    MapWidgetSizeBox.this.onUniformResetValue();
                }
            } else {
                this.setValue(MapWidgetSizeBox.this.defaultValue);
            }
        }

        @Override
        protected void onDraw(boolean focused) {
            super.onDraw(focused || MapWidgetSizeBox.this.isFocused());
        }
    }
}

