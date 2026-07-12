/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 */
package com.bergerkiller.bukkit.tc.attachments.ui.item;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetTooltip;
import com.bergerkiller.bukkit.tc.attachments.ui.SetValueTarget;

public abstract class CustomModelDataSelector
extends MapWidget
implements SetValueTarget {
    private int numDigits = 0;
    private int selectedDigit = 0;
    private int value = 0;
    private boolean isKeyUp = false;
    private boolean isKeyDown = false;
    public final MapWidgetTooltip tooltip = new MapWidgetTooltip();

    public CustomModelDataSelector() {
        this.setNumDigits(8);
        this.setFocusable(true);
        this.tooltip.setText("Custom Model Data");
    }

    public CustomModelDataSelector setNumDigits(int num) {
        if (this.numDigits != num) {
            this.numDigits = num;
            this.setSize(num * 4 + 3, 13);
        }
        return this;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        if (this.value != (value = MathUtil.clamp((int)value, (int)0, (int)((int)Math.pow(10.0, this.numDigits) - 1)))) {
            this.value = value;
            this.invalidate();
        }
    }

    public void onDraw() {
        int text_x = this.getWidth() - 1;
        int text_y = (this.getHeight() - 5) / 2;
        int tmp = this.value;
        for (int digit = 0; digit < this.numDigits; ++digit) {
            text_x -= 4;
            char ch = '-';
            if (this.value > 0) {
                ch = Character.forDigit(tmp % 10, 10);
                tmp /= 10;
            }
            byte color = 34;
            if (this.isActivated() && digit == this.selectedDigit) {
                color = 122;
            }
            this.view.draw((MapCanvas)MapFont.TINY.getSprite((Object)Character.valueOf(ch)), text_x, text_y, color);
        }
        if (this.isActivated()) {
            int selX = this.getWidth() - 4 * (this.selectedDigit + 1) - 1;
            int upY = 0;
            int downY = this.getHeight() - 2;
            byte upColor = this.isKeyUp ? (byte)18 : 122;
            byte downColor = this.isKeyDown ? (byte)18 : 122;
            this.view.drawPixel(selX, upY + 1, upColor);
            this.view.drawPixel(selX + 1, upY, upColor);
            this.view.drawPixel(selX + 2, upY + 1, upColor);
            this.view.drawPixel(selX, downY, downColor);
            this.view.drawPixel(selX + 1, downY + 1, downColor);
            this.view.drawPixel(selX + 2, downY, downColor);
        } else if (this.isFocused()) {
            this.view.drawRectangle(0, (this.getHeight() - 5) / 2 - 2, this.getWidth(), 9, (byte)18);
        }
    }

    private static double getExp(int repeat) {
        int a = repeat / 3;
        int b = repeat % 3;
        double f = b == 0 ? 1.0 : (b == 1 ? 2.0 : 5.0);
        return f * Math.pow(10.0, a);
    }

    public void onKeyReleased(MapKeyEvent event) {
        if (this.isKeyUp && event.getKey() == MapPlayerInput.Key.UP) {
            this.isKeyUp = false;
            this.invalidate();
        } else if (this.isKeyDown && event.getKey() == MapPlayerInput.Key.DOWN) {
            this.isKeyDown = false;
            this.invalidate();
        }
        super.onKeyReleased(event);
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.ENTER) {
            if (this.isActivated()) {
                this.deactivate();
            } else {
                this.selectedDigit = 0;
                this.activate();
            }
            return;
        }
        if (!this.isActivated()) {
            super.onKeyPressed(event);
            return;
        }
        if (event.getKey() == MapPlayerInput.Key.BACK) {
            this.deactivate();
        } else if (event.getKey() == MapPlayerInput.Key.LEFT) {
            ++this.selectedDigit;
            if (this.selectedDigit >= this.numDigits) {
                this.selectedDigit = this.numDigits - 1;
            }
            this.invalidate();
        } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
            --this.selectedDigit;
            if (this.selectedDigit < 0) {
                this.selectedDigit = 0;
            }
            this.invalidate();
        } else {
            int incr = 0;
            if (event.getKey() == MapPlayerInput.Key.UP) {
                incr = 1;
                this.isKeyUp = true;
                this.isKeyDown = false;
            } else if (event.getKey() == MapPlayerInput.Key.DOWN) {
                incr = -1;
                this.isKeyUp = false;
                this.isKeyDown = true;
            }
            incr = (int)((double)incr * CustomModelDataSelector.getExp(event.getRepeat() / 50));
            this.value += (incr *= (int)Math.pow(10.0, this.selectedDigit));
            int max_value = (int)Math.pow(10.0, this.numDigits) - 1;
            if (this.value < 0) {
                this.value = 0;
            } else if (this.value > max_value) {
                this.value = max_value;
            }
            this.onValueChanged();
            this.invalidate();
        }
    }

    public void onFocus() {
        super.onFocus();
        this.addWidget(this.tooltip);
        this.display.playSound(SoundEffect.CLICK_WOOD);
    }

    public void onBlur() {
        super.onBlur();
        this.removeWidget(this.tooltip);
    }

    @Override
    public String getAcceptedPropertyName() {
        return "Custom Model Data";
    }

    @Override
    public boolean acceptTextValue(String value) {
        return this.acceptTextValue(SetValueTarget.Operation.SET, value);
    }

    @Override
    public boolean acceptTextValue(SetValueTarget.Operation operation, String value) {
        if (operation.perform(this::getValue, this::setValue, value)) {
            this.onValueChanged();
            return true;
        }
        return false;
    }

    public abstract void onValueChanged();
}

