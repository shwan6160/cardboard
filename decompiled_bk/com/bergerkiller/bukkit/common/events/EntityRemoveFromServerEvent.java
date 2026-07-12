/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.entity.EntityEvent
 */
package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class EntityRemoveFromServerEvent
extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public EntityRemoveFromServerEvent(Entity removed) {
        super(removed);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

