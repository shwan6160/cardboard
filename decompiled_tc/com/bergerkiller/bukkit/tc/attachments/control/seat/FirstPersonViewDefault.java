/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.controller.VehicleMountController
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerRotationPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacketHandle
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonView;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewLockMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityHead;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerRotationPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacketHandle;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

public class FirstPersonViewDefault
extends FirstPersonView {
    private VirtualEntity _fakeCameraMount = null;
    private double _playerYawRemainder = 0.0;
    private double _playerPitchRemainder = 0.0;

    public FirstPersonViewDefault(CartAttachmentSeat seat, AttachmentViewer player) {
        super(seat, player);
    }

    public boolean isFakeCameraUsed() {
        if (!this._eyePosition.isDefault()) {
            return true;
        }
        if (this.seat.useSmoothCoasters()) {
            return true;
        }
        switch (this.getLiveMode()) {
            case THIRD_P: {
                return true;
            }
        }
        return this.seat.seated.isFirstPersonCameraFake();
    }

    @Override
    public void makeVisible(AttachmentViewer viewer, boolean isReload) {
        VehicleMountController vmc = viewer.getVehicleMountController();
        boolean useFakeCamera = this.isFakeCameraUsed();
        if (useFakeCamera || this.seat.useSmoothCoasters() || this.seat.isRotationLocked()) {
            Matrix4x4 eyeTransform = this.getEyeTransform();
            if (!isReload && this.seat.useSmoothCoasters()) {
                this.syncSmoothCoastersRotations(eyeTransform, true);
            }
            if (!isReload) {
                if (this.seat.useSmoothCoasters()) {
                    if (this.seat.isRotationLocked()) {
                        this.seat.getPlugin().getSmoothCoastersAPI().setRotationLimit(viewer.getSmoothCoastersNetwork(), viewer.getPlayer(), -70.0f, 70.0f, -90.0f, 90.0f);
                    }
                } else if (this.seat.isRotationLocked()) {
                    FirstPersonView.HeadRotation rot = FirstPersonView.HeadRotation.compute(eyeTransform).ensureLevel();
                    viewer.send((PacketHandle)ClientboundPlayerRotationPacketHandle.createAbsolute((float)rot.yaw, (float)rot.pitch));
                }
            }
            if (useFakeCamera && this._fakeCameraMount == null) {
                this._fakeCameraMount = this.seat.seated.createPassengerVehicle();
                vmc.mount(this._fakeCameraMount.getEntityId(), viewer.getEntityId());
                this._fakeCameraMount.addRelativeOffset(0.0, -1.0, 0.0);
                this._fakeCameraMount.updatePosition(eyeTransform);
                this._fakeCameraMount.syncPosition(true);
                this._fakeCameraMount.spawn(viewer, this.seat.calcMotion());
                viewer.send((PacketHandle)ClientboundUpdateAttributesPacketHandle.createZeroMaxHealth((int)this._fakeCameraMount.getEntityId()));
            }
        }
        if (this.getLiveMode().isRealPlayerInvisible()) {
            FirstPersonViewDefault.setPlayerVisible(viewer, false);
        }
        if (this.getLiveMode() == FirstPersonViewMode.HEAD) {
            FirstPersonViewDefault.sendEquipment(viewer, EquipmentSlot.HEAD, SeatedEntityHead.createSkullItem((Entity)viewer.getPlayer()));
        }
        if (!useFakeCamera) {
            if (this.getLiveMode() != FirstPersonViewMode.STANDING) {
                vmc.mount(this.seat.seated.spawnVehicleMount(viewer), viewer.getEntityId());
            }
        } else if (this.getLiveMode() == FirstPersonViewMode.THIRD_P) {
            this.seat.seated.makeVisibleFirstPerson(viewer);
        }
    }

    @Override
    public void makeHidden(AttachmentViewer viewer, boolean isReload) {
        VehicleMountController vmc = viewer.getVehicleMountController();
        if (!isReload && this.seat.useSmoothCoasters()) {
            this.seat.getPlugin().getSmoothCoastersAPI().resetRotation(viewer.getSmoothCoastersNetwork(), viewer.getPlayer());
            this.seat.getPlugin().getSmoothCoastersAPI().resetRotationLimit(viewer.getSmoothCoastersNetwork(), viewer.getPlayer());
        }
        if (this._fakeCameraMount != null) {
            vmc.unmount(this._fakeCameraMount.getEntityId(), viewer.getEntityId());
            this._fakeCameraMount.destroy(viewer);
            this._fakeCameraMount = null;
        }
        if (!this.isFakeCameraUsed()) {
            vmc.unmount(this.seat.seated.parentMountId, viewer.getEntityId());
        } else if (this.getLiveMode() == FirstPersonViewMode.THIRD_P) {
            this.seat.seated.makeHiddenFirstPerson(viewer);
        }
        if (this.getLiveMode().isRealPlayerInvisible()) {
            FirstPersonViewDefault.setPlayerVisible(viewer, true);
        }
    }

    @Override
    public void onTick() {
        if (this.getLockMode() == FirstPersonViewLockMode.MOVE && this.seat.seated.isPlayer() && !this.seat.useSmoothCoasters()) {
            Location eye_loc = ((Player)this.seat.seated.getEntity()).getEyeLocation();
            Vector player_pyr = new Vector((double)eye_loc.getPitch(), (double)eye_loc.getYaw(), 0.0);
            player_pyr.setX(-player_pyr.getX());
            Quaternion diff = Quaternion.diff((Quaternion)this.seat.getPreviousTransform().getRotation(), (Quaternion)Quaternion.fromYawPitchRoll((Vector)player_pyr));
            Quaternion new_rotation = this.seat.getTransform().getRotation();
            new_rotation.multiply(diff);
            Vector new_pyr = new_rotation.getYawPitchRoll();
            Vector pyr = new_pyr.clone().subtract(player_pyr);
            pyr.setX(pyr.getX() + this._playerPitchRemainder);
            pyr.setY(pyr.getY() + this._playerYawRemainder);
            if (Math.abs(pyr.getX()) > 1.0E-5 || Math.abs(pyr.getY()) > 1.0E-5) {
                if (this.getViewer().supportRelativeRotationUpdate()) {
                    ClientboundPlayerRotationPacketHandle p = ClientboundPlayerRotationPacketHandle.createRelative((float)((float)pyr.getY()), (float)((float)pyr.getX()));
                    this._playerPitchRemainder = pyr.getX() - (double)p.getPitch();
                    this._playerYawRemainder = pyr.getY() - (double)p.getYaw();
                    this.player.send((PacketHandle)p);
                } else {
                    ClientboundPlayerRotationPacketHandle p = ClientboundPlayerRotationPacketHandle.createAbsolute((float)((float)(pyr.getY() + player_pyr.getY())), (float)((float)(pyr.getX() - player_pyr.getX())));
                    this._playerPitchRemainder = 0.0;
                    this._playerYawRemainder = 0.0;
                    this.player.send((PacketHandle)p);
                }
            } else {
                this._playerPitchRemainder = pyr.getX();
                this._playerYawRemainder = pyr.getY();
            }
        }
    }

    @Override
    public void onMove(boolean absolute) {
        if (this._fakeCameraMount != null || this.seat.useSmoothCoasters()) {
            Matrix4x4 eyeTransform = this.getEyeTransform();
            if (this.seat.useSmoothCoasters()) {
                this.syncSmoothCoastersRotations(eyeTransform, false);
            }
            if (this._fakeCameraMount != null) {
                this._fakeCameraMount.updatePosition(eyeTransform);
                this._fakeCameraMount.syncPosition(absolute);
            }
        }
    }

    private void syncSmoothCoastersRotations(Matrix4x4 eyeTransform, boolean instant) {
        this.seat.sendSmoothCoastersRelativeRotation(eyeTransform.getRotation(), instant);
        if (!this.getLiveMode().isRealPlayerInvisible()) {
            // empty if block
        }
    }
}

