/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.EditSession$Stage
 *  com.sk89q.worldedit.Vector
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.WorldEditException
 *  com.sk89q.worldedit.blocks.BaseBlock
 *  com.sk89q.worldedit.event.extent.EditSessionEvent
 *  com.sk89q.worldedit.extent.AbstractDelegateExtent
 *  com.sk89q.worldedit.extent.Extent
 *  com.sk89q.worldedit.util.eventbus.EventBus
 *  com.sk89q.worldedit.util.eventbus.Subscribe
 *  org.bukkit.World
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.regionchangetracker;

import com.bergerkiller.bukkit.common.events.RegionChangeSource;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTrackerHandler;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTrackerHandlerChunkDebouncer;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTrackerHandlerOps;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.eventbus.EventBus;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import java.lang.reflect.Method;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

final class WorldEditHandlerV1
implements RegionChangeTrackerHandler {
    WorldEditHandlerV1() {
    }

    @Override
    public boolean isSupported(RegionChangeTrackerHandlerOps ops) {
        Plugin plugin = ops.findPluginEnabledOrProvided("WorldEdit");
        if (plugin == null) {
            return false;
        }
        try {
            ClassLoader loader = plugin.getClass().getClassLoader();
            Class.forName("com.sk89q.worldedit.extent.AbstractDelegateExtent", false, loader);
            Class.forName("com.sk89q.worldedit.Vector", false, loader);
            Class.forName("com.sk89q.worldedit.blocks.BaseBlock", false, loader);
        }
        catch (ClassNotFoundException | IllegalStateException ex) {
            return false;
        }
        return !plugin.getName().equalsIgnoreCase("FastAsyncWorldEdit");
    }

    @Override
    public String name() {
        return "WorldEdit (<= v6.0.0)";
    }

    @Override
    public RegionChangeTrackerHandler.HandlerInstance<?> enable(RegionChangeTrackerHandlerOps ops) throws Exception, Error {
        return new WEV1HandlerInstance(this, ops);
    }

    private static final class WEV1HandlerInstance
    extends RegionChangeTrackerHandler.HandlerInstance<WorldEditHandlerV1> {
        private final Method adaptWorldMethod;
        private final EventBus eventBus;

        public WEV1HandlerInstance(WorldEditHandlerV1 handler, RegionChangeTrackerHandlerOps ops) throws Exception {
            super(handler, ops);
            Class<?> bukkitAdapter = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Class<?> worldClass = Class.forName("com.sk89q.worldedit.world.World");
            this.adaptWorldMethod = bukkitAdapter.getDeclaredMethod("adapt", worldClass);
            this.adaptWorldMethod.setAccessible(true);
            this.eventBus = WorldEdit.getInstance().getEventBus();
            this.eventBus.register((Object)this);
        }

        @Override
        public void disable() {
            this.eventBus.unregister((Object)this);
        }

        @Subscribe
        public void onEditSession(EditSessionEvent event) {
            if (event.getStage() == EditSession.Stage.BEFORE_CHANGE) {
                World world;
                try {
                    world = (World)this.adaptWorldMethod.invoke(null, event.getWorld());
                }
                catch (Throwable t) {
                    this.notifyError("Failed to get WorldEdit EditSessionEvent world", t);
                    return;
                }
                event.setExtent((Extent)new ChangeTrackerAdapter(event.getExtent(), this.ops, world));
            }
        }

        public static class ChangeTrackerAdapter
        extends AbstractDelegateExtent {
            private final RegionChangeTrackerHandlerChunkDebouncer _debouncer;

            public ChangeTrackerAdapter(Extent extent, RegionChangeTrackerHandlerOps ops, World world) {
                super(extent);
                this._debouncer = new RegionChangeTrackerHandlerChunkDebouncer(RegionChangeSource.WORLDEDIT, world, ops);
            }

            public boolean setBlock(Vector location, BaseBlock block) throws WorldEditException {
                if (super.setBlock(location, block)) {
                    this._debouncer.addBlock(location.getBlockX(), location.getBlockZ());
                    return true;
                }
                return false;
            }
        }
    }
}

