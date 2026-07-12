/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.tc.events.seat;

import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.events.MemberEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class MemberBeforeSeatExitEvent
extends MemberEvent
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Entity entity;
    private final boolean playerInitiated;
    private final CartAttachmentSeat seat;
    private final Location seatPosition;
    private Location exitPosition;
    private boolean exitPreservePlayerRotation;
    private boolean cancelled;

    public MemberBeforeSeatExitEvent(CartAttachmentSeat seat, Entity entity, Location seatPosition, Location exitPosition, boolean exitPreservePlayerRotation, boolean playerInitiated) {
        super(seat.getMember());
        this.seat = seat;
        this.entity = entity;
        this.seatPosition = seatPosition;
        this.exitPosition = exitPosition;
        this.exitPreservePlayerRotation = exitPreservePlayerRotation;
        this.playerInitiated = playerInitiated;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public boolean isPlayer() {
        return this.entity instanceof Player;
    }

    public boolean isPlayerInitiated() {
        return this.playerInitiated;
    }

    public Location getSeatPosition() {
        return this.seatPosition;
    }

    public Location getExitPosition() {
        return this.exitPosition;
    }

    public boolean isExitPlayerRotationPreserved() {
        return this.exitPreservePlayerRotation;
    }

    public void setExitPosition(Location position) {
        this.exitPosition = position;
        this.exitPreservePlayerRotation = false;
    }

    public void setExitPosition(Location position, boolean preservePlayerRotation) {
        this.exitPosition = position;
        this.exitPreservePlayerRotation = preservePlayerRotation;
    }

    public CartAttachmentSeat getSeat() {
        return this.seat;
    }

    public boolean isSeatChange() {
        return false;
    }

    public boolean isMemberVehicleChange() {
        return true;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String toString() {
        return "MemberBeforeSeatExitEvent{member=" + this.getMember() + ", passenger=" + this.getEntity() + ", playerInitiated=" + this.playerInitiated + ", cancelled=" + this.isCancelled() + "}";
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

