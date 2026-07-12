/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;

public class MapTinyFont
extends MapFont<Character> {
    private MapTexture _fontMap = null;

    @Override
    protected MapTexture loadSprite(Character key) {
        if (key.charValue() == ' ' || key.charValue() == '\t') {
            return MapTexture.createEmpty(2, 6);
        }
        char code = key.charValue();
        if (code >= '\u0000' && code < '\u0100') {
            int leftColumn;
            if (this._fontMap == null) {
                this._fontMap = CommonPlugin.getInstance().loadTexture("com/bergerkiller/bukkit/common/internal/resources/textures/tinyfont.png");
            }
            MapCanvas sprite = this._fontMap.getView((code & 0xF) * 4, (code >> 4) * 6, 4, 6);
            for (leftColumn = 0; leftColumn < 2; ++leftColumn) {
                boolean columnHasPixel = false;
                for (int y = 0; y < 5; ++y) {
                    if (sprite.readPixel(leftColumn, y) == 0) continue;
                    columnHasPixel = true;
                    break;
                }
                if (columnHasPixel) break;
            }
            if (leftColumn > 0) {
                sprite = sprite.getView(leftColumn, 0, sprite.getWidth() - leftColumn, sprite.getHeight());
            }
            return sprite.clone();
        }
        return this.getSprite(null);
    }
}

