/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.tc.events;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GroupLinkEvent
extends Event
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final MinecartGroup group1;
    private final MinecartGroup group2;
    private boolean cancelled = false;

    public GroupLinkEvent(MinecartGroup group1, MinecartGroup group2) {
        this.group1 = group1;
        this.group2 = group2;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static GroupLinkEvent call(MinecartGroup group1, MinecartGroup group2) {
        return (GroupLinkEvent)CommonUtil.callEvent((Event)new GroupLinkEvent(group1, group2));
    }

    public MinecartGroup getGroup1() {
        return this.group1;
    }

    public MinecartGroup getGroup2() {
        return this.group2;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

