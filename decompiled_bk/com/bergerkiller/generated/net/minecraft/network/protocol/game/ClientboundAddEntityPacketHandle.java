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
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypeHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundAddEntityPacket")
public abstract class ClientboundAddEntityPacketHandle
extends PacketHandle {
    public static final ClientboundAddEntityPacketClass T = Template.Class.create(ClientboundAddEntityPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundAddEntityPacketHandle createHandle(Object handleInstance) {
        return (ClientboundAddEntityPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundAddEntityPacketHandle createNew() {
        return ClientboundAddEntityPacketHandle.T.createNew.invoke();
    }

    public abstract float getYaw();

    public abstract float getPitch();

    public abstract void setYaw(float var1);

    public abstract void setPitch(float var1);

    public abstract Vector getMotVector();

    public abstract void setMotVector(Vector var1);

    public abstract double getMotX();

    public abstract double getMotY();

    public abstract double getMotZ();

    public abstract void setMotX(double var1);

    public abstract void setMotY(double var1);

    public abstract void setMotZ(double var1);

    @Override
    public PacketType getPacketType() {
        return PacketType.OUT_ENTITY_SPAWN;
    }

    public void setEntityUUID(UUID uuid) {
        if (ClientboundAddEntityPacketHandle.T.entityUUID.isAvailable()) {
            ClientboundAddEntityPacketHandle.T.entityUUID.set(this.getRaw(), uuid);
        }
    }

    public static boolean isEntityTypeSupported(EntityType type) {
        return ClientboundAddEntityPacketHandle.isCommonEntityTypeSupported(CommonEntityType.byEntityType(type));
    }

    public static boolean isCommonEntityTypeSupported(CommonEntityType commonEntityType) {
        if (commonEntityType == null) {
            return false;
        }
        if (ClientboundAddEntityPacketHandle.T.opt_entityTypeId.isAvailable()) {
            return commonEntityType.objectTypeId != -1;
        }
        return commonEntityType.nmsEntityType != null;
    }

    public void setCommonEntityType(CommonEntityType commonEntityType) {
        if (commonEntityType == null) {
            throw new IllegalArgumentException("Input CommonEntityType is null");
        }
        if (ClientboundAddEntityPacketHandle.T.opt_entityTypeId.isAvailable()) {
            if (commonEntityType.objectTypeId == -1) {
                throw new IllegalArgumentException("Input " + commonEntityType.toString() + " cannot be spawned using this packet");
            }
            ClientboundAddEntityPacketHandle.T.opt_entityTypeId.setInteger(this.getRaw(), commonEntityType.objectTypeId);
        } else {
            if (commonEntityType.nmsEntityType == null) {
                throw new IllegalArgumentException("Input " + commonEntityType.toString() + " cannot be spawned using this packet");
            }
            ClientboundAddEntityPacketHandle.T.opt_entityType.set(this.getRaw(), commonEntityType.nmsEntityType);
        }
        if (commonEntityType.objectExtraData != -1) {
            this.setExtraData(commonEntityType.objectExtraData);
        }
    }

    public CommonEntityType getCommonEntityType() {
        if (ClientboundAddEntityPacketHandle.T.opt_entityTypeId.isAvailable()) {
            int id = ClientboundAddEntityPacketHandle.T.opt_entityTypeId.getInteger(this.getRaw());
            return CommonEntityType.byObjectTypeId(id);
        }
        EntityTypeHandle nmsType = ClientboundAddEntityPacketHandle.T.opt_entityType.get(this.getRaw());
        return CommonEntityType.byNMSEntityType(nmsType);
    }

    public void setEntityType(EntityType type) {
        this.setCommonEntityType(CommonEntityType.byEntityType(type));
    }

    public EntityType getEntityType() {
        return this.getCommonEntityType().entityType;
    }

    @Deprecated
    public void setEntityTypeId(int typeId) {
        if (ClientboundAddEntityPacketHandle.T.opt_entityTypeId.isAvailable()) {
            ClientboundAddEntityPacketHandle.T.opt_entityTypeId.setInteger(this.getRaw(), typeId);
        } else {
            this.setCommonEntityType(CommonEntityType.byObjectTypeId(typeId));
        }
    }

    @Deprecated
    public int getEntityTypeId() {
        if (ClientboundAddEntityPacketHandle.T.opt_entityTypeId.isAvailable()) {
            return ClientboundAddEntityPacketHandle.T.opt_entityTypeId.getInteger(this.getRaw());
        }
        return this.getCommonEntityType().objectTypeId;
    }

    public void setFallingBlockData(BlockData blockData) {
        this.setExtraData(blockData.getCombinedId());
    }

    public double getPosX() {
        return this.getProtocolPosition(ClientboundAddEntityPacketHandle.T.posX_1_8_8, ClientboundAddEntityPacketHandle.T.posX_1_10_2);
    }

    public double getPosY() {
        return this.getProtocolPosition(ClientboundAddEntityPacketHandle.T.posY_1_8_8, ClientboundAddEntityPacketHandle.T.posY_1_10_2);
    }

    public double getPosZ() {
        return this.getProtocolPosition(ClientboundAddEntityPacketHandle.T.posZ_1_8_8, ClientboundAddEntityPacketHandle.T.posZ_1_10_2);
    }

    public void setPosX(double posX) {
        this.setProtocolPosition(ClientboundAddEntityPacketHandle.T.posX_1_8_8, ClientboundAddEntityPacketHandle.T.posX_1_10_2, posX);
    }

    public void setPosY(double posY) {
        this.setProtocolPosition(ClientboundAddEntityPacketHandle.T.posY_1_8_8, ClientboundAddEntityPacketHandle.T.posY_1_10_2, posY);
    }

    public void setPosZ(double posZ) {
        this.setProtocolPosition(ClientboundAddEntityPacketHandle.T.posZ_1_8_8, ClientboundAddEntityPacketHandle.T.posZ_1_10_2, posZ);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract int getExtraData();

    public abstract void setExtraData(int var1);

    public static final class ClientboundAddEntityPacketClass
    extends Template.Class<ClientboundAddEntityPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<UUID> entityUUID = new Template.Field();
        @Template.Optional
        public final Template.Field.Converted<EntityTypeHandle> opt_entityType = new Template.Field.Converted();
        @Template.Optional
        public final Template.Field.Integer posX_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posY_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posZ_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Double posX_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Double posY_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Double posZ_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Integer opt_entityTypeId = new Template.Field.Integer();
        public final Template.Field.Integer extraData = new Template.Field.Integer();
        public final Template.StaticMethod.Converted<ClientboundAddEntityPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Float> getYaw = new Template.Method();
        public final Template.Method<Float> getPitch = new Template.Method();
        public final Template.Method<Void> setYaw = new Template.Method();
        public final Template.Method<Void> setPitch = new Template.Method();
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

