/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.generator.BlockPopulator
 */
package com.bergerkiller.bukkit.common.internal;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public interface TimingsListener {
    public void onNextTicked(Runnable var1, long var2);

    public void onChunkLoad(Chunk var1, long var2);

    public void onChunkGenerate(Chunk var1, long var2);

    public void onChunkUnloading(World var1, long var2);

    public void onChunkPopulate(Chunk var1, BlockPopulator var2, long var3);
}

