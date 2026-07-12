/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.controller.VehicleMountController
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityNormal;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class SeatedEntityStanding
extends SeatedEntityNormal {
    public SeatedEntityStanding(CartAttachmentSeat seat) {
        super(seat);
        this._fake = true;
    }

    @Override
    public Vector getThirdPersonCameraOffset() {
        return new Vector(0.0, 2.2, 0.0);
    }

    @Override
    public Vector getFirstPersonCameraOffset() {
        return new Vector(0.0, 1.62, 0.0);
    }

    @Override
    public boolean isFirstPersonCameraFake() {
        return false;
    }

    @Override
    protected boolean detectFake(boolean new_isUpsideDown, FirstPersonViewMode new_firstPersonMode) {
        return true;
    }

    private void makeFakePlayerVisible(AttachmentViewer viewer) {
        if (this._fakeEntityId == -1) {
            this._fakeEntityId = EntityUtil.getUniqueEntityId();
        }
        Vector fpp_pos = this.seat.getTransform().toVector();
        FakePlayerSpawner.FakePlayerPosition fpp = FakePlayerSpawner.FakePlayerPosition.create(fpp_pos.getX(), fpp_pos.getY(), fpp_pos.getZ(), this.orientation.getPassengerYaw(), this.orientation.getPassengerPitch(), this.orientation.getPassengerHeadYaw());
        if (this._upsideDown) {
            FakePlayerSpawner.UPSIDEDOWN.spawnPlayer(viewer, (Player)this.entity, this._fakeEntityId, fpp, this::applyFakePlayerMetadata);
        } else {
            FakePlayerSpawner.NO_NAMETAG.spawnPlayer(viewer, (Player)this.entity, this._fakeEntityId, fpp, this::applyFakePlayerMetadata);
        }
    }

    private void makeFakePlayerInvisible(AttachmentViewer viewer) {
        VehicleMountController vmc = viewer.getVehicleMountController();
        viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)this._fakeEntityId));
        vmc.remove(this._fakeEntityId);
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        if (this.isDummyPlayer() && this.isEmpty()) {
            this.makeFakePlayerVisible(viewer);
            return;
        }
        if (this.isPlayer()) {
            if (this.entity != viewer.getPlayer()) {
                this.hideRealPlayer(viewer);
            }
            this.makeFakePlayerVisible(viewer);
            return;
        }
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        if (this.isDummyPlayer() && this.isEmpty()) {
            this.makeFakePlayerInvisible(viewer);
            return;
        }
        if (this.isPlayer()) {
            this.makeFakePlayerInvisible(viewer);
            if (this.entity != viewer.getPlayer()) {
                this.showRealPlayer(viewer);
            }
            return;
        }
    }

    @Override
    public void updatePosition(Matrix4x4 transform) {
        super.updatePosition(transform);
        Vector pos = transform.toVector();
        if (this.isUpsideDown()) {
            pos.setY(pos.getY() - 1.95);
        }
        for (AttachmentViewer viewer : this.seat.getAttachmentViewers()) {
            if (viewer.getPlayer() == this.entity && !this.seat.firstPerson.getLiveMode().hasFakePlayer()) continue;
            ClientboundEntityPositionSyncPacketHandle p = ClientboundEntityPositionSyncPacketHandle.createNew((int)this._fakeEntityId, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (float)this.orientation.getPassengerYaw(), (float)this.orientation.getPassengerPitch(), (boolean)false);
            viewer.send((PacketHandle)p);
        }
    }

    @Override
    public void syncPosition(boolean absolute) {
    }

    @Override
    public void updateFocus(boolean focused) {
    }
}

