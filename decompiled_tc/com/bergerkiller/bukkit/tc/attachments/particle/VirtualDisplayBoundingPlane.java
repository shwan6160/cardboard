/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
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

import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualBoundingBox;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPassengersPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class VirtualDisplayBoundingPlane
extends VirtualBoundingBox {
    private final Material solidFloorMaterial;
    private final int mountEntityId;
    private final List<Part> parts;
    private final int[] allEntityIds;
    private final List<UUID> allEntityUUIDs;
    private final Vector position = new Vector();
    private final Vector size = new Vector();
    private final Quaternion rotation = new Quaternion();

    public VirtualDisplayBoundingPlane(AttachmentManager manager) {
        this(manager, null);
    }

    public VirtualDisplayBoundingPlane(AttachmentManager manager, Material solidFloorMaterial) {
        super(manager);
        this.solidFloorMaterial = solidFloorMaterial;
        this.mountEntityId = EntityUtil.getUniqueEntityId();
        ArrayList<Part> tmp = new ArrayList<Part>(12);
        this.loadParts(tmp);
        tmp.trimToSize();
        this.parts = Collections.unmodifiableList(tmp);
        this.allEntityIds = this.parts.stream().mapToInt(l -> l.entityId).toArray();
        this.allEntityUUIDs = this.parts.stream().map(l -> l.entityUUID).collect(Collectors.toList());
    }

    protected void loadParts(List<Part> parts) {
        if (this.solidFloorMaterial != null) {
            parts.add(new Platform(this.solidFloorMaterial, t -> t.applyPlatformTransform()));
        }
        parts.add(Line.transform(t -> t.applyPosition(0.0, 0.0, 1.0).applyScaleX(1.0)));
        parts.add(Line.transform(t -> t.applyPosition(0.0, 0.0, 0.0).applyScaleX(1.0)));
        parts.add(Line.transform(t -> t.applyPosition(1.0, 0.0, 0.0).applyScaleZ(1.0)));
        parts.add(Line.transform(t -> t.applyPosition(0.0, 0.0, 0.0).applyScaleZ(1.0)));
    }

    @Override
    public void update(OrientedBoundingBox boundingBox) {
        MathUtil.setVector((Vector)this.position, (Vector)boundingBox.getPosition());
        MathUtil.setVector((Vector)this.size, (Vector)boundingBox.getSize());
        this.rotation.setTo(boundingBox.getOrientation());
        double minSize = 0.02 * Util.absMinAxis(this.size);
        double lineThickness = MathUtil.clamp((double)minSize, (double)0.02, (double)0.3);
        for (Part part : this.parts) {
            PartTransformer transformer = new PartTransformer(part.metadata, lineThickness);
            part.transform.accept(transformer);
        }
    }

    @Override
    protected void sendSpawnPackets(AttachmentViewer viewer, Vector motion) {
        for (Part part : this.parts) {
            part.spawn(viewer, this.position, motion);
        }
        ClientboundAddMobPacketHandle spawnPacket = ClientboundAddMobPacketHandle.createNew();
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
        viewer.sendEntityLivingSpawnPacket(spawnPacket, VirtualDisplayEntity.ARMORSTAND_MOUNT_METADATA);
        viewer.send((PacketHandle)ClientboundSetPassengersPacketHandle.createNew((int)this.mountEntityId, (int[])this.allEntityIds));
    }

    @Override
    protected void sendDestroyPackets(AttachmentViewer viewer) {
        int[] ids = Arrays.copyOf(this.allEntityIds, this.allEntityIds.length + 1);
        ids[ids.length - 1] = this.mountEntityId;
        viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewMultiple((int[])ids));
    }

    @Override
    protected void applyGlowing(ChatColor color) {
        byte data = color != null ? (byte)64 : 0;
        for (Part part : this.parts) {
            if (!(part instanceof Line)) continue;
            part.metadata.set(EntityHandle.DATA_FLAGS, (Object)data);
        }
    }

    @Override
    protected void applyGlowColorForViewer(AttachmentViewer viewer, ChatColor color) {
        viewer.updateGlowColor(this.allEntityUUIDs, color);
    }

    @Override
    public void syncPosition(boolean absolute) {
        for (Part part : this.parts) {
            this.broadcast((PacketHandle)part.createMetaPacket(false));
        }
        this.broadcast((PacketHandle)ClientboundEntityPositionSyncPacketHandle.createNew((int)this.mountEntityId, (double)this.position.getX(), (double)this.position.getY(), (double)this.position.getZ(), (float)0.0f, (float)0.0f, (boolean)false));
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return entityId == this.mountEntityId;
    }

    protected static class Platform
    extends Part {
        private static final DataWatcher.Prototype PLATFORM_METADATA = DataWatcher.Prototype.build().setClientByteDefault(EntityHandle.DATA_FLAGS, 0).setClientDefault(DisplayHandle.DATA_TRANSLATION, (Object)new Vector()).setClientDefault(DisplayHandle.DATA_LEFT_ROTATION, (Object)new Quaternion()).setClientDefault(DisplayHandle.DATA_SCALE, (Object)new Vector(1, 1, 1)).setClientDefault(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)0).set(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)3).setClientDefault(DisplayHandle.DATA_INTERPOLATION_START_DELTA_TICKS, (Object)0).setClientDefault(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)BlockData.AIR).set(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)BlockData.fromMaterial((Material)MaterialUtil.getMaterial((String)"BLACK_CONCRETE"))).create();

        public Platform(Material material, Consumer<PartTransformer> transform) {
            super(PLATFORM_METADATA.create(), transform);
            this.metadata.set(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)BlockData.fromMaterial((Material)material));
        }
    }

    protected static class Line
    extends Part {
        private static final DataWatcher.Prototype LINE_METADATA = DataWatcher.Prototype.build().setClientByteDefault(EntityHandle.DATA_FLAGS, 0).setClientDefault(DisplayHandle.DATA_TRANSLATION, (Object)new Vector()).setClientDefault(DisplayHandle.DATA_LEFT_ROTATION, (Object)new Quaternion()).setClientDefault(DisplayHandle.DATA_SCALE, (Object)new Vector(1, 1, 1)).setClientDefault(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)0).set(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)3).setClientDefault(DisplayHandle.DATA_INTERPOLATION_START_DELTA_TICKS, (Object)0).setClientDefault(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)BlockData.AIR).set(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)BlockData.fromMaterial((Material)MaterialUtil.getMaterial((String)"BLACK_CONCRETE"))).create();

        public static Line transform(Consumer<PartTransformer> transform) {
            return new Line(transform);
        }

        private Line(Consumer<PartTransformer> transform) {
            super(LINE_METADATA.create(), transform);
        }
    }

    protected static abstract class Part {
        public final Consumer<PartTransformer> transform;
        public final int entityId;
        public final UUID entityUUID;
        public final DataWatcher metadata;

        public Part(DataWatcher metadata, Consumer<PartTransformer> transform) {
            this.transform = transform;
            this.entityId = EntityUtil.getUniqueEntityId();
            this.entityUUID = UUID.randomUUID();
            this.metadata = metadata;
        }

        public void spawn(AttachmentViewer viewer, Vector position, Vector motion) {
            ClientboundAddEntityPacketHandle spawnPacket = ClientboundAddEntityPacketHandle.createNew();
            spawnPacket.setEntityId(this.entityId);
            spawnPacket.setEntityUUID(this.entityUUID);
            spawnPacket.setEntityType(VirtualDisplayEntity.BLOCK_DISPLAY_ENTITY_TYPE);
            spawnPacket.setPosX(position.getX() - motion.getX());
            spawnPacket.setPosY(position.getY() - motion.getY());
            spawnPacket.setPosZ(position.getZ() - motion.getZ());
            spawnPacket.setMotX(motion.getX());
            spawnPacket.setMotY(motion.getY());
            spawnPacket.setMotZ(motion.getZ());
            spawnPacket.setYaw(0.0f);
            spawnPacket.setPitch(0.0f);
            viewer.send((PacketHandle)spawnPacket);
            viewer.send((PacketHandle)this.createMetaPacket(true));
        }

        public ClientboundSetEntityDataPacketHandle createMetaPacket(boolean includeUnchangedData) {
            return ClientboundSetEntityDataPacketHandle.createNew((int)this.entityId, (DataWatcher)this.metadata, (boolean)includeUnchangedData);
        }
    }

    protected class PartTransformer {
        public final DataWatcher metadata;
        public final double lineThickness;

        public PartTransformer(DataWatcher metadata, double lineThickness) {
            this.metadata = metadata;
            this.lineThickness = lineThickness;
        }

        public PartTransformer applyRelativePosition(double x, double y, double z) {
            Vector v = new Vector(x, y, z);
            VirtualDisplayBoundingPlane.this.rotation.transformPoint(v);
            this.metadata.forceSet(DisplayHandle.DATA_LEFT_ROTATION, (Object)VirtualDisplayBoundingPlane.this.rotation);
            this.metadata.forceSet(DisplayHandle.DATA_TRANSLATION, (Object)v);
            this.metadata.forceSet(DisplayHandle.DATA_INTERPOLATION_START_DELTA_TICKS, (Object)0);
            return this;
        }

        public PartTransformer applyPosition(double tx, double ty, double tz) {
            return this.applyRelativePosition((-0.5 + tx) * VirtualDisplayBoundingPlane.this.size.getX() - tx * this.lineThickness, (-0.5 + ty) * VirtualDisplayBoundingPlane.this.size.getY() - ty * this.lineThickness, (-0.5 + tz) * VirtualDisplayBoundingPlane.this.size.getZ() - tz * this.lineThickness);
        }

        public PartTransformer applyPlatformTransform() {
            this.applyRelativePosition(-0.5 * VirtualDisplayBoundingPlane.this.size.getX() + this.lineThickness, -0.5 * VirtualDisplayBoundingPlane.this.size.getY(), -0.5 * VirtualDisplayBoundingPlane.this.size.getZ() + this.lineThickness);
            this.metadata.forceSet(DisplayHandle.DATA_SCALE, (Object)new Vector(Math.max(0.0, VirtualDisplayBoundingPlane.this.size.getX() - 2.0 * this.lineThickness), this.lineThickness, Math.max(0.0, VirtualDisplayBoundingPlane.this.size.getZ() - 2.0 * this.lineThickness)));
            return this;
        }

        public PartTransformer applyScaleX(double x) {
            this.metadata.forceSet(DisplayHandle.DATA_SCALE, (Object)new Vector(VirtualDisplayBoundingPlane.this.size.getX() * x, this.lineThickness, this.lineThickness));
            return this;
        }

        public PartTransformer applyScaleY(double y) {
            this.metadata.forceSet(DisplayHandle.DATA_SCALE, (Object)new Vector(this.lineThickness, VirtualDisplayBoundingPlane.this.size.getY() * y, this.lineThickness));
            return this;
        }

        public PartTransformer applyScaleZ(double z) {
            this.metadata.forceSet(DisplayHandle.DATA_SCALE, (Object)new Vector(this.lineThickness, this.lineThickness, VirtualDisplayBoundingPlane.this.size.getZ() * z));
            return this;
        }
    }
}

