/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.util.RGBColorToIntConversion;

class BaseBGRToInt
implements RGBColorToIntConversion {
    BaseBGRToInt() {
    }

    @Override
    public boolean hasTransparency() {
        return false;
    }

    @Override
    public int singleBytesInputLength() {
        return 3;
    }

    @Override
    public int singleBytesToInt(byte[] input, int inputOffset) {
        return (input[inputOffset] & 0xFF) << 16 | (input[inputOffset + 1] & 0xFF) << 8 | input[inputOffset + 2] & 0xFF;
    }

    @Override
    public int singleIntToInt(int input) {
        return input & 0xFFFFFF;
    }
}

