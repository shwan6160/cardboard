/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.PacketReceiveEvent
 *  com.bergerkiller.bukkit.common.events.PacketSendEvent
 *  com.bergerkiller.bukkit.common.protocol.PacketListener
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.wrappers.IntHashMap
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRotateHeadPacketHandle
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRotateHeadPacketHandle;

public class SeatAttachmentMap
implements PacketListener {
    public static PacketType[] LISTENED_TYPES = new PacketType[]{PacketType.OUT_ENTITY_MOVE_LOOK, PacketType.OUT_ENTITY_LOOK, PacketType.OUT_ENTITY_HEAD_ROTATION};
    private final IntHashMap<CartAttachmentSeat> _map = new IntHashMap();

    public void set(int passengerEntityId, CartAttachmentSeat seat) {
        this._map.put(passengerEntityId, (Object)seat);
    }

    public void remove(int passengerEntityId, CartAttachmentSeat seat) {
        CartAttachmentSeat removed = (CartAttachmentSeat)this._map.remove(passengerEntityId);
        if (removed != seat) {
            this._map.put(passengerEntityId, (Object)removed);
        }
    }

    public CartAttachmentSeat get(int passengerEntityId) {
        CartAttachmentSeat seat = (CartAttachmentSeat)this._map.get(passengerEntityId);
        if (seat != null && (seat.getEntity() == null || seat.getEntity().getEntityId() != passengerEntityId)) {
            this._map.remove(passengerEntityId);
            seat = null;
        }
        return seat;
    }

    public void onPacketReceive(PacketReceiveEvent event) {
    }

    public void onPacketSend(PacketSendEvent event) {
        ClientboundRotateHeadPacketHandle packet;
        CartAttachmentSeat seat;
        if (event.getType() == PacketType.OUT_ENTITY_MOVE_LOOK) {
            ClientboundMoveEntityPacketHandle packet2 = ClientboundMoveEntityPacketHandle.createHandle((Object)event.getPacket().getHandle());
            CartAttachmentSeat seat2 = this.get(packet2.getEntityId());
            if (seat2 != null && seat2.isRotationLocked()) {
                packet2.setYaw(seat2.getPassengerYaw());
                packet2.setPitch(seat2.getPassengerPitch());
            }
        } else if (event.getType() == PacketType.OUT_ENTITY_LOOK) {
            ClientboundMoveEntityPacketHandle packet3 = ClientboundMoveEntityPacketHandle.createHandle((Object)event.getPacket().getHandle());
            CartAttachmentSeat seat3 = this.get(packet3.getEntityId());
            if (seat3 != null && seat3.isRotationLocked()) {
                packet3.setYaw(seat3.getPassengerYaw());
                packet3.setPitch(seat3.getPassengerPitch());
            }
        } else if (event.getType() == PacketType.OUT_ENTITY_HEAD_ROTATION && (seat = this.get((packet = ClientboundRotateHeadPacketHandle.createHandle((Object)event.getPacket().getHandle())).getEntityId())) != null && seat.isRotationLocked()) {
            packet.setHeadYaw(seat.getPassengerHeadYaw());
        }
    }
}

