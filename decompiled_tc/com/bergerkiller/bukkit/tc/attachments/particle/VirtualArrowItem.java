/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.collections.octree.DoubleOctree$Entry
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.protocol.CommonPacket
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.PacketUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEquipmentPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.collections.octree.DoubleOctree;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEquipmentPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class VirtualArrowItem {
    private int entityId;
    private boolean glowing = false;
    private double posX;
    private double posY;
    private double posZ;
    private Vector rotation;
    private ItemStack item;

    private VirtualArrowItem(int entityId) {
        this.entityId = entityId;
    }

    public static VirtualArrowItem create(int entityId) {
        return new VirtualArrowItem(entityId);
    }

    public boolean hasEntityId() {
        return this.entityId != -1;
    }

    public VirtualArrowItem glowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public VirtualArrowItem item(ItemStack item) {
        this.item = item;
        return this;
    }

    public VirtualArrowItem position(DoubleOctree.Entry<?> position, Quaternion orientation) {
        this.rotation = Util.getArmorStandPose(orientation);
        this.rotation.setX(this.rotation.getX() - 90.0);
        this.posX = position.getX() + 0.315;
        this.posY = position.getY() - 1.35;
        this.posZ = position.getZ();
        Vector upVector = new Vector(0.05, -0.05, -0.56);
        orientation.transformPoint(upVector);
        this.posX += upVector.getX();
        this.posY += upVector.getY();
        this.posZ += upVector.getZ();
        return this;
    }

    public VirtualArrowItem position(Vector position, Quaternion orientation) {
        this.rotation = Util.getArmorStandPose(orientation);
        this.rotation.setX(this.rotation.getX() - 90.0);
        this.posX = position.getX() + 0.315;
        this.posY = position.getY() - 1.35;
        this.posZ = position.getZ();
        Vector upVector = new Vector(0.05, -0.05, -0.56);
        orientation.transformPoint(upVector);
        this.posX += upVector.getX();
        this.posY += upVector.getY();
        this.posZ += upVector.getZ();
        return this;
    }

    public VirtualArrowItem move(Iterable<Player> viewers) {
        if (this.entityId != -1) {
            ClientboundEntityPositionSyncPacketHandle tpPacket = ClientboundEntityPositionSyncPacketHandle.createNew((int)this.entityId, (double)this.posX, (double)this.posY, (double)this.posZ, (float)0.0f, (float)0.0f, (boolean)false);
            DataWatcher metadata = new DataWatcher();
            metadata.set(ArmorStandHandle.DATA_POSE_ARM_RIGHT, (Object)this.rotation);
            ClientboundSetEntityDataPacketHandle metaPacket = ClientboundSetEntityDataPacketHandle.createNew((int)this.entityId, (DataWatcher)metadata, (boolean)true);
            for (Player viewer : viewers) {
                PacketUtil.sendPacket((Player)viewer, (PacketHandle)tpPacket);
                PacketUtil.sendPacket((Player)viewer, (CommonPacket)metaPacket.toCommonPacket());
            }
        }
        return this;
    }

    public VirtualArrowItem updateItem(Player viewer) {
        if (this.entityId != -1) {
            ClientboundSetEquipmentPacketHandle equipPacket = Util.createNonPlayerEquipmentPacket(this.entityId, EquipmentSlot.HAND, this.item);
            PacketUtil.sendPacket((Player)viewer, (PacketHandle)equipPacket);
        }
        return this;
    }

    public VirtualArrowItem updateGlowing(Player viewer) {
        if (this.entityId != -1) {
            DataWatcher metadata = new DataWatcher();
            metadata.setByte(EntityHandle.DATA_FLAGS, 160);
            metadata.setFlag(EntityHandle.DATA_FLAGS, 1, Common.evaluateMCVersion((String)">", (String)"1.8"));
            metadata.setByte(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 20);
            if (this.glowing) {
                metadata.setFlag(EntityHandle.DATA_FLAGS, 64, true);
            }
            ClientboundSetEntityDataPacketHandle metaPacket = ClientboundSetEntityDataPacketHandle.createNew((int)this.entityId, (DataWatcher)metadata, (boolean)true);
            PacketUtil.sendPacket((Player)viewer, (CommonPacket)metaPacket.toCommonPacket());
        }
        return this;
    }

    public int spawn(Player viewer) {
        if (this.entityId == -1) {
            this.entityId = EntityUtil.getUniqueEntityId();
        }
        ClientboundAddEntityPacketHandle spawnPacket = ClientboundAddEntityPacketHandle.createNew();
        spawnPacket.setEntityId(this.entityId);
        spawnPacket.setEntityUUID(UUID.randomUUID());
        spawnPacket.setEntityType(EntityType.ARMOR_STAND);
        spawnPacket.setPosX(this.posX);
        spawnPacket.setPosY(this.posY);
        spawnPacket.setPosZ(this.posZ);
        PacketUtil.sendPacket((Player)viewer, (PacketHandle)spawnPacket);
        DataWatcher metadata = new DataWatcher();
        metadata.set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
        metadata.setByte(EntityHandle.DATA_FLAGS, 160);
        metadata.setFlag(EntityHandle.DATA_FLAGS, 1, Common.evaluateMCVersion((String)">", (String)"1.8"));
        metadata.setByte(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 28);
        if (this.glowing) {
            metadata.setFlag(EntityHandle.DATA_FLAGS, 64, true);
        }
        metadata.set(ArmorStandHandle.DATA_POSE_ARM_RIGHT, (Object)this.rotation);
        ClientboundSetEntityDataPacketHandle metaPacket = ClientboundSetEntityDataPacketHandle.createNew((int)this.entityId, (DataWatcher)metadata, (boolean)true);
        PacketUtil.sendPacket((Player)viewer, (CommonPacket)metaPacket.toCommonPacket());
        ClientboundSetEquipmentPacketHandle equipPacket = Util.createNonPlayerEquipmentPacket(this.entityId, EquipmentSlot.HAND, this.item);
        PacketUtil.sendPacket((Player)viewer, (PacketHandle)equipPacket);
        return this.entityId;
    }

    public void destroy(Player viewer) {
        if (this.entityId != -1) {
            PacketUtil.sendPacket((Player)viewer, (PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)this.entityId));
        }
    }
}

