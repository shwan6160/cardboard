/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.LightLayerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

@Deprecated
public class NMSChunk {
    public static final ClassTemplate<?> T = ClassTemplate.create(LevelChunkHandle.T.getType());
    private static final MethodAccessor<Void> addEntities = LevelChunkHandle.T.addEntities.toMethodAccessor();
    private static final MethodAccessor<Boolean> needsSaving = LevelChunkHandle.T.checkCanSave.toMethodAccessor();
    public static final int XZ_MASK = 15;
    public static final int Y_MASK = 255;

    public static void addEntities(Object chunkHandle) {
        addEntities.invoke(chunkHandle, new Object[0]);
    }

    public static boolean needsSaving(Object chunkHandle) {
        return needsSaving.invoke(chunkHandle, false);
    }

    public static int getTopSectionY(Object chunkHandle) {
        return LevelChunkHandle.T.getTopSliceY.invoke(chunkHandle);
    }

    public static int getBlockLight(Object chunkHandle, int x, int y, int z) {
        return NMSChunk.getBrightness(chunkHandle, x, y, z, LightLayerHandle.BLOCK);
    }

    public static int getSkyLight(Object chunkHandle, int x, int y, int z) {
        return NMSChunk.getBrightness(chunkHandle, x, y, z, LightLayerHandle.SKY);
    }

    private static int getBrightness(Object chunkHandle, int x, int y, int z, LightLayerHandle mode) {
        if (y < 0) {
            return 0;
        }
        if (y >= LevelChunkHandle.T.getWorld.invoke(chunkHandle).getWorld().getMaxHeight()) {
            return mode == LightLayerHandle.SKY ? 15 : 0;
        }
        return LevelChunkHandle.T.getBrightness.invoke(chunkHandle, mode.getRaw(), new IntVector3(x & 0xF, y, z & 0xF));
    }

    public static boolean setBlockData(Object chunkHandle, int x, int y, int z, BlockData data) {
        return LevelChunkHandle.T.setBlockData.invoke(chunkHandle, new IntVector3(x & 0xF, y, z & 0xF), data) != null;
    }

    public static BlockData getBlockData(Object chunkHandle, int x, int y, int z) {
        return LevelChunkHandle.T.getBlockData.invoke(chunkHandle, new IntVector3(x & 0xF, y, z & 0xF));
    }
}

