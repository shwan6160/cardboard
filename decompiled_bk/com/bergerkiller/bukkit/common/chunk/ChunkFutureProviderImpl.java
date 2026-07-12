/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.event.world.ChunkLoadEvent
 *  org.bukkit.event.world.ChunkUnloadEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.chunk;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.chunk.ChunkFutureProvider;
import com.bergerkiller.bukkit.common.events.ChunkLoadEntitiesEvent;
import com.bergerkiller.bukkit.common.events.ChunkUnloadEntitiesEvent;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

abstract class ChunkFutureProviderImpl
implements ChunkFutureProvider,
Listener,
Executor {
    protected final Plugin plugin;
    protected final LongHashMap<Chain> entries;
    private World currentlyUnloadingWorld = null;

    public ChunkFutureProviderImpl(Plugin plugin) {
        this.plugin = plugin;
        this.entries = new LongHashMap();
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler(priority=EventPriority.MONITOR)
            public void onChunkLoad(ChunkLoadEvent event) {
                ChunkFutureProviderImpl.this.handleEvent(event.getChunk(), Mode.CHUNK_LOADED);
            }

            @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
            public void onChunkUnload(ChunkUnloadEvent event) {
                ChunkFutureProviderImpl.this.handleEvent(event.getChunk(), Mode.CHUNK_UNLOADED);
            }

            @EventHandler(priority=EventPriority.MONITOR)
            public void onChunkLoadEntities(ChunkLoadEntitiesEvent event) {
                ChunkFutureProviderImpl.this.handleEvent(event.getChunk(), Mode.CHUNK_ENTITIES_LOADED);
            }

            @EventHandler(priority=EventPriority.MONITOR)
            public void onChunkUnloadEntities(ChunkUnloadEntitiesEvent event) {
                ChunkFutureProviderImpl.this.handleEvent(event.getChunk(), Mode.CHUNK_ENTITIES_UNLOADED);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
            public void onWorldUnload(WorldUnloadEvent event) {
                try {
                    ChunkFutureProviderImpl.this.currentlyUnloadingWorld = event.getWorld();
                    for (Chunk chunk : event.getWorld().getLoadedChunks()) {
                        ChunkFutureProviderImpl.this.handleEvent(chunk, Mode.CHUNK_UNLOADED);
                    }
                    World world = event.getWorld();
                    for (Chain chain : new ArrayList<Chain>(ChunkFutureProviderImpl.this.entries.values())) {
                        chain.process(e -> e.world == world, e -> ChunkFutureProviderImpl.cancelFast(e.future));
                    }
                }
                finally {
                    ChunkFutureProviderImpl.this.currentlyUnloadingWorld = null;
                }
            }
        }, plugin);
    }

    @Override
    public CompletableFuture<Chunk> whenAllNeighboursLoaded(Chunk mainChunk, ChunkFutureProvider.ChunkNeighbourList neighbours) {
        final CompletableFuture<Chunk> future = new CompletableFuture<Chunk>();
        ChunkFutureProvider.ChunkStateTracker tracker = this.trackNeighboursLoaded(mainChunk, neighbours, new ChunkFutureProvider.ChunkStateListener(){
            final /* synthetic */ ChunkFutureProviderImpl this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onRegistered(ChunkFutureProvider.ChunkStateTracker tracker) {
                if (tracker.isLoaded()) {
                    this.onLoaded(tracker);
                }
            }

            @Override
            public void onCancelled(ChunkFutureProvider.ChunkStateTracker tracker) {
                ChunkFutureProviderImpl.cancelFast(future);
            }

            @Override
            public void onLoaded(ChunkFutureProvider.ChunkStateTracker tracker) {
                future.complete(tracker.getChunk());
                tracker.cancel();
            }

            @Override
            public void onUnloaded(ChunkFutureProvider.ChunkStateTracker tracker) {
            }
        });
        if (!future.isDone()) {
            LogicUtil.exceptionallyAsync(future, t -> {
                if (t instanceof CompletionException) {
                    tracker.cancel();
                }
                return null;
            }, this);
        }
        return future;
    }

    private void handleEvent(Chunk chunk, Mode mode) {
        Chain chain = this.entries.get(MathUtil.longHashToLong(chunk.getX(), chunk.getZ()));
        if (chain == null) {
            return;
        }
        World world = chunk.getWorld();
        chain.process(e -> e.world == world && e.mode == mode, e -> e.future.complete((Chunk)(e.passChunkToFuture ? chunk : null)));
    }

    protected <T> CompletableFuture<T> createEntry(Chunk chunk, Mode mode, boolean passChunkToFuture, CompletableFuture<T> future) {
        return this.createEntry(chunk.getWorld(), chunk.getX(), chunk.getZ(), mode, passChunkToFuture, future);
    }

    protected <T> CompletableFuture<T> createEntry(World world, int cx, int cz, Mode mode, boolean passChunkToFuture, CompletableFuture<T> future) {
        Entry entry;
        if (world == this.currentlyUnloadingWorld) {
            ChunkFutureProviderImpl.cancelFast(future);
            return future;
        }
        long key = MathUtil.longHashToLong(cx, cz);
        Chain chain = this.entries.get(key);
        if (chain == null) {
            chain = new Chain(this, key, world, mode, passChunkToFuture, (CompletableFuture)LogicUtil.unsafeCast(future));
            entry = chain.first;
            this.entries.put(key, chain);
        } else {
            entry = chain.add(world, mode, passChunkToFuture, (CompletableFuture)LogicUtil.unsafeCast(future));
        }
        entry.removeWhenFutureCancelled();
        return future;
    }

    private static void cancelFast(CompletableFuture<?> completableFuture) {
        completableFuture.completeExceptionally(ChunkFutureProvider.FutureCancelledException.INSTANCE);
    }

    public static enum Mode {
        CHUNK_LOADED{

            @Override
            public boolean isCompleted(Chunk chunk) {
                return chunk != null && chunk.isLoaded();
            }
        }
        ,
        CHUNK_UNLOADED{

            @Override
            public boolean isCompleted(Chunk chunk) {
                return chunk == null || !chunk.isLoaded();
            }
        }
        ,
        CHUNK_ENTITIES_LOADED{

            @Override
            public boolean isCompleted(Chunk chunk) {
                return chunk != null && WorldUtil.isChunkEntitiesLoaded(chunk);
            }
        }
        ,
        CHUNK_ENTITIES_UNLOADED{

            @Override
            public boolean isCompleted(Chunk chunk) {
                return chunk == null || !WorldUtil.isChunkEntitiesLoaded(chunk);
            }
        };


        public abstract boolean isCompleted(Chunk var1);
    }

    private static final class Chain {
        public final ChunkFutureProviderImpl handler;
        public final long key;
        public Entry first;
        public Entry last;

        public Chain(ChunkFutureProviderImpl handler, long key, World world, Mode mode, boolean passChunkToFuture, CompletableFuture<Chunk> future) {
            this.handler = handler;
            this.key = key;
            this.last = this.first = new Entry(this, world, mode, passChunkToFuture, future);
        }

        public Entry add(World world, Mode mode, boolean passChunkToFuture, CompletableFuture<Chunk> future) {
            Entry entry = new Entry(this, world, mode, passChunkToFuture, future);
            entry.previous = this.last;
            this.last.next = entry;
            this.last = entry;
            return entry;
        }

        public void process(Predicate<Entry> filter, Consumer<Entry> consumer) {
            Entry current;
            while ((current = this.first) != null) {
                if (filter.test(current)) {
                    current.remove();
                    consumer.accept(current);
                    continue;
                }
                do {
                    Entry previous = current;
                    current = current.next;
                    if (current == null) {
                        return;
                    }
                    if (!filter.test(current)) continue;
                    current.remove();
                    consumer.accept(current);
                    current = previous;
                } while (!current.removed);
            }
        }
    }

    private static final class Entry {
        public final Chain chain;
        public final World world;
        public final Mode mode;
        public boolean removed;
        public Entry previous;
        public Entry next;
        public final boolean passChunkToFuture;
        public final CompletableFuture<Chunk> future;

        public Entry(Chain chain, World world, Mode mode, boolean passChunkToFuture, CompletableFuture<Chunk> future) {
            this.chain = chain;
            this.world = world;
            this.mode = mode;
            this.removed = false;
            this.previous = null;
            this.next = null;
            this.passChunkToFuture = passChunkToFuture;
            this.future = future;
        }

        public void remove() {
            this.removed = true;
            if (this == this.chain.first) {
                this.chain.first = this.next;
                this.next = null;
                if (this.chain.first != null) {
                    this.chain.first.previous = null;
                } else {
                    this.chain.last = null;
                    this.chain.handler.entries.remove(this.chain.key);
                }
            } else if (this == this.chain.last) {
                this.chain.last = this.previous;
                this.chain.last.next = null;
                this.previous = null;
            } else if (this.previous != null && this.previous.next == this) {
                this.previous.next = this.next;
                this.next.previous = this.previous;
                this.previous = null;
                this.next = null;
            }
        }

        public void removeWhenFutureCancelled() {
            LogicUtil.exceptionallyAsync(this.future, err -> {
                if (err instanceof CompletionException) {
                    this.remove();
                }
                return null;
            }, this.chain.handler);
        }
    }

    private static final class ChunkLoadedTrackerSingleImpl
    implements ChunkFutureProvider.ChunkStateTracker {
        private ChunkFutureProviderImpl provider;
        private final World world;
        private final int chunkX;
        private final int chunkZ;
        private boolean loaded;
        private Chunk cachedChunk;
        private final ChunkFutureProvider.ChunkStateListener listener;
        private final Consumer<Chunk> whenLoaded;
        private final Consumer<Chunk> whenUnloaded;
        private CompletableFuture<?> currentFuture;
        private boolean cancelled;

        public ChunkLoadedTrackerSingleImpl(World world, int chunkX, int chunkZ, ChunkFutureProvider.ChunkStateListener listener) {
            this.world = world;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.listener = listener;
            this.currentFuture = null;
            this.cancelled = false;
            this.loaded = false;
            this.cachedChunk = null;
            this.whenLoaded = c -> {
                if (!this.cancelled) {
                    this.onLoaded((Chunk)c);
                    this.fireLoaded();
                }
            };
            this.whenUnloaded = c -> {
                if (!this.cancelled) {
                    this.onUnloaded((Chunk)c);
                    this.fireUnloaded();
                    this.cachedChunk = null;
                }
            };
        }

        public void start(ChunkFutureProviderImpl provider) {
            if (this.cancelled) {
                return;
            }
            this.provider = provider;
            this.cancelled = false;
            this.cachedChunk = null;
            Chunk chunk = WorldUtil.getChunk(this.world, this.chunkX, this.chunkZ);
            if (chunk != null) {
                this.onLoaded(chunk);
            } else {
                this.onUnloaded(null);
            }
            try {
                this.listener.onRegistered(this);
            }
            catch (Throwable t) {
                provider.plugin.getLogger().log(Level.SEVERE, "Chunk tracking failed: Error calling listener onRegistered", t);
            }
        }

        private void onLoaded(Chunk chunk) {
            this.cachedChunk = chunk;
            this.loaded = true;
            CompletableFuture whenUnloadedFuture = this.provider.createEntry(this.world, this.chunkX, this.chunkZ, Mode.CHUNK_UNLOADED, true, new CompletableFuture());
            whenUnloadedFuture.thenAccept(this.whenUnloaded);
            this.currentFuture = whenUnloadedFuture;
        }

        private void onUnloaded(Chunk chunk) {
            this.cachedChunk = chunk;
            this.loaded = false;
            CompletableFuture whenLoadedFuture = this.provider.createEntry(this.world, this.chunkX, this.chunkZ, Mode.CHUNK_LOADED, true, new CompletableFuture());
            whenLoadedFuture.thenAccept(this.whenLoaded);
            this.currentFuture = whenLoadedFuture;
        }

        private void fireLoaded() {
            try {
                this.listener.onLoaded(this);
            }
            catch (Throwable t) {
                this.provider.plugin.getLogger().log(Level.SEVERE, "Chunk tracking failed: Error calling listener onLoaded", t);
            }
        }

        private void fireUnloaded() {
            try {
                this.listener.onUnloaded(this);
            }
            catch (Throwable t) {
                this.provider.plugin.getLogger().log(Level.SEVERE, "Chunk tracking failed: Error calling listener onUnloaded", t);
            }
        }

        private void fireCancelled() {
            try {
                this.listener.onCancelled(this);
            }
            catch (Throwable t) {
                this.provider.plugin.getLogger().log(Level.SEVERE, "Chunk tracking failed: Error calling listener onCancelled", t);
            }
        }

        @Override
        public void cancel() {
            if (!this.cancelled) {
                this.provider.execute(() -> {
                    if (!this.cancelled) {
                        this.cancelled = true;
                        ChunkFutureProviderImpl.cancelFast(this.currentFuture);
                        this.fireCancelled();
                    }
                });
            }
        }

        @Override
        public World getWorld() {
            return this.world;
        }

        @Override
        public int getChunkX() {
            return this.chunkX;
        }

        @Override
        public int getChunkZ() {
            return this.chunkZ;
        }

        @Override
        public Chunk getChunk() {
            return this.cachedChunk;
        }

        @Override
        public boolean isLoaded() {
            return this.loaded;
        }
    }

    protected static class ChunkTrackerListImpl
    implements ChunkFutureProvider.ChunkNeighbourList,
    ChunkFutureProvider.ChunkStateTracker {
        private static final CompletableFuture<Void> UNLOAD_FUTURE_INITIAL = CompletableFuture.completedFuture(null);
        private final ArrayList<ChunkLoadedTrackerSingleImpl> trackers;
        private final ChunkFutureProvider.ChunkStateListener neighbourListener;
        private int numLoaded = 0;
        private CompletableFuture<Void> mainChunkUnloadFuture = UNLOAD_FUTURE_INITIAL;
        private Chunk mainChunk = null;
        private ChunkFutureProviderImpl provider = null;
        private boolean isHandlingUnloadEvent = false;
        private boolean cancelled = false;
        private ChunkFutureProvider.ChunkStateListener listener;

        public ChunkTrackerListImpl(int numTrackersCapacity) {
            this.trackers = new ArrayList(numTrackersCapacity);
            this.neighbourListener = new ChunkFutureProvider.ChunkStateListener(){

                @Override
                public void onRegistered(ChunkFutureProvider.ChunkStateTracker tracker) {
                    if (tracker.isLoaded()) {
                        ++numLoaded;
                    }
                }

                @Override
                public void onCancelled(ChunkFutureProvider.ChunkStateTracker tracker) {
                }

                @Override
                public void onLoaded(ChunkFutureProvider.ChunkStateTracker tracker) {
                    if (++numLoaded == trackers.size()) {
                        this.fireNeighboursLoaded();
                    }
                }

                @Override
                public void onUnloaded(ChunkFutureProvider.ChunkStateTracker tracker) {
                    if (numLoaded-- == trackers.size()) {
                        this.fireNeighboursUnloaded();
                    }
                }
            };
        }

        @Override
        public void add(World world, int chunkX, int chunkZ) {
            this.trackers.add(new ChunkLoadedTrackerSingleImpl(world, chunkX, chunkZ, this.neighbourListener));
        }

        public void start(Chunk mainChunk, ChunkFutureProviderImpl provider, ChunkFutureProvider.ChunkStateListener listener) {
            if (this.cancelled) {
                return;
            }
            if (this.mainChunk != null) {
                throw new IllegalStateException("ChunkTrackerList can only be used once");
            }
            this.mainChunk = mainChunk;
            this.provider = provider;
            this.listener = listener;
            if (!this.trackers.isEmpty()) {
                for (ChunkLoadedTrackerSingleImpl tracker : this.trackers) {
                    tracker.start(provider);
                }
            }
            this.fireNeighboursRegistered();
            if (this.cancelled) {
                return;
            }
            this.mainChunkUnloadFuture = provider.whenUnloaded(mainChunk);
            this.mainChunkUnloadFuture.thenAccept(unused -> {
                this.cancelled = true;
                this.trackers.forEach(ChunkLoadedTrackerSingleImpl::cancel);
                if (this.numLoaded == this.trackers.size()) {
                    this.fireNeighboursUnloaded();
                }
                this.fireCancelled();
            });
        }

        @Override
        public void cancel() {
            if (!this.cancelled) {
                this.provider.execute(() -> {
                    if (!this.cancelled) {
                        this.cancelled = true;
                        this.trackers.forEach(ChunkLoadedTrackerSingleImpl::cancel);
                        this.fireCancelled();
                        ChunkFutureProviderImpl.cancelFast(this.mainChunkUnloadFuture);
                    }
                });
            }
        }

        private void fireNeighboursRegistered() {
            try {
                this.listener.onRegistered(this);
            }
            catch (Throwable t) {
                this.provider.plugin.getLogger().log(Level.SEVERE, "Chunk neighbour tracking failed: Error calling onRegistered", t);
            }
        }

        private void fireNeighboursLoaded() {
            try {
                this.listener.onLoaded(this);
            }
            catch (Throwable t) {
                this.provider.plugin.getLogger().log(Level.SEVERE, "Chunk neighbour tracking failed: Error calling onLoaded", t);
            }
        }

        private void fireNeighboursUnloaded() {
            boolean prevHandlingUnload = this.isHandlingUnloadEvent;
            try {
                this.isHandlingUnloadEvent = true;
                this.listener.onUnloaded(this);
            }
            catch (Throwable t) {
                this.provider.plugin.getLogger().log(Level.SEVERE, "Chunk neighbour tracking failed: Error calling onUnloaded", t);
            }
            finally {
                this.isHandlingUnloadEvent = prevHandlingUnload;
            }
        }

        private void fireCancelled() {
            try {
                this.listener.onCancelled(this);
            }
            catch (Throwable t) {
                this.provider.plugin.getLogger().log(Level.SEVERE, "Chunk neighbour tracking failed: Error calling onCancelled", t);
            }
        }

        @Override
        public World getWorld() {
            return this.mainChunk.getWorld();
        }

        @Override
        public int getChunkX() {
            return this.mainChunk.getX();
        }

        @Override
        public int getChunkZ() {
            return this.mainChunk.getZ();
        }

        @Override
        public Chunk getChunk() {
            return this.isHandlingUnloadEvent || this.isLoaded() ? this.mainChunk : null;
        }

        @Override
        public boolean isLoaded() {
            return this.numLoaded == this.trackers.size();
        }
    }

    protected static final class ChunkTrackerListFutureImpl
    extends ChunkTrackerListImpl {
        public final CompletableFuture<Chunk> future = new CompletableFuture();

        public ChunkTrackerListFutureImpl(int numTrackersCapacity) {
            super(numTrackersCapacity);
        }
    }

    public static final class SyncLoadHandler
    implements ChunkFutureProvider {
        public static final SyncLoadHandler INSTANCE = new SyncLoadHandler();

        @Override
        public CompletableFuture<Chunk> whenLoaded(World world, int chunkX, int chunkZ) {
            return CompletableFuture.completedFuture(world.getChunkAt(chunkX, chunkZ));
        }

        @Override
        public CompletableFuture<Chunk> whenLoaded(Chunk chunk) {
            return CompletableFuture.completedFuture(chunk);
        }

        @Override
        public CompletableFuture<Chunk> whenNeighbourLoaded(Chunk mainChunk, int neighbourChunkX, int neighbourChunkZ) {
            if (neighbourChunkX == mainChunk.getX() && neighbourChunkZ == mainChunk.getZ()) {
                return CompletableFuture.completedFuture(mainChunk);
            }
            return CompletableFuture.completedFuture(mainChunk.getWorld().getChunkAt(neighbourChunkX, neighbourChunkZ));
        }

        @Override
        public CompletableFuture<Chunk> whenAllNeighboursLoaded(Chunk mainChunk, ChunkFutureProvider.ChunkNeighbourList neighbours) {
            return CompletableFuture.completedFuture(mainChunk);
        }

        @Override
        public CompletableFuture<BlockData> readNeighbourBlockData(Chunk mainChunk, Block block) {
            return CompletableFuture.completedFuture(WorldUtil.getBlockData(block));
        }

        @Override
        public CompletableFuture<Void> whenUnloaded(World world, int chunkX, int chunkZ) {
            throw new UnsupportedOperationException("Only whenLoaded is supported by the Sync ChunkFutureProvider");
        }

        @Override
        public CompletableFuture<Chunk> whenEntitiesLoaded(World world, int chunkX, int chunkZ) {
            throw new UnsupportedOperationException("Only whenLoaded is supported by the Sync ChunkFutureProvider");
        }

        @Override
        public CompletableFuture<Void> whenEntitiesUnloaded(World world, int chunkX, int chunkZ) {
            throw new UnsupportedOperationException("Only whenLoaded is supported by the Sync ChunkFutureProvider");
        }

        @Override
        public ChunkFutureProvider.ChunkStateTracker trackLoaded(World world, int chunkX, int chunkZ, ChunkFutureProvider.ChunkStateListener listener) {
            throw new UnsupportedOperationException("Only whenLoaded is supported by the Sync ChunkFutureProvider");
        }

        @Override
        public ChunkFutureProvider.ChunkStateTracker trackNeighboursLoaded(Chunk mainChunk, ChunkFutureProvider.ChunkNeighbourList neighbours, ChunkFutureProvider.ChunkStateListener listener) {
            throw new UnsupportedOperationException("Only whenLoaded is supported by the Sync ChunkFutureProvider");
        }
    }

    public static final class ThreadSafeHandler
    extends ChunkFutureProviderImpl {
        public static final Map<Plugin, ChunkFutureProvider> handlers = new IdentityHashMap<Plugin, ChunkFutureProvider>();

        public ThreadSafeHandler(Plugin plugin) {
            super(plugin);
        }

        @Override
        public void execute(Runnable command) {
            if (CommonUtil.isMainThread()) {
                command.run();
            } else if (!this.plugin.isEnabled()) {
                Logging.LOGGER.warning("Failed to execute task for plugin " + this.plugin.getName() + " because plugin is disabled");
            } else if (this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, command) == -1) {
                Logging.LOGGER.warning("Failed to execute task for plugin " + this.plugin.getName() + " because scheduling failed");
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @EventHandler(priority=EventPriority.MONITOR)
        public void onPluginDisabled(PluginDisableEvent event) {
            if (event.getPlugin() != this.plugin) return;
            Class<ThreadSafeHandler> clazz = ThreadSafeHandler.class;
            synchronized (ThreadSafeHandler.class) {
                handlers.remove(this.plugin);
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return;
            }
        }

        @Override
        public CompletableFuture<Chunk> whenLoaded(World world, int chunkX, int chunkZ) {
            CompletableFuture<Chunk> future = new CompletableFuture<Chunk>();
            this.execute(() -> {
                Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
                if (chunk != null) {
                    future.complete(chunk);
                } else {
                    this.createEntry(world, chunkX, chunkZ, Mode.CHUNK_LOADED, true, future);
                }
            });
            return future;
        }

        @Override
        public CompletableFuture<Void> whenUnloaded(World world, int chunkX, int chunkZ) {
            CompletableFuture<Void> future = new CompletableFuture<Void>();
            this.execute(() -> {
                if (!WorldUtil.isLoaded(world, chunkX, chunkZ)) {
                    future.complete(null);
                } else {
                    this.createEntry(world, chunkX, chunkZ, Mode.CHUNK_UNLOADED, false, future);
                }
            });
            return future;
        }

        @Override
        public CompletableFuture<Chunk> whenEntitiesLoaded(World world, int chunkX, int chunkZ) {
            CompletableFuture<Chunk> future = new CompletableFuture<Chunk>();
            this.execute(() -> {
                Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
                if (chunk != null && WorldUtil.isChunkEntitiesLoaded(chunk)) {
                    future.complete(chunk);
                } else {
                    this.createEntry(world, chunkX, chunkZ, Mode.CHUNK_ENTITIES_LOADED, true, future);
                }
            });
            return future;
        }

        @Override
        public CompletableFuture<Void> whenEntitiesUnloaded(World world, int chunkX, int chunkZ) {
            CompletableFuture<Void> future = new CompletableFuture<Void>();
            this.execute(() -> {
                Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
                if (chunk == null || !WorldUtil.isChunkEntitiesLoaded(chunk)) {
                    future.complete(null);
                } else {
                    this.createEntry(world, chunkX, chunkZ, Mode.CHUNK_ENTITIES_UNLOADED, false, future);
                }
            });
            return future;
        }

        @Override
        public ChunkFutureProvider.ChunkStateTracker trackLoaded(World world, int chunkX, int chunkZ, ChunkFutureProvider.ChunkStateListener listener) {
            ChunkLoadedTrackerSingleImpl tracker = new ChunkLoadedTrackerSingleImpl(world, chunkX, chunkZ, listener);
            this.execute(() -> tracker.start(this));
            return tracker;
        }

        @Override
        public ChunkFutureProvider.ChunkStateTracker trackNeighboursLoaded(Chunk mainChunk, ChunkFutureProvider.ChunkNeighbourList neighbours, ChunkFutureProvider.ChunkStateListener listener) {
            ChunkTrackerListImpl tracker;
            try {
                tracker = (ChunkTrackerListImpl)neighbours;
            }
            catch (ClassCastException ex) {
                throw new IllegalArgumentException("Neighbours argument must be created using ChunkTrackerList.create()");
            }
            this.execute(() -> tracker.start(mainChunk, this, listener));
            return tracker;
        }
    }

    public static final class MainThreadHandler
    extends ChunkFutureProviderImpl {
        public static final Map<Plugin, ChunkFutureProvider> handlers = new IdentityHashMap<Plugin, ChunkFutureProvider>();

        public MainThreadHandler(Plugin plugin) {
            super(plugin);
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }

        @EventHandler(priority=EventPriority.MONITOR)
        public void onPluginDisabled(PluginDisableEvent event) {
            if (event.getPlugin() == this.plugin) {
                handlers.remove(this.plugin);
            }
        }

        @Override
        public CompletableFuture<Chunk> whenLoaded(World world, int chunkX, int chunkZ) {
            Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
            if (chunk != null) {
                return CompletableFuture.completedFuture(chunk);
            }
            return this.createEntry(world, chunkX, chunkZ, Mode.CHUNK_LOADED, true, new CompletableFuture());
        }

        @Override
        public CompletableFuture<Chunk> whenLoaded(Chunk chunk) {
            if (WorldUtil.isLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ())) {
                return CompletableFuture.completedFuture(chunk);
            }
            return this.createEntry(chunk, Mode.CHUNK_LOADED, true, new CompletableFuture());
        }

        @Override
        public CompletableFuture<Void> whenUnloaded(World world, int chunkX, int chunkZ) {
            if (!WorldUtil.isLoaded(world, chunkX, chunkZ)) {
                return CompletableFuture.completedFuture(null);
            }
            return this.createEntry(world, chunkX, chunkZ, Mode.CHUNK_UNLOADED, false, new CompletableFuture());
        }

        @Override
        public CompletableFuture<Chunk> whenEntitiesLoaded(World world, int chunkX, int chunkZ) {
            Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
            if (chunk != null && WorldUtil.isChunkEntitiesLoaded(chunk)) {
                return CompletableFuture.completedFuture(chunk);
            }
            return this.createEntry(world, chunkX, chunkZ, Mode.CHUNK_ENTITIES_LOADED, true, new CompletableFuture());
        }

        @Override
        public CompletableFuture<Chunk> whenEntitiesLoaded(Chunk chunk) {
            if (WorldUtil.isChunkEntitiesLoaded(chunk)) {
                return CompletableFuture.completedFuture(chunk);
            }
            return this.createEntry(chunk, Mode.CHUNK_ENTITIES_LOADED, true, new CompletableFuture());
        }

        @Override
        public CompletableFuture<Void> whenEntitiesUnloaded(World world, int chunkX, int chunkZ) {
            if (!WorldUtil.isChunkEntitiesLoaded(world, chunkX, chunkZ)) {
                return CompletableFuture.completedFuture(null);
            }
            return this.createEntry(world, chunkX, chunkZ, Mode.CHUNK_ENTITIES_UNLOADED, false, new CompletableFuture());
        }

        @Override
        public CompletableFuture<Void> whenEntitiesUnloaded(Chunk chunk) {
            if (!WorldUtil.isChunkEntitiesLoaded(chunk)) {
                return CompletableFuture.completedFuture(null);
            }
            return this.createEntry(chunk, Mode.CHUNK_ENTITIES_UNLOADED, false, new CompletableFuture());
        }

        @Override
        public ChunkFutureProvider.ChunkStateTracker trackLoaded(World world, int chunkX, int chunkZ, ChunkFutureProvider.ChunkStateListener listener) {
            ChunkLoadedTrackerSingleImpl tracker = new ChunkLoadedTrackerSingleImpl(world, chunkX, chunkZ, listener);
            tracker.start(this);
            return tracker;
        }

        @Override
        public ChunkFutureProvider.ChunkStateTracker trackNeighboursLoaded(Chunk mainChunk, ChunkFutureProvider.ChunkNeighbourList neighbours, ChunkFutureProvider.ChunkStateListener listener) {
            ChunkTrackerListImpl tracker;
            try {
                tracker = (ChunkTrackerListImpl)neighbours;
            }
            catch (ClassCastException ex) {
                throw new IllegalArgumentException("Neighbours argument must be created using ChunkTrackerList.create()");
            }
            tracker.start(mainChunk, this, listener);
            return tracker;
        }
    }
}

