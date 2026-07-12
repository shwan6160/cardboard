/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket")
public abstract class ClientboundEntityPositionSyncPacketHandle
extends PacketHandle {
    public static final ClientboundEntityPositionSyncPacketClass T = Template.Class.create(ClientboundEntityPositionSyncPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundEntityPositionSyncPacketHandle createHandle(Object handleInstance) {
        return (ClientboundEntityPositionSyncPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundEntityPositionSyncPacketHandle createNewForEntity(Entity entity) {
        return ClientboundEntityPositionSyncPacketHandle.T.createNewForEntity.invoke(entity);
    }

    public static ClientboundEntityPositionSyncPacketHandle createNew(int entityId, double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
        return ClientboundEntityPositionSyncPacketHandle.T.createNew.invokeVA(entityId, posX, posY, posZ, Float.valueOf(yaw), Float.valueOf(pitch), onGround);
    }

    public abstract int getEntityId();

    public abstract double getPosX();

    public abstract double getPosY();

    public abstract double getPosZ();

    public abstract float getYaw();

    public abstract float getPitch();

    public abstract boolean isOnGround();

    public abstract int getEncodedPosX();

    public abstract int getEncodedPosY();

    public abstract int getEncodedPosZ();

    public abstract int getEncodedYaw();

    public abstract int getEncodedPitch();

    @Override
    public PacketType getPacketType() {
        return PacketType.OUT_ENTITY_TELEPORT;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class ClientboundEntityPositionSyncPacketClass
    extends Template.Class<ClientboundEntityPositionSyncPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundEntityPositionSyncPacketHandle> createNewForEntity = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ClientboundEntityPositionSyncPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Integer> getEntityId = new Template.Method();
        public final Template.Method<Double> getPosX = new Template.Method();
        public final Template.Method<Double> getPosY = new Template.Method();
        public final Template.Method<Double> getPosZ = new Template.Method();
        public final Template.Method<Float> getYaw = new Template.Method();
        public final Template.Method<Float> getPitch = new Template.Method();
        public final Template.Method<Boolean> isOnGround = new Template.Method();
        public final Template.Method<Integer> getEncodedPosX = new Template.Method();
        public final Template.Method<Integer> getEncodedPosY = new Template.Method();
        public final Template.Method<Integer> getEncodedPosZ = new Template.Method();
        public final Template.Method<Integer> getEncodedYaw = new Template.Method();
        public final Template.Method<Integer> getEncodedPitch = new Template.Method();
    }

    public static class Builder {
        private int entityId;
        private double posX;
        private double posY;
        private double posZ;
        private float yaw;
        private float pitch;
        private boolean onGround;

        public Builder entityId(int entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder posX(double posX) {
            this.posX = posX;
            return this;
        }

        public Builder posY(double posY) {
            this.posY = posY;
            return this;
        }

        public Builder posZ(double posZ) {
            this.posZ = posZ;
            return this;
        }

        public Builder position(Vector position) {
            return this.position(position.getX(), position.getY(), position.getZ());
        }

        public Builder position(double x, double y, double z) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            return this;
        }

        public Builder yaw(float yaw) {
            this.yaw = yaw;
            return this;
        }

        public Builder pitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        public Builder rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
            return this;
        }

        public Builder onGround(boolean onGround) {
            this.onGround = onGround;
            return this;
        }

        public ClientboundEntityPositionSyncPacketHandle create() {
            return ClientboundEntityPositionSyncPacketHandle.createNew(this.entityId, this.posX, this.posY, this.posZ, this.yaw, this.pitch, this.onGround);
        }
    }
}

