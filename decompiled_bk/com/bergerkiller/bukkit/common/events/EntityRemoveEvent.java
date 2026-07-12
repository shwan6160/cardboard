/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.entity.EntityEvent
 */
package com.bergerkiller.bukkit.common.events;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class EntityRemoveEvent
extends EntityEvent {
    private final World world;
    private static final HandlerList handlers = new HandlerList();

    public EntityRemoveEvent(World world, Entity removed) {
        super(removed);
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

