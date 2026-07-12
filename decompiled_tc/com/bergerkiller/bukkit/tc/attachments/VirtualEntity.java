/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.protocol.CommonPacket
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle$PosHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle$PosRotHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle$RotHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveMinecartPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacketHandle
 *  com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.NewMinecartBehaviorHandle$MinecartStepHandle
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.AttachmentControllerMember;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveMinecartPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.NewMinecartBehaviorHandle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VirtualEntity
extends VirtualSpawnableObject {
    public static final double PLAYER_SIT_BUTT_EYE_HEIGHT = 1.0;
    public static final double PLAYER_STANDING_EYE_HEIGHT = 1.62;
    public static final double PLAYER_SIT_CHICKEN_BUTT_OFFSET = -0.62;
    @Deprecated
    public static final double ARMORSTAND_BUTT_OFFSET = 0.27;
    private final int entityId;
    private final UUID entityUUID;
    protected DataWatcher metaData;
    private double posX;
    private double posY;
    private double posZ;
    private boolean posSet;
    private final Vector liveAbsPos;
    private final Vector syncAbsPos;
    private final Vector velSyncAbsPos;
    private float liveYaw;
    private float livePitch;
    private float syncYaw;
    private float syncPitch;
    private double liveVel;
    private double syncVel;
    private int lastLiveVelTick = -1;
    private Vector relativePos = new Vector();
    private ByViewerPositionAdjustment byViewerPositionAdjustment = null;
    private EntityType entityType = EntityType.CHICKEN;
    private boolean entityTypeIsMinecart = false;
    private boolean respawnOnPitchFlip = false;
    private int rotateCtr = 0;
    private SyncMode syncMode = SyncMode.NORMAL;
    private boolean minecartInterpolation = false;
    private boolean useParentMetadata = false;
    private Vector yawPitchRoll = new Vector(0.0, 0.0, 0.0);

    public VirtualEntity(AttachmentManager manager) {
        this(manager, EntityUtil.getUniqueEntityId(), UUID.randomUUID());
    }

    public VirtualEntity(AttachmentManager manager, int entityId, UUID entityUUID) {
        super(manager);
        this.entityId = entityId;
        this.entityUUID = entityUUID;
        this.metaData = new DataWatcher();
        this.liveAbsPos = new Vector();
        this.syncAbsPos = new Vector(Double.NaN, Double.NaN, Double.NaN);
        this.velSyncAbsPos = new Vector(Double.NaN, Double.NaN, Double.NaN);
        this.syncVel = 0.0;
        this.posZ = 0.0;
        this.posY = 0.0;
        this.posX = 0.0;
        this.posSet = false;
    }

    public DataWatcher getMetaData() {
        return this.metaData;
    }

    public UUID getEntityUUID() {
        return this.entityUUID;
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return entityId == this.entityId;
    }

    public double getPosX() {
        return this.liveAbsPos.getX();
    }

    public double getPosY() {
        return this.liveAbsPos.getY();
    }

    public double getPosZ() {
        return this.liveAbsPos.getZ();
    }

    public Vector getPos() {
        return this.liveAbsPos;
    }

    public boolean isMountable() {
        return VehicleMountRegistry.isMountable(this.entityType);
    }

    public double getMountOffset() {
        return VehicleMountRegistry.getOffset(this.entityType);
    }

    public boolean syncPositionIfMounted() {
        return VehicleMountRegistry.syncPositionIfMounted(this.entityType);
    }

    public void setRespawnOnPitchFlip(boolean respawn) {
        this.respawnOnPitchFlip = respawn;
    }

    public void setPosition(Vector position) {
        this.posX = position.getX();
        this.posY = position.getY();
        this.posZ = position.getZ();
        this.posSet = true;
    }

    public Vector getRelativeOffset() {
        return this.relativePos;
    }

    public void setRelativeOffset(Vector offset) {
        MathUtil.setVector((Vector)this.relativePos, (Vector)offset);
    }

    public void setRelativeOffset(double dx, double dy, double dz) {
        MathUtil.setVector((Vector)this.relativePos, (double)dx, (double)dy, (double)dz);
    }

    public void addRelativeOffset(Vector offset) {
        this.relativePos.add(offset);
    }

    public void addRelativeOffset(double dx, double dy, double dz) {
        MathUtil.addToVector((Vector)this.relativePos, (double)dx, (double)dy, (double)dz);
    }

    public void setByViewerPositionAdjustment(ByViewerPositionAdjustment adjustment) {
        this.byViewerPositionAdjustment = adjustment;
    }

    public void setSyncMode(SyncMode mode) {
        this.syncMode = mode;
        if (mode == SyncMode.SEAT) {
            this.syncPitch = 0.0f;
            this.livePitch = 0.0f;
        }
    }

    @Override
    public void setUseMinecartInterpolation(boolean use) {
        this.minecartInterpolation = use;
    }

    public Vector getYawPitchRoll() {
        return this.yawPitchRoll;
    }

    public Vector getSyncPos() {
        return this.syncAbsPos;
    }

    public float getLiveYaw() {
        return this.liveYaw;
    }

    public float getLivePitch() {
        return this.livePitch;
    }

    public float getSyncYaw() {
        return this.syncYaw;
    }

    public float getSyncPitch() {
        return this.syncPitch;
    }

    @Override
    public void updatePosition(Matrix4x4 transform) {
        double yaw;
        double pitch;
        Quaternion rotation = transform.getRotation();
        Vector f = rotation.forwardVector();
        if (this.hasPitch()) {
            boolean isFrontSideDown;
            Vector u = rotation.upVector();
            double yawmode_factor_start = 0.9;
            double yawmode_factor_end = 0.99;
            double yawmode_factor = (Math.abs(f.getY()) - 0.9) / 0.010000000000000009;
            boolean bl = isFrontSideDown = f.getY() < 0.0;
            if (u.getY() < 0.0) {
                pitch = 180.0 + (double)MathUtil.getLookAtPitch((double)f.getX(), (double)(-f.getY()), (double)f.getZ());
                f.multiply(-1.0);
            } else {
                pitch = MathUtil.getLookAtPitch((double)f.getX(), (double)f.getY(), (double)f.getZ());
            }
            if (isFrontSideDown) {
                u.multiply(-1.0);
            }
            if (yawmode_factor <= 0.0) {
                yaw = MathUtil.getLookAtYaw((double)(-f.getZ()), (double)f.getX());
            } else if (yawmode_factor >= 1.0) {
                yaw = MathUtil.getLookAtYaw((double)u.getZ(), (double)(-u.getX()));
            } else {
                double ax = yawmode_factor * u.getZ() + (1.0 - yawmode_factor) * -f.getZ();
                double az = yawmode_factor * -u.getX() + (1.0 - yawmode_factor) * f.getX();
                yaw = MathUtil.getLookAtYaw((double)ax, (double)az);
            }
        } else {
            yaw = MathUtil.getLookAtYaw((double)(-f.getZ()), (double)f.getX());
            pitch = 0.0;
        }
        this.updatePosition(transform, new Vector(pitch, yaw, 0.0));
    }

    public void updatePosition(Matrix4x4 transform, Vector yawPitchRoll) {
        if (this.posSet) {
            Vector v = new Vector(this.posX, this.posY, this.posZ);
            transform.transformPoint(v);
            this.updatePosition(v, yawPitchRoll);
        } else {
            this.updatePosition(transform.toVector(), yawPitchRoll);
        }
    }

    public void updatePosition(Vector position, Vector yawPitchRoll) {
        MathUtil.setVector((Vector)this.liveAbsPos, (Vector)position);
        this.liveAbsPos.add(this.relativePos);
        this.yawPitchRoll = yawPitchRoll;
        this.liveYaw = (float)this.yawPitchRoll.getY();
        this.livePitch = this.syncMode != SyncMode.SEAT && this.hasPitch() ? (float)this.yawPitchRoll.getX() : 0.0f;
        if (this.entityTypeIsMinecart) {
            this.liveYaw -= 90.0f;
        }
        if (Double.isNaN(this.syncAbsPos.getX())) {
            this.syncPositionSilent();
        }
        this.liveVel = this.calcNewVelocity();
    }

    private double calcNewVelocity() {
        if (!this.entityTypeIsMinecart || !(this.manager instanceof AttachmentControllerMember)) {
            return 0.0;
        }
        MinecartMember<?> member = ((AttachmentControllerMember)this.manager).getMember();
        if (!member.hasInitializedGroup() || !member.getGroup().getProperties().isSoundEnabled() || member.isDerailed()) {
            return 0.0;
        }
        int serverTicks = CommonUtil.getServerTicks();
        int elapsedTicks = serverTicks - this.lastLiveVelTick;
        this.lastLiveVelTick = serverTicks;
        if (elapsedTicks == 0) {
            return this.liveVel;
        }
        double newLiveVel = this.liveVel;
        if (elapsedTicks <= 20 && !Double.isNaN(this.velSyncAbsPos.getX())) {
            newLiveVel = this.liveAbsPos.distance(this.velSyncAbsPos);
            newLiveVel /= (double)elapsedTicks;
        }
        MathUtil.setVector((Vector)this.velSyncAbsPos, (Vector)this.liveAbsPos);
        if (newLiveVel > 1.0) {
            newLiveVel = 1.0;
        }
        if (newLiveVel < 0.001) {
            newLiveVel = 0.0;
        }
        return newLiveVel;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
        this.entityTypeIsMinecart = VirtualEntity.isMinecart(entityType);
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public boolean isMinecart() {
        return this.entityTypeIsMinecart;
    }

    public boolean isExperimentalMinecart() {
        return this.isMinecart() && this.manager != null && this.manager.getWorldFeatures().MINECART_IMPROVEMENTS;
    }

    public void setUseParentMetadata(boolean use) {
        this.useParentMetadata = use;
    }

    @Override
    @Deprecated
    public void addViewerWithoutSpawning(Player viewer) {
        super.addViewerWithoutSpawning(viewer);
    }

    @Override
    public void addViewerWithoutSpawning(AttachmentViewer viewer) {
        super.addViewerWithoutSpawning(viewer);
    }

    @Override
    public boolean hasViewers() {
        return super.hasViewers();
    }

    @Override
    @Deprecated
    public boolean isViewer(Player viewer) {
        return super.isViewer(viewer);
    }

    @Override
    public boolean isViewer(AttachmentViewer viewer) {
        return super.isViewer(viewer);
    }

    @Override
    @Deprecated
    public final void spawn(Player viewer, Vector motion) {
        super.spawn(viewer, motion);
    }

    @Override
    public final void spawn(AttachmentViewer viewer, Vector motion) {
        super.spawn(viewer, motion);
    }

    @Override
    protected void sendSpawnPackets(AttachmentViewer viewer, Vector motion) {
        ClientboundMoveEntityPacketHandle.PosRotHandle movePacket;
        ClientboundAddMobPacketHandle spawnPacket;
        Vector spawnPos = this.syncAbsPos.clone();
        if (this.byViewerPositionAdjustment != null) {
            this.byViewerPositionAdjustment.adjust(viewer, spawnPos);
        }
        spawnPos.subtract(motion);
        if (this.isLivingEntity()) {
            spawnPacket = ClientboundAddMobPacketHandle.createNew();
            spawnPacket.setEntityId(this.entityId);
            spawnPacket.setEntityUUID(this.entityUUID);
            spawnPacket.setEntityType(this.entityType);
            spawnPacket.setPosX(spawnPos.getX());
            spawnPacket.setPosY(spawnPos.getY());
            spawnPacket.setPosZ(spawnPos.getZ());
            spawnPacket.setMotX(motion.getX());
            spawnPacket.setMotY(motion.getY());
            spawnPacket.setMotZ(motion.getZ());
            spawnPacket.setYaw(this.syncYaw);
            spawnPacket.setPitch(this.syncPitch);
            spawnPacket.setHeadYaw(this.syncMode == SyncMode.ITEM ? 0.0f : this.syncYaw);
            viewer.sendEntityLivingSpawnPacket(spawnPacket, this.getUsedMeta());
        } else {
            spawnPacket = ClientboundAddEntityPacketHandle.createNew();
            spawnPacket.setEntityId(this.entityId);
            spawnPacket.setEntityUUID(this.entityUUID);
            spawnPacket.setEntityType(this.entityType);
            spawnPacket.setPosX(spawnPos.getX());
            spawnPacket.setPosY(spawnPos.getY());
            spawnPacket.setPosZ(spawnPos.getZ());
            spawnPacket.setMotX(motion.getX());
            spawnPacket.setMotY(motion.getY());
            spawnPacket.setMotZ(motion.getZ());
            if (this.isExperimentalMinecart()) {
                spawnPacket.setYaw(180.0f - this.syncYaw);
                spawnPacket.setPitch(this.syncPitch);
            } else {
                spawnPacket.setYaw(this.syncYaw);
                spawnPacket.setPitch(this.syncPitch);
            }
            viewer.send((PacketHandle)spawnPacket);
            viewer.send(ClientboundSetEntityDataPacketHandle.createNew((int)this.entityId, (DataWatcher)this.getUsedMeta(), (boolean)true).toCommonPacket());
        }
        if (this.syncMode == SyncMode.SEAT) {
            movePacket = ClientboundMoveEntityPacketHandle.PosRotHandle.createNew((int)this.entityId, (double)motion.getX(), (double)motion.getY(), (double)motion.getZ(), (float)this.syncYaw, (float)this.syncPitch, (boolean)false);
            viewer.send((PacketHandle)movePacket);
        } else if (motion.lengthSquared() > 0.001) {
            movePacket = PacketType.OUT_ENTITY_MOVE.newInstance(this.entityId, motion.getX(), motion.getY(), motion.getZ(), false);
            viewer.send((CommonPacket)movePacket);
        }
        if (this.syncVel > 0.0) {
            viewer.send((PacketHandle)ClientboundSetEntityMotionPacketHandle.createNew((int)this.entityId, (double)this.syncVel, (double)0.0, (double)0.0));
        }
    }

    @Override
    protected void applyGlowing(ChatColor color) {
        this.getMetaData().setFlag(EntityHandle.DATA_FLAGS, 64, color != null);
        this.syncMetadata();
    }

    @Override
    protected void applyGlowColorForViewer(AttachmentViewer viewer, ChatColor color) {
        viewer.updateGlowColor(this.entityUUID, color);
    }

    public void syncMetadata() {
        DataWatcher metaData = this.getUsedMeta();
        if (metaData.isChanged()) {
            this.broadcast((PacketHandle)ClientboundSetEntityDataPacketHandle.createNew((int)this.entityId, (DataWatcher)metaData, (boolean)false));
        }
    }

    public void syncPositionSilent() {
        MathUtil.setVector((Vector)this.syncAbsPos, (Vector)this.liveAbsPos);
        this.syncYaw = this.liveYaw;
        this.syncPitch = this.livePitch;
        this.syncVel = this.liveVel;
    }

    @Override
    public void syncPosition(boolean absolute) {
        boolean largeChange;
        if (!this.hasViewers()) {
            this.syncPositionSilent();
            return;
        }
        if (Math.abs(this.liveVel - this.syncVel) > 0.01 || this.syncVel > 0.0 && this.liveVel == 0.0) {
            this.syncVel = this.liveVel;
            this.broadcast((PacketHandle)ClientboundSetEntityMotionPacketHandle.createNew((int)this.entityId, (double)this.syncVel, (double)0.0, (double)0.0));
        }
        this.syncMetadata();
        double dx = this.liveAbsPos.getX() - this.syncAbsPos.getX();
        double dy = this.liveAbsPos.getY() - this.syncAbsPos.getY();
        double dz = this.liveAbsPos.getZ() - this.syncAbsPos.getZ();
        double abs_delta = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));
        boolean bl = largeChange = abs_delta > 8.0;
        if (this.isExperimentalMinecart()) {
            boolean isPitchGlitched = MathUtil.getAngleDifference((float)this.syncPitch, (float)180.0f) < 90.0f || MathUtil.getAngleDifference((float)this.livePitch, (float)180.0f) < 90.0f;
            ArrayList<NewMinecartBehaviorHandle.MinecartStepHandle> steps = new ArrayList<NewMinecartBehaviorHandle.MinecartStepHandle>(2);
            if (isPitchGlitched) {
                steps.add(NewMinecartBehaviorHandle.MinecartStepHandle.createNew((Vector)this.syncAbsPos, (Vector)new Vector(), (float)(180.0f - this.syncYaw), (float)this.syncPitch, (float)0.0f));
            }
            steps.add(NewMinecartBehaviorHandle.MinecartStepHandle.createNew((Vector)this.liveAbsPos, (Vector)new Vector(dx, dy, dz), (float)(180.0f - this.liveYaw), (float)this.livePitch, (float)1.0f));
            ClientboundMoveMinecartPacketHandle p = ClientboundMoveMinecartPacketHandle.createNew((int)this.entityId, steps);
            this.broadcast((PacketHandle)p);
            MathUtil.setVector((Vector)this.syncAbsPos, (Vector)this.liveAbsPos);
            this.syncYaw = this.liveYaw;
            this.syncPitch = this.livePitch;
            return;
        }
        if (this.respawnOnPitchFlip && this.syncPitch != this.livePitch && Util.isProtocolRotationGlitched(this.syncPitch, this.livePitch)) {
            this.forAllViewers(this::sendDestroyPacketsWithoutVMC);
            this.syncPositionSilent();
            for (AttachmentViewer viewer : this.getViewers()) {
                this.sendSpawnPackets(viewer, largeChange ? new Vector() : new Vector(dx, dy, dz));
            }
            return;
        }
        if (absolute || largeChange) {
            if (this.byViewerPositionAdjustment != null) {
                Vector pos = new Vector();
                for (AttachmentViewer viewer : this.getViewers()) {
                    MathUtil.setVector((Vector)pos, (Vector)this.liveAbsPos);
                    this.byViewerPositionAdjustment.adjust(viewer, pos);
                    viewer.send((PacketHandle)ClientboundEntityPositionSyncPacketHandle.createNew((int)this.entityId, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (float)this.liveYaw, (float)this.livePitch, (boolean)false));
                }
            } else {
                this.broadcast((PacketHandle)ClientboundEntityPositionSyncPacketHandle.createNew((int)this.entityId, (double)this.liveAbsPos.getX(), (double)this.liveAbsPos.getY(), (double)this.liveAbsPos.getZ(), (float)this.liveYaw, (float)this.livePitch, (boolean)false));
            }
            this.syncPositionSilent();
            this.refreshHeadRotation();
            return;
        }
        boolean moved = abs_delta >= 2.44140625E-4;
        boolean rotatedNow = EntityTrackerEntryStateHandle.hasProtocolRotationChanged((float)this.liveYaw, (float)this.syncYaw) || EntityTrackerEntryStateHandle.hasProtocolRotationChanged((float)this.livePitch, (float)this.syncPitch);
        boolean rotated = false;
        if (rotatedNow) {
            this.forceSyncRotation();
            rotated = true;
        } else if (this.rotateCtr > 0) {
            --this.rotateCtr;
            rotated = true;
        }
        if (rotatedNow) {
            this.refreshHeadRotation();
        }
        if (this.minecartInterpolation) {
            double FACTOR = 0.6;
            dx *= 0.6;
            dy *= 0.6;
            dz *= 0.6;
        }
        if (moved && rotated) {
            ClientboundMoveEntityPacketHandle.PosRotHandle packet = ClientboundMoveEntityPacketHandle.PosRotHandle.createNew((int)this.entityId, (double)dx, (double)dy, (double)dz, (float)this.liveYaw, (float)this.livePitch, (boolean)false);
            this.syncYaw = packet.getYaw();
            this.syncPitch = packet.getPitch();
            MathUtil.addToVector((Vector)this.syncAbsPos, (double)packet.getDeltaX(), (double)packet.getDeltaY(), (double)packet.getDeltaZ());
            this.broadcast((PacketHandle)packet);
        } else if (moved) {
            ClientboundMoveEntityPacketHandle.PosHandle packet = ClientboundMoveEntityPacketHandle.PosHandle.createNew((int)this.entityId, (double)dx, (double)dy, (double)dz, (boolean)false);
            MathUtil.addToVector((Vector)this.syncAbsPos, (double)packet.getDeltaX(), (double)packet.getDeltaY(), (double)packet.getDeltaZ());
            this.broadcast((PacketHandle)packet);
        } else if (rotated) {
            for (AttachmentViewer viewer : this.getViewers()) {
                ClientboundMoveEntityPacketHandle.RotHandle packet;
                if (viewer.evaluateGameVersion(">=", "1.15")) {
                    packet = ClientboundMoveEntityPacketHandle.PosRotHandle.createNew((int)this.entityId, (double)0.0, (double)0.0, (double)0.0, (float)this.liveYaw, (float)this.livePitch, (boolean)false);
                    viewer.send((PacketHandle)packet);
                    this.syncYaw = packet.getYaw();
                    this.syncPitch = packet.getPitch();
                    continue;
                }
                packet = ClientboundMoveEntityPacketHandle.RotHandle.createNew((int)this.entityId, (float)this.liveYaw, (float)this.livePitch, (boolean)false);
                viewer.send((PacketHandle)packet);
                this.syncYaw = packet.getYaw();
                this.syncPitch = packet.getPitch();
            }
        }
    }

    public void forceSyncRotation() {
        this.rotateCtr = 14;
    }

    private void refreshHeadRotation() {
        if (this.syncMode.isNormal() && this.isLivingEntity()) {
            CommonPacket packet = PacketType.OUT_ENTITY_HEAD_ROTATION.newInstance();
            packet.write(PacketType.OUT_ENTITY_HEAD_ROTATION.entityId, (Object)this.entityId);
            packet.write(PacketType.OUT_ENTITY_HEAD_ROTATION.headYaw, (Object)Float.valueOf(this.liveYaw));
            this.broadcast(packet);
        }
    }

    public static boolean isLivingEntity(EntityType entityType) {
        Class entityClass = entityType.getEntityClass();
        return entityClass != null && LivingEntity.class.isAssignableFrom(entityClass);
    }

    private boolean isLivingEntity() {
        return VirtualEntity.isLivingEntity(this.entityType);
    }

    public void respawnForAll(Vector motion) {
        this.forAllViewers(this::sendDestroyPacketsWithoutVMC);
        this.syncPosition(true);
        this.forAllViewers(v -> this.sendSpawnPackets((AttachmentViewer)v, motion));
    }

    @Override
    public void destroyForAll() {
        super.destroyForAll();
    }

    @Override
    @Deprecated
    public void destroy(Player viewer) {
        super.destroy(viewer);
    }

    @Override
    public void destroy(AttachmentViewer viewer) {
        super.destroy(viewer);
    }

    @Override
    protected void sendDestroyPackets(AttachmentViewer viewer) {
        this.sendDestroyPacketsWithoutVMC(viewer);
        viewer.getVehicleMountController().remove(this.entityId);
    }

    private void sendDestroyPacketsWithoutVMC(AttachmentViewer viewer) {
        if (this.syncVel > 0.0) {
            viewer.send(PacketType.OUT_ENTITY_VELOCITY.newInstance(this.entityId, new Vector()));
        }
        ClientboundRemoveEntitiesPacketHandle destroyPacket = ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)this.entityId);
        viewer.send((PacketHandle)destroyPacket);
    }

    @Override
    public void broadcast(CommonPacket packet) {
        super.broadcast(packet);
    }

    @Override
    public void broadcast(PacketHandle packet) {
        super.broadcast(packet);
    }

    private DataWatcher getUsedMeta() {
        if (this.useParentMetadata && this.manager instanceof AttachmentControllerMember) {
            return ((CommonMinecart)((AttachmentControllerMember)this.manager).getMember().getEntity()).getMetaData();
        }
        return this.metaData;
    }

    private boolean hasPitch() {
        return this.entityTypeIsMinecart || this.isLivingEntity();
    }

    public static boolean isMinecart(EntityType entityType) {
        switch (entityType) {
            case MINECART: 
            case MINECART_CHEST: 
            case MINECART_FURNACE: 
            case MINECART_TNT: 
            case MINECART_COMMAND: 
            case MINECART_MOB_SPAWNER: 
            case MINECART_HOPPER: {
                return true;
            }
        }
        return false;
    }

    @FunctionalInterface
    public static interface ByViewerPositionAdjustment {
        public void adjust(AttachmentViewer var1, Vector var2);
    }

    public static enum SyncMode {
        ITEM(false),
        NORMAL(true),
        SEAT(false);

        private final boolean _normal;

        private SyncMode(boolean normal) {
            this._normal = normal;
        }

        public boolean isNormal() {
            return this._normal;
        }
    }

    private static class VehicleMountRegistry {
        private static final Map<EntityType, Double> _lookup = new EnumMap<EntityType, Double>(EntityType.class);
        private static final Set<EntityType> _unmountable = EnumSet.noneOf(EntityType.class);
        private static final Set<EntityType> _noPositionSyncIfMounted = EnumSet.noneOf(EntityType.class);
        private static final Double DEFAULT_OFFSET = 1.0;

        private VehicleMountRegistry() {
        }

        public static double getOffset(EntityType type) {
            return _lookup.getOrDefault(type, DEFAULT_OFFSET);
        }

        public static boolean isMountable(EntityType type) {
            return !_unmountable.contains(type);
        }

        public static boolean syncPositionIfMounted(EntityType type) {
            return !_noPositionSyncIfMounted.contains(type);
        }

        private static void register(Predicate<EntityType> condition, double offset) {
            VehicleMountRegistry.register(condition, offset, true, true);
        }

        private static void register(Predicate<EntityType> condition, double offset, boolean mountable, boolean syncPosition) {
            Stream.of(EntityType.values()).filter(condition).forEachOrdered(type -> {
                _lookup.put((EntityType)type, offset);
                if (!mountable) {
                    _unmountable.add((EntityType)type);
                }
                if (!syncPosition) {
                    _noPositionSyncIfMounted.add((EntityType)type);
                }
            });
        }

        private static void register(String name, double offset) {
            VehicleMountRegistry.register(name, offset, true, true);
        }

        private static void register(String name, double offset, boolean mountable, boolean syncPosition) {
            try {
                EntityType type = EntityType.valueOf((String)name);
                _lookup.put(type, offset);
                if (!mountable) {
                    _unmountable.add(type);
                }
                if (!syncPosition) {
                    _noPositionSyncIfMounted.add(type);
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }

        static {
            VehicleMountRegistry.register("AXOLOTL", 0.59);
            VehicleMountRegistry.register("BAT", 1.0);
            VehicleMountRegistry.register("BEE", 0.7);
            VehicleMountRegistry.register("BLAZE", 1.6);
            VehicleMountRegistry.register("BOAT", 0.2, false, true);
            VehicleMountRegistry.register("CAT", 0.8);
            VehicleMountRegistry.register("CAVE_SPIDER", 0.5);
            VehicleMountRegistry.register("CHICKEN", 0.62);
            VehicleMountRegistry.register("COD", 0.5);
            VehicleMountRegistry.register("COW", 1.3);
            VehicleMountRegistry.register("CREEPER", 1.55);
            VehicleMountRegistry.register("DOLPHIN", 0.75);
            VehicleMountRegistry.register("DONKEY", 1.15);
            VehicleMountRegistry.register("DROWNED", 1.75);
            VehicleMountRegistry.register("ENDERMAN", 2.45);
            VehicleMountRegistry.register("ENDERMITE", 0.5);
            VehicleMountRegistry.register("ENDER_DRAGON", 3.4, false, true);
            VehicleMountRegistry.register("EVOKER", 1.75);
            VehicleMountRegistry.register("FALLING_BLOCK", 1.0);
            VehicleMountRegistry.register("FOX", 0.8);
            VehicleMountRegistry.register("GHAST", 4.0, false, true);
            VehicleMountRegistry.register("GIANT", 12.0, false, true);
            VehicleMountRegistry.register("GLOW_SQUID", 0.9);
            VehicleMountRegistry.register("GOAT", 1.25);
            VehicleMountRegistry.register("GUARDIAN", 0.92);
            VehicleMountRegistry.register("HOGLIN", 1.5);
            VehicleMountRegistry.register("HORSE", 1.4, false, true);
            VehicleMountRegistry.register("HUSK", 1.75);
            VehicleMountRegistry.register("ILLUSIONER", 1.75);
            VehicleMountRegistry.register("IRON_GOLEM", 2.3);
            VehicleMountRegistry.register("LEASH_HITCH", 0.97);
            VehicleMountRegistry.register("LLAMA", 1.37, false, true);
            VehicleMountRegistry.register((EntityType e) -> e.name().contains("MINECART"), 0.27);
            VehicleMountRegistry.register("MULE", 1.22);
            VehicleMountRegistry.register("MUSHROOM_COW", 1.3);
            VehicleMountRegistry.register("OCELOT", 0.8);
            VehicleMountRegistry.register("PANDA", 1.2);
            VehicleMountRegistry.register("PARROT", 0.96);
            VehicleMountRegistry.register("PHANTOM", 0.67);
            VehicleMountRegistry.register("PIG", 0.965);
            VehicleMountRegistry.register("PIGLIN", 2.05);
            VehicleMountRegistry.register("PIGLIN_BRUTE", 1.75);
            VehicleMountRegistry.register("PILLAGER", 1.75);
            VehicleMountRegistry.register("POLAR_BEAR", 1.305);
            VehicleMountRegistry.register("PRIMED_TNT", 1.0);
            VehicleMountRegistry.register("PUFFERFISH", 0.55);
            VehicleMountRegistry.register("RABBIT", 0.63);
            VehicleMountRegistry.register("RAVAGER", 2.4);
            VehicleMountRegistry.register("SALMON", 0.58);
            VehicleMountRegistry.register("SHEEP", 1.25);
            VehicleMountRegistry.register("SHULKER", 1.0, false, false);
            VehicleMountRegistry.register("SHULKER_BULLET", 0.52);
            VehicleMountRegistry.register("SILVERFISH", 0.51);
            VehicleMountRegistry.register("SKELETON", 1.75);
            VehicleMountRegistry.register("SKELETON_HORSE", 1.3, false, true);
            VehicleMountRegistry.register("SMALL_FIREBALL", 0.52);
            VehicleMountRegistry.register("SNOWMAN", 1.67);
            VehicleMountRegistry.register("SPIDER", 0.73);
            VehicleMountRegistry.register("SQUID", 0.88);
            VehicleMountRegistry.register("STRAY", 1.775);
            VehicleMountRegistry.register("STRIDER", 1.79, false, true);
            VehicleMountRegistry.register("TRADER_LLAMA", 1.36);
            VehicleMountRegistry.register("TURTLE", 0.58);
            VehicleMountRegistry.register("VEX", 0.88);
            VehicleMountRegistry.register("VILLAGER", 1.75);
            VehicleMountRegistry.register("VINDICATOR", 1.75);
            VehicleMountRegistry.register("WANDERING_TRADER", 1.75);
            VehicleMountRegistry.register("WITCH", 1.75);
            VehicleMountRegistry.register("WITHER", 3.5, false, true);
            VehicleMountRegistry.register("WITHER_SKELETON", 2.07);
            VehicleMountRegistry.register("WITHER_SKULL", 0.52);
            VehicleMountRegistry.register("WOLF", 0.92);
            VehicleMountRegistry.register("ZOGLIN", 1.53);
            VehicleMountRegistry.register("ZOMBIE", 1.75);
            VehicleMountRegistry.register("ZOMBIE_HORSE", 1.47);
            VehicleMountRegistry.register("ZOMBIE_VILLAGER", 1.75);
            VehicleMountRegistry.register("ZOMBIFIED_PIGLIN", 1.75);
            VehicleMountRegistry.register("ZOMBIE_VILLAGER", 1.75);
        }
    }
}

