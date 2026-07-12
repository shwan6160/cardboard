/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.VirtualArmorStandItemEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.config.transform.ArmorStandItemTransformType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntity;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SeatedEntityHead
extends SeatedEntity {
    private VirtualArmorStandItemEntity skull;

    public SeatedEntityHead(CartAttachmentSeat seat) {
        super(seat);
    }

    public int getSkullEntityId() {
        return this.skull.getEntityId();
    }

    @Override
    public Vector getThirdPersonCameraOffset() {
        return new Vector(0.0, 1.0, 0.0);
    }

    @Override
    public Vector getFirstPersonCameraOffset() {
        return new Vector(0.0, 0.215, 0.0);
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        if (this.isPlayer() || this.isDummyPlayerDisplayed()) {
            if (this.entity != viewer.getPlayer() && !this.isDummyPlayerDisplayed()) {
                this.hideRealPlayer(viewer);
            }
            if (this.skull == null) {
                this.skull = new VirtualArmorStandItemEntity(this.seat.getManager());
                this.skull.setSyncMode(VirtualEntity.SyncMode.ITEM);
                this.skull.setUseMinecartInterpolation(this.seat.isMinecartInterpolation());
                this.skull.setItem(ArmorStandItemTransformType.HEAD, SeatedEntityHead.createSkullItem(this.entity));
                this.skull.getMetaData().set(EntityHandle.DATA_CUSTOM_NAME, (Object)FakePlayerSpawner.UPSIDEDOWN.getPlayerName());
                this.skull.getMetaData().set(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, (Object)false);
                this.updateFocus(this.seat.isFocused());
                this.skull.updatePosition(this.seat.getTransform(), this.getCurrentHeadRotationQuat(this.seat.getTransform()));
                this.skull.syncPosition(true);
            }
            this.skull.spawn(viewer, this.seat.calcMotion());
        } else if (!this.isEmpty()) {
            viewer.getVehicleMountController().mount(this.spawnVehicleMount(viewer), this.entity.getEntityId());
        }
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        if (this.isPlayer() || this.isDummyPlayerDisplayed()) {
            if (this.skull != null) {
                this.skull.destroy(viewer);
                if (!this.skull.hasViewers()) {
                    this.skull = null;
                }
            }
            if (viewer.getPlayer() != this.entity && !this.isDummyPlayerDisplayed()) {
                this.showRealPlayer(viewer);
            }
        } else if (!this.isEmpty()) {
            viewer.getVehicleMountController().unmount(this.parentMountId, this.entity.getEntityId());
            this.despawnVehicleMount(viewer);
        }
    }

    @Override
    public void updatePosition(Matrix4x4 transform) {
        if (this.skull != null) {
            this.skull.updatePosition(transform, this.getCurrentHeadRotationQuat(transform));
            this.skull.syncMetadata();
        }
        this.updateVehicleMountPosition(transform);
    }

    @Override
    public void syncPosition(boolean absolute) {
        if (this.skull != null) {
            this.skull.syncPosition(absolute);
        }
        this.syncVehicleMountPosition(absolute);
    }

    @Override
    public void updateFocus(boolean focused) {
        if (this.skull != null) {
            this.skull.getMetaData().setFlag(EntityHandle.DATA_FLAGS, 65, focused);
            this.skull.getMetaData().setFlag(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 16, focused);
        }
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return this.skull != null && entityId == this.skull.getEntityId();
    }

    public static ItemStack createSkullItem(Entity entity) {
        if (entity == null || entity instanceof Player) {
            GameProfileHandle profile = entity == null ? FakePlayerSpawner.createDummyPlayerProfile() : GameProfileHandle.getForPlayer((HumanEntity)((Player)entity));
            return ItemUtil.createPlayerHeadItem((GameProfileHandle)profile);
        }
        return null;
    }
}

