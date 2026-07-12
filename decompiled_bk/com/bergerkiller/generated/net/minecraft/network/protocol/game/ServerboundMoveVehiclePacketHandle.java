/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket")
public abstract class ServerboundMoveVehiclePacketHandle
extends PacketHandle {
    public static final ServerboundMoveVehiclePacketClass T = Template.Class.create(ServerboundMoveVehiclePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundMoveVehiclePacketHandle createHandle(Object handleInstance) {
        return (ServerboundMoveVehiclePacketHandle)T.createHandle(handleInstance);
    }

    public static ServerboundMoveVehiclePacketHandle createNew(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
        return ServerboundMoveVehiclePacketHandle.T.createNew.invokeVA(posX, posY, posZ, Float.valueOf(yaw), Float.valueOf(pitch), onGround);
    }

    public abstract double getPosX();

    public abstract double getPosY();

    public abstract double getPosZ();

    public abstract float getYaw();

    public abstract float getPitch();

    public abstract boolean isOnGround();

    public static final class ServerboundMoveVehiclePacketClass
    extends Template.Class<ServerboundMoveVehiclePacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundMoveVehiclePacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Double> getPosX = new Template.Method();
        public final Template.Method<Double> getPosY = new Template.Method();
        public final Template.Method<Double> getPosZ = new Template.Method();
        public final Template.Method<Float> getYaw = new Template.Method();
        public final Template.Method<Float> getPitch = new Template.Method();
        public final Template.Method<Boolean> isOnGround = new Template.Method();
    }
}

