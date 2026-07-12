/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 */
package com.bergerkiller.bukkit.tc.attachments.control.sound;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundElement;

public abstract class MapWidgetSoundButton
extends MapWidgetSoundElement {
    protected boolean pressed = false;
    protected int pressedTicks = 0;

    public abstract void onClick();

    public void onClickHold(int pressedTicks) {
    }

    public void onKey(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.ENTER) {
            if (!this.pressed) {
                this.pressed = true;
                this.pressedTicks = 0;
                this.invalidate();
                this.onClick();
            } else {
                ++this.pressedTicks;
                this.onClickHold(this.pressedTicks);
            }
        } else {
            super.onKey(event);
        }
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (event.getKey() != MapPlayerInput.Key.ENTER) {
            super.onKeyPressed(event);
        }
    }

    public void onKeyReleased(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.ENTER) {
            this.pressedTicks = 0;
            if (this.pressed) {
                this.pressed = false;
                this.invalidate();
            }
        } else {
            super.onKeyReleased(event);
        }
    }

    public void onBlur() {
        this.pressedTicks = 0;
        this.pressed = false;
    }

    @Override
    public void onDraw() {
        if (this.pressed) {
            this.drawBackground((byte)119, MapColorPalette.getColor((int)36, (int)89, (int)152), MapColorPalette.getColor((int)44, (int)109, (int)186));
        } else {
            super.onDraw();
        }
    }
}

