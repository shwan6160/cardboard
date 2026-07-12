/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 */
package com.bergerkiller.bukkit.tc.events.seat;

import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.events.MemberEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class MemberSeatEnterEvent
extends MemberEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Entity entity;
    private final boolean wasSeatChange;
    private final boolean wasVehicleChange;
    private final boolean playerInitiated;
    private CartAttachmentSeat seat;

    public MemberSeatEnterEvent(CartAttachmentSeat seat, Entity entity, boolean playerInitiated, boolean wasSeatChange, boolean wasVehicleChange) {
        super(seat.getMember());
        this.seat = seat;
        this.entity = entity;
        this.wasSeatChange = wasSeatChange;
        this.wasVehicleChange = wasVehicleChange;
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

    public boolean wasSeatChange() {
        return this.wasSeatChange;
    }

    public boolean wasMemberVehicleChange() {
        return this.wasVehicleChange;
    }

    public CartAttachmentSeat getSeat() {
        return this.seat;
    }

    public void setSeat(CartAttachmentSeat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat can not be null");
        }
        if (seat.getEntity() != null && seat.getEntity() != this.getEntity()) {
            throw new IllegalArgumentException("The specified seat is already occupied");
        }
        this.seat = seat;
        this.member = seat.getMember();
    }

    public String toString() {
        return "MemberSeatEnterEvent{member=" + this.getMember() + ", passenger=" + this.getEntity() + ", playerInitiated=" + this.playerInitiated + ", seatChange=" + this.wasSeatChange + ", vehicleChange=" + this.wasVehicleChange + "}";
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

