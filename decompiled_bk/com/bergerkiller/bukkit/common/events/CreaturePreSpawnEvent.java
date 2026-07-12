/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 */
package com.bergerkiller.bukkit.common.events;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.CreatureSpawnEvent;

public final class CreaturePreSpawnEvent
extends Event
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancelled;
    protected EntityType entityType;
    protected CreatureSpawnEvent.SpawnReason reason;
    protected final Location spawnLocation = new Location(null, 0.0, 0.0, 0.0);

    protected CreaturePreSpawnEvent() {
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public CreatureSpawnEvent.SpawnReason getSpawnReason() {
        return this.reason;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

