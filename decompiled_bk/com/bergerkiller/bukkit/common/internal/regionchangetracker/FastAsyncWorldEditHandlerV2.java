/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fastasyncworldedit.core.queue.IBatchProcessor
 *  com.fastasyncworldedit.core.queue.IChunk
 *  com.fastasyncworldedit.core.queue.IChunkGet
 *  com.fastasyncworldedit.core.queue.IChunkSet
 *  com.sk89q.worldedit.EditSession$Stage
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.event.extent.EditSessionEvent
 *  com.sk89q.worldedit.extent.Extent
 *  com.sk89q.worldedit.util.eventbus.EventBus
 *  com.sk89q.worldedit.util.eventbus.Subscribe
 *  com.sk89q.worldedit.world.World
 *  org.bukkit.World
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.regionchangetracker;

import com.bergerkiller.bukkit.common.events.RegionChangeSource;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTrackerHandler;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTrackerHandlerChunkDebouncer;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTrackerHandlerOps;
import com.fastasyncworldedit.core.queue.IBatchProcessor;
import com.fastasyncworldedit.core.queue.IChunk;
import com.fastasyncworldedit.core.queue.IChunkGet;
import com.fastasyncworldedit.core.queue.IChunkSet;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.eventbus.EventBus;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.bukkit.plugin.Plugin;

final class FastAsyncWorldEditHandlerV2
implements RegionChangeTrackerHandler {
    FastAsyncWorldEditHandlerV2() {
    }

    @Override
    public boolean isSupported(RegionChangeTrackerHandlerOps ops) {
        Plugin plugin = ops.findPluginEnabledOrProvided("FastAsyncWorldEdit");
        if (plugin == null) {
            return false;
        }
        try {
            ClassLoader loader = plugin.getClass().getClassLoader();
            Class.forName("com.fastasyncworldedit.core.queue.IBatchProcessor", false, loader);
            Class.forName("com.fastasyncworldedit.core.queue.IChunk", false, loader);
        }
        catch (ClassNotFoundException | IllegalStateException ex) {
            return false;
        }
        return true;
    }

    @Override
    public String name() {
        return "FastAsyncWorldEdit (>= v1.17)";
    }

    @Override
    public RegionChangeTrackerHandler.HandlerInstance<?> enable(RegionChangeTrackerHandlerOps ops) {
        return new FAWEV2HandlerInstance(this, ops);
    }

    private static final class FAWEV2HandlerInstance
    extends RegionChangeTrackerHandler.HandlerInstance<FastAsyncWorldEditHandlerV2> {
        private final EventBus eventBus = WorldEdit.getInstance().getEventBus();

        public FAWEV2HandlerInstance(FastAsyncWorldEditHandlerV2 handler, RegionChangeTrackerHandlerOps ops) {
            super(handler, ops);
            this.eventBus.register((Object)this);
        }

        @Override
        public void disable() {
            this.eventBus.unregister((Object)this);
        }

        @Subscribe
        public void onEditSession(EditSessionEvent event) {
            if (event.getStage() == EditSession.Stage.BEFORE_CHANGE) {
                org.bukkit.World world = BukkitAdapter.adapt((World)event.getWorld());
                final RegionChangeTrackerHandlerChunkDebouncer debouncer = new RegionChangeTrackerHandlerChunkDebouncer(RegionChangeSource.FASTASYNCWORLDEDIT, world, this.ops);
                event.getExtent().addProcessor(new IBatchProcessor(){

                    public Extent construct(Extent extent) {
                        return extent;
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public IChunkSet processSet(IChunk chunk, IChunkGet chunkGet, IChunkSet chunkSet) {
                        RegionChangeTrackerHandlerChunkDebouncer regionChangeTrackerHandlerChunkDebouncer = debouncer;
                        synchronized (regionChangeTrackerHandlerChunkDebouncer) {
                            debouncer.addChunk(chunk.getX(), chunk.getZ());
                        }
                        return chunkSet;
                    }

                    public Future<IChunkSet> postProcessSet(IChunk chunk, IChunkGet get, IChunkSet set) {
                        return CompletableFuture.completedFuture(set);
                    }
                });
            }
        }
    }
}

