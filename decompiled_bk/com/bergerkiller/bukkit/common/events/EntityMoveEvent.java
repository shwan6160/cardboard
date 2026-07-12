/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.entity.EntityEvent
 */
package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class EntityMoveEvent
extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private EntityHandle entityHandle;

    public EntityMoveEvent() {
        super(null);
    }

    public void setEntity(EntityHandle entityHandle) {
        this.entityHandle = entityHandle;
        this.entity = this.entityHandle.getBukkitEntity();
    }

    public World getWorld() {
        return this.entity.getWorld();
    }

    public Location getFrom(Location from) {
        if (from != null) {
            from.setWorld(this.entity.getWorld());
            from.setX(this.entityHandle.getLastX());
            from.setY(this.entityHandle.getLastY());
            from.setZ(this.entityHandle.getLastZ());
            from.setYaw(this.entityHandle.getLastYaw());
            from.setPitch(this.entityHandle.getLastPitch());
        }
        return from;
    }

    public Location getTo(Location to) {
        return this.entity.getLocation(to);
    }

    public double getFromX() {
        return this.entityHandle.getLastX();
    }

    public double getFromY() {
        return this.entityHandle.getLastY();
    }

    public double getFromZ() {
        return this.entityHandle.getLastZ();
    }

    public float getFromYaw() {
        return this.entityHandle.getLastYaw();
    }

    public float getFromPitch() {
        return this.entityHandle.getLastPitch();
    }

    public double getToX() {
        return this.entityHandle.getLocX();
    }

    public double getToY() {
        return this.entityHandle.getLocY();
    }

    public double getToZ() {
        return this.entityHandle.getLocZ();
    }

    public float getToYaw() {
        return this.entityHandle.getYaw();
    }

    public float getToPitch() {
        return this.entityHandle.getPitch();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

