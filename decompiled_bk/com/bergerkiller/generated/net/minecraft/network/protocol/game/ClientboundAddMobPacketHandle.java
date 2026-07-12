/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundAddMobPacket")
public abstract class ClientboundAddMobPacketHandle
extends PacketHandle {
    public static final ClientboundAddMobPacketClass T = Template.Class.create(ClientboundAddMobPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundAddMobPacketHandle createHandle(Object handleInstance) {
        return (ClientboundAddMobPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundAddMobPacketHandle createNew() {
        return ClientboundAddMobPacketHandle.T.createNew.invoke();
    }

    public abstract CommonEntityType getCommonEntityType();

    public abstract void setCommonEntityType(CommonEntityType var1);

    public abstract double getPosX();

    public abstract double getPosY();

    public abstract double getPosZ();

    public abstract void setPosX(double var1);

    public abstract void setPosY(double var1);

    public abstract void setPosZ(double var1);

    public abstract Vector getMotVector();

    public abstract void setMotVector(Vector var1);

    public abstract double getMotX();

    public abstract double getMotY();

    public abstract double getMotZ();

    public abstract void setMotX(double var1);

    public abstract void setMotY(double var1);

    public abstract void setMotZ(double var1);

    public boolean hasDataWatcherSupport() {
        return ClientboundAddMobPacketHandle.T.opt_dataWatcher.isAvailable();
    }

    @Deprecated
    public DataWatcher getDataWatcher() {
        if (ClientboundAddMobPacketHandle.T.opt_dataWatcher.isAvailable()) {
            return ClientboundAddMobPacketHandle.T.opt_dataWatcher.get(this.getRaw());
        }
        return null;
    }

    @Deprecated
    public void setDataWatcher(DataWatcher dataWatcher) {
        if (ClientboundAddMobPacketHandle.T.opt_dataWatcher.isAvailable()) {
            ClientboundAddMobPacketHandle.T.opt_dataWatcher.set(this.getRaw(), dataWatcher);
        }
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.OUT_ENTITY_SPAWN_LIVING;
    }

    public void setEntityUUID(UUID uuid) {
        if (ClientboundAddMobPacketHandle.T.entityUUID.isAvailable()) {
            ClientboundAddMobPacketHandle.T.entityUUID.set(this.getRaw(), uuid);
        }
    }

    public static boolean isCommonEntityTypeSupported(CommonEntityType type) {
        return type != null && type.entityTypeId != -1;
    }

    public static boolean isEntityTypeSupported(EntityType type) {
        return type != null && ClientboundAddMobPacketHandle.isCommonEntityTypeSupported(CommonEntityType.byEntityType(type));
    }

    public void setEntityType(EntityType type) {
        if (type == null) {
            throw new IllegalArgumentException("Input EntityType is null");
        }
        this.setCommonEntityType(CommonEntityType.byEntityType(type));
    }

    public EntityType getEntityType() {
        return this.getCommonEntityType().entityType;
    }

    public float getYaw() {
        return this.getProtocolRotation(ClientboundAddMobPacketHandle.T.yaw_raw);
    }

    public float getPitch() {
        return this.getProtocolRotation(ClientboundAddMobPacketHandle.T.pitch_raw);
    }

    public float getHeadYaw() {
        return this.getProtocolRotation(ClientboundAddMobPacketHandle.T.headYaw_raw);
    }

    public void setYaw(float yaw) {
        this.setProtocolRotation(ClientboundAddMobPacketHandle.T.yaw_raw, yaw);
    }

    public void setPitch(float pitch) {
        this.setProtocolRotation(ClientboundAddMobPacketHandle.T.pitch_raw, pitch);
    }

    public void setHeadYaw(float headYaw) {
        this.setProtocolRotation(ClientboundAddMobPacketHandle.T.headYaw_raw, headYaw);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public static final class ClientboundAddMobPacketClass
    extends Template.Class<ClientboundAddMobPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<UUID> entityUUID = new Template.Field();
        @Template.Optional
        public final Template.Field.Byte pitch_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte yaw_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte headYaw_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Converted<DataWatcher> opt_dataWatcher = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<ClientboundAddMobPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<CommonEntityType> getCommonEntityType = new Template.Method();
        public final Template.Method<Void> setCommonEntityType = new Template.Method();
        public final Template.Method<Double> getPosX = new Template.Method();
        public final Template.Method<Double> getPosY = new Template.Method();
        public final Template.Method<Double> getPosZ = new Template.Method();
        public final Template.Method<Void> setPosX = new Template.Method();
        public final Template.Method<Void> setPosY = new Template.Method();
        public final Template.Method<Void> setPosZ = new Template.Method();
        public final Template.Method.Converted<Vector> getMotVector = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setMotVector = new Template.Method.Converted();
        public final Template.Method<Double> getMotX = new Template.Method();
        public final Template.Method<Double> getMotY = new Template.Method();
        public final Template.Method<Double> getMotZ = new Template.Method();
        public final Template.Method<Void> setMotX = new Template.Method();
        public final Template.Method<Void> setMotY = new Template.Method();
        public final Template.Method<Void> setMotZ = new Template.Method();
    }
}

