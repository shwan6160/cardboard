/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket")
public abstract class ClientboundMoveVehiclePacketHandle
extends PacketHandle {
    public static final ClientboundMoveVehiclePacketClass T = Template.Class.create(ClientboundMoveVehiclePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundMoveVehiclePacketHandle createHandle(Object handleInstance) {
        return (ClientboundMoveVehiclePacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundMoveVehiclePacketHandle createNew(double posX, double posY, double posZ, float yaw, float pitch) {
        return ClientboundMoveVehiclePacketHandle.T.createNew.invoke(posX, posY, posZ, Float.valueOf(yaw), Float.valueOf(pitch));
    }

    public abstract double getPosX();

    public abstract double getPosY();

    public abstract double getPosZ();

    public abstract float getYaw();

    public abstract float getPitch();

    public static final class ClientboundMoveVehiclePacketClass
    extends Template.Class<ClientboundMoveVehiclePacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundMoveVehiclePacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Double> getPosX = new Template.Method();
        public final Template.Method<Double> getPosY = new Template.Method();
        public final Template.Method<Double> getPosZ = new Template.Method();
        public final Template.Method<Float> getYaw = new Template.Method();
        public final Template.Method<Float> getPitch = new Template.Method();
    }
}

