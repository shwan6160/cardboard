/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.tc.events.signactions;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SignActionRegisterEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final SignAction action;
    private final boolean priority;

    public SignActionRegisterEvent(SignAction action, boolean priority) {
        this.action = action;
        this.priority = priority;
    }

    public SignAction getSignAction() {
        return this.action;
    }

    public boolean isPriority() {
        return this.priority;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

