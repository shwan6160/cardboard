/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.math.Vector3
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle$PosHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPassengersPacketHandle
 *  org.bukkit.entity.EntityType
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.schematic;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.schematic.SingleSchematicBlock;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPassengersPacketHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class MovingSchematic
extends VirtualSpawnableObject {
    private final int mountEntityId;
    private final List<SingleSchematicBlock> blocks = new ArrayList<SingleSchematicBlock>();
    private final Vector livePos = new Vector();
    private final Vector syncPos = new Vector();
    private final Quaternion liveRot = new Quaternion();
    private IntVector3 blockBounds = new IntVector3(1, 1, 1);
    private final Vector scale = new Vector(1.0, 1.0, 1.0);
    private final Vector origin = new Vector(0.0, 0.0, 0.0);
    private final Vector spacing = new Vector(0.0, 0.0, 0.0);
    private boolean hasSpacing = false;
    private boolean hasOrigin = false;
    private boolean hasClipping = true;
    private float bbSize = 1.5f;
    private int[] cachedBlockEntityIds = null;
    private boolean hasKnownPosition = false;

    public MovingSchematic(AttachmentManager manager) {
        super(manager);
        this.mountEntityId = EntityUtil.getUniqueEntityId();
    }

    public void setBlockBounds(IntVector3 blockBounds) {
        if (!this.blockBounds.equals((Object)blockBounds)) {
            this.blockBounds = blockBounds;
            this.rescaleAllBlocks();
        }
    }

    public void addBlock(double x, double y, double z, BlockData blockData) {
        if (!MaterialUtil.ISAIR.get(blockData).booleanValue()) {
            SingleSchematicBlock block = new SingleSchematicBlock(x, y, z, blockData);
            this.blocks.add(block);
            this.cachedBlockEntityIds = null;
            if (this.hasKnownPosition) {
                if (this.hasSpacing) {
                    block.setScaleAndSpacing(this.scale, this.origin, this.spacing);
                } else {
                    block.setScaleZeroSpacing(this.scale, this.origin);
                }
                block.setClipBox(Float.valueOf(this.bbSize));
                block.sync(this.liveRot, Collections.emptyList());
                this.forAllViewers(v -> block.spawn((AttachmentViewer)v, this.syncPos, new Vector(0.0, 0.0, 0.0)));
            }
        }
    }

    public OrientedBoundingBox createBBOX() {
        Vector bbSize = new Vector(((double)this.blockBounds.x + this.spacing.getX() * (double)(this.blockBounds.x - 1)) * this.scale.getX(), ((double)this.blockBounds.y + this.spacing.getY() * (double)(this.blockBounds.y - 1)) * this.scale.getY(), ((double)this.blockBounds.z + this.spacing.getZ() * (double)(this.blockBounds.z - 1)) * this.scale.getZ());
        Vector position = this.livePos.clone().add(this.liveRot.upVector().multiply(0.5 * bbSize.getY()));
        if (this.hasOrigin) {
            Vector offset = this.origin.clone();
            this.liveRot.transformPoint(offset);
            position.subtract(offset);
        }
        return new OrientedBoundingBox(position, bbSize, this.liveRot);
    }

    public Matrix4x4 createOriginPointTransform() {
        Matrix4x4 transform = Matrix4x4.translation((Vector)this.livePos);
        transform.rotate(this.liveRot);
        transform.translate(this.origin);
        return transform;
    }

    public void resendMounts() {
        if (this.hasKnownPosition) {
            this.broadcast((PacketHandle)ClientboundSetPassengersPacketHandle.createNew((int)this.mountEntityId, (int[])this.getBlockEntityIds()));
        }
    }

    private int[] getBlockEntityIds() {
        int[] ids = this.cachedBlockEntityIds;
        if (ids == null) {
            this.cachedBlockEntityIds = ids = this.blocks.stream().mapToInt(SingleSchematicBlock::getEntityId).toArray();
        }
        return ids;
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return entityId == this.mountEntityId;
    }

    public void setHasClipping(boolean clipping) {
        if (this.hasClipping != clipping) {
            this.hasClipping = clipping;
            this.rescaleAllBlocks();
        }
    }

    public void setScale(Vector3 scale) {
        if (this.scale.getX() != scale.x || this.scale.getY() != scale.y || this.scale.getZ() != scale.z) {
            MathUtil.setVector((Vector)this.scale, (double)scale.x, (double)scale.y, (double)scale.z);
            this.rescaleAllBlocks();
        }
    }

    public void setScale(Vector scale) {
        if (this.scale.getX() != scale.getX() || this.scale.getY() != scale.getY() || this.scale.getZ() != scale.getZ()) {
            MathUtil.setVector((Vector)this.scale, (Vector)scale);
            this.rescaleAllBlocks();
        }
    }

    public void setSpacing(Vector spacing) {
        if (this.spacing.getX() != spacing.getX() || this.spacing.getY() != spacing.getY() || this.spacing.getZ() != spacing.getZ()) {
            MathUtil.setVector((Vector)this.spacing, (Vector)spacing);
            this.hasSpacing = spacing.getX() != 0.0 || spacing.getY() != 0.0 || spacing.getZ() != 0.0;
            this.rescaleAllBlocks();
        }
    }

    public void setOrigin(Vector origin) {
        if (this.origin.getX() != origin.getX() || this.origin.getY() != origin.getY() || this.origin.getZ() != origin.getZ()) {
            MathUtil.setVector((Vector)this.origin, (Vector)origin);
            this.hasOrigin = origin.getX() != 0.0 || origin.getY() != 0.0 || origin.getZ() != 0.0;
            this.rescaleAllBlocks();
        }
    }

    public boolean hasOrigin() {
        return this.hasOrigin;
    }

    public Vector getOrigin() {
        return this.origin;
    }

    private void rescaleAllBlocks() {
        block8: {
            boolean clipChanged;
            block7: {
                float newBBSize = this.hasClipping ? (float)(1.41421356274619 * Util.absMaxAxis(this.blockBounds.toVector().add(this.spacing).multiply(this.scale))) : 0.0f;
                clipChanged = this.bbSize != newBBSize;
                this.bbSize = newBBSize;
                if (!this.hasKnownPosition) break block7;
                Float bbSize = Float.valueOf(this.bbSize);
                if (this.hasSpacing) {
                    for (SingleSchematicBlock block : this.blocks) {
                        block.setScaleAndSpacing(this.scale, this.origin, this.spacing);
                        if (clipChanged) {
                            block.setClipBox(bbSize);
                        }
                        block.sync(this.liveRot, this.getViewers());
                    }
                } else {
                    for (SingleSchematicBlock block : this.blocks) {
                        block.setScaleZeroSpacing(this.scale, this.origin);
                        if (clipChanged) {
                            block.setClipBox(bbSize);
                        }
                        block.sync(this.liveRot, this.getViewers());
                    }
                }
                break block8;
            }
            if (!clipChanged) break block8;
            for (SingleSchematicBlock block : this.blocks) {
                block.setClipBox(Float.valueOf(this.bbSize));
            }
        }
    }

    private void syncBlockPositions() {
        if (this.hasKnownPosition) {
            for (SingleSchematicBlock block : this.blocks) {
                block.sync(this.liveRot, this.getViewers());
            }
        }
    }

    @Override
    protected void sendSpawnPackets(AttachmentViewer viewer, Vector motion) {
        ClientboundAddMobPacketHandle spawnPacket = ClientboundAddMobPacketHandle.createNew();
        spawnPacket.setEntityId(this.mountEntityId);
        spawnPacket.setEntityUUID(UUID.randomUUID());
        spawnPacket.setEntityType(EntityType.ARMOR_STAND);
        spawnPacket.setPosX(this.syncPos.getX() - motion.getX());
        spawnPacket.setPosY(this.syncPos.getY() - motion.getY());
        spawnPacket.setPosZ(this.syncPos.getZ() - motion.getZ());
        spawnPacket.setMotX(motion.getX());
        spawnPacket.setMotY(motion.getY());
        spawnPacket.setMotZ(motion.getZ());
        spawnPacket.setYaw(0.0f);
        spawnPacket.setPitch(0.0f);
        spawnPacket.setHeadYaw(0.0f);
        viewer.sendEntityLivingSpawnPacket(spawnPacket, VirtualDisplayEntity.ARMORSTAND_MOUNT_METADATA);
        for (SingleSchematicBlock block : this.blocks) {
            block.spawn(viewer, this.syncPos, motion);
        }
        viewer.send((PacketHandle)ClientboundSetPassengersPacketHandle.createNew((int)this.mountEntityId, (int[])this.getBlockEntityIds()));
    }

    @Override
    protected void sendDestroyPackets(AttachmentViewer viewer) {
        int[] ids = this.getBlockEntityIds();
        ids = Arrays.copyOf(ids, ids.length + 1);
        ids[ids.length - 1] = this.mountEntityId;
        if (ClientboundRemoveEntitiesPacketHandle.canDestroyMultiple()) {
            viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewMultiple((int[])ids));
        } else {
            for (int entityId : ids) {
                viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)entityId));
            }
        }
    }

    @Override
    public void updatePosition(Matrix4x4 transform) {
        MathUtil.setVector((Vector)this.livePos, (Vector)transform.toVector());
        this.liveRot.setTo(transform.getRotation());
        if (this.hasOrigin) {
            Vector offset = this.origin.clone();
            this.liveRot.transformPoint(offset);
            this.livePos.add(offset);
        }
        if (!this.hasKnownPosition) {
            this.hasKnownPosition = true;
            MathUtil.setVector((Vector)this.syncPos, (Vector)this.livePos);
            this.rescaleAllBlocks();
        }
    }

    @Override
    public void syncPosition(boolean absolute) {
        this.syncBlockPositions();
        double dx = 0.0;
        double dy = 0.0;
        double dz = 0.0;
        if (!absolute) {
            dx = this.livePos.getX() - this.syncPos.getX();
            dy = this.livePos.getY() - this.syncPos.getY();
            dz = this.livePos.getZ() - this.syncPos.getZ();
            double abs_delta = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));
            boolean bl = absolute = abs_delta > 8.0;
        }
        if (absolute) {
            MathUtil.setVector((Vector)this.syncPos, (Vector)this.livePos);
            this.broadcast((PacketHandle)ClientboundEntityPositionSyncPacketHandle.createNew((int)this.mountEntityId, (double)this.syncPos.getX(), (double)this.syncPos.getY(), (double)this.syncPos.getZ(), (float)0.0f, (float)0.0f, (boolean)false));
        } else {
            ClientboundMoveEntityPacketHandle.PosHandle packet = ClientboundMoveEntityPacketHandle.PosHandle.createNew((int)this.mountEntityId, (double)dx, (double)dy, (double)dz, (boolean)false);
            MathUtil.addToVector((Vector)this.syncPos, (double)packet.getDeltaX(), (double)packet.getDeltaY(), (double)packet.getDeltaZ());
            this.broadcast((PacketHandle)packet);
        }
    }
}

