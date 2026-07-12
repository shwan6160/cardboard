/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.world.ChunkEvent
 */
package com.bergerkiller.bukkit.common.events;

import org.bukkit.Chunk;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.ChunkEvent;

public class ChunkUnloadEntitiesEvent
extends ChunkEvent {
    private static final HandlerList handlers = new HandlerList();

    public ChunkUnloadEntitiesEvent(Chunk chunk) {
        super(chunk);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

