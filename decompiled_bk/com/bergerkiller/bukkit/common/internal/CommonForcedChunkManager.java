/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.ChunkUnloadEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.RunOnceTask;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.chunk.ForcedChunkLoadTimeoutException;
import com.bergerkiller.bukkit.common.chunk.ForcedChunkManager;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonNextTickExecutor;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.generated.net.minecraft.server.level.ServerChunkCacheHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonForcedChunkManager
extends ForcedChunkManager {
    private HashMap<WorldRadiusKey, ForcedWorld> forcedWorldsByWorldRadius = new HashMap();
    private IdentityHashMap<World, ForcedWorld[]> forcedWorldsByWorld = new IdentityHashMap();
    private ForcedWorld forcedWorldLastGet = new ForcedWorld();
    private final List<Entry> pendingChunkUnloadRequests = new ArrayList<Entry>();
    private final ChunkUnloadEventListener chunkUnloadListener = new ChunkUnloadEventListener();
    private final WorldUnloadEventListener worldUnloadListener = new WorldUnloadEventListener();
    private final CommonPlugin plugin;
    private Task pendingHandler = null;
    private final CommonNextTickExecutor asyncLoadCallbackHandler = new CommonNextTickExecutor();
    private final ChunkUnloadRequestTask chunkUnloadRequestTask;
    private ChunkLoadTimeoutTracker loadTimeoutTracker = null;
    private static final int FAIL_LOAD_AFTER_SECONDS = 300;
    private static final int FAIL_LOAD_AFTER_TICKS = 6000;
    private static final ForcedWorld[] NO_WORLDS = new ForcedWorld[0];

    public CommonForcedChunkManager(CommonPlugin plugin, boolean trackForcedChunkCreationStack) {
        super.setTrackingCreationStack(trackForcedChunkCreationStack);
        this.plugin = plugin;
        this.chunkUnloadRequestTask = new ChunkUnloadRequestTask((Plugin)plugin);
    }

    public void enable() {
        this.asyncLoadCallbackHandler.setExecutorTask(new ChunkLoadCallbackExecutor(this.plugin));
        this.loadTimeoutTracker = new ChunkLoadTimeoutTracker(this.plugin);
        this.loadTimeoutTracker.start(1L, 1L);
        this.pendingHandler = new Task(this.plugin){

            @Override
            public void run() {
                CommonForcedChunkManager.this.forcedWorldsByWorldRadius.values().forEach(ForcedWorld::sync);
            }
        };
        if (CommonCapabilities.CAN_CANCEL_CHUNK_UNLOAD_EVENT) {
            this.plugin.register(this.chunkUnloadListener);
        }
        this.plugin.register(this.worldUnloadListener);
    }

    public void disable(CommonPlugin plugin) {
        this.loadTimeoutTracker.stop();
        this.loadTimeoutTracker = null;
        this.pendingHandler.stop();
        this.pendingHandler = null;
        this.forcedWorldsByWorldRadius.values().forEach(f -> f.unload(false));
        this.forcedWorldsByWorldRadius = new HashMap();
        this.forcedWorldsByWorld = new IdentityHashMap();
        this.asyncLoadCallbackHandler.setExecutorTask(null);
        super.shutdown();
    }

    private static long makeChunkKey(int cx, int cz) {
        return (long)cx & 0xFFFFFFFFL | ((long)cz & 0xFFFFFFFFL) << 32;
    }

    public int getNumberOfForcedLoadedChunks() {
        int count = 0;
        for (ForcedWorld world : this.forcedWorldsByWorldRadius.values()) {
            count += world.numKeptLoaded();
        }
        return count;
    }

    public boolean isForced(Chunk chunk) {
        return this.isForced(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public boolean isForced(World world, int cx, int cz) {
        for (ForcedWorld forced : this.forcedWorldsByWorld.getOrDefault(world, NO_WORLDS)) {
            if (!forced.isKeptLoaded(cx, cz)) continue;
            return true;
        }
        return false;
    }

    @Override
    public ForcedChunkManager.ForcedChunkEntry add(World world, int chunkX, int chunkZ, int radius) {
        return this.getOrCreateForcedWorld(world, radius).add(chunkX, chunkZ);
    }

    private ForcedWorld getOrCreateForcedWorld(World world, int radius) {
        ForcedWorld last = this.forcedWorldLastGet;
        if (last.world == world && last.radius == radius) {
            return last;
        }
        this.forcedWorldLastGet = LogicUtil.synchronizeCopyOnWrite(this, l -> this.forcedWorldsByWorldRadius, new WorldRadiusKey(world, radius), HashMap::get, (fwmap, key) -> {
            if (!WorldUtil.isLoaded(world)) {
                throw new IllegalStateException("Can't keep chunk on world " + world.getName() + " loaded because the world is unloaded!");
            }
            ForcedWorld forcedWorld = new ForcedWorld(world, radius);
            HashMap<WorldRadiusKey, ForcedWorld> newForcedWorldsByWorldRadius = new HashMap<WorldRadiusKey, ForcedWorld>((Map<WorldRadiusKey, ForcedWorld>)fwmap);
            newForcedWorldsByWorldRadius.put((WorldRadiusKey)key, forcedWorld);
            this.forcedWorldsByWorldRadius = newForcedWorldsByWorldRadius;
            IdentityHashMap<World, ForcedWorld[]> newForcedWorldsByWorld = new IdentityHashMap<World, ForcedWorld[]>(this.forcedWorldsByWorld);
            newForcedWorldsByWorld.compute(world, (w, forcedWorlds) -> {
                if (forcedWorlds == null) {
                    return new ForcedWorld[]{forcedWorld};
                }
                return LogicUtil.appendArrayElement(forcedWorlds, forcedWorld);
            });
            this.forcedWorldsByWorld = newForcedWorldsByWorld;
            this.pendingHandler.start();
            return forcedWorld;
        });
        return this.forcedWorldLastGet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void unloadForcedWorld(World world) {
        ForcedWorld[] forcedWorldArray = this;
        synchronized (this) {
            if (!this.forcedWorldsByWorld.containsKey(world)) {
                // ** MonitorExit[var3_2] (shouldn't be in output)
                return;
            }
            IdentityHashMap<World, ForcedWorld[]> newForcedWorldsByWorld = new IdentityHashMap<World, ForcedWorld[]>(this.forcedWorldsByWorld);
            HashMap<WorldRadiusKey, ForcedWorld> newForcedWorldsByRadius = new HashMap<WorldRadiusKey, ForcedWorld>(this.forcedWorldsByWorldRadius);
            ForcedWorld[] removedWorlds = newForcedWorldsByWorld.remove(world);
            if (removedWorlds != null) {
                for (ForcedWorld forced : removedWorlds) {
                    newForcedWorldsByRadius.remove(new WorldRadiusKey(world, forced.radius));
                }
            }
            this.forcedWorldsByWorld = newForcedWorldsByWorld;
            this.forcedWorldsByWorldRadius = newForcedWorldsByRadius;
            // ** MonitorExit[var3_2] (shouldn't be in output)
            if (removedWorlds != null) {
                for (ForcedWorld forcedWorld : removedWorlds) {
                    forcedWorld.unload(false);
                }
            }
            return;
        }
    }

    private final class ChunkUnloadRequestTask
    extends RunOnceTask {
        public ChunkUnloadRequestTask(Plugin plugin) {
            super(plugin);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            List pending = CommonForcedChunkManager.this.pendingChunkUnloadRequests;
            try {
                int size;
                int i = 0;
                int cycle = 0;
                while (i < (size = pending.size())) {
                    if (++cycle > 10) {
                        this.getPlugin().getLogger().log(Level.WARNING, "[ForcedChunk API] Somebody is loading and then unloading forced chunks inside the ChunkUnloadEvent. Infinite cycle aborted.");
                        return;
                    }
                    do {
                        World world;
                        Entry e;
                        if ((e = (Entry)pending.get(i)).world.unloaded || (world = ((Entry)e).world.world) == null || CommonForcedChunkManager.this.isForced(world, e.getX(), e.getZ())) continue;
                        world.unloadChunkRequest(e.getX(), e.getZ());
                    } while (++i < size);
                }
            }
            finally {
                pending.clear();
            }
        }
    }

    private static class ChunkLoadTimeoutTracker
    extends Task {
        private final LinkedList<PendingChunkLoadTask> tasks = new LinkedList();
        private int currentTick = 0;

        public ChunkLoadTimeoutTracker(JavaPlugin plugin) {
            super(plugin);
        }

        public void add(Entry entry) {
            PendingChunkLoadTask task;
            block3: {
                block2: {
                    if (this.tasks.isEmpty()) break block2;
                    task = this.tasks.peekLast();
                    if (task.tick == this.currentTick) break block3;
                }
                task = new PendingChunkLoadTask(this.currentTick);
                this.tasks.addLast(task);
            }
            task.add(entry);
        }

        @Override
        public void run() {
            ++this.currentTick;
            while (!this.tasks.isEmpty() && this.currentTick - 6000 >= this.tasks.peek().tick) {
                this.tasks.poll().abortAllIfNotLoaded();
            }
        }
    }

    private final class ForcedWorld {
        public World world;
        public ServerLevelHandle handle;
        private final int radius;
        private boolean unloaded;
        public final String worldName;
        public final LongHashMap<Entry> chunks = new LongHashMap();
        public final LongHashSet pending = new LongHashSet();

        public ForcedWorld() {
            this.world = null;
            this.handle = null;
            this.radius = -1;
            this.worldName = null;
            this.unloaded = true;
        }

        public ForcedWorld(World world, int radius) {
            this.world = world;
            this.handle = ServerLevelHandle.fromBukkit(world);
            this.radius = radius;
            this.worldName = world.getName();
            this.unloaded = false;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Converted monitor instructions to comments
         * Lifted jumps to return sites
         */
        public void unload(boolean removeFromMapping) {
            Entry[] entryArray;
            World loadedWorld;
            if (removeFromMapping && (loadedWorld = this.world) != null) {
                WorldRadiusKey key = new WorldRadiusKey(loadedWorld, this.radius);
                entryArray = CommonForcedChunkManager.this;
                // MONITORENTER : CommonForcedChunkManager.this
                HashMap newForcedWorldsByRadius = new HashMap(CommonForcedChunkManager.this.forcedWorldsByWorldRadius);
                if (newForcedWorldsByRadius.remove(key) == this) {
                    CommonForcedChunkManager.this.forcedWorldsByWorldRadius = newForcedWorldsByRadius;
                    IdentityHashMap<World, ForcedWorld[]> newForcedWorldsByWorld = new IdentityHashMap<World, ForcedWorld[]>(CommonForcedChunkManager.this.forcedWorldsByWorld);
                    newForcedWorldsByWorld.computeIfPresent(loadedWorld, (w, forcedWorlds) -> {
                        if (((ForcedWorld[])forcedWorlds).length == 1 && forcedWorlds[0] == this) {
                            return null;
                        }
                        return LogicUtil.removeArrayElement(forcedWorlds, this);
                    });
                    CommonForcedChunkManager.this.forcedWorldsByWorld = newForcedWorldsByWorld;
                }
                // MONITOREXIT : entryArray
            }
            ForcedWorld forcedWorld = this;
            // MONITORENTER : forcedWorld
            if (this.unloaded) {
                this.pending.clear();
                this.chunks.clear();
                // MONITOREXIT : forcedWorld
                return;
            }
            this.unloaded = true;
            this.pending.clear();
            block4: while (true) {
                Entry[] entries;
                if ((entries = this.chunks.values().toArray(new Entry[0])).length <= 0) {
                    this.world = null;
                    this.handle = null;
                    // MONITOREXIT : forcedWorld
                    return;
                }
                entryArray = entries;
                int n = entryArray.length;
                int n2 = 0;
                while (true) {
                    if (n2 >= n) continue block4;
                    Entry e = entryArray[n2];
                    e.disable();
                    e.resetAsyncLoad();
                    ++n2;
                }
                break;
            }
        }

        public synchronized void sync() {
            if (this.handle.isLoaded()) {
                LongHashSet.LongIterator iter = this.pending.longIterator();
                while (iter.hasNext()) {
                    Entry entry = this.chunks.get(iter.next());
                    if (entry == null) continue;
                    entry.sync();
                }
                this.pending.clear();
            } else {
                this.unload(true);
            }
        }

        private void checkUnloaded() {
            if (this.unloaded) {
                throw new IllegalStateException("World " + this.worldName + " has unloaded, chunks cannot be kept loaded");
            }
        }

        public synchronized Entry add(int cx, int cz) {
            this.checkUnloaded();
            Entry entry = this.chunks.computeIfAbsent(CommonForcedChunkManager.makeChunkKey(cx, cz), k -> new Entry(this, k, cx, cz));
            entry.add();
            return entry;
        }

        public synchronized void scheduleUpdate(Entry entry) {
            if (this.pending.isEmpty()) {
                this.checkUnloaded();
                CommonForcedChunkManager.this.pendingHandler.start();
            }
            this.pending.add(entry.getKey());
        }

        public synchronized int numKeptLoaded() {
            return this.chunks.size();
        }

        public synchronized boolean isKeptLoaded(int x, int z) {
            return this.chunks.contains(CommonForcedChunkManager.makeChunkKey(x, z));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void setForced(Entry entry, boolean forced) {
            if (!forced) {
                ForcedWorld forcedWorld = this;
                synchronized (forcedWorld) {
                    Entry e = this.chunks.remove(entry.getKey());
                    if (e != null) {
                        e.resetAsyncLoad();
                    }
                }
            }
            if (this.unloaded) {
                entry.resetCounters();
                if (forced) {
                    this.checkUnloaded();
                } else {
                    return;
                }
            }
            if (CommonCapabilities.HAS_CHUNK_TICKET_API) {
                ServerLevelHandle.fromBukkit(this.world).setForceLoadedAsync(entry.getX(), entry.getZ(), (Plugin)CommonForcedChunkManager.this.plugin, forced, this.radius);
            }
            if (forced) {
                CommonForcedChunkManager.this.loadTimeoutTracker.add(entry);
                entry.startLoadingAsync();
            } else {
                if (!CommonCapabilities.HAS_CHUNK_TICKET_API) {
                    CommonForcedChunkManager.this.pendingChunkUnloadRequests.add(entry);
                    CommonForcedChunkManager.this.chunkUnloadRequestTask.start();
                }
                entry.resetAsyncLoad();
            }
        }
    }

    private class ChunkUnloadEventListener
    implements Listener {
        private ChunkUnloadEventListener() {
        }

        @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
        public void onChunkUnload(ChunkUnloadEvent event) {
            if (CommonForcedChunkManager.this.isForced(event.getChunk())) {
                ((Cancellable)event).setCancelled(true);
            }
        }
    }

    private class WorldUnloadEventListener
    implements Listener {
        private WorldUnloadEventListener() {
        }

        @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
        public void onWorldUnload(WorldUnloadEvent event) {
            CommonForcedChunkManager.this.unloadForcedWorld(event.getWorld());
        }
    }

    private static final class ChunkLoadCallbackExecutor
    extends CommonNextTickExecutor.ExecutorTask {
        public ChunkLoadCallbackExecutor(JavaPlugin plugin) {
            super(plugin);
        }
    }

    private final class Entry
    implements ForcedChunkManager.ForcedChunkEntry,
    Consumer<Object> {
        private final ForcedWorld world;
        private final long key;
        private final int cx;
        private final int cz;
        private final AtomicInteger asyncCounter;
        private int counter;
        private CompletableFuture<Chunk> chunkFuture;

        public Entry(ForcedWorld world, long key, int cx, int cz) {
            this.world = world;
            this.key = key;
            this.cx = cx;
            this.cz = cz;
            this.asyncCounter = new AtomicInteger();
            this.counter = 0;
            this.resetAsyncLoad();
        }

        public void resetAsyncLoad() {
            this.chunkFuture = new CompletableFuture();
        }

        public boolean isForced() {
            return this.counter > 0;
        }

        public void disable() {
            this.asyncCounter.set(0);
            if (this.counter > 0) {
                this.counter = 0;
                this.world.setForced(this, false);
            }
        }

        public void resetCounters() {
            this.asyncCounter.set(0);
            this.counter = 0;
        }

        public void sync() {
            if (this.counter <= 0) {
                this.counter += this.asyncCounter.getAndSet(0);
                if (this.counter > 0) {
                    this.world.setForced(this, true);
                }
            } else {
                this.counter += this.asyncCounter.getAndSet(0);
                if (this.counter <= 0) {
                    this.world.setForced(this, false);
                }
            }
        }

        @Override
        public CommonForcedChunkManager getManager() {
            return CommonForcedChunkManager.this;
        }

        @Override
        public void add() {
            int new_async = this.asyncCounter.incrementAndGet();
            if (CommonUtil.isMainThread()) {
                this.sync();
            } else if (new_async == 1) {
                this.world.scheduleUpdate(this);
            }
        }

        @Override
        public void remove() {
            int new_async = this.asyncCounter.decrementAndGet();
            if (CommonUtil.isMainThread()) {
                this.sync();
            } else if (new_async == -1) {
                this.world.scheduleUpdate(this);
            }
        }

        public long getKey() {
            return this.key;
        }

        @Override
        public World getWorld() {
            return this.world.world;
        }

        @Override
        public int getRadius() {
            return this.world.radius;
        }

        @Override
        public int getX() {
            return this.cx;
        }

        @Override
        public int getZ() {
            return this.cz;
        }

        public String toString() {
            World world = this.getWorld();
            String worldName = world == null ? "UNLOADED" : world.getName();
            return "{world=" + worldName + ", cx=" + this.cx + ", cz=" + this.cz + "}";
        }

        @Override
        public Chunk getChunk() {
            Chunk chunk;
            if (this.chunkFuture.isDone()) {
                try {
                    return this.chunkFuture.get();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            try {
                chunk = this.world.world.getChunkAt(this.cx, this.cz);
            }
            catch (RuntimeException ex) {
                this.world.checkUnloaded();
                throw ex;
            }
            this.chunkFuture.complete(chunk);
            return chunk;
        }

        @Override
        public CompletableFuture<Chunk> getChunkAsync() {
            return this.chunkFuture;
        }

        public void abortIfNotLoaded() {
            if (this.isForced() && !this.chunkFuture.isDone()) {
                this.chunkFuture.completeExceptionally(new ForcedChunkLoadTimeoutException(this.world.world, this.cx, this.cz));
            }
        }

        @Override
        public void accept(Object result) {
            Chunk chunk = ServerChunkCacheHandle.unpackGetChunkAsyncResult(result);
            if (chunk != null) {
                CommonForcedChunkManager.this.asyncLoadCallbackHandler.execute(() -> this.chunkFuture.complete(chunk));
                return;
            }
            if (this.isForced()) {
                CommonForcedChunkManager.this.asyncLoadCallbackHandler.execute(this::startLoadingAsync);
            }
        }

        public void startLoadingAsync() {
            if (this.chunkFuture.isDone()) {
                return;
            }
            Chunk loadedChunk = this.world.handle.getChunkIfLoaded(this.cx, this.cz);
            if (loadedChunk != null) {
                this.chunkFuture.complete(loadedChunk);
                return;
            }
            ServerChunkCacheHandle cps_handle = this.world.handle.getChunkProviderServer();
            Executor executor = cps_handle.getAsyncExecutor();
            if (executor == null) {
                cps_handle.getChunkAtAsync(this.cx, this.cz, this);
            } else {
                CompletableFuture.runAsync(() -> cps_handle.getChunkAtAsync(this.cx, this.cz, this), executor);
            }
        }
    }

    private static final class WorldRadiusKey {
        public final World world;
        public final int radius;

        public WorldRadiusKey(World world, int radius) {
            this.world = world;
            this.radius = radius;
        }

        public boolean equals(Object o) {
            WorldRadiusKey other = (WorldRadiusKey)o;
            return this.world == other.world && this.radius == other.radius;
        }

        public int hashCode() {
            return this.world.hashCode() + this.radius;
        }
    }

    private static class PendingChunkLoadTask {
        public final int tick;
        private final LinkedList<Entry> entries = new LinkedList();

        public PendingChunkLoadTask(int ticks) {
            this.tick = ticks;
        }

        public void add(Entry entry) {
            this.entries.add(entry);
        }

        public void abortAllIfNotLoaded() {
            while (!this.entries.isEmpty()) {
                this.entries.poll().abortIfNotLoaded();
            }
        }
    }
}

