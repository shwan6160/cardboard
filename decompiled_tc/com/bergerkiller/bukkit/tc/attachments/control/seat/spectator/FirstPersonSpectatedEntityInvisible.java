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
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewSpectator;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.FirstPersonSpectatedEntity;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.PitchSwappedEntity;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import org.bukkit.entity.EntityType;

class FirstPersonSpectatedEntityInvisible
extends FirstPersonSpectatedEntity {
    private PitchSwappedEntity<VirtualEntity> entity;

    public FirstPersonSpectatedEntityInvisible(CartAttachmentSeat seat, FirstPersonViewSpectator view, AttachmentViewer player) {
        super(seat, view, player);
    }

    @Override
    public void start(Matrix4x4 eyeTransform) {
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

    @Override
    public void stop() {
        this.entity.destroy();
    }

    @Override
    public void updatePosition(Matrix4x4 eyeTransform) {
        this.entity.updatePosition(eyeTransform);
    }

    @Override
    public void syncPosition(boolean absolute) {
        this.entity.syncPosition(absolute);
    }

    @Override
    public VirtualEntity getCurrentEntity() {
        return this.entity.entity;
    }
}

