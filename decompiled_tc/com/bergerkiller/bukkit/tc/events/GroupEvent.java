/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 */
package com.bergerkiller.bukkit.tc.events;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import org.bukkit.event.Event;

public abstract class GroupEvent
extends Event {
    private final MinecartGroup group;

    public GroupEvent(MinecartGroup group) {
        this.group = group;
    }

    public MinecartGroup getGroup() {
        return this.group;
    }
}

