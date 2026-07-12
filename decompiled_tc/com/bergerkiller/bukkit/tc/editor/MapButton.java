/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapDisplay
 *  com.bergerkiller.bukkit.common.map.MapDisplay$Layer
 */
package com.bergerkiller.bukkit.tc.editor;

import com.bergerkiller.bukkit.common.map.MapDisplay;

public class MapButton {
    private final MapDisplay.Layer layer;
    private final int x;
    private final int y;

    public MapButton(MapDisplay display, int x, int y, int z) {
        this.layer = display.getLayer(z);
        this.x = x;
        this.y = y;
    }
}

