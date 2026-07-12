/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.controller.VehicleMountController
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntity;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.Collection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class SeatedEntityNormal
extends SeatedEntity {
    protected boolean _upsideDown = false;
    protected int _fakeEntityId = -1;
    protected boolean _fake = false;
    private VirtualEntity _upsideDownVehicle = null;

    public SeatedEntityNormal(CartAttachmentSeat seat) {
        super(seat);
    }

    public boolean isUpsideDown() {
        return this._upsideDown;
    }

    public void setUpsideDown(boolean upsideDown) {
        this._upsideDown = upsideDown;
    }

    public boolean isFake() {
        return this._fake;
    }

    public void setFake(boolean fake) {
        this._fake = fake;
    }

    public void refreshUpsideDownMetadata(AttachmentViewer viewer, boolean upsideDown) {
        if (this.isEmpty() || this.isPlayer() || this.isDummyPlayer()) {
            return;
        }
        if (upsideDown) {
            DataWatcher metaTmp = new DataWatcher();
            metaTmp.set(EntityHandle.DATA_CUSTOM_NAME, (Object)FakePlayerSpawner.UPSIDEDOWN.getPlayerName());
            metaTmp.set(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, (Object)false);
            viewer.send(ClientboundSetEntityDataPacketHandle.createNew((int)this.entity.getEntityId(), (DataWatcher)metaTmp, (boolean)true).toCommonPacket());
        } else {
            DataWatcher metaTmp = EntityHandle.fromBukkit((Entity)this.entity).getDataWatcher();
            viewer.send(ClientboundSetEntityDataPacketHandle.createNew((int)this.entity.getEntityId(), (DataWatcher)metaTmp, (boolean)true).toCommonPacket());
        }
    }

    private void makeFakePlayerVisible(AttachmentViewer viewer) {
        if (this._fakeEntityId == -1) {
            this._fakeEntityId = EntityUtil.getUniqueEntityId();
        }
        Vector fpp_pos = this.seat.getTransform().toVector();
        FakePlayerSpawner.FakePlayerPosition fpp = FakePlayerSpawner.FakePlayerPosition.create(fpp_pos.getX(), fpp_pos.getY(), fpp_pos.getZ(), this.orientation.getPassengerYaw(), this.orientation.getPassengerPitch(), this.orientation.getPassengerHeadYaw());
        VehicleMountController vmc = viewer.getVehicleMountController();
        if (this._upsideDown) {
            if (this._upsideDownVehicle == null) {
                this._upsideDownVehicle = this.createPassengerVehicle();
                this._upsideDownVehicle.addRelativeOffset(0.0, -0.65, 0.0);
                this._upsideDownVehicle.updatePosition(this.seat.getTransform(), new Vector(0.0, (double)this.orientation.getMountYaw(), 0.0));
                this._upsideDownVehicle.syncPosition(true);
            }
            this._upsideDownVehicle.spawn(viewer, this.seat.calcMotion());
            FakePlayerSpawner.UPSIDEDOWN.spawnPlayer(viewer, (Player)this.entity, this._fakeEntityId, fpp, this::applyFakePlayerMetadata);
            vmc.mount(this._upsideDownVehicle.getEntityId(), this._fakeEntityId);
        } else {
            FakePlayerSpawner.NO_NAMETAG.spawnPlayer(viewer, (Player)this.entity, this._fakeEntityId, fpp, this::applyFakePlayerMetadata);
            vmc.mount(this.parentMountId, this._fakeEntityId);
        }
        if (this.seat.isRotationLocked()) {
            this.orientation.sendLockedRotations(viewer, this._fakeEntityId);
        }
    }

    protected void applyFakePlayerMetadata(DataWatcher metadata) {
        metadata.setFlag(EntityHandle.DATA_FLAGS, 128, false);
        metadata.setFlag(EntityHandle.DATA_FLAGS, 64, this.isDummyPlayer() && this.seat.isFocused());
    }

    private void makeFakePlayerInvisible(AttachmentViewer viewer) {
        VehicleMountController vmc = viewer.getVehicleMountController();
        viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)this._fakeEntityId));
        vmc.remove(this._fakeEntityId);
        if (this._upsideDown && this._upsideDownVehicle != null) {
            this._upsideDownVehicle.destroy(viewer);
            vmc.remove(this._upsideDownVehicle.getEntityId());
        }
    }

    @Override
    public Vector getThirdPersonCameraOffset() {
        return new Vector(0.0, 1.6, 0.0);
    }

    @Override
    public Vector getFirstPersonCameraOffset() {
        return new Vector(0.0, 1.0, 0.0);
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        this.spawnVehicleMount(viewer);
        if (this.isDummyPlayer() && this.isEmpty()) {
            this.makeFakePlayerVisible(viewer);
        } else if (this.entity == viewer.getPlayer()) {
            this.makeFakePlayerVisible(viewer);
        } else if (this._fake && this.isPlayer()) {
            viewer.getVehicleMountController().despawn(this.entity.getEntityId());
            this.makeFakePlayerVisible(viewer);
        } else if (!this.isEmpty()) {
            if (this._upsideDown) {
                this.refreshUpsideDownMetadata(viewer, true);
            }
            viewer.getVehicleMountController().mount(this.parentMountId, this.entity.getEntityId());
        }
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        if (this.isDummyPlayer() && this.isEmpty()) {
            this.makeFakePlayerInvisible(viewer);
        } else if (this.entity == viewer.getPlayer()) {
            this.makeFakePlayerInvisible(viewer);
        } else if (this._fake && this.isPlayer()) {
            this.makeFakePlayerInvisible(viewer);
            this.showRealPlayer(viewer);
        } else if (!this.isEmpty()) {
            if (this._upsideDown) {
                this.refreshUpsideDownMetadata(viewer, false);
            }
            viewer.getVehicleMountController().unmount(this.parentMountId, this.entity.getEntityId());
        }
        this.despawnVehicleMount(viewer);
    }

    protected boolean detectFake(boolean new_isUpsideDown, FirstPersonViewMode new_firstPersonMode) {
        boolean noNametag = this.displayMode == SeatedEntity.DisplayMode.NO_NAMETAG;
        return this.isDummyPlayer() || this.isPlayer() && (noNametag || new_isUpsideDown || new_firstPersonMode.hasFakePlayer());
    }

    @Override
    public void updateMode(boolean silent) {
        boolean new_isUpsideDown;
        boolean new_isFake;
        FirstPersonViewMode new_firstPersonMode = FirstPersonViewMode.DEFAULT;
        if (!this.isDisplayed()) {
            new_isFake = false;
            new_isUpsideDown = false;
        } else if (this.seat.getTransform() == null && this.isDummyPlayer()) {
            new_isFake = true;
            new_isUpsideDown = false;
            silent = true;
        } else {
            Quaternion rotation = this.seat.getTransform().getRotation();
            double selfPitch = rotation.getPitch();
            new_isUpsideDown = this.isUpsideDown();
            if (MathUtil.getAngleDifference((double)selfPitch, (double)180.0) < 89.0) {
                new_isUpsideDown = true;
            } else if (MathUtil.getAngleDifference((double)selfPitch, (double)0.0) < 89.0) {
                new_isUpsideDown = false;
            }
            new_firstPersonMode = this.seat.firstPerson.getMode();
            if (new_firstPersonMode == FirstPersonViewMode.DYNAMIC) {
                new_firstPersonMode = TCConfig.enableSeatThirdPersonView && this.isPlayer() && Math.abs(selfPitch) > 70.0 ? FirstPersonViewMode.THIRD_P : FirstPersonViewMode.DEFAULT;
            }
            new_isFake = this.detectFake(new_isUpsideDown, new_firstPersonMode);
        }
        if (silent) {
            this.setFake(new_isFake);
            this.setUpsideDown(new_isUpsideDown);
            this.seat.firstPerson.setLiveMode(new_firstPersonMode);
            return;
        }
        if (new_isFake != this.isFake() || this.isPlayer() && new_isUpsideDown != this.isUpsideDown()) {
            boolean refreshFPV = this.seat.firstPerson.doesViewModeChangeRequireReset(new_firstPersonMode);
            Entity entity = this.getEntity();
            Collection<AttachmentViewer> viewers = this.seat.getAttachmentViewersSynced();
            for (AttachmentViewer viewer : viewers) {
                if (!refreshFPV && viewer.getPlayer() == entity) continue;
                this.seat.makeHiddenImpl(viewer, true);
            }
            this.setFake(new_isFake);
            this.setUpsideDown(new_isUpsideDown);
            this.seat.firstPerson.setLiveMode(new_firstPersonMode);
            for (AttachmentViewer viewer : viewers) {
                if (!refreshFPV && viewer.getPlayer() == entity) continue;
                this.seat.makeVisibleImpl(viewer, true);
            }
        } else {
            if (new_isUpsideDown != this.isUpsideDown()) {
                this.setUpsideDown(new_isUpsideDown);
                if (!this.isEmpty()) {
                    for (AttachmentViewer viewer : this.seat.getAttachmentViewersSynced()) {
                        this.refreshUpsideDownMetadata(viewer, new_isUpsideDown);
                    }
                }
            }
            if (new_firstPersonMode != this.seat.firstPerson.getLiveMode()) {
                Collection<AttachmentViewer> viewers = this.seat.getAttachmentViewersSynced();
                if (this.isPlayer() && viewers.contains(this.seat.firstPerson.player)) {
                    this.seat.makeHiddenImpl(this.seat.firstPerson.player, true);
                    this.seat.firstPerson.setLiveMode(new_firstPersonMode);
                    this.seat.makeVisibleImpl(this.seat.firstPerson.player, true);
                } else {
                    this.seat.firstPerson.setLiveMode(new_firstPersonMode);
                }
            }
        }
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return entityId == this._fakeEntityId;
    }

    @Override
    public void updatePosition(Matrix4x4 transform) {
        if (this.isDisplayed()) {
            int entityId = this._fake ? this._fakeEntityId : (this.entity == null ? -1 : this.entity.getEntityId());
            this.orientation.synchronizeNormal(this.seat, transform, this, entityId);
        }
        this.updateVehicleMountPosition(transform);
        if (this._upsideDownVehicle != null) {
            this._upsideDownVehicle.updatePosition(transform, new Vector(0.0, (double)this.orientation.getMountYaw(), 0.0));
        }
    }

    @Override
    public void syncPosition(boolean absolute) {
        this.syncVehicleMountPosition(absolute);
        if (this._upsideDownVehicle != null) {
            this._upsideDownVehicle.syncPosition(absolute);
        }
    }

    @Override
    public void updateFocus(boolean focused) {
        if (this._fakeEntityId != -1 && this.isDisplayed()) {
            DataWatcher metadata;
            if (this.isPlayer()) {
                metadata = EntityUtil.getDataWatcher((Entity)this.entity).clone();
            } else {
                metadata = new DataWatcher();
                metadata.set(EntityHandle.DATA_FLAGS, (Object)0);
            }
            this.applyFakePlayerMetadata(metadata);
            int id = this.entity != null ? this.entity.getEntityId() : this._fakeEntityId;
            for (AttachmentViewer viewer : this.seat.getAttachmentViewersSynced()) {
                viewer.send(ClientboundSetEntityDataPacketHandle.createNew((int)id, (DataWatcher)metadata, (boolean)true).toCommonPacket());
            }
        }
    }
}

