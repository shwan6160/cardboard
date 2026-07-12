/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.chunk;

import com.bergerkiller.bukkit.common.chunk.ForcedChunkManager;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Chunk;
import org.bukkit.World;

public class ForcedChunk
implements AutoCloseable,
Cloneable {
    final AtomicReference<ForcedChunkManager.ForcedChunkEntry> entry;

    protected ForcedChunk(ForcedChunkManager.ForcedChunkEntry entry) {
        this.entry = new AtomicReference<ForcedChunkManager.ForcedChunkEntry>(entry);
    }

    public boolean isNone() {
        return this.entry.get() == null;
    }

    public static ForcedChunk none() {
        if (CommonPlugin.hasInstance()) {
            return CommonPlugin.getInstance().getForcedChunkManager().newNone();
        }
        return new ForcedChunk(null);
    }

    public static ForcedChunk load(World world, int chunkX, int chunkZ) {
        return CommonPlugin.getInstance().getForcedChunkManager().newForcedChunk(world, chunkX, chunkZ);
    }

    public static ForcedChunk load(Chunk chunk) {
        return ForcedChunk.load(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public static ForcedChunk load(World world, int chunkX, int chunkZ, int radius) {
        return CommonPlugin.getInstance().getForcedChunkManager().newForcedChunk(world, chunkX, chunkZ, radius);
    }

    public static ForcedChunk load(Chunk chunk, int radius) {
        return ForcedChunk.load(chunk.getWorld(), chunk.getX(), chunk.getZ(), radius);
    }

    public void move(ForcedChunk other) {
        ForcedChunkManager.ForcedChunkEntry old_entry = this.entry.getAndSet(other.entry.getAndSet(null));
        if (old_entry != null) {
            old_entry.remove();
        }
    }

    public World getWorld() {
        return this.access().getWorld();
    }

    public int getRadius() {
        return this.access().getRadius();
    }

    public int getX() {
        return this.access().getX();
    }

    public int getZ() {
        return this.access().getZ();
    }

    public Chunk getChunk() {
        return this.access().getChunk();
    }

    public String toString() {
        ForcedChunkManager.ForcedChunkEntry entry = this.entry.get();
        if (entry == null) {
            return "ForcedChunk{None}";
        }
        return "ForcedChunk" + entry.toString();
    }

    public CompletableFuture<Chunk> getChunkAsync() {
        return this.access().getChunkAsync();
    }

    private ForcedChunkManager.ForcedChunkEntry access() {
        ForcedChunkManager.ForcedChunkEntry entry = this.entry.get();
        if (entry == null) {
            throw new IllegalStateException("ForcedChunk was closed");
        }
        return entry;
    }

    public ForcedChunk clone() {
        ForcedChunkManager.ForcedChunkEntry entry = this.entry.get();
        if (entry != null) {
            entry.add();
        }
        return new ForcedChunk(entry);
    }

    @Override
    public void close() {
        ForcedChunkManager.ForcedChunkEntry entry = this.entry.getAndSet(null);
        if (entry != null) {
            entry.remove();
        }
    }
}

