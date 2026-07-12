/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundMoveEntityPacket")
public abstract class ClientboundMoveEntityPacketHandle
extends PacketHandle {
    public static final ClientboundMoveEntityPacketClass T = Template.Class.create(ClientboundMoveEntityPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundMoveEntityPacketHandle createHandle(Object handleInstance) {
        return (ClientboundMoveEntityPacketHandle)T.createHandle(handleInstance);
    }

    public abstract double getDeltaX();

    public abstract double getDeltaY();

    public abstract double getDeltaZ();

    public abstract void setDeltaX(double var1);

    public abstract void setDeltaY(double var1);

    public abstract void setDeltaZ(double var1);

    public abstract float getYaw();

    public abstract float getPitch();

    public abstract void setYaw(float var1);

    public abstract void setPitch(float var1);

    @Deprecated
    public float getDeltaYaw() {
        return this.getYaw();
    }

    @Deprecated
    public float getDeltaPitch() {
        return this.getPitch();
    }

    @Deprecated
    public void setDeltaYaw(float deltaYaw) {
        this.setYaw(deltaYaw);
    }

    @Deprecated
    public void setDeltaPitch(float deltaPitch) {
        this.setPitch(deltaPitch);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract boolean isOnGround();

    public abstract void setOnGround(boolean var1);

    public static final class ClientboundMoveEntityPacketClass
    extends Template.Class<ClientboundMoveEntityPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();
        public final Template.Method<Double> getDeltaX = new Template.Method();
        public final Template.Method<Double> getDeltaY = new Template.Method();
        public final Template.Method<Double> getDeltaZ = new Template.Method();
        public final Template.Method<Void> setDeltaX = new Template.Method();
        public final Template.Method<Void> setDeltaY = new Template.Method();
        public final Template.Method<Void> setDeltaZ = new Template.Method();
        public final Template.Method<Float> getYaw = new Template.Method();
        public final Template.Method<Float> getPitch = new Template.Method();
        public final Template.Method<Void> setYaw = new Template.Method();
        public final Template.Method<Void> setPitch = new Template.Method();
    }

    @Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.PosRot")
    public static abstract class PosRotHandle
    extends ClientboundMoveEntityPacketHandle {
        public static final PosRotClass T = Template.Class.create(PosRotClass.class, Common.TEMPLATE_RESOLVER);

        public static PosRotHandle createHandle(Object handleInstance) {
            return (PosRotHandle)T.createHandle(handleInstance);
        }

        public static PosRotHandle createNew() {
            return PosRotHandle.T.createNew.invoke();
        }

        @Override
        public PacketType getPacketType() {
            return PacketType.OUT_ENTITY_MOVE_LOOK;
        }

        public static PosRotHandle createNew(int entityId, double dx, double dy, double dz, float yaw, float pitch, boolean onGround) {
            PosRotHandle handle = PosRotHandle.createNew();
            handle.setEntityId(entityId);
            handle.setDeltaX(dx);
            handle.setDeltaY(dy);
            handle.setDeltaZ(dz);
            handle.setYaw(yaw);
            handle.setPitch(pitch);
            handle.setOnGround(onGround);
            return handle;
        }

        public static final class PosRotClass
        extends Template.Class<PosRotHandle> {
            public final Template.StaticMethod.Converted<PosRotHandle> createNew = new Template.StaticMethod.Converted();
        }
    }

    @Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Pos")
    public static abstract class PosHandle
    extends ClientboundMoveEntityPacketHandle {
        public static final PosClass T = Template.Class.create(PosClass.class, Common.TEMPLATE_RESOLVER);

        public static PosHandle createHandle(Object handleInstance) {
            return (PosHandle)T.createHandle(handleInstance);
        }

        public static PosHandle createNew() {
            return PosHandle.T.createNew.invoke();
        }

        @Override
        public PacketType getPacketType() {
            return PacketType.OUT_ENTITY_MOVE;
        }

        public static PosHandle createNew(int entityId, double dx, double dy, double dz, boolean onGround) {
            PosHandle handle = PosHandle.createNew();
            handle.setEntityId(entityId);
            handle.setDeltaX(dx);
            handle.setDeltaY(dy);
            handle.setDeltaZ(dz);
            handle.setOnGround(onGround);
            return handle;
        }

        public static final class PosClass
        extends Template.Class<PosHandle> {
            public final Template.StaticMethod.Converted<PosHandle> createNew = new Template.StaticMethod.Converted();
        }
    }

    @Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Rot")
    public static abstract class RotHandle
    extends ClientboundMoveEntityPacketHandle {
        public static final RotClass T = Template.Class.create(RotClass.class, Common.TEMPLATE_RESOLVER);

        public static RotHandle createHandle(Object handleInstance) {
            return (RotHandle)T.createHandle(handleInstance);
        }

        public static RotHandle createNew() {
            return RotHandle.T.createNew.invoke();
        }

        @Override
        public PacketType getPacketType() {
            return PacketType.OUT_ENTITY_LOOK;
        }

        public static RotHandle createNew(int entityId, float yaw, float pitch, boolean onGround) {
            RotHandle handle = RotHandle.createNew();
            handle.setEntityId(entityId);
            handle.setYaw(yaw);
            handle.setPitch(pitch);
            handle.setOnGround(onGround);
            return handle;
        }

        public static final class RotClass
        extends Template.Class<RotHandle> {
            public final Template.StaticMethod.Converted<RotHandle> createNew = new Template.StaticMethod.Converted();
        }
    }
}

