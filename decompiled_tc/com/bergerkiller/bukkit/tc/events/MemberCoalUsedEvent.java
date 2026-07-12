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
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.MemberEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MemberCoalUsedEvent
extends MemberEvent {
    private static final HandlerList handlers = new HandlerList();
    private boolean useCoal = TCConfig.useCoalFromStorageCart;
    private boolean refill = false;

    public MemberCoalUsedEvent(MinecartMember<?> source) {
        super(source);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static MemberCoalUsedEvent call(MinecartMember<?> member) {
        return (MemberCoalUsedEvent)CommonUtil.callEvent((Event)new MemberCoalUsedEvent(member));
    }

    public boolean useCoal() {
        return this.useCoal;
    }

    public boolean refill() {
        return this.refill;
    }

    public void setUseCoal(boolean use) {
        this.useCoal = use;
    }

    public void setRefill(boolean refill) {
        this.refill = refill;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

