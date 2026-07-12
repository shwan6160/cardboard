/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.chunk;

import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.chunk.ForcedChunkCleaner;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.World;

public abstract class ForcedChunkManager {
    private ForcedChunkCreator creator = new ForcedChunkCreatorUntracked(ForcedChunkCleaner.create());

    public final ForcedChunk newNone() {
        return this.creator.create(null);
    }

    public final ForcedChunk newForcedChunk(World world, int chunkX, int chunkZ) {
        return this.creator.create(this.add(world, chunkX, chunkZ));
    }

    public final ForcedChunk newForcedChunk(World world, int chunkX, int chunkZ, int radius) {
        return this.creator.create(this.add(world, chunkX, chunkZ, radius));
    }

    public final ForcedChunkEntry add(World world, int chunkX, int chunkZ) {
        return this.add(world, chunkX, chunkZ, 2);
    }

    public abstract ForcedChunkEntry add(World var1, int var2, int var3, int var4);

    public boolean isTrackingCreationStack() {
        return this.creator.isTrackingCreationStack();
    }

    public void setTrackingCreationStack(boolean tracking) {
        this.creator = this.creator.updateTrackingCreationStack(tracking);
    }

    protected void shutdown() {
        this.creator.shutdown();
    }

    private static class ForcedChunkCreatorUntracked
    implements ForcedChunkCreator {
        private final ForcedChunkCleaner cleaner;

        private ForcedChunkCreatorUntracked(ForcedChunkCleaner cleaner) {
            this.cleaner = cleaner;
        }

        @Override
        public boolean isTrackingCreationStack() {
            return false;
        }

        @Override
        public ForcedChunkCreator updateTrackingCreationStack(boolean tracking) {
            return tracking ? new ForcedChunkCreatorTracked(this.cleaner) : this;
        }

        @Override
        public ForcedChunk create(ForcedChunkEntry entry) {
            return this.cleaner.createDefault(entry);
        }

        @Override
        public void shutdown() {
            this.cleaner.shutdown();
        }
    }

    private static interface ForcedChunkCreator {
        public void shutdown();

        public boolean isTrackingCreationStack();

        public ForcedChunkCreator updateTrackingCreationStack(boolean var1);

        public ForcedChunk create(ForcedChunkEntry var1);
    }

    public static interface ForcedChunkEntry {
        public ForcedChunkManager getManager();

        public void add();

        public void remove();

        public World getWorld();

        public int getRadius();

        public int getX();

        public int getZ();

        public Chunk getChunk();

        public CompletableFuture<Chunk> getChunkAsync();
    }

    private static class ForcedChunkCreatorTracked
    implements ForcedChunkCreator {
        private final ForcedChunkCleaner cleaner;

        private ForcedChunkCreatorTracked(ForcedChunkCleaner cleaner) {
            this.cleaner = cleaner;
        }

        @Override
        public boolean isTrackingCreationStack() {
            return true;
        }

        @Override
        public ForcedChunkCreator updateTrackingCreationStack(boolean tracking) {
            return tracking ? this : new ForcedChunkCreatorUntracked(this.cleaner);
        }

        @Override
        public ForcedChunk create(ForcedChunkEntry entry) {
            return this.cleaner.createAndTrackStack(entry, new Throwable());
        }

        @Override
        public void shutdown() {
            this.cleaner.shutdown();
        }
    }
}

