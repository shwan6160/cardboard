/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.widgets;

import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import java.awt.Point;
import java.util.Arrays;

public class MapWidgetWindow
extends MapWidget {
    private byte _outerBorderColor = (byte)119;
    private byte _innerBorderColor1 = (byte)34;
    private byte _innerBorderColor2 = MapColorPalette.getColor(86, 86, 86);
    private byte _backgroundColor = MapColorPalette.getColor(198, 198, 198);
    private MapWidgetText _titleWidget = new MapWidgetText();

    public MapWidgetWindow() {
        this._titleWidget.setPosition(6, 4);
        this._titleWidget.setColor(MapColorPalette.getColor(64, 64, 64));
        this.addWidget(this._titleWidget);
    }

    public MapWidgetWindow setOuterBorderColor(byte color) {
        if (this._outerBorderColor != color) {
            this._outerBorderColor = color;
            this.invalidate();
        }
        return this;
    }

    public MapWidgetWindow setBackgroundColor(byte color) {
        if (this._backgroundColor != color) {
            this._backgroundColor = color;
            this.invalidate();
        }
        return this;
    }

    public MapWidgetWindow setInnerBorderColors(byte color1, byte color2) {
        if (this._innerBorderColor1 != color1 || this._innerBorderColor2 != color2) {
            this._innerBorderColor1 = color1;
            this._innerBorderColor2 = color2;
            this.invalidate();
        }
        return this;
    }

    public MapWidgetText getTitle() {
        return this._titleWidget;
    }

    @Override
    public void onDraw() {
        this.view.fillRectangle(2, 2, this.getWidth() - 4, this.getHeight() - 4, this._backgroundColor);
        this.view.drawContour(Arrays.asList(new Point(0, 3), new Point(3, 0), new Point(this.getWidth() - 4, 0), new Point(this.getWidth() - 1, 3), new Point(this.getWidth() - 1, this.getHeight() - 4), new Point(this.getWidth() - 4, this.getHeight() - 1), new Point(3, this.getHeight() - 1), new Point(0, this.getHeight() - 4)), this._outerBorderColor);
        this.view.drawLine(3, 1, this.getWidth() - 4, 1, this._innerBorderColor1);
        this.view.drawLine(1, 3, 1, this.getHeight() - 4, this._innerBorderColor1);
        this.view.drawPixel(2, 2, this._innerBorderColor1);
        this.view.drawLine(3, this.getHeight() - 2, this.getWidth() - 4, this.getHeight() - 2, this._innerBorderColor2);
        this.view.drawLine(this.getWidth() - 2, 3, this.getWidth() - 2, this.getHeight() - 4, this._innerBorderColor2);
        this.view.drawPixel(this.getWidth() - 3, this.getHeight() - 3, this._innerBorderColor2);
        byte innerColorAvg = MapBlendMode.AVERAGE.process(this._innerBorderColor1, this._innerBorderColor2);
        this.view.drawPixel(2, this.getHeight() - 3, innerColorAvg);
        this.view.drawPixel(this.getWidth() - 3, 2, innerColorAvg);
    }
}

