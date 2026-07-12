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

public class GroupCreateEvent
extends GroupEvent {
    private static final HandlerList handlers = new HandlerList();

    public GroupCreateEvent(MinecartGroup group) {
        super(group);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static void call(MinecartGroup group) {
        CommonUtil.callEvent((Event)new GroupCreateEvent(group));
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

