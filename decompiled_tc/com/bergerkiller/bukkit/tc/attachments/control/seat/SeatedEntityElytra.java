/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.controller.VehicleMountController
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntity;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class SeatedEntityElytra
extends SeatedEntity {
    private int _fakeEntityId = -1;
    private int _fakeEntityIdFlipped = -1;
    private VirtualEntity fakeVehicle = null;
    private Vector fakeVehicleInitialOffset = new Vector();

    public SeatedEntityElytra(CartAttachmentSeat seat) {
        super(seat);
    }

    protected int getFakePlayerId() {
        return this._fakeEntityId;
    }

    protected int getFlippedFakePlayerId() {
        return this._fakeEntityIdFlipped;
    }

    public void flipFakes(CartAttachmentSeat seat) {
        if (this._fakeEntityId == -1 || this._fakeEntityIdFlipped == -1) {
            return;
        }
        this.sendUpdatedMetadata(this._fakeEntityId, true);
        this.sendUpdatedMetadata(this._fakeEntityIdFlipped, false);
        int old = this._fakeEntityId;
        this._fakeEntityId = this._fakeEntityIdFlipped;
        this._fakeEntityIdFlipped = old;
    }

    private void sendUpdatedMetadata(int entityId, boolean invisible) {
        DataWatcher meta = new DataWatcher();
        this.getMetadataFunction(invisible).accept(meta);
        ClientboundSetEntityDataPacketHandle packet = ClientboundSetEntityDataPacketHandle.createNew((int)entityId, (DataWatcher)meta, (boolean)true);
        for (AttachmentViewer viewer : this.seat.getAttachmentViewers()) {
            if (this.entity == viewer.getPlayer() && !this.isDummyPlayer() && !this.isMadeVisibleInFirstPerson()) continue;
            viewer.send((PacketHandle)packet);
        }
    }

    public void makeFakePlayerVisible(AttachmentViewer viewer) {
        if (this.fakeVehicle == null) {
            this.fakeVehicle = this.createPassengerVehicle();
            MathUtil.setVector((Vector)this.fakeVehicleInitialOffset, (Vector)this.fakeVehicle.getRelativeOffset());
            this.fakeVehicle.addRelativeOffset(this.orientation.computeElytraRelativeOffset(this.seat.getTransform().getYawPitchRoll()));
            this.fakeVehicle.updatePosition(this.seat.getTransform());
            this.fakeVehicle.syncPosition(true);
        }
        this.fakeVehicle.spawn(viewer, this.seat.calcMotion());
        if (this._fakeEntityId == -1) {
            this._fakeEntityId = EntityUtil.getUniqueEntityId();
        }
        if (this._fakeEntityIdFlipped == -1) {
            this._fakeEntityIdFlipped = EntityUtil.getUniqueEntityId();
        }
        FakePlayerSpawner.FakePlayerPosition fpp = FakePlayerSpawner.FakePlayerPosition.create(this.fakeVehicle.getPosX(), this.fakeVehicle.getPosY(), this.fakeVehicle.getPosZ(), this.orientation.getPassengerYaw(), this.orientation.getPassengerPitch(), this.orientation.getPassengerHeadYaw());
        VehicleMountController vmc = viewer.getVehicleMountController();
        Consumer<DataWatcher> metaFunction = this.getMetadataFunction(false);
        FakePlayerSpawner.NO_NAMETAG_SECONDARY.spawnPlayer(viewer, (Player)this.entity, this._fakeEntityId, fpp, metaFunction);
        vmc.mount(this.fakeVehicle.getEntityId(), this._fakeEntityId);
        Consumer<DataWatcher> metaFunctionFlipped = this.getMetadataFunction(true);
        FakePlayerSpawner.NO_NAMETAG.spawnPlayer(viewer, (Player)this.entity, this._fakeEntityIdFlipped, fpp.atOppositePitchBoundary(), metaFunctionFlipped);
        vmc.mount(this.fakeVehicle.getEntityId(), this._fakeEntityIdFlipped);
        if (this.seat.isRotationLocked()) {
            this.orientation.sendLockedRotations(viewer, this._fakeEntityId);
        }
    }

    public void makeFakePlayerHidden(AttachmentViewer viewer) {
        if (this._fakeEntityId != -1 && (this.isPlayer() || this.isDummyPlayer())) {
            viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)this._fakeEntityId));
            viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)this._fakeEntityIdFlipped));
            VehicleMountController vmc = viewer.getVehicleMountController();
            if (this.fakeVehicle != null) {
                this.fakeVehicle.destroy(viewer);
                vmc.remove(this.fakeVehicle.getEntityId());
                if (!this.fakeVehicle.hasViewers()) {
                    this.fakeVehicle = null;
                }
            }
            vmc.remove(this._fakeEntityId);
            vmc.remove(this._fakeEntityIdFlipped);
        }
    }

    private Consumer<DataWatcher> getMetadataFunction(boolean invisible) {
        return metadata -> {
            metadata.setFlag(EntityHandle.DATA_FLAGS, 128, true);
            metadata.setFlag(EntityHandle.DATA_FLAGS, 32, invisible);
            metadata.setFlag(EntityHandle.DATA_FLAGS, 64, !invisible && this.seat.isFocused());
        };
    }

    @Override
    public Vector getThirdPersonCameraOffset() {
        return new Vector(0.0, 1.4, 0.0);
    }

    @Override
    public Vector getFirstPersonCameraOffset() {
        return new Vector(0.0, 1.0, 0.0);
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        if (this.isPlayer()) {
            if (this.entity != viewer.getPlayer() && !this.isDummyPlayer()) {
                this.hideRealPlayer(viewer);
            }
            this.makeFakePlayerVisible(viewer);
        } else if (!this.isEmpty()) {
            viewer.getVehicleMountController().mount(this.spawnVehicleMount(viewer), this.entity.getEntityId());
        } else if (this.isDummyPlayer()) {
            this.makeFakePlayerVisible(viewer);
        }
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        if (this.isPlayer()) {
            this.makeFakePlayerHidden(viewer);
            if (this.entity != viewer.getPlayer()) {
                this.showRealPlayer(viewer);
            }
        } else if (!this.isEmpty()) {
            viewer.getVehicleMountController().unmount(this.parentMountId, this.entity.getEntityId());
            this.despawnVehicleMount(viewer);
        } else if (this.isDummyPlayer()) {
            this.makeFakePlayerHidden(viewer);
        }
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return entityId == this._fakeEntityId;
    }

    @Override
    public void updatePosition(Matrix4x4 transform) {
        if (this.isDisplayed()) {
            Vector pyr = transform.getYawPitchRoll();
            this.orientation.synchronizeElytra(this.seat, transform, pyr, this);
            if (this.fakeVehicle != null) {
                this.fakeVehicle.setRelativeOffset(this.fakeVehicleInitialOffset);
                this.fakeVehicle.addRelativeOffset(this.orientation.computeElytraRelativeOffset(pyr));
                this.fakeVehicle.updatePosition(transform, new Vector(0.0, (double)this.orientation.getMountYaw(), 0.0));
            }
        }
        this.updateVehicleMountPosition(transform);
    }

    @Override
    public void syncPosition(boolean absolute) {
        if (this.fakeVehicle != null) {
            this.fakeVehicle.syncPosition(absolute);
        }
        this.syncVehicleMountPosition(absolute);
    }

    @Override
    public void updateFocus(boolean focused) {
        if (this._fakeEntityId != -1 && this._fakeEntityIdFlipped != -1) {
            this.sendUpdatedMetadata(this._fakeEntityId, false);
            this.sendUpdatedMetadata(this._fakeEntityIdFlipped, true);
        }
    }
}

