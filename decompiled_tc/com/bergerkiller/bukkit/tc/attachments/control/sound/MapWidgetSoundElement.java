/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 */
package com.bergerkiller.bukkit.tc.attachments.control.sound;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;

public class MapWidgetSoundElement
extends MapWidget {
    public MapWidgetSoundElement() {
        this.setFocusable(true);
    }

    public void onDraw() {
        byte innerColorBottom;
        byte innerColorTop;
        byte edgeColor;
        if (this.isActivated()) {
            edgeColor = 119;
            innerColorTop = MapColorPalette.getColor((int)36, (int)89, (int)152);
            innerColorBottom = MapColorPalette.getColor((int)44, (int)109, (int)186);
        } else if (this.isFocused()) {
            edgeColor = MapColorPalette.getColor((int)25, (int)25, (int)25);
            innerColorTop = MapColorPalette.getColor((int)78, (int)185, (int)180);
            innerColorBottom = MapColorPalette.getColor((int)100, (int)151, (int)213);
        } else {
            edgeColor = 119;
            innerColorTop = MapColorPalette.getColor((int)44, (int)109, (int)186);
            innerColorBottom = MapColorPalette.getColor((int)36, (int)89, (int)152);
        }
        this.drawBackground(edgeColor, innerColorTop, innerColorBottom);
    }

    protected void drawBackground(byte edgeColor, byte innerColorTop, byte innerColorBottom) {
        this.view.fillRectangle(2, 2, this.getWidth() - 3, this.getHeight() - 3, innerColorBottom);
        this.view.drawPixel(2, 2, innerColorTop);
        this.view.drawLine(2, 1, this.getWidth() - 3, 1, innerColorTop);
        this.view.drawLine(1, 2, 1, this.getHeight() - 3, innerColorTop);
        this.view.drawLine(1, 0, this.getWidth() - 2, 0, edgeColor);
        this.view.drawLine(1, this.getHeight() - 1, this.getWidth() - 2, this.getHeight() - 1, edgeColor);
        this.view.drawLine(0, 1, 0, this.getHeight() - 2, edgeColor);
        this.view.drawLine(this.getWidth() - 1, 1, this.getWidth() - 1, this.getHeight() - 2, edgeColor);
        this.view.drawPixel(1, 1, edgeColor);
        this.view.drawPixel(1, this.getHeight() - 2, edgeColor);
        this.view.drawPixel(this.getWidth() - 2, this.getHeight() - 2, edgeColor);
        this.view.drawPixel(this.getWidth() - 2, 1, edgeColor);
    }
}

