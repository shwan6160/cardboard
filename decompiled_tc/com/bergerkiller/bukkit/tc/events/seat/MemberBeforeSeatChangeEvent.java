/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.bukkit.tc.events.seat;

import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.seat.MemberBeforeSeatExitEvent;
import org.bukkit.entity.Entity;

public class MemberBeforeSeatChangeEvent
extends MemberBeforeSeatExitEvent {
    private CartAttachmentSeat newSeat;

    public MemberBeforeSeatChangeEvent(CartAttachmentSeat oldSeat, CartAttachmentSeat newSeat, Entity entity, boolean playerInitiated) {
        super(oldSeat, entity, oldSeat.getPosition(entity), newSeat.getPosition(entity), true, playerInitiated);
        this.newSeat = newSeat;
    }

    @Override
    public boolean isSeatChange() {
        return true;
    }

    @Override
    public boolean isMemberVehicleChange() {
        return this.getMember() != this.getEnteredMember();
    }

    @Override
    public Entity getEntity() {
        return super.getEntity();
    }

    @Override
    public boolean isPlayer() {
        return super.isPlayer();
    }

    @Override
    public CartAttachmentSeat getSeat() {
        return super.getSeat();
    }

    public CartAttachmentSeat getEnteredSeat() {
        return this.newSeat;
    }

    public MinecartMember<?> getEnteredMember() {
        return this.newSeat.getMember();
    }

    public void setEnteredSeat(CartAttachmentSeat seat) {
        if (this.newSeat != seat) {
            if (seat == null) {
                throw new IllegalArgumentException("Seat can not be null");
            }
            if (seat.getEntity() != null && seat.getEntity() != this.getEntity()) {
                throw new IllegalArgumentException("The specified seat is already occupied");
            }
        }
        this.newSeat = seat;
    }

    @Override
    public String toString() {
        return "MemberBeforeSeatChangeEvent{member=" + this.getMember() + ", passenger=" + this.getEntity() + ", playerInitiated=" + this.isPlayerInitiated() + ", vehicleChange=" + this.isMemberVehicleChange() + ", newMember=" + this.getEnteredMember() + ", cancelled=" + this.isCancelled() + "}";
    }
}

