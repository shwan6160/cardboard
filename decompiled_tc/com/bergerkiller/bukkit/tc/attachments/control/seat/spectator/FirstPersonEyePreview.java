/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  org.bukkit.entity.EntityType
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat.spectator;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.PitchSwappedEntity;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import org.bukkit.entity.EntityType;

public class FirstPersonEyePreview {
    public final CartAttachmentSeat seat;
    public final AttachmentViewer player;
    private int remaining = 0;
    private PitchSwappedEntity<VirtualEntity> entity;

    public FirstPersonEyePreview(CartAttachmentSeat seat, AttachmentViewer player) {
        this.seat = seat;
        this.player = player;
    }

    public boolean updateRemaining() {
        if (this.remaining == 1) {
            this.remaining = 0;
            this.handleStop();
            return false;
        }
        if (this.remaining > 1) {
            --this.remaining;
            return true;
        }
        return true;
    }

    public boolean start(int numTicks, Matrix4x4 eyeTransform) {
        if (this.remaining == 0 && numTicks > 0) {
            this.handleStart(eyeTransform);
            this.remaining = numTicks;
            return true;
        }
        if (this.remaining > 0 && numTicks == 0) {
            this.remaining = 0;
            this.handleStop();
            return false;
        }
        this.remaining = numTicks;
        return false;
    }

    public void stop() {
        if (this.remaining > 0) {
            this.remaining = 0;
            this.handleStop();
        }
    }

    private void handleStart(Matrix4x4 eyeTransform) {
        this.entity = PitchSwappedEntity.create(this.player, () -> {
            VirtualEntity entity = new VirtualEntity(this.seat.getManager());
            entity.setEntityType(EntityType.ARMOR_STAND);
            entity.setSyncMode(VirtualEntity.SyncMode.NORMAL);
            entity.setUseMinecartInterpolation(this.seat.isMinecartInterpolation());
            entity.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
            entity.getMetaData().set(LivingEntityHandle.DATA_HEALTH, (Object)Float.valueOf(10.0f));
            entity.getMetaData().set(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, (Object)25);
            return entity;
        });
        this.entity.spawn(eyeTransform, this.seat.calcMotion());
        this.entity.spectate();
    }

    private void handleStop() {
        this.entity.destroy();
    }

    public void updatePosition(Matrix4x4 eyeTransform) {
        this.entity.updatePosition(eyeTransform);
    }

    public void syncPosition(boolean absolute) {
        this.entity.syncPosition(absolute);
    }
}

