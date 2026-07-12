/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.ChunkHandleTracker;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftChunkHandle;
import org.bukkit.Chunk;

class ChunkHandleTracker_Default
implements ChunkHandleTracker {
    ChunkHandleTracker_Default() {
    }

    @Override
    public void enable() throws Throwable {
    }

    @Override
    public void disable() throws Throwable {
    }

    @Override
    public void startTracking(CommonPlugin plugin) {
    }

    @Override
    public void stopTracking() {
    }

    @Override
    public Object getChunkHandle(Chunk chunk) {
        return ChunkHandleTracker_Default.getHandle(chunk);
    }

    protected static Object getHandle(Chunk chunk) {
        try {
            return CraftChunkHandle.T.getHandle.invoker.invoke(chunk);
        }
        catch (RuntimeException ex) {
            if (CraftChunkHandle.T.isAssignableFrom(chunk)) {
                throw ex;
            }
            return null;
        }
    }
}

