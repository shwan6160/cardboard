/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.MapTexture;

public class SinglePixelTexture {
    private static final MapTexture[] _textures = new MapTexture[256];

    public static MapTexture get(byte color) {
        return _textures[color & 0xFF];
    }

    static {
        for (int i = 0; i < 256; ++i) {
            SinglePixelTexture._textures[i] = MapTexture.createEmpty(1, 1);
            _textures[i].writePixel(0, 0, (byte)i);
        }
    }
}

