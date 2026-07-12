/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.tc.events;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.GroupEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GroupForceUpdateEvent
extends GroupEvent {
    private static final HandlerList handlers = new HandlerList();
    private double force;

    public GroupForceUpdateEvent(MinecartGroup group, double force) {
        super(group);
        this.force = force;
    }

    public static double call(MinecartGroup group, double force) {
        return ((GroupForceUpdateEvent)CommonUtil.callEvent((Event)new GroupForceUpdateEvent(group, force))).getForce();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public double getForce() {
        return this.force;
    }

    public void setForce(double value) {
        this.force = value;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

