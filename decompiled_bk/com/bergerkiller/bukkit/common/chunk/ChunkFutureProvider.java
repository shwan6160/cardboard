/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.chunk;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.chunk.ChunkFutureProviderImpl;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public interface ChunkFutureProvider {
    public static ChunkFutureProvider of(Plugin plugin) {
        return ChunkFutureProviderImpl.MainThreadHandler.handlers.computeIfAbsent(plugin, p -> {
            ChunkFutureProviderImpl.MainThreadHandler provider = new ChunkFutureProviderImpl.MainThreadHandler((Plugin)p);
            Bukkit.getPluginManager().registerEvents((Listener)provider, p);
            return provider;
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ChunkFutureProvider ofThreadSafe(Plugin plugin) {
        Class<ChunkFutureProviderImpl.ThreadSafeHandler> clazz = ChunkFutureProviderImpl.ThreadSafeHandler.class;
        synchronized (ChunkFutureProviderImpl.ThreadSafeHandler.class) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return ChunkFutureProviderImpl.ThreadSafeHandler.handlers.computeIfAbsent(plugin, p -> {
                ChunkFutureProviderImpl.ThreadSafeHandler provider = new ChunkFutureProviderImpl.ThreadSafeHandler((Plugin)p);
                Bukkit.getPluginManager().registerEvents((Listener)provider, p);
                return provider;
            });
        }
    }

    public static ChunkFutureProvider ofSyncLoad() {
        return ChunkFutureProviderImpl.SyncLoadHandler.INSTANCE;
    }

    public CompletableFuture<Chunk> whenLoaded(World var1, int var2, int var3);

    default public CompletableFuture<Chunk> whenLoaded(Chunk chunk) {
        return this.whenLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public CompletableFuture<Void> whenUnloaded(World var1, int var2, int var3);

    default public CompletableFuture<Void> whenUnloaded(Chunk chunk) {
        return this.whenUnloaded(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public ChunkStateTracker trackLoaded(World var1, int var2, int var3, ChunkStateListener var4);

    default public ChunkStateTracker trackLoaded(Chunk chunk, ChunkStateListener listener) {
        return this.trackLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ(), listener);
    }

    public ChunkStateTracker trackNeighboursLoaded(Chunk var1, ChunkNeighbourList var2, ChunkStateListener var3);

    public CompletableFuture<Chunk> whenEntitiesLoaded(World var1, int var2, int var3);

    default public CompletableFuture<Chunk> whenEntitiesLoaded(Chunk chunk) {
        return this.whenEntitiesLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public CompletableFuture<Void> whenEntitiesUnloaded(World var1, int var2, int var3);

    default public CompletableFuture<Void> whenEntitiesUnloaded(Chunk chunk) {
        return this.whenEntitiesUnloaded(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    default public CompletableFuture<Chunk> whenNeighbourLoaded(Chunk mainChunk, int neighbourChunkX, int neighbourChunkZ) {
        if (neighbourChunkX == mainChunk.getX() && neighbourChunkZ == mainChunk.getZ()) {
            return CompletableFuture.completedFuture(mainChunk);
        }
        CompletableFuture<Chunk> neighbourFuture = this.whenLoaded(mainChunk.getWorld(), neighbourChunkX, neighbourChunkZ);
        if (neighbourFuture.isDone()) {
            return neighbourFuture;
        }
        CompletableFuture<Void> mainFuture = this.whenUnloaded(mainChunk);
        if (mainFuture.isDone()) {
            neighbourFuture.cancel(false);
        } else {
            mainFuture.thenAccept(u -> neighbourFuture.cancel(false));
            neighbourFuture.exceptionally(t -> {
                if (t instanceof CompletionException) {
                    mainFuture.cancel(false);
                }
                return null;
            });
        }
        return neighbourFuture;
    }

    default public CompletableFuture<BlockData> readNeighbourBlockData(Chunk mainChunk, Block block) {
        int neighChunkX = MathUtil.toChunk(block.getX());
        int neighChunkZ = MathUtil.toChunk(block.getZ());
        if (mainChunk.getX() == neighChunkX && mainChunk.getZ() == neighChunkZ) {
            return CompletableFuture.completedFuture(WorldUtil.getBlockData(block));
        }
        return this.whenNeighbourLoaded(mainChunk, neighChunkX, neighChunkZ).thenApply(c -> WorldUtil.getBlockData(block));
    }

    public CompletableFuture<Chunk> whenAllNeighboursLoaded(Chunk var1, ChunkNeighbourList var2);

    public static interface ChunkStateListener {
        public void onRegistered(ChunkStateTracker var1);

        public void onCancelled(ChunkStateTracker var1);

        public void onLoaded(ChunkStateTracker var1);

        public void onUnloaded(ChunkStateTracker var1);
    }

    public static interface ChunkStateTracker {
        public World getWorld();

        public int getChunkX();

        public int getChunkZ();

        public Chunk getChunk();

        public boolean isLoaded();

        public void cancel();
    }

    public static class FutureCancelledException
    extends CompletionException {
        public static final FutureCancelledException INSTANCE = new FutureCancelledException();
        private static final long serialVersionUID = 7273969366281170690L;

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }

    public static interface ChunkNeighbourList {
        public void add(World var1, int var2, int var3);

        default public void add(World world, IntVector2 coordinates) {
            this.add(world, coordinates.x, coordinates.z);
        }

        public static ChunkNeighbourList create() {
            return new ChunkFutureProviderImpl.ChunkTrackerListImpl(16);
        }

        public static ChunkNeighbourList neighboursOf(Chunk chunk, int radius) {
            World world = chunk.getWorld();
            int chunkX = chunk.getX();
            int chunkZ = chunk.getZ();
            if (radius == 1) {
                ChunkFutureProviderImpl.ChunkTrackerListImpl list = new ChunkFutureProviderImpl.ChunkTrackerListImpl(8);
                list.add(world, chunkX - 1, chunkZ - 1);
                list.add(world, chunkX - 1, chunkZ);
                list.add(world, chunkX - 1, chunkZ + 1);
                list.add(world, chunkX, chunkZ + 1);
                list.add(world, chunkX + 1, chunkZ + 1);
                list.add(world, chunkX + 1, chunkZ);
                list.add(world, chunkX + 1, chunkZ - 1);
                list.add(world, chunkX, chunkZ - 1);
                return list;
            }
            int edge = 1 + 2 * radius;
            ChunkFutureProviderImpl.ChunkTrackerListImpl list = new ChunkFutureProviderImpl.ChunkTrackerListImpl(edge * edge - 1);
            int cx0 = chunkX - radius;
            int cz0 = chunkZ - radius;
            int cx1 = chunkX + radius;
            int cz1 = chunkZ + radius;
            for (int cx = cx0; cx <= cx1; ++cx) {
                for (int cz = cz0; cz <= cz1; ++cz) {
                    if (cx == chunkX || cz == chunkZ) continue;
                    list.add(chunk.getWorld(), cx, cz);
                }
            }
            return list;
        }
    }
}

