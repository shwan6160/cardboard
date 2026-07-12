/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher$Prototype
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.monster.ShulkerHandle
 *  com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.EntityType
 */
package com.bergerkiller.bukkit.tc.attachments.surface;

import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.surface.ShulkerTracker;
import com.bergerkiller.bukkit.tc.attachments.surface.StationaryCollisionElement;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.monster.ShulkerHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import java.util.EnumMap;
import java.util.UUID;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;

final class Shulker
implements StationaryCollisionElement {
    public final ShulkerTracker shulkerTracker;
    public final int mountEntityId = EntityUtil.getUniqueEntityId();
    public final int entityId = EntityUtil.getUniqueEntityId();
    public BlockFace pushDirection;
    public double x;
    public double y;
    public double z;
    public double sync_x;
    public double sync_y;
    public double sync_z;
    private AABBHandle boundingBox = null;
    public boolean picked = false;
    private boolean pendingDestroy = false;
    private boolean pendingSpawn = false;
    private boolean pendingMove = false;
    private static final DataWatcher SHULKER_MOUNT_METADATA = DataWatcher.Prototype.build().setClientByteDefault(EntityHandle.DATA_FLAGS, 0).setClientByteDefault(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 0).setByte(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 25).setByte(EntityHandle.DATA_FLAGS, 32).create().create();
    private static final EnumMap<BlockFace, DataWatcher> SHULKER_METADATA_BY_FACE = new EnumMap(BlockFace.class);

    public Shulker(ShulkerTracker shulkerTracker) {
        this.shulkerTracker = shulkerTracker;
    }

    public void scheduleMovement() {
        if (!this.pendingSpawn && !this.pendingMove) {
            this.pendingMove = true;
            this.shulkerTracker.shulkersToMove.add(this);
        }
    }

    void scheduleSpawn() {
        if (!this.pendingSpawn) {
            this.pendingSpawn = true;
            this.shulkerTracker.shulkersToSpawn.add(this);
        }
    }

    void scheduleDestroy() {
        if (this.pendingSpawn) {
            this.pendingSpawn = false;
        } else if (!this.pendingDestroy) {
            this.pendingDestroy = true;
            this.shulkerTracker.shulkersToDestroy.add(this);
        }
        this.pendingMove = false;
    }

    boolean clearMove() {
        if (this.pendingMove) {
            this.pendingMove = false;
            return false;
        }
        return true;
    }

    boolean clearSpawn() {
        if (this.pendingSpawn) {
            this.pendingSpawn = false;
            return false;
        }
        return true;
    }

    boolean clearDestroy() {
        if (this.pendingDestroy) {
            this.pendingDestroy = false;
            return false;
        }
        return true;
    }

    public void syncPositionSilent() {
        this.sync_x = this.x;
        this.sync_y = this.y;
        this.sync_z = this.z;
    }

    public void syncPosition(AttachmentViewer viewer) {
        if (this.x != this.sync_x || this.y != this.sync_y || this.z != this.sync_z) {
            this.syncPositionSilent();
            ClientboundEntityPositionSyncPacketHandle p = ClientboundEntityPositionSyncPacketHandle.createNew((int)this.mountEntityId, (double)this.x, (double)(this.y - 0.5), (double)this.z, (float)0.0f, (float)0.0f, (boolean)false);
            viewer.send((PacketHandle)p);
        }
    }

    public void invalidateBoundingBox() {
        this.boundingBox = null;
    }

    @Override
    public AABBHandle getBoundingBox() {
        AABBHandle bb = this.boundingBox;
        if (bb == null) {
            this.boundingBox = bb = AABBHandle.createNew((double)(this.x - 0.5), (double)(this.y - 0.5), (double)(this.z - 0.5), (double)(this.x + 0.5), (double)(this.y + 0.5), (double)(this.z + 0.5));
        }
        return bb;
    }

    @Override
    public BlockFace getPushDirection() {
        return this.pushDirection;
    }

    public void spawn(AttachmentViewer viewer) {
        this.syncPositionSilent();
        ClientboundAddMobPacketHandle spawnPacket = ClientboundAddMobPacketHandle.createNew();
        spawnPacket.setEntityId(this.mountEntityId);
        spawnPacket.setEntityUUID(UUID.randomUUID());
        spawnPacket.setEntityType(EntityType.ARMOR_STAND);
        spawnPacket.setPosX(this.x);
        spawnPacket.setPosY(this.y - 0.5);
        spawnPacket.setPosZ(this.z);
        spawnPacket.setMotX(0.0);
        spawnPacket.setMotY(0.0);
        spawnPacket.setMotZ(0.0);
        spawnPacket.setYaw(0.0f);
        spawnPacket.setPitch(0.0f);
        spawnPacket.setHeadYaw(0.0f);
        viewer.sendEntityLivingSpawnPacket(spawnPacket, SHULKER_MOUNT_METADATA);
        DataWatcher shulkerMeta = SHULKER_METADATA_BY_FACE.get(this.pushDirection);
        if (shulkerMeta == null) {
            throw new IllegalStateException("Invalid push direction: " + this.pushDirection);
        }
        ClientboundAddMobPacketHandle spawnPacket2 = ClientboundAddMobPacketHandle.createNew();
        spawnPacket2.setEntityId(this.entityId);
        spawnPacket2.setEntityUUID(UUID.randomUUID());
        spawnPacket2.setEntityType(EntityType.SHULKER);
        spawnPacket2.setPosX(this.x);
        spawnPacket2.setPosY(this.y - 1.0);
        spawnPacket2.setPosZ(this.z);
        spawnPacket2.setMotX(0.0);
        spawnPacket2.setMotY(0.0);
        spawnPacket2.setMotZ(0.0);
        spawnPacket2.setYaw(0.0f);
        spawnPacket2.setPitch(0.0f);
        spawnPacket2.setHeadYaw(0.0f);
        viewer.sendEntityLivingSpawnPacket(spawnPacket2, shulkerMeta);
        viewer.getVehicleMountController().mount(this.mountEntityId, this.entityId);
    }

    static {
        DataWatcher.Prototype SHULKER_METADATA_PROTOTYPE = DataWatcher.Prototype.build().setClientByteDefault(EntityHandle.DATA_FLAGS, 0).setClientDefault(ShulkerHandle.DATA_FACE_DIRECTION, (Object)BlockFace.SOUTH).setByte(EntityHandle.DATA_FLAGS, 128).create();
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            DataWatcher dw = SHULKER_METADATA_PROTOTYPE.create();
            dw.set(ShulkerHandle.DATA_FACE_DIRECTION, (Object)face);
            SHULKER_METADATA_BY_FACE.put(face, dw);
        }
    }
}

