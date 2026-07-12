/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.test;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapFont;
import java.awt.Color;

public class MapTestDisplay
extends MapDisplay {
    @Override
    public void onAttached() {
        this.getLayer().setAlignment(MapFont.Alignment.MIDDLE);
        int nxTiles = this.getWidth() / 128;
        int nyTiles = this.getHeight() / 128;
        for (int tx = 0; tx < nxTiles; ++tx) {
            for (int ty = 0; ty < nyTiles; ++ty) {
                int px = tx * 128;
                int py = ty * 128;
                float hue = (float)(ty * nxTiles + tx) / (float)(nxTiles * nyTiles);
                float sat = 1.0f;
                float bri = 1.0f;
                byte color = MapColorPalette.getColor(Color.getHSBColor(hue, sat, bri));
                byte textColor = MapColorPalette.getSpecular(color, 0.5f);
                String label = "(" + tx + ", " + ty + ")";
                this.getLayer().fillRectangle(px, py, 128, 128, color);
                this.getLayer().drawRectangle(px, py, 128, 128, textColor);
                this.getLayer().draw(MapFont.MINECRAFT, px + 64, py + 64, textColor, label);
            }
        }
    }
}

