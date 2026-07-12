/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundMovePlayerPacket")
public abstract class ServerboundMovePlayerPacketHandle
extends PacketHandle {
    public static final ServerboundMovePlayerPacketClass T = Template.Class.create(ServerboundMovePlayerPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundMovePlayerPacketHandle createHandle(Object handleInstance) {
        return (ServerboundMovePlayerPacketHandle)T.createHandle(handleInstance);
    }

    public abstract double getX();

    public abstract void setX(double var1);

    public abstract double getY();

    public abstract void setY(double var1);

    public abstract double getZ();

    public abstract void setZ(double var1);

    public abstract float getYaw();

    public abstract void setYaw(float var1);

    public abstract float getPitch();

    public abstract void setPitch(float var1);

    public abstract boolean isOnGround();

    public abstract void setOnGround(boolean var1);

    public abstract boolean isHasPos();

    public abstract void setHasPos(boolean var1);

    public abstract boolean isHasLook();

    public abstract void setHasLook(boolean var1);

    public static final class ServerboundMovePlayerPacketClass
    extends Template.Class<ServerboundMovePlayerPacketHandle> {
        public final Template.Field.Double x = new Template.Field.Double();
        public final Template.Field.Double y = new Template.Field.Double();
        public final Template.Field.Double z = new Template.Field.Double();
        public final Template.Field.Float yaw = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();
        public final Template.Field.Boolean hasPos = new Template.Field.Boolean();
        public final Template.Field.Boolean hasLook = new Template.Field.Boolean();
    }
}

