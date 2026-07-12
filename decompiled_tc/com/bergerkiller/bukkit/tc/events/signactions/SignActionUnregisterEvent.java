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

public class SignActionUnregisterEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final SignAction action;

    public SignActionUnregisterEvent(SignAction action) {
        this.action = action;
    }

    public SignAction getSignAction() {
        return this.action;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

