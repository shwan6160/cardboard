/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.tc.events.seat;

import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.events.MemberEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class MemberSeatExitEvent
extends MemberEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Entity entity;
    private final boolean playerInitiated;
    private final Location seatPosition;
    private final Location exitPosition;
    private final boolean exitPreserveRotation;
    private final CartAttachmentSeat seat;

    public MemberSeatExitEvent(CartAttachmentSeat seat, Entity entity, Location seatPosition, Location exitPosition, boolean exitPreserveRotation, boolean playerInitiated) {
        super(seat.getMember());
        this.seat = seat;
        this.entity = entity;
        this.seatPosition = seatPosition;
        this.exitPosition = exitPosition;
        this.exitPreserveRotation = exitPreserveRotation;
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

    public boolean isExitRotationPreserved() {
        return this.exitPreserveRotation;
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

    public String toString() {
        return "MemberSeatExitEvent{member=" + this.getMember() + ", passenger=" + this.getEntity() + ", playerInitiated=" + this.playerInitiated + "}";
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

