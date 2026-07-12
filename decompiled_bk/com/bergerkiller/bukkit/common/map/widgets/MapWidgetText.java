/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.widgets;

import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class MapWidgetText
extends MapWidget {
    private MapFont<Character> _font = MapFont.MINECRAFT;
    private MapFont.Alignment _alignment = MapFont.Alignment.LEFT;
    private byte _color = (byte)119;
    private byte _shadowColor = 0;
    private String _text = "";
    private boolean _autoSize = true;

    public String getText() {
        return this._text;
    }

    public MapWidgetText setText(String text) {
        if (!LogicUtil.bothNullOrEqual(this._text, text)) {
            this._text = text;
            this.calcAutoSize();
            this.invalidate();
        }
        return this;
    }

    public byte getColor() {
        return this._color;
    }

    public MapWidgetText setColor(byte color) {
        if (this._color != color) {
            this._color = color;
            this.invalidate();
        }
        return this;
    }

    public MapWidgetText setAutoSize(boolean autoSize) {
        if (this._autoSize != autoSize) {
            this._autoSize = autoSize;
            this.calcAutoSize();
        }
        return this;
    }

    public MapFont<Character> getFont() {
        return this._font;
    }

    public MapWidgetText setFont(MapFont<Character> font) {
        if (this._font != font) {
            this._font = font;
            this.calcAutoSize();
            this.invalidate();
        }
        return this;
    }

    public MapFont.Alignment getAlignment() {
        return this._alignment;
    }

    public MapWidgetText setAlignment(MapFont.Alignment alignment) {
        if (this._alignment != alignment) {
            this._alignment = alignment;
            this.invalidate();
        }
        return this;
    }

    public byte getShadowColor() {
        return this._shadowColor;
    }

    public MapWidgetText setShadowColor(byte color) {
        if (this._shadowColor != color) {
            this._shadowColor = color;
            this.invalidate();
        }
        return this;
    }

    @Override
    public void onAttached() {
        if (this._autoSize) {
            this.calcAutoSize();
        }
    }

    @Override
    public void onDraw() {
        if (this._text != null && !this._text.isEmpty()) {
            MapFont.Alignment oldAlignment = this.view.getAlignment();
            this.view.setAlignment(this._alignment);
            int x = 0;
            if (this._alignment == MapFont.Alignment.RIGHT) {
                x = this.getWidth() - 1;
            } else if (this._alignment == MapFont.Alignment.MIDDLE) {
                x = this.getWidth() / 2;
            }
            if (this._shadowColor != 0) {
                this.view.draw(this._font, x + 1, 1, this._shadowColor, this._text);
            }
            this.view.draw(this._font, x, 0, this._color, this._text);
            this.view.setAlignment(oldAlignment);
        }
    }

    private void calcAutoSize() {
        if (this._autoSize && this.view != null) {
            if (this._text == null || this._text.isEmpty()) {
                this.setSize(0, 0);
            } else {
                this.setSize(this.view.calcFontSize(this._font, this._text));
            }
        }
    }
}

