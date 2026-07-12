/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.world.WorldEvent
 */
package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.events.RegionChangeSource;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;

public class MultiBlockChangeEvent
extends WorldEvent {
    private static final HandlerList handlers = new HandlerList();
    private final RegionChangeSource source;
    private final Set<IntVector2> chunkCoordinates;

    public MultiBlockChangeEvent(RegionChangeSource source, World world, Set<IntVector2> chunkCoordinates) {
        super(world);
        this.source = source;
        this.chunkCoordinates = chunkCoordinates;
    }

    public RegionChangeSource getSource() {
        return this.source;
    }

    public Set<IntVector2> getChunkCoordinates() {
        return this.chunkCoordinates;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

