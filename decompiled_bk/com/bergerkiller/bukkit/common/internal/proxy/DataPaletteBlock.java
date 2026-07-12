/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.bukkit.common.wrappers.BlockData;

public class DataPaletteBlock {
    private final char[] blockIds;

    public DataPaletteBlock(char[] blockIds) {
        this.blockIds = blockIds;
    }

    public BlockData getBlockData(int x, int y, int z) {
        return BlockData.fromCombinedId_1_8_8(this.blockIds[y << 8 | z << 4 | x]);
    }

    public void setBlockData(int x, int y, int z, BlockData data) {
        this.blockIds[y << 8 | z << 4 | x] = (char)data.getCombinedId_1_8_8();
    }
}

