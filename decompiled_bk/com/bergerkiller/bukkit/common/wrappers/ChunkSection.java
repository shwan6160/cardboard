/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkSectionHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.PalettedContainerHandle;

public class ChunkSection
extends BasicWrapper<LevelChunkSectionHandle> {
    private final PalettedContainerHandle blockIds;
    private final int yPos;

    public ChunkSection(LevelChunkSectionHandle nmsChunkSectionHandle, int yPos) {
        this.setHandle(nmsChunkSectionHandle);
        this.blockIds = ((LevelChunkSectionHandle)this.handle).getBlockPalette();
        this.yPos = yPos;
    }

    public int getY() {
        return this.yPos >> 4;
    }

    public int getYPosition() {
        return this.yPos;
    }

    public BlockData getBlockData(int x, int y, int z) {
        return this.blockIds.getBlockData(x, y, z);
    }

    public void setBlockData(int x, int y, int z, BlockData data) {
        this.blockIds.setBlockData(x, y, z, data);
    }
}

