/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.ChunkLoadEvent
 *  org.bukkit.event.world.ChunkUnloadEvent
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.ChunkHandleTracker;
import com.bergerkiller.bukkit.common.internal.logic.ChunkHandleTracker_Default;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.lang.invoke.LambdaMetafactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

class ChunkHandleTracker_Spigot_1_21
implements ChunkHandleTracker {
    private final ConcurrentHashMap<Chunk, CachedChunkHandle> cache = new ConcurrentHashMap();
    private final FastTrackedUpdateSet<CachedChunkHandle> pendingRemoval = new FastTrackedUpdateSet();
    private boolean cacheEnabled = true;
    private boolean bugDetectOccurred = false;
    private final Listener listener = new Listener(){

        @EventHandler(priority=EventPriority.LOWEST)
        public void onChunkLoad(ChunkLoadEvent event) {
            if (!ChunkHandleTracker_Spigot_1_21.this.cacheEnabled) {
                return;
            }
            CachedChunkHandle previous = ChunkHandleTracker_Spigot_1_21.this.cache.put(event.getChunk(), new CachedChunkHandle(event.getChunk(), ChunkHandleTracker_Spigot_1_21.this.pendingRemoval));
            if (previous != null) {
                previous.tracker.set(false);
            }
        }

        @EventHandler(priority=EventPriority.MONITOR)
        public void onChunkUnload(ChunkUnloadEvent event) {
            CachedChunkHandle cached;
            if (!ChunkHandleTracker_Spigot_1_21.this.cacheEnabled) {
                return;
            }
            if (!ChunkHandleTracker_Spigot_1_21.this.bugDetectOccurred) {
                ChunkHandleTracker_Spigot_1_21.this.bugDetectOccurred = true;
                try {
                    ChunkHandleTracker_Default.getHandle(event.getChunk());
                    ChunkHandleTracker_Spigot_1_21.this.cacheEnabled = false;
                    ChunkHandleTracker_Spigot_1_21.this.stopTracking();
                    return;
                }
                catch (Throwable t) {
                    ChunkHandleTracker_Spigot_1_21.this.cacheEnabled = true;
                }
            }
            if ((cached = (CachedChunkHandle)ChunkHandleTracker_Spigot_1_21.this.cache.get(event.getChunk())) != null) {
                cached.tracker.set(true);
            }
        }
    };
    private Task cleanupTask;

    ChunkHandleTracker_Spigot_1_21() {
    }

    @Override
    public void enable() throws Throwable {
    }

    @Override
    public void disable() throws Throwable {
    }

    @Override
    public void startTracking(CommonPlugin plugin) {
        this.cacheEnabled = true;
        this.bugDetectOccurred = false;
        plugin.register(this.listener);
        this.cleanupTask = new Task(plugin){

            @Override
            public void run() {
                ChunkHandleTracker_Spigot_1_21.this.pendingRemoval.forEachAndClear(c -> ChunkHandleTracker_Spigot_1_21.this.cache.remove(c.chunk, c));
            }
        }.start(1L, 1L);
        try {
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    this.getChunkHandle(chunk);
                }
            }
        }
        catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Failed to cache chunk handles", t);
        }
    }

    @Override
    public void stopTracking() {
        this.cacheEnabled = false;
        CommonUtil.unregisterListener(this.listener);
        Task.stop(this.cleanupTask);
        this.cleanupTask = null;
        this.cache.clear();
        this.pendingRemoval.clear();
    }

    @Override
    public Object getChunkHandle(Chunk chunk) {
        if (this.cacheEnabled) {
            return this.cache.computeIfAbsent((Chunk)chunk, (Function<Chunk, CachedChunkHandle>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$getChunkHandle$0(org.bukkit.Chunk ), (Lorg/bukkit/Chunk;)Lcom/bergerkiller/bukkit/common/internal/logic/ChunkHandleTracker_Spigot_1_21$CachedChunkHandle;)((ChunkHandleTracker_Spigot_1_21)this)).handle;
        }
        try {
            return ChunkHandleTracker_Default.getHandle(chunk);
        }
        catch (Throwable t) {
            this.bugDetectOccurred = true;
            this.cacheEnabled = true;
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    private /* synthetic */ CachedChunkHandle lambda$getChunkHandle$0(Chunk c) {
        return new CachedChunkHandle(c, this.pendingRemoval);
    }

    private static class CachedChunkHandle {
        public final Chunk chunk;
        public final Object handle;
        public final FastTrackedUpdateSet.Tracker<CachedChunkHandle> tracker;

        public CachedChunkHandle(Chunk chunk, FastTrackedUpdateSet<CachedChunkHandle> set) {
            this.chunk = chunk;
            this.handle = ChunkHandleTracker_Default.getHandle(chunk);
            this.tracker = set.track(this);
        }
    }
}

