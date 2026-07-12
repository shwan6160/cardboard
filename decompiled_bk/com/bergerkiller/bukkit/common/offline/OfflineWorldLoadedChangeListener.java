/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.WorldInitEvent
 *  org.bukkit.event.world.WorldLoadEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package com.bergerkiller.bukkit.common.offline;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

final class OfflineWorldLoadedChangeListener
implements LibraryComponent,
OfflineWorld.BukkitWorldSupplierHandler {
    private final Plugin plugin;
    private BukkitTask asyncClearTask;

    public OfflineWorldLoadedChangeListener(Plugin plugin) {
        this.plugin = plugin;
        this.asyncClearTask = null;
    }

    @Override
    public void enable() throws Throwable {
        OfflineWorld.setLoadedWorldSupplier(this);
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler(priority=EventPriority.LOWEST)
            public void onWorldInit(WorldInitEvent event) {
                OfflineWorld.BukkitWorldSupplier supplier = OfflineWorld.of((World)event.getWorld()).loadedWorldSupplier;
                if (supplier instanceof WorldSupplier) {
                    ((WorldSupplier)supplier).update(event.getWorld());
                }
            }

            @EventHandler(priority=EventPriority.LOWEST)
            public void onWorldLoad(WorldLoadEvent event) {
                OfflineWorld.BukkitWorldSupplier supplier = OfflineWorld.of((World)event.getWorld()).loadedWorldSupplier;
                if (supplier instanceof WorldSupplier) {
                    ((WorldSupplier)supplier).update(event.getWorld());
                }
            }

            @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
            public void onWorldUnload(WorldUnloadEvent event) {
                OfflineWorld.BukkitWorldSupplier supplier = OfflineWorld.of((World)event.getWorld()).loadedWorldSupplier;
                if (supplier instanceof WorldSupplier) {
                    ((WorldSupplier)supplier).update(null);
                }
                OfflineWorld.clearByBukkitWorldCache();
            }
        }, this.plugin);
        int clearInterval = 1200;
        this.asyncClearTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, OfflineWorld::clearByBukkitWorldCache, 1200L, 1200L);
    }

    @Override
    public void disable() {
        this.asyncClearTask.cancel();
        this.asyncClearTask = null;
        OfflineWorld.setLoadedWorldSupplier(OfflineWorld.DefaultBukkitWorldSupplierHandler.INSTANCE);
    }

    @Override
    public OfflineWorld.BukkitWorldSupplier createBukkitWorldSupplier(UUID worldUUID) {
        return new WorldSupplier(worldUUID);
    }

    @Override
    public boolean cacheByBukkitWorld() {
        return true;
    }

    private static final class WorldSupplier
    implements OfflineWorld.BukkitWorldSupplier {
        private World loadedWorld;
        private Object comparer;

        public WorldSupplier(UUID worldUUID) {
            this.loadedWorld = Bukkit.getWorld((UUID)worldUUID);
            this.comparer = this.loadedWorld;
        }

        public void update(World world) {
            this.loadedWorld = world;
            this.comparer = world == null ? new Object() : world;
        }

        @Override
        public World get() {
            return this.loadedWorld;
        }

        @Override
        public boolean isWorld(World world) {
            return world == this.comparer;
        }
    }
}

