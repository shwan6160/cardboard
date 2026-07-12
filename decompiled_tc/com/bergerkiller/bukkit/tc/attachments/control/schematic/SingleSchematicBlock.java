/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher$Prototype
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle$BlockDisplayHandle
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.schematic;

import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.util.Vector;

class SingleSchematicBlock {
    private final double x;
    private final double y;
    private final double z;
    private double sx;
    private double sy;
    private double sz;
    private final Vector translation;
    private final int entityId;
    private final UUID entityUUID;
    private final DataWatcher metadata;
    private static final DataWatcher.Prototype BLOCK_METADATA = VirtualDisplayEntity.BASE_DISPLAY_METADATA.modify().set(DisplayHandle.DATA_WIDTH, (Object)Float.valueOf(1.5f)).set(DisplayHandle.DATA_HEIGHT, (Object)Float.valueOf(1.5f)).setClientDefault(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)BlockData.AIR).create();

    public SingleSchematicBlock(double x, double y, double z, BlockData blockData) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sx = x;
        this.sy = y;
        this.sz = z;
        this.translation = new Vector(x, y, z);
        this.entityId = EntityUtil.getUniqueEntityId();
        this.entityUUID = UUID.randomUUID();
        this.metadata = BLOCK_METADATA.create();
        this.metadata.set(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)blockData);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setScaleAndSpacing(Vector scale, Vector origin, Vector spacing) {
        this.sx = scale.getX() * (this.x + spacing.getX() * (this.x + 0.5)) - origin.getX();
        this.sy = scale.getY() * (this.y + spacing.getY() * this.y) - origin.getY();
        this.sz = scale.getZ() * (this.z + spacing.getZ() * (this.z + 0.5)) - origin.getZ();
        this.metadata.set(DisplayHandle.DATA_SCALE, (Object)scale);
    }

    public void setScaleZeroSpacing(Vector scale, Vector origin) {
        this.sx = scale.getX() * this.x - origin.getX();
        this.sy = scale.getY() * this.y - origin.getY();
        this.sz = scale.getZ() * this.z - origin.getZ();
        this.metadata.set(DisplayHandle.DATA_SCALE, (Object)scale);
    }

    public void setClipBox(Float bb) {
        this.metadata.set(DisplayHandle.DATA_WIDTH, (Object)bb);
        this.metadata.set(DisplayHandle.DATA_HEIGHT, (Object)bb);
    }

    public void sync(Quaternion rotation, Iterable<AttachmentViewer> viewers) {
        Vector translation = this.translation;
        MathUtil.setVector((Vector)translation, (double)this.sx, (double)this.sy, (double)this.sz);
        rotation.transformPoint(translation);
        this.metadata.forceSet(DisplayHandle.DATA_TRANSLATION, (Object)translation);
        this.metadata.forceSet(DisplayHandle.DATA_LEFT_ROTATION, (Object)rotation);
        this.metadata.forceSet(DisplayHandle.DATA_INTERPOLATION_START_DELTA_TICKS, (Object)0);
        Iterator<AttachmentViewer> iter = viewers.iterator();
        if (iter.hasNext()) {
            ClientboundSetEntityDataPacketHandle packet = ClientboundSetEntityDataPacketHandle.createNew((int)this.entityId, (DataWatcher)this.metadata, (boolean)false);
            do {
                iter.next().send((PacketHandle)packet);
            } while (iter.hasNext());
        }
    }

    public void spawn(AttachmentViewer viewer, Vector position, Vector motion) {
        ClientboundAddEntityPacketHandle spawnPacket = ClientboundAddEntityPacketHandle.createNew();
        spawnPacket.setEntityId(this.entityId);
        spawnPacket.setEntityUUID(this.entityUUID);
        spawnPacket.setEntityType(VirtualDisplayEntity.BLOCK_DISPLAY_ENTITY_TYPE);
        spawnPacket.setPosX(position.getX());
        spawnPacket.setPosY(position.getY());
        spawnPacket.setPosZ(position.getZ());
        spawnPacket.setMotX(motion.getX());
        spawnPacket.setMotY(motion.getY());
        spawnPacket.setMotZ(motion.getZ());
        spawnPacket.setYaw(0.0f);
        spawnPacket.setPitch(0.0f);
        viewer.send((PacketHandle)spawnPacket);
        viewer.send(ClientboundSetEntityDataPacketHandle.createNew((int)this.entityId, (DataWatcher)this.metadata, (boolean)true).toCommonPacket());
    }
}

