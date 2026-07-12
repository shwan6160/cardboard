/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerRotationPacketHandle
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonView;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewDefault;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerRotationPacketHandle;
import org.bukkit.util.Vector;

public class FirstPersonViewStanding
extends FirstPersonViewDefault {
    private AttachmentViewer.MovementController _movementControl = null;

    public FirstPersonViewStanding(CartAttachmentSeat seat, AttachmentViewer player) {
        super(seat, player);
    }

    @Override
    public boolean isFakeCameraUsed() {
        return false;
    }

    @Override
    public void makeVisible(AttachmentViewer viewer, boolean isReload) {
        if (!isReload && this.seat.isRotationLocked()) {
            FirstPersonView.HeadRotation rot = FirstPersonView.HeadRotation.compute(this.getEyeTransform()).ensureLevel();
            viewer.send((PacketHandle)ClientboundPlayerRotationPacketHandle.createAbsolute((float)rot.yaw, (float)rot.pitch));
        }
        this.updateVelocityControl();
    }

    @Override
    public void makeHidden(AttachmentViewer viewer, boolean isReload) {
        if (this._movementControl != null) {
            this._movementControl.stop();
            this._movementControl = null;
        }
    }

    @Override
    public void onTick() {
        this.updateVelocityControl();
        super.onTick();
    }

    private void updateVelocityControl() {
        Vector pos;
        if (this._movementControl == null) {
            this._movementControl = this.getViewer().controlMovement(AttachmentViewer.MovementController.Options.create().preserveInput(true));
        }
        if (this._eyePosition.isDefault()) {
            pos = this.seat.getTransform().toVector();
        } else {
            pos = this.getEyeTransform().toVector();
            pos.setY(pos.getY() - 1.62);
        }
        this._movementControl.update(pos);
    }
}

