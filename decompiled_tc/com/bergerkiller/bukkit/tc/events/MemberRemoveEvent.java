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
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.MemberEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MemberRemoveEvent
extends MemberEvent {
    private static final HandlerList handlers = new HandlerList();

    public MemberRemoveEvent(MinecartMember<?> member) {
        super(member);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static void call(MinecartMember<?> member) {
        CommonUtil.callEvent((Event)new MemberRemoveEvent(member));
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

