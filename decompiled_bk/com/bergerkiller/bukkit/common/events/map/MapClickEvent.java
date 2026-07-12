/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.common.events.map;

import com.bergerkiller.bukkit.common.events.map.MapAction;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.util.IMapLookPosition;
import com.bergerkiller.bukkit.common.map.util.MapLookPosition;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MapClickEvent
extends Event
implements Cancellable,
IMapLookPosition {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final MapLookPosition position;
    private final MapDisplay display;
    private final MapAction action;
    private boolean cancelled;

    public MapClickEvent(Player player, MapLookPosition position, MapDisplay display, MapAction action) {
        this.player = player;
        this.position = position;
        this.display = display;
        this.action = action;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return this.player;
    }

    public MapDisplay getDisplay() {
        return this.display;
    }

    @Override
    public ItemFrameInfo getItemFrameInfo() {
        return this.position.getItemFrameInfo();
    }

    @Override
    public ItemFrame getItemFrame() {
        return this.position.getItemFrame();
    }

    public MapAction getAction() {
        return this.action;
    }

    @Override
    public int getX() {
        return this.position.getX();
    }

    @Override
    public int getY() {
        return this.position.getY();
    }

    @Override
    public double getDoubleX() {
        return this.position.getDoubleX();
    }

    @Override
    public double getDoubleY() {
        return this.position.getDoubleY();
    }

    @Override
    public double getDistance() {
        return this.position.getDistance();
    }

    @Override
    public boolean isWithinBounds() {
        return this.position.isWithinBounds();
    }

    @Override
    public double getEdgeDistance() {
        return this.position.getEdgeDistance();
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

