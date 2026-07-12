/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  org.bukkit.block.Block
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.tc.events;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.MemberEvent;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MemberBlockChangeEvent
extends MemberEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Block from;
    private final Block to;

    private MemberBlockChangeEvent(MinecartMember<?> member, Block from, Block to) {
        super(member);
        this.from = from;
        this.to = to;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static void call(MinecartMember<?> member, Block from, Block to) {
        CommonUtil.callEvent((Event)new MemberBlockChangeEvent(member, from, to));
    }

    public Block getFrom() {
        return this.from;
    }

    public Block getTo() {
        return this.to;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

