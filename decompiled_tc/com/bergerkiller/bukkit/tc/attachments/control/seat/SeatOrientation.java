/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle$RotHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRotateHeadPacketHandle
 *  com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntity;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityElytra;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityNormal;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRotateHeadPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SeatOrientation {
    private float _entityLastYaw = 0.0f;
    private float _entityLastPitch = 0.0f;
    private float _entityLastHeadYaw = 0.0f;
    private float _mountYaw = 0.0f;
    private int _entityRotationCtr = 0;

    public float getPassengerYaw() {
        return this._entityLastYaw;
    }

    public float getPassengerPitch() {
        return this._entityLastPitch;
    }

    public float getPassengerHeadYaw() {
        return this._entityLastHeadYaw;
    }

    public float getMountYaw() {
        return this._mountYaw;
    }

    public void sendLockedRotations(AttachmentViewer viewer, int entityId) {
        if (entityId != viewer.getEntityId()) {
            ClientboundRotateHeadPacketHandle headPacket = ClientboundRotateHeadPacketHandle.createNew((int)entityId, (float)this.getPassengerHeadYaw());
            viewer.send((PacketHandle)headPacket);
            ClientboundMoveEntityPacketHandle.RotHandle lookPacket = ClientboundMoveEntityPacketHandle.RotHandle.createNew((int)entityId, (float)this.getPassengerYaw(), (float)this.getPassengerPitch(), (boolean)false);
            viewer.send((PacketHandle)lookPacket);
        }
    }

    protected Vector computeElytraRelativeOffset(Vector pyr) {
        double yaw_sin = Math.sin(Math.toRadians(pyr.getY()));
        double yaw_cos = Math.cos(Math.toRadians(pyr.getY()));
        double pitch_sin = Math.sin(Math.toRadians(pyr.getX()));
        double pitch_cos = Math.cos(Math.toRadians(pyr.getX()));
        double l = 0.6;
        double m = 0.1;
        double rx = 0.6 * pitch_sin + (0.1 * pitch_cos - 0.1);
        double ry = 0.6 * pitch_cos - 0.6 - 0.1 * pitch_sin;
        double off_x = -yaw_sin * rx;
        double off_y = ry;
        double off_z = yaw_cos * rx;
        return new Vector(-off_x, -off_y, -off_z);
    }

    protected void synchronizeElytra(CartAttachmentSeat seat, Matrix4x4 transform, Vector pyr, SeatedEntityElytra seated) {
        Player viewerToIgnore = seated.isPlayer() && !seated.isMadeVisibleInFirstPerson() ? (Player)seated.getEntity() : null;
        this._mountYaw = (float)pyr.getY();
        float pitch = (float)(pyr.getX() - 90.0);
        float headRot = seated.isDummyPlayer() ? this._mountYaw : EntityHandle.fromBukkit((Entity)seated.getEntity()).getHeadRotation();
        float HEAD_ROT_LIM = 30.0f;
        if (MathUtil.getAngleDifference((float)headRot, (float)this._mountYaw) > 30.0f) {
            headRot = MathUtil.getAngleDifference((float)headRot, (float)(this._mountYaw + 30.0f)) < MathUtil.getAngleDifference((float)headRot, (float)(this._mountYaw - 30.0f)) ? this._mountYaw + 30.0f : this._mountYaw - 30.0f;
        }
        if (Util.isProtocolRotationGlitched(pitch, this._entityLastPitch)) {
            seated.flipFakes(seat);
            this._entityRotationCtr = 0;
        }
        int entityId = seated.getFakePlayerId();
        int flippedId = seated.getFlippedFakePlayerId();
        if (EntityTrackerEntryStateHandle.hasProtocolRotationChanged((float)headRot, (float)this._entityLastHeadYaw)) {
            ClientboundRotateHeadPacketHandle headPacket = ClientboundRotateHeadPacketHandle.createNew((int)entityId, (float)headRot);
            ClientboundRotateHeadPacketHandle headPacketFlipped = ClientboundRotateHeadPacketHandle.createNew((int)flippedId, (float)headRot);
            this._entityLastHeadYaw = headPacket.getHeadYaw();
            for (AttachmentViewer viewer : seat.getAttachmentViewers()) {
                if (viewer.getPlayer() == viewerToIgnore) continue;
                viewer.send((PacketHandle)headPacket);
                viewer.send((PacketHandle)headPacketFlipped);
            }
        }
        if (this._entityRotationCtr == 0 || EntityTrackerEntryStateHandle.hasProtocolRotationChanged((float)this._mountYaw, (float)this._entityLastYaw) || EntityTrackerEntryStateHandle.hasProtocolRotationChanged((float)pitch, (float)this._entityLastPitch)) {
            this._entityRotationCtr = 10;
            ClientboundMoveEntityPacketHandle.RotHandle lookPacket = ClientboundMoveEntityPacketHandle.RotHandle.createNew((int)entityId, (float)this._mountYaw, (float)pitch, (boolean)false);
            this._entityLastYaw = lookPacket.getYaw();
            this._entityLastPitch = lookPacket.getPitch();
            for (AttachmentViewer viewer : seat.getAttachmentViewers()) {
                if (viewer.getPlayer() == viewerToIgnore) continue;
                viewer.send((PacketHandle)lookPacket);
            }
            float k = 180.0f;
            float f = 10.0f;
            float flippedPitch = this._entityLastPitch >= k ? k + f : k - f;
            ClientboundMoveEntityPacketHandle.RotHandle flipLookPacket = ClientboundMoveEntityPacketHandle.RotHandle.createNew((int)flippedId, (float)this._entityLastYaw, (float)flippedPitch, (boolean)false);
            for (AttachmentViewer viewer : seat.getAttachmentViewers()) {
                if (viewer.getPlayer() == viewerToIgnore) continue;
                viewer.send((PacketHandle)flipLookPacket);
            }
        } else {
            --this._entityRotationCtr;
        }
    }

    protected void synchronizeNormal(CartAttachmentSeat seat, Matrix4x4 transform, SeatedEntityNormal seated, int entityId) {
        Player viewerToIgnore = seated.isPlayer() && !seated.isMadeVisibleInFirstPerson() ? (Player)seated.getEntity() : null;
        SeatedEntity.PassengerPose pose = seated.getCurrentHeadRotation(transform);
        this._mountYaw = pose.bodyYaw;
        if (seat.isRotationLocked()) {
            pose = pose.limitHeadYaw(30.0f);
        }
        SeatedEntity.PassengerPose poseFixed = seated.isUpsideDown() ? pose.upsideDownFix_Pre_1_17() : pose;
        ClientboundRotateHeadPacketHandle headPacket = null;
        ClientboundMoveEntityPacketHandle.RotHandle lookPacket = null;
        if (EntityTrackerEntryStateHandle.hasProtocolRotationChanged((float)pose.headYaw, (float)this._entityLastHeadYaw)) {
            headPacket = ClientboundRotateHeadPacketHandle.createNew((int)entityId, (float)pose.headYaw);
            this._entityLastHeadYaw = headPacket.getHeadYaw();
        }
        if (this._entityRotationCtr == 0 || EntityTrackerEntryStateHandle.hasProtocolRotationChanged((float)pose.bodyYaw, (float)this._entityLastYaw) || EntityTrackerEntryStateHandle.hasProtocolRotationChanged((float)pose.headPitch, (float)this._entityLastPitch)) {
            this._entityRotationCtr = 10;
            lookPacket = ClientboundMoveEntityPacketHandle.RotHandle.createNew((int)entityId, (float)pose.bodyYaw, (float)pose.headPitch, (boolean)false);
            this._entityLastYaw = lookPacket.getYaw();
            this._entityLastPitch = lookPacket.getPitch();
        } else if (seat.isRotationLocked()) {
            --this._entityRotationCtr;
        }
        ClientboundRotateHeadPacketHandle headPacket_1_17_fix = null;
        ClientboundMoveEntityPacketHandle.RotHandle lookPacket_1_17_fix = null;
        if (headPacket != null || lookPacket != null) {
            for (AttachmentViewer viewer : seat.getAttachmentViewers()) {
                if (viewer.getPlayer() == viewerToIgnore) continue;
                if (seated.isUpsideDown() && viewer.evaluateGameVersion("<=", "1.17.1")) {
                    if (headPacket != null) {
                        if (headPacket_1_17_fix == null) {
                            headPacket_1_17_fix = ClientboundRotateHeadPacketHandle.createNew((int)entityId, (float)poseFixed.headYaw);
                        }
                        viewer.send((PacketHandle)headPacket_1_17_fix);
                    }
                    if (lookPacket == null) continue;
                    if (lookPacket_1_17_fix == null) {
                        lookPacket_1_17_fix = ClientboundMoveEntityPacketHandle.RotHandle.createNew((int)entityId, (float)poseFixed.bodyYaw, (float)poseFixed.headPitch, (boolean)false);
                    }
                    viewer.send((PacketHandle)lookPacket_1_17_fix);
                    continue;
                }
                if (headPacket != null) {
                    viewer.send((PacketHandle)headPacket);
                }
                if (lookPacket == null) continue;
                viewer.send((PacketHandle)lookPacket);
            }
        }
    }
}

