/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartRideable
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  com.bergerkiller.bukkit.common.wrappers.InteractionResult
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecartRideable;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.components.AttachmentControllerMember;
import com.bergerkiller.bukkit.tc.exception.GroupUnloadedException;
import com.bergerkiller.bukkit.tc.exception.MemberMissingException;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MinecartMemberRideable
extends MinecartMember<CommonMinecartRideable> {
    private List<Entity> oldPassengers = new ArrayList<Entity>();

    public MinecartMemberRideable(TrainCarts plugin) {
        super(plugin);
    }

    public InteractionResult onInteractBy(HumanEntity interacter, HumanHand hand, Vector location) {
        if (interacter instanceof Player && ((Player)interacter).isSneaking()) {
            return InteractionResult.PASS;
        }
        if (this.getAvailableSeatCount((Entity)interacter) == 0) {
            return InteractionResult.PASS;
        }
        if (((CommonMinecartRideable)this.entity).isPassenger((Entity)interacter)) {
            CartAttachmentSeat seat = this.getAttachments().findSeat((Entity)interacter);
            if (seat != null && seat.getTicksInSeat() < 10) {
                return InteractionResult.PASS;
            }
            TrainProperties tprop = this.getGroup().getProperties();
            if (!tprop.getPlayersExit() || !tprop.getPlayersEnter()) {
                return InteractionResult.PASS;
            }
            if (this.getAttachments().changeSeatsLookingAt((Entity)interacter)) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        CartAttachmentSeat new_seat = this.getAttachments().findNewSeatForEntity((Entity)interacter);
        if (new_seat == null) {
            return InteractionResult.PASS;
        }
        MinecartMember<?> previous = MinecartMemberStore.getFromEntity(interacter.getVehicle());
        if (previous != null) {
            CartAttachmentSeat old_seat = previous.getAttachments().findSeat((Entity)interacter);
            if (old_seat == new_seat) {
                return InteractionResult.PASS;
            }
            return AttachmentControllerMember.handleSeatChange((Entity)interacter, old_seat, new_seat, true) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return AttachmentControllerMember.handleSeatChange((Entity)interacter, null, new_seat, true) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.oldPassengers.clear();
        this.oldPassengers.addAll(((CommonMinecartRideable)this.entity).getPassengers());
    }

    @Override
    public void onActivate() {
        super.onActivate();
        if (TCConfig.activatorEjectEnabled) {
            this.eject();
        }
    }

    @Override
    public void onPhysicsPostMove() throws MemberMissingException, GroupUnloadedException {
        super.onPhysicsPostMove();
        List newPassengers = ((CommonMinecartRideable)this.entity).getPassengers();
        if (!this.oldPassengers.equals(newPassengers)) {
            this.oldPassengers.clear();
            this.oldPassengers.addAll(newPassengers);
            this.onPropertiesChanged();
        }
    }
}

