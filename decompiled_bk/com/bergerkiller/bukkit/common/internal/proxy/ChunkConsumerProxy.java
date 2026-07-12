/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.generated.net.minecraft.server.level.ServerChunkCacheHandle;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ChunkConsumerProxy
implements Runnable {
    private static final SafeMethod<Object> cps_getChunkAt = new SafeMethod(ServerChunkCacheHandle.T.getType(), "getChunkAt", Integer.TYPE, Integer.TYPE);
    private final Consumer<Object> consumer;
    private final Object cps;
    private final int chunkX;
    private final int chunkZ;

    public ChunkConsumerProxy(Consumer<Object> consumer, Object cps, int chunkX, int chunkZ) {
        this.consumer = consumer;
        this.cps = cps;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    @Override
    public void run() {
        Object chunk = null;
        try {
            chunk = cps_getChunkAt.invoke(this.cps, this.chunkX, this.chunkZ);
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Error in ChunkConsumerProxy", t);
        }
        this.consumer.accept(chunk);
    }
}

