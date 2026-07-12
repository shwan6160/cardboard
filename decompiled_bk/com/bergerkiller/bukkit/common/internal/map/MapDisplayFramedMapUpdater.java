/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;
import java.util.function.BiConsumer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

class MapDisplayFramedMapUpdater
extends Task {
    public static final int NUM_IDLE_FRAMES_POLLED_PER_TICK = 50;
    private final CommonMapController controller;
    private final BiConsumer<ItemFrameInfo, Player> handleMapShowEvent;
    private ItemFrameInfo.UpdateEntry firstEntryUpdated = null;
    private ItemFrameInfo.UpdateEntry lastEntryUpdated = null;

    public MapDisplayFramedMapUpdater(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
        this.handleMapShowEvent = (currentFrame, newViewer) -> this.controller.handleMapShowEvent(new MapShowEvent((Player)newViewer, currentFrame.itemFrame));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        this.controller.itemFrameClustersByWorldEnabled = true;
        try {
            CommonMapController commonMapController = this.controller;
            synchronized (commonMapController) {
                this.updateItemFrameItemAndViewers();
                this.controller.mapsWithItemFrameResolutionChanges.forEachAndClear(MapDisplayInfo::updateItemFrameResolution);
                this.controller.mapsWithItemFrameViewerChanges.forEachAndClear(MapDisplayInfo::updateItemFrameViewers);
                this.controller.itemFramesThatNeedItemRefresh.forEachAndClear(c -> c.itemFrameHandle.refreshItem());
            }
        }
        finally {
            this.controller.itemFrameClustersByWorldEnabled = false;
            this.controller.itemFrameClustersByWorld.clear();
        }
    }

    private void updateItemFrameItemAndViewers() {
        ItemFrameInfo.UpdateEntry entry = this.controller.itemFrameUpdateList.first();
        try {
            while (entry != null && entry.prioritized) {
                entry.prioritized = false;
                this.updateItemAndViewers(entry);
                entry = entry.next;
            }
            int numNonPrioritized = 0;
            while (entry != null && ++numNonPrioritized <= 50) {
                this.updateItemAndViewers(entry);
                entry = entry.next;
            }
            while (entry != null) {
                this.updateViewersPassive(entry);
                entry = entry.next;
            }
            if (this.firstEntryUpdated != null) {
                this.controller.itemFrameUpdateList.moveRangeToEnd(this.firstEntryUpdated, this.lastEntryUpdated);
            }
        }
        finally {
            this.firstEntryUpdated = null;
            this.lastEntryUpdated = null;
        }
    }

    private void updateViewersPassive(ItemFrameInfo.UpdateEntry entry) {
        if (!entry.info.handleRemoved()) {
            entry.info.updateViewers(this.handleMapShowEvent);
            if (!entry.info.handleRemoved()) {
                return;
            }
        }
        this.remove(entry);
    }

    private void updateItemAndViewers(ItemFrameInfo.UpdateEntry entry) {
        if (!entry.info.handleRemoved()) {
            entry.info.updateItem();
            entry.info.updateViewers(this.handleMapShowEvent);
            if (!entry.info.handleRemoved()) {
                this.lastEntryUpdated = entry;
                if (this.firstEntryUpdated == null) {
                    this.firstEntryUpdated = entry;
                }
                return;
            }
        }
        this.remove(entry);
    }

    private void remove(ItemFrameInfo.UpdateEntry entry) {
        this.controller.itemFrameUpdateList.remove(entry);
        this.controller.itemFrames.remove(entry.info.itemFrameHandle.getId());
    }
}

