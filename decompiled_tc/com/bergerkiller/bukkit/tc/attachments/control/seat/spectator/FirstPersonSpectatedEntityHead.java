/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.entity.Entity
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat.spectator;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.VirtualArmorStandItemEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.config.transform.ArmorStandItemTransformType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewSpectator;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityHead;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.FirstPersonSpectatedEntity;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.PitchSwappedEntity;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Deprecated
class FirstPersonSpectatedEntityHead
extends FirstPersonSpectatedEntity {
    private final ItemStack skullItem;
    private PitchSwappedEntity<VirtualArmorStandItemEntity> skull;

    public FirstPersonSpectatedEntityHead(CartAttachmentSeat seat, FirstPersonViewSpectator view, AttachmentViewer player) {
        super(seat, view, player);
        this.skullItem = SeatedEntityHead.createSkullItem((Entity)player.getPlayer());
    }

    @Override
    public void start(Matrix4x4 eyeTransform) {
        this.skull = PitchSwappedEntity.create(this.player, () -> {
            VirtualArmorStandItemEntity entity = new VirtualArmorStandItemEntity(this.seat.getManager());
            entity.setSyncMode(VirtualEntity.SyncMode.NORMAL);
            entity.setUseMinecartInterpolation(this.seat.isMinecartInterpolation());
            if (!this.view.getEyePosition().isDefault() || this.view.getLiveMode() == FirstPersonViewMode.THIRD_P) {
                entity.setRelativeOffset(0.0, 0.24, 0.0);
            } else {
                entity.setRelativeOffset(0.0, -0.215, 0.0);
            }
            entity.getMetaData().set(EntityHandle.DATA_CUSTOM_NAME, (Object)FakePlayerSpawner.UPSIDEDOWN.getPlayerName());
            entity.getMetaData().set(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, (Object)false);
            return entity;
        });
        this.skull.beforeSwap(swapped -> {
            ((VirtualArmorStandItemEntity)this.skull.entity).setItem(ArmorStandItemTransformType.HEAD, null);
            swapped.setItem(ArmorStandItemTransformType.HEAD, this.skullItem);
        });
        ((VirtualArmorStandItemEntity)this.skull.entity).setItem(ArmorStandItemTransformType.HEAD, this.skullItem);
        ((VirtualArmorStandItemEntity)this.skull.entityAlt).setItem(ArmorStandItemTransformType.HEAD, null);
        this.skull.spawn(eyeTransform, new Vector());
        this.skull.spectate();
    }

    @Override
    public void stop() {
        this.skull.destroy();
    }

    @Override
    public void updatePosition(Matrix4x4 eyeTransform) {
        this.skull.updatePosition(eyeTransform);
        ((VirtualArmorStandItemEntity)this.skull.entity).syncMetadata();
    }

    @Override
    public void syncPosition(boolean absolute) {
        this.skull.syncPosition(absolute);
    }

    @Override
    public VirtualEntity getCurrentEntity() {
        return this.skull.entity;
    }
}

