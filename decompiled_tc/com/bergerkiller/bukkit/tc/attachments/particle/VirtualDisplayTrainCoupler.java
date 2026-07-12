/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher$Prototype
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPassengersPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle$BlockDisplayHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.EntityType
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualTrainCoupler;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPassengersPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class VirtualDisplayTrainCoupler
extends VirtualTrainCoupler {
    private static final double COUPLER_DIAMETER = 0.2;
    private Vector position;
    private final int mountEntityId = EntityUtil.getUniqueEntityId();
    private final int entityId = EntityUtil.getUniqueEntityId();
    private final UUID entityUUID = UUID.randomUUID();
    private final DataWatcher metadata = LINE_METADATA.create();
    private static final DataWatcher.Prototype LINE_METADATA = DataWatcher.Prototype.build().setClientByteDefault(EntityHandle.DATA_FLAGS, 0).setClientDefault(DisplayHandle.DATA_TRANSLATION, (Object)new Vector()).setClientDefault(DisplayHandle.DATA_LEFT_ROTATION, (Object)new Quaternion()).setClientDefault(DisplayHandle.DATA_SCALE, (Object)new Vector(1, 1, 1)).setClientDefault(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)0).set(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)3).setClientDefault(DisplayHandle.DATA_INTERPOLATION_START_DELTA_TICKS, (Object)0).setClientDefault(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)BlockData.AIR).set(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)BlockData.fromMaterial((Material)MaterialUtil.getMaterial((String)"LIGHT_GRAY_CONCRETE"))).create();

    public VirtualDisplayTrainCoupler(AttachmentManager manager) {
        super(manager);
    }

    @Override
    public void update(Matrix4x4 transform, double length) {
        this.position = transform.toVector();
        Vector v = new Vector(-0.1, 0.0, 0.0);
        transform.getRotation().transformPoint(v);
        this.metadata.forceSet(DisplayHandle.DATA_LEFT_ROTATION, (Object)transform.getRotation());
        this.metadata.forceSet(DisplayHandle.DATA_TRANSLATION, (Object)v);
        this.metadata.forceSet(DisplayHandle.DATA_SCALE, (Object)new Vector(0.2, 0.2, length));
        this.metadata.forceSet(DisplayHandle.DATA_INTERPOLATION_START_DELTA_TICKS, (Object)0);
    }

    @Override
    public void updatePosition(Matrix4x4 transform) {
        throw new UnsupportedOperationException("Must specify a transform with length");
    }

    @Override
    protected void applyGlowing(ChatColor color) {
        byte data = color != null ? (byte)64 : 0;
        this.metadata.set(EntityHandle.DATA_FLAGS, (Object)data);
    }

    @Override
    protected void applyGlowColorForViewer(AttachmentViewer viewer, ChatColor color) {
        viewer.updateGlowColor(this.entityUUID, color);
    }

    @Override
    protected void sendSpawnPackets(AttachmentViewer viewer, Vector motion) {
        ClientboundAddEntityPacketHandle spawnPacket = ClientboundAddEntityPacketHandle.createNew();
        spawnPacket.setEntityId(this.entityId);
        spawnPacket.setEntityUUID(this.entityUUID);
        spawnPacket.setEntityType(VirtualDisplayEntity.BLOCK_DISPLAY_ENTITY_TYPE);
        spawnPacket.setPosX(this.position.getX() - motion.getX());
        spawnPacket.setPosY(this.position.getY() - motion.getY());
        spawnPacket.setPosZ(this.position.getZ() - motion.getZ());
        spawnPacket.setMotX(motion.getX());
        spawnPacket.setMotY(motion.getY());
        spawnPacket.setMotZ(motion.getZ());
        spawnPacket.setYaw(0.0f);
        spawnPacket.setPitch(0.0f);
        viewer.send((PacketHandle)spawnPacket);
        viewer.send((PacketHandle)this.createMetaPacket(true));
        spawnPacket = ClientboundAddMobPacketHandle.createNew();
        spawnPacket.setEntityId(this.mountEntityId);
        spawnPacket.setEntityUUID(UUID.randomUUID());
        spawnPacket.setEntityType(EntityType.ARMOR_STAND);
        spawnPacket.setPosX(this.position.getX() - motion.getX());
        spawnPacket.setPosY(this.position.getY() - motion.getY());
        spawnPacket.setPosZ(this.position.getZ() - motion.getZ());
        spawnPacket.setMotX(motion.getX());
        spawnPacket.setMotY(motion.getY());
        spawnPacket.setMotZ(motion.getZ());
        spawnPacket.setYaw(0.0f);
        spawnPacket.setPitch(0.0f);
        spawnPacket.setHeadYaw(0.0f);
        viewer.sendEntityLivingSpawnPacket((ClientboundAddMobPacketHandle)spawnPacket, VirtualDisplayEntity.ARMORSTAND_MOUNT_METADATA);
        viewer.send((PacketHandle)ClientboundSetPassengersPacketHandle.createNew((int)this.mountEntityId, (int[])new int[]{this.entityId}));
    }

    private ClientboundSetEntityDataPacketHandle createMetaPacket(boolean includeUnchangedData) {
        return ClientboundSetEntityDataPacketHandle.createNew((int)this.entityId, (DataWatcher)this.metadata, (boolean)includeUnchangedData);
    }

    @Override
    protected void sendDestroyPackets(AttachmentViewer viewer) {
        viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewMultiple((int[])new int[]{this.mountEntityId, this.entityId}));
    }

    @Override
    public void syncPosition(boolean absolute) {
        this.broadcast((PacketHandle)this.createMetaPacket(false));
        this.broadcast((PacketHandle)ClientboundEntityPositionSyncPacketHandle.createNew((int)this.mountEntityId, (double)this.position.getX(), (double)this.position.getY(), (double)this.position.getZ(), (float)0.0f, (float)0.0f, (boolean)false));
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return entityId == this.mountEntityId;
    }
}

