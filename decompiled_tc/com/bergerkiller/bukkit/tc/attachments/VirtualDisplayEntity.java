/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.math.Vector3
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.Brightness
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher$Prototype
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle$PosHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.EntityType
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.Brightness;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import java.util.Optional;
import java.util.UUID;
import java.util.function.IntFunction;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public abstract class VirtualDisplayEntity
extends VirtualSpawnableObject {
    public static final EntityType BLOCK_DISPLAY_ENTITY_TYPE = (EntityType)LogicUtil.tryMake(() -> EntityType.valueOf((String)"BLOCK_DISPLAY"), null);
    public static final EntityType ITEM_DISPLAY_ENTITY_TYPE = (EntityType)LogicUtil.tryMake(() -> EntityType.valueOf((String)"ITEM_DISPLAY"), null);
    public static final EntityType TEXT_DISPLAY_ENTITY_TYPE = (EntityType)LogicUtil.tryMake(() -> EntityType.valueOf((String)"TEXT_DISPLAY"), null);
    public static final EntityType INTERACTION_ENTITY_TYPE = (EntityType)LogicUtil.tryMake(() -> EntityType.valueOf((String)"INTERACTION"), null);
    public static final double BBOX_FACT = 1.41421356274619;
    public static final DataWatcher ARMORSTAND_MOUNT_METADATA = new DataWatcher();
    public static final DataWatcher.Prototype BASE_DISPLAY_METADATA;
    private final int mountEntityId = EntityUtil.getUniqueEntityId();
    private final int displayEntityId = EntityUtil.getUniqueEntityId();
    private final UUID displayEntityUUID = UUID.randomUUID();
    private final EntityType entityType;
    private final Vector syncPos;
    private final Vector livePos;
    private final Quaternion liveRot;
    protected final DataWatcher metadata;
    protected final Vector scale;
    private Brightness brightness;

    public VirtualDisplayEntity(AttachmentManager manager, EntityType entityType) {
        this(manager, entityType, BASE_DISPLAY_METADATA.create());
    }

    public VirtualDisplayEntity(AttachmentManager manager, EntityType entityType, DataWatcher metadata) {
        super(manager);
        this.entityType = entityType;
        this.metadata = metadata;
        this.syncPos = new Vector(Double.NaN, Double.NaN, Double.NaN);
        this.livePos = new Vector(Double.NaN, Double.NaN, Double.NaN);
        this.liveRot = new Quaternion();
        this.scale = new Vector(1.0, 1.0, 1.0);
        this.brightness = Brightness.UNSET;
    }

    public DataWatcher getMetadata() {
        return this.metadata;
    }

    protected Vector computeTranslation(Quaternion rotation) {
        return new Vector();
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return entityId == this.mountEntityId || entityId == this.displayEntityId;
    }

    public Vector getScale() {
        return this.scale;
    }

    public void setScale(Vector3 scale) {
        if (this.scale.getX() != scale.x || this.scale.getY() != scale.y || this.scale.getZ() != scale.z) {
            MathUtil.setVector((Vector)this.scale, (double)scale.x, (double)scale.y, (double)scale.z);
            this.onScaleUpdated();
        }
    }

    public void setScale(Vector scale) {
        if (this.scale.getX() != scale.getX() || this.scale.getY() != scale.getY() || this.scale.getZ() != scale.getZ()) {
            MathUtil.setVector((Vector)this.scale, (Vector)scale);
            this.onScaleUpdated();
        }
    }

    protected void onScaleUpdated() {
        this.metadata.set(DisplayHandle.DATA_SCALE, (Object)this.scale);
    }

    public void setBrightness(Brightness brightness) {
        if (!this.brightness.equals((Object)brightness)) {
            this.brightness = brightness;
            this.metadata.set(DisplayHandle.DATA_BRIGHTNESS_OVERRIDE, (Object)brightness);
        }
    }

    @Override
    public void updatePosition(Matrix4x4 transform) {
        MathUtil.setVector((Vector)this.livePos, (Vector)transform.toVector());
        this.liveRot.setTo(transform.getRotation());
        this.onRotationUpdated(this.liveRot);
        if (Double.isNaN(this.syncPos.getX())) {
            MathUtil.setVector((Vector)this.syncPos, (Vector)this.livePos);
            this.syncPosition(true);
        }
    }

    protected void onRotationUpdated(Quaternion rotation) {
    }

    @Override
    protected void sendSpawnPackets(AttachmentViewer viewer, Vector motion) {
        ClientboundAddMobPacketHandle spawnPacket;
        boolean canInterpolate = viewer.supportsDisplayEntityLocationInterpolation();
        if (!canInterpolate) {
            spawnPacket = ClientboundAddMobPacketHandle.createNew();
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
            viewer.sendEntityLivingSpawnPacket(spawnPacket, ARMORSTAND_MOUNT_METADATA);
        }
        spawnPacket = ClientboundAddEntityPacketHandle.createNew();
        spawnPacket.setEntityId(this.displayEntityId);
        spawnPacket.setEntityUUID(this.displayEntityUUID);
        spawnPacket.setEntityType(this.entityType);
        spawnPacket.setPosX(this.syncPos.getX() - motion.getX());
        spawnPacket.setPosY(this.syncPos.getY() - motion.getY());
        spawnPacket.setPosZ(this.syncPos.getZ() - motion.getZ());
        spawnPacket.setMotX(motion.getX());
        spawnPacket.setMotY(motion.getY());
        spawnPacket.setMotZ(motion.getZ());
        spawnPacket.setYaw(0.0f);
        spawnPacket.setPitch(0.0f);
        viewer.send((PacketHandle)spawnPacket);
        viewer.send((PacketHandle)ClientboundSetEntityDataPacketHandle.createNew((int)this.displayEntityId, (DataWatcher)this.metadata, (boolean)true));
        if (!canInterpolate) {
            viewer.getVehicleMountController().mount(this.mountEntityId, this.displayEntityId);
        }
    }

    @Override
    protected void sendDestroyPackets(AttachmentViewer viewer) {
        if (viewer.supportsDisplayEntityLocationInterpolation()) {
            viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)this.displayEntityId));
            viewer.getVehicleMountController().remove(this.displayEntityId);
        } else {
            viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewMultiple((int[])new int[]{this.displayEntityId, this.mountEntityId}));
            viewer.getVehicleMountController().remove(this.displayEntityId);
            viewer.getVehicleMountController().remove(this.mountEntityId);
        }
    }

    @Override
    protected void applyGlowing(ChatColor color) {
        this.metadata.setFlag(EntityHandle.DATA_FLAGS, 64, color != null);
        this.syncMeta();
    }

    @Override
    protected void applyGlowColorForViewer(AttachmentViewer viewer, ChatColor color) {
        viewer.updateGlowColor(this.displayEntityUUID, color);
    }

    @Override
    public void setUseMinecartInterpolation(boolean use) {
        this.metadata.set(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)(use ? 5 : 3));
    }

    @Override
    public void syncPosition(boolean absolute) {
        double dz;
        double dy;
        double dx;
        this.metadata.forceSet(DisplayHandle.DATA_TRANSLATION, (Object)this.computeTranslation(this.liveRot));
        this.metadata.forceSet(DisplayHandle.DATA_LEFT_ROTATION, (Object)this.liveRot);
        this.metadata.forceSet(DisplayHandle.DATA_INTERPOLATION_START_DELTA_TICKS, (Object)0);
        if (!absolute) {
            dx = this.livePos.getX() - this.syncPos.getX();
            dy = this.livePos.getY() - this.syncPos.getY();
            dz = this.livePos.getZ() - this.syncPos.getZ();
            double abs_delta = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));
            absolute = abs_delta > 8.0;
        } else {
            dx = 0.0;
            dy = 0.0;
            dz = 0.0;
        }
        if (absolute) {
            MathUtil.setVector((Vector)this.syncPos, (Vector)this.livePos);
            this.syncPositionLogic(id -> ClientboundEntityPositionSyncPacketHandle.createNew((int)id, (double)this.syncPos.getX(), (double)this.syncPos.getY(), (double)this.syncPos.getZ(), (float)0.0f, (float)0.0f, (boolean)false));
        } else {
            ClientboundMoveEntityPacketHandle.PosHandle packet = this.syncPositionLogicAlwaysCreate(id -> ClientboundMoveEntityPacketHandle.PosHandle.createNew((int)id, (double)dx, (double)dy, (double)dz, (boolean)false));
            MathUtil.addToVector((Vector)this.syncPos, (double)packet.getDeltaX(), (double)packet.getDeltaY(), (double)packet.getDeltaZ());
        }
        this.syncMeta();
    }

    private <T extends PacketHandle> T syncPositionLogicAlwaysCreate(IntFunction<T> packetCreator) {
        return (T)this.syncPositionLogic(packetCreator).orElseGet(() -> (PacketHandle)packetCreator.apply(this.displayEntityId));
    }

    private <T extends PacketHandle> Optional<T> syncPositionLogic(IntFunction<T> packetCreator) {
        PacketHandle packetForNewClients = null;
        PacketHandle packetForOldClients = null;
        for (AttachmentViewer viewer : this.getViewers()) {
            if (viewer.supportsDisplayEntityLocationInterpolation()) {
                if (packetForNewClients == null) {
                    packetForNewClients = (PacketHandle)packetCreator.apply(this.displayEntityId);
                }
                viewer.send(packetForNewClients);
                continue;
            }
            if (packetForOldClients == null) {
                packetForOldClients = (PacketHandle)packetCreator.apply(this.mountEntityId);
            }
            viewer.send(packetForOldClients);
        }
        if (packetForNewClients != null) {
            return Optional.of(packetForNewClients);
        }
        if (packetForOldClients != null) {
            return Optional.of(packetForOldClients);
        }
        return Optional.empty();
    }

    protected void syncMeta() {
        this.broadcast((PacketHandle)ClientboundSetEntityDataPacketHandle.createNew((int)this.displayEntityId, (DataWatcher)this.metadata, (boolean)false));
    }

    public static Brightness loadBrightnessFromConfig(ConfigurationNode config) {
        ConfigurationNode brightnessConfig = config.getNodeIfExists("brightness");
        if (brightnessConfig != null) {
            return Brightness.blockAndSkyLight((int)((Integer)brightnessConfig.get("block", (Object)0)), (int)((Integer)brightnessConfig.get("sky", (Object)0)));
        }
        return Brightness.UNSET;
    }

    public static void saveBrightnessToConfig(ConfigurationNode config, Brightness brightness) {
        if (brightness == Brightness.UNSET) {
            config.remove("brightness");
        } else {
            ConfigurationNode brightnessConfig = config.getNode("brightness");
            brightnessConfig.set("block", (Object)brightness.blockLight());
            brightnessConfig.set("sky", (Object)brightness.skyLight());
        }
    }

    static {
        ARMORSTAND_MOUNT_METADATA.set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
        ARMORSTAND_MOUNT_METADATA.set(EntityHandle.DATA_FLAGS, (Object)-96);
        ARMORSTAND_MOUNT_METADATA.set(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, (Object)25);
        BASE_DISPLAY_METADATA = DataWatcher.Prototype.build().setClientDefault(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)0).set(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)3).set(DisplayHandle.DATA_POS_ROT_INTERPOLATION_DURATION, (Object)3).setClientDefault(DisplayHandle.DATA_INTERPOLATION_START_DELTA_TICKS, (Object)0).setClientDefault(DisplayHandle.DATA_SCALE, (Object)new Vector(1, 1, 1)).setClientDefault(DisplayHandle.DATA_TRANSLATION, (Object)new Vector()).setClientDefault(DisplayHandle.DATA_LEFT_ROTATION, (Object)new Quaternion()).setClientDefault(DisplayHandle.DATA_RIGHT_ROTATION, (Object)new Quaternion()).setClientDefault(DisplayHandle.DATA_BRIGHTNESS_OVERRIDE, (Object)Brightness.UNSET).setClientDefault(DisplayHandle.DATA_WIDTH, (Object)Float.valueOf(0.0f)).setClientDefault(DisplayHandle.DATA_HEIGHT, (Object)Float.valueOf(0.0f)).create();
    }
}

