/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.widgets;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import java.awt.Dimension;

public class MapWidgetButton
extends MapWidget {
    private String _text = "";
    private MapTexture _icon = null;
    private boolean showBorder = true;

    public MapWidgetButton() {
        this.setFocusable(true);
    }

    public MapWidgetButton setText(String text) {
        if (!this._text.equals(text)) {
            this._text = text;
            if (this._text == null) {
                this._text = "";
            }
            this.invalidate();
        }
        return this;
    }

    public String getText() {
        return this._text;
    }

    public MapWidgetButton setIcon(MapTexture icon) {
        if (this._icon != icon) {
            this._icon = icon;
            this.invalidate();
        }
        return this;
    }

    public MapWidgetButton setShowBorder(boolean showBorder) {
        if (this.showBorder != showBorder) {
            this.showBorder = showBorder;
            this.invalidate();
        }
        return this;
    }

    public boolean isShowBorder() {
        return this.showBorder;
    }

    public MapTexture getIcon() {
        return this._icon;
    }

    @Override
    public void onDraw() {
        byte textShadowColor;
        byte textColor;
        if (!this.isEnabled()) {
            textColor = MapColorPalette.getColor(160, 160, 160);
            textShadowColor = 0;
        } else if (this.isFocused()) {
            textColor = MapColorPalette.getColor(255, 255, 160);
            textShadowColor = MapColorPalette.getColor(63, 63, 40);
        } else {
            textColor = MapColorPalette.getColor(224, 224, 224);
            textShadowColor = MapColorPalette.getColor(56, 56, 56);
        }
        if (this.showBorder) {
            MapWidgetButton.fillBackground(this.view.getView(1, 1, this.getWidth() - 2, this.getHeight() - 2), this.isEnabled(), this.isFocused());
            this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
        } else {
            MapWidgetButton.fillBackground(this.view, this.isEnabled(), this.isFocused());
        }
        if (!this._text.isEmpty()) {
            Dimension textSize = this.view.calcFontSize(MapFont.MINECRAFT, this._text);
            int textX = (this.getWidth() - textSize.width) / 2;
            int textY = (this.getHeight() - textSize.height) / 2;
            this.view.setAlignment(MapFont.Alignment.LEFT);
            if (textShadowColor != 0) {
                this.view.draw(MapFont.MINECRAFT, textX + 1, textY + 1, textShadowColor, this._text);
            }
            this.view.draw(MapFont.MINECRAFT, textX, textY, textColor, this._text);
        }
        if (this._icon != null) {
            int iconY = (this.getHeight() - this._icon.getHeight()) / 2;
            int iconX = this.getWidth() - iconY - this._icon.getWidth();
            this.view.draw(this._icon, iconX, iconY);
        }
    }

    public static void fillBackground(MapCanvas canvas, boolean enabled, boolean focused) {
        byte topEdgeColor;
        byte fillColor;
        byte btmEdgeColor;
        if (!enabled) {
            fillColor = btmEdgeColor = MapColorPalette.getColor(44, 44, 44);
            topEdgeColor = btmEdgeColor;
        } else if (focused) {
            topEdgeColor = MapColorPalette.getColor(190, 200, 255);
            fillColor = MapColorPalette.getColor(126, 136, 191);
            btmEdgeColor = MapColorPalette.getColor(92, 102, 157);
        } else {
            topEdgeColor = MapColorPalette.getColor(170, 170, 170);
            fillColor = MapColorPalette.getColor(111, 111, 111);
            btmEdgeColor = MapColorPalette.getColor(86, 86, 86);
        }
        int x1 = 0;
        int y1 = 0;
        int x2 = canvas.getWidth() - 1;
        int y2 = canvas.getHeight() - 1;
        canvas.drawLine(x1, y1, x2 - 1, y1, topEdgeColor);
        canvas.drawLine(x1, y1 + 1, x1, y2 - 2, topEdgeColor);
        canvas.drawLine(x1, y2 - 1, x1, y2, fillColor);
        canvas.drawPixel(x2, y1, fillColor);
        canvas.drawLine(x1 + 1, y2 - 1, x2, y2 - 1, btmEdgeColor);
        canvas.drawLine(x1 + 1, y2, x2, y2, btmEdgeColor);
        canvas.drawLine(x2, 1, x2, y2 - 2, btmEdgeColor);
        canvas.fillRectangle(x1 + 1, y1 + 1, x2 - x1 - 1, y2 - x1 - 2, fillColor);
    }
}

