/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.common.events.map;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MapKeyEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final MapDisplay _map;
    private final MapPlayerInput.Key _key;
    private final MapPlayerInput _input;

    public MapKeyEvent(MapDisplay map, MapPlayerInput input, MapPlayerInput.Key key) {
        this._input = input;
        this._key = key;
        this._map = map;
    }

    public Player getPlayer() {
        return this._input.player;
    }

    public MapPlayerInput getInput() {
        return this._input;
    }

    public MapDisplay getMapDisplay() {
        return this._map;
    }

    public MapPlayerInput.Key getKey() {
        return this._key;
    }

    public int getRepeat() {
        return this._input.getRepeat();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

