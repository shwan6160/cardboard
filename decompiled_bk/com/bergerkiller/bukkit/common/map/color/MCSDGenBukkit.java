/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.map.MapPalette
 */
package com.bergerkiller.bukkit.common.map.color;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.color.MapColorSpaceData;
import org.bukkit.map.MapPalette;

public class MCSDGenBukkit
extends MapColorSpaceData {
    public void generate() {
        this.clear();
        for (int i = 0; i < 256; ++i) {
            try {
                this.setColor((byte)i, MapPalette.getColor((byte)((byte)i)));
                continue;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        for (int r = 0; r < 256; ++r) {
            Logging.LOGGER_MAPDISPLAY.info("Generating Bukkit color map " + (r + 1) + "/256");
            for (int g = 0; g < 256; ++g) {
                for (int b = 0; b < 256; ++b) {
                    this.set(r, g, b, MapPalette.matchColor((int)r, (int)g, (int)b));
                }
            }
        }
    }
}

