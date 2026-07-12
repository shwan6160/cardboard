/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package com.bergerkiller.bukkit.common.events.map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class PlayerMapEvent
extends Event {
    private final Player player;

    public PlayerMapEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}

