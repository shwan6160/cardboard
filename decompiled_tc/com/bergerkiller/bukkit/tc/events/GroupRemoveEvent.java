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

public class GroupRemoveEvent
extends GroupEvent {
    private static final HandlerList handlers = new HandlerList();

    public GroupRemoveEvent(MinecartGroup group) {
        super(group);
    }

    public static void call(MinecartGroup group) {
        CommonUtil.callEvent((Event)new GroupRemoveEvent(group));
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

