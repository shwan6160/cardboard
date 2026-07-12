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

public class EntityAddEvent
extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private final World world;

    public EntityAddEvent(World world, Entity added) {
        super(added);
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

