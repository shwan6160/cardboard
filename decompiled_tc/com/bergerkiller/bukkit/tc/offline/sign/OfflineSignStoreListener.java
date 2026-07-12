/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.ChunkLoadEvent
 *  org.bukkit.event.world.WorldInitEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 */
package com.bergerkiller.bukkit.tc.offline.sign;

import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

class OfflineSignStoreListener
implements Listener {
    private final OfflineSignStore store;

    public OfflineSignStoreListener(OfflineSignStore store) {
        this.store = store;
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onWorldInit(WorldInitEvent event) {
        this.store.loadSignsOnWorld(event.getWorld());
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onChunkLoad(ChunkLoadEvent event) {
        this.store.verifySignsInChunk(event.getChunk());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onWorldUnload(WorldUnloadEvent event) {
        this.store.unloadSignsOnWorld(event.getWorld());
    }
}

