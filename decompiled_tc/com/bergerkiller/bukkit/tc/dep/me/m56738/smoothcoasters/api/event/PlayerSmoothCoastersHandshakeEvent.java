/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.player.PlayerEvent
 */
package com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.event;

import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.implementation.Implementation;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerSmoothCoastersHandshakeEvent
extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Implementation implementation;
    private final String version;

    public PlayerSmoothCoastersHandshakeEvent(Player who, Implementation implementation, String version) {
        super(who);
        this.implementation = implementation;
        this.version = version;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public byte getImplementationVersion() {
        return this.implementation.getVersion();
    }

    public String getVersion() {
        return this.version;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}

