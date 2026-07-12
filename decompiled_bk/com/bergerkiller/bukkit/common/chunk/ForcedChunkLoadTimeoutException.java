/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.chunk;

import org.bukkit.World;

public class ForcedChunkLoadTimeoutException
extends RuntimeException {
    private static final long serialVersionUID = -1967028311293256931L;

    public ForcedChunkLoadTimeoutException(World world, int chunkX, int chunkZ) {
        super("Loading of Chunk [world=" + world.getName() + ", x=" + chunkX + ", z=" + chunkZ + "] timed out!");
    }
}

