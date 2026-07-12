/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.tc.events;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.GroupEvent;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlot;
import org.bukkit.event.HandlerList;

public class MutexZoneConflictEvent
extends GroupEvent {
    private static final HandlerList handlers = new HandlerList();
    private final MinecartGroup groupCrossed;
    private final MutexZoneSlot slot;
    private final IntVector3 rail;

    public MutexZoneConflictEvent(MinecartGroup group, MinecartGroup groupCrossed, MutexZoneSlot slot, IntVector3 rail) {
        super(group);
        this.groupCrossed = groupCrossed;
        this.slot = slot;
        this.rail = rail;
    }

    public MutexZoneSlot getMutexZoneSlot() {
        return this.slot;
    }

    public MinecartGroup getGroupCrossed() {
        return this.groupCrossed;
    }

    public IntVector3 getRailPosition() {
        return this.rail;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

