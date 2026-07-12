/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.tc.events;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.MemberEvent;
import org.bukkit.event.HandlerList;

public class MemberAddEvent
extends MemberEvent {
    private static final HandlerList handlers = new HandlerList();
    private final MinecartGroup toGroup;

    public MemberAddEvent(MinecartMember<?> member, MinecartGroup toGroup) {
        super(member);
        this.toGroup = toGroup;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MinecartGroup getTo() {
        return this.toGroup;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

