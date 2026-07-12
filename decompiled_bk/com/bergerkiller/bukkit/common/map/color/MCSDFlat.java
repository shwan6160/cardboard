/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.color;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.color.MapColorSpaceData;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MCSDFlat
extends MapColorSpaceData {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void readFrom(InputStream stream) throws IOException {
        try (GZIPInputStream zip = new GZIPInputStream(stream);){
            for (int i = 0; i < 256; ++i) {
                int r = zip.read();
                int g = zip.read();
                int b = zip.read();
                int a = zip.read();
                this.setColor((byte)i, new Color(r, g, b, a));
            }
            for (int index = 0; index < 0x1000000; ++index) {
                this.set(index, (byte)zip.read());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeTo(OutputStream stream) throws IOException {
        Logging.LOGGER_MAPDISPLAY.info("Compressing flat map color space data...");
        try (GZIPOutputStream zip = new GZIPOutputStream(stream);){
            for (int i = 0; i < 256; ++i) {
                Color color = this.getColor((byte)i);
                zip.write(color.getRed());
                zip.write(color.getGreen());
                zip.write(color.getBlue());
                zip.write(color.getAlpha());
            }
            for (int index = 0; index < 0x1000000; ++index) {
                zip.write(this.get(index) & 0xFF);
            }
            Logging.LOGGER_MAPDISPLAY.info("Finished compressing map color space data");
        }
    }
}

