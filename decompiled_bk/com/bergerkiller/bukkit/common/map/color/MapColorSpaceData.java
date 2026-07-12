/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.color;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.util.Arrays;

public class MapColorSpaceData
implements Cloneable {
    private final Color[] colors = new Color[256];
    private final byte[] data = new byte[0x1000000];

    public MapColorSpaceData() {
        Arrays.fill(this.colors, new Color(0, 0, 0, 0));
    }

    public final int getColorCount() {
        int count = 0;
        boolean found_all_transparent = false;
        for (Color color : this.colors) {
            if (color.getAlpha() >= 128) {
                found_all_transparent = true;
                ++count;
                continue;
            }
            if (found_all_transparent) continue;
            ++count;
        }
        return count;
    }

    public final void clearRGBData() {
        Arrays.fill(this.data, (byte)0);
    }

    public final void clear() {
        Arrays.fill(this.colors, new Color(0, 0, 0, 0));
        Arrays.fill(this.data, (byte)0);
    }

    public void readFrom(MapColorSpaceData data) {
        System.arraycopy(data.data, 0, this.data, 0, this.data.length);
        System.arraycopy(data.colors, 0, this.colors, 0, this.colors.length);
    }

    public final void setColor(byte code, Color color) {
        this.colors[code & 0xFF] = color;
    }

    public final Color getColor(int index) {
        return this.colors[index & 0xFF];
    }

    public final Color getColor(byte code) {
        return this.colors[code & 0xFF];
    }

    public final void set(int r, int g, int b, byte code) {
        this.data[MapColorSpaceData.getDataIndex((int)r, (int)g, (int)b)] = code;
    }

    public final byte get(byte r, byte g, byte b) {
        return this.data[MapColorSpaceData.getDataIndex(r, g, b)];
    }

    public final byte get(int r, int g, int b) {
        return this.data[MapColorSpaceData.getDataIndex(r, g, b)];
    }

    public final void set(int index, byte code) {
        this.data[index] = code;
    }

    public final byte get(int index) {
        return this.data[index];
    }

    public final byte[] getRG(int b) {
        byte[] result = new byte[65536];
        this.getRG(b, result);
        return result;
    }

    public final void getRG(int b, byte[] data) {
        System.arraycopy(this.data, b << 16, data, 0, 65536);
    }

    public final void setRG(int b, byte[] data) {
        System.arraycopy(data, 0, this.data, b << 16, 65536);
    }

    public IndexColorModel toIndexColorModel() {
        int num_colors = 0;
        byte[] r = new byte[256];
        byte[] g = new byte[256];
        byte[] b = new byte[256];
        byte[] a = new byte[256];
        for (int i = 0; i < 256; ++i) {
            Color c = this.colors[i];
            if (c.getAlpha() < 128) continue;
            num_colors = i + 1;
            r[i] = (byte)c.getRed();
            g[i] = (byte)c.getGreen();
            b[i] = (byte)c.getBlue();
            a[i] = (byte)c.getAlpha();
        }
        return new IndexColorModel(8, num_colors, r, g, b, a);
    }

    public MapColorSpaceData clone() {
        MapColorSpaceData clone = new MapColorSpaceData();
        System.arraycopy(this.colors, 0, clone.colors, 0, this.colors.length);
        System.arraycopy(this.data, 0, clone.data, 0, this.data.length);
        return clone;
    }

    private static final int getDataIndex(byte r, byte g, byte b) {
        return (r & 0xFF) + ((g & 0xFF) << 8) + ((b & 0xFF) << 16);
    }

    private static final int getDataIndex(int r, int g, int b) {
        return (r & 0xFF) + ((g & 0xFF) << 8) + ((b & 0xFF) << 16);
    }
}

