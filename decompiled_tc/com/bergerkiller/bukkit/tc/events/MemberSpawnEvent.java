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

public class MemberSpawnEvent
extends MemberEvent {
    private static final HandlerList handlers = new HandlerList();

    public MemberSpawnEvent(MinecartMember<?> member) {
        super(member);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static MemberSpawnEvent call(MinecartMember<?> member) {
        return (MemberSpawnEvent)CommonUtil.callEvent((Event)new MemberSpawnEvent(member));
    }

    public void setMember(MinecartMember<?> member) {
        this.member = member;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

