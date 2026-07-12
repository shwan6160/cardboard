/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.EntityType
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.config.transform.ArmorStandItemTransformType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class VirtualArmorStandItemEntity
extends VirtualEntity
implements VirtualSpawnableObject.ItemDisplay {
    private ArmorStandItemTransformType transformType;
    private ItemStack item;
    private Quaternion last_rot;

    public VirtualArmorStandItemEntity(AttachmentManager manager) {
        super(manager);
        this.setEntityType(EntityType.ARMOR_STAND);
        this.setSyncMode(VirtualEntity.SyncMode.ITEM);
        this.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
        this.getMetaData().setFlag(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 4, false);
        this.getMetaData().setFlag(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 8, true);
        this.transformType = ArmorStandItemTransformType.HEAD;
        this.item = null;
        this.last_rot = null;
    }

    public ArmorStandItemTransformType getTransformType() {
        return this.transformType;
    }

    @Override
    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public void setItem(ItemStack item) {
        this.setItem(this.transformType, item);
    }

    public void setItem(ArmorStandItemTransformType transformType, ItemStack item) {
        if (!LogicUtil.bothNullOrEqual((Object)item, (Object)this.item) || this.transformType != transformType) {
            if (this.item != null) {
                this.broadcast((PacketHandle)this.transformType.createEquipmentPacket(this.getEntityId(), null));
            }
            this.transformType = transformType;
            this.item = item;
            if (this.item != null) {
                this.broadcast((PacketHandle)this.transformType.createEquipmentPacket(this.getEntityId(), this.item));
            }
            this.getMetaData().setFlag(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 1, transformType.isSmallArmorStand());
        }
    }

    @Override
    public void updatePosition(Matrix4x4 transform) {
        this.updatePosition(transform, transform.getRotation());
    }

    public void updatePosition(Matrix4x4 transform, Quaternion rotation) {
        double yaw_change;
        if (this.last_rot != null) {
            Quaternion changes = rotation.clone();
            changes.divide(this.last_rot);
            yaw_change = Util.fastGetRotationYaw(changes);
        } else {
            yaw_change = 0.0;
        }
        this.last_rot = rotation;
        Vector new_entity_ypr = this.getYawPitchRoll().clone();
        new_entity_ypr.setY(Util.getNextEntityYaw((float)new_entity_ypr.getY(), yaw_change));
        this.updateArmorStandPosition(transform, new_entity_ypr, rotation);
    }

    @Override
    public void updatePosition(Matrix4x4 transform, Vector yawPitchRoll) {
        this.updateArmorStandPosition(transform, yawPitchRoll, transform.getRotation());
    }

    private void updateArmorStandPosition(Matrix4x4 transform, Vector entityYawPitchRoll, Quaternion poseOrientation) {
        Quaternion q = new Quaternion();
        q.rotateY(entityYawPitchRoll.getY());
        poseOrientation = Quaternion.multiply((Quaternion)q, (Quaternion)poseOrientation);
        double hor_offset = this.transformType.getArmorStandHorizontalOffset();
        double ver_offset = this.transformType.getArmorStandVerticalOffset();
        Vector original_offset = super.getRelativeOffset().clone();
        if (hor_offset != 0.0) {
            this.addRelativeOffset(-hor_offset * Math.cos(Math.toRadians(entityYawPitchRoll.getY())), -ver_offset, -hor_offset * Math.sin(Math.toRadians(entityYawPitchRoll.getY())));
        } else {
            this.addRelativeOffset(0.0, -ver_offset, 0.0);
        }
        super.updatePosition(transform, entityYawPitchRoll);
        this.setRelativeOffset(original_offset);
        Vector rotation = Util.getArmorStandPose(poseOrientation);
        DataWatcher meta = this.getMetaData();
        if (this.transformType.isHead()) {
            meta.set(ArmorStandHandle.DATA_POSE_HEAD, (Object)rotation);
        } else if (this.transformType == ArmorStandItemTransformType.CHEST || this.transformType == ArmorStandItemTransformType.SMALL_CHEST) {
            meta.set(ArmorStandHandle.DATA_POSE_BODY, (Object)rotation);
        } else if (this.transformType.isLeftHand()) {
            rotation.setX(rotation.getX() - 90.0);
            meta.set(ArmorStandHandle.DATA_POSE_ARM_LEFT, (Object)rotation);
        } else if (this.transformType.isRightHand()) {
            rotation.setX(rotation.getX() - 90.0);
            meta.set(ArmorStandHandle.DATA_POSE_ARM_RIGHT, (Object)rotation);
        } else if (this.transformType.isLeg()) {
            meta.set(ArmorStandHandle.DATA_POSE_LEG_LEFT, (Object)rotation);
            meta.set(ArmorStandHandle.DATA_POSE_LEG_RIGHT, (Object)rotation);
        }
        this.syncMetadata();
    }

    @Override
    protected void sendSpawnPackets(AttachmentViewer viewer, Vector motion) {
        super.sendSpawnPackets(viewer, motion);
        if (this.item != null) {
            viewer.send((PacketHandle)this.transformType.createEquipmentPacket(this.getEntityId(), this.item));
        }
    }

    @Override
    protected void applyGlowing(ChatColor color) {
        this.getMetaData().setFlag(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 16, color != null);
        this.getMetaData().setFlag(EntityHandle.DATA_FLAGS, 65, color != null);
        this.syncMetadata();
    }
}

