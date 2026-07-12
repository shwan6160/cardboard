/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Location;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket")
public abstract class ClientboundPlayerPositionPacketHandle
extends PacketHandle {
    public static final ClientboundPlayerPositionPacketClass T = Template.Class.create(ClientboundPlayerPositionPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundPlayerPositionPacketHandle createHandle(Object handleInstance) {
        return (ClientboundPlayerPositionPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundPlayerPositionPacketHandle createNew(double x, double y, double z, float yaw, float pitch, double deltaX, double deltaY, double deltaZ, RelativeFlags relativeFlags, int teleportWaitTimer) {
        return ClientboundPlayerPositionPacketHandle.T.createNew.invokeVA(x, y, z, Float.valueOf(yaw), Float.valueOf(pitch), deltaX, deltaY, deltaZ, relativeFlags, teleportWaitTimer);
    }

    public abstract double getX();

    public abstract double getY();

    public abstract double getZ();

    public abstract float getYaw();

    public abstract float getPitch();

    public abstract RelativeFlags getRelativeFlags();

    public abstract int getTeleportWaitTimer();

    public static ClientboundPlayerPositionPacketHandle createNew(double x, double y, double z, float yaw, float pitch, double deltaX, double deltaY, double deltaZ, RelativeFlags relativeFlags) {
        return ClientboundPlayerPositionPacketHandle.createNew(x, y, z, yaw, pitch, deltaX, deltaY, deltaZ, relativeFlags, 0);
    }

    public static ClientboundPlayerPositionPacketHandle createNew(double x, double y, double z, float yaw, float pitch, RelativeFlags relativeFlags) {
        return ClientboundPlayerPositionPacketHandle.createNew(x, y, z, yaw, pitch, 0.0, 0.0, 0.0, relativeFlags, 0);
    }

    public static ClientboundPlayerPositionPacketHandle createRelative(double dx, double dy, double dz, float dyaw, float dpitch) {
        return ClientboundPlayerPositionPacketHandle.createNew(dx, dy, dz, dyaw, dpitch, RelativeFlags.RELATIVE_POSITION_ROTATION);
    }

    public static ClientboundPlayerPositionPacketHandle createAbsolute(Location location) {
        return ClientboundPlayerPositionPacketHandle.createAbsolute(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static ClientboundPlayerPositionPacketHandle createAbsolute(double x, double y, double z, float yaw, float pitch) {
        return ClientboundPlayerPositionPacketHandle.createNew(x, y, z, yaw, pitch, RelativeFlags.ABSOLUTE_POSITION);
    }

    public static final class ClientboundPlayerPositionPacketClass
    extends Template.Class<ClientboundPlayerPositionPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundPlayerPositionPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Double> getX = new Template.Method();
        public final Template.Method<Double> getY = new Template.Method();
        public final Template.Method<Double> getZ = new Template.Method();
        public final Template.Method<Float> getYaw = new Template.Method();
        public final Template.Method<Float> getPitch = new Template.Method();
        public final Template.Method.Converted<RelativeFlags> getRelativeFlags = new Template.Method.Converted();
        public final Template.Method<Integer> getTeleportWaitTimer = new Template.Method();
    }
}

