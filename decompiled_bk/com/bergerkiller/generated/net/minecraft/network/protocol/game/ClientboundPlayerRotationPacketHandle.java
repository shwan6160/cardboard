/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket")
public abstract class ClientboundPlayerRotationPacketHandle
extends PacketHandle {
    public static final ClientboundPlayerRotationPacketClass T = Template.Class.create(ClientboundPlayerRotationPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundPlayerRotationPacketHandle createHandle(Object handleInstance) {
        return (ClientboundPlayerRotationPacketHandle)T.createHandle(handleInstance);
    }

    public static boolean isRelativeSupported() {
        return (Boolean)ClientboundPlayerRotationPacketHandle.T.isRelativeSupported.invoker.invoke(null);
    }

    public static ClientboundPlayerRotationPacketHandle createNew(float yaw, boolean isYawRelative, float pitch, boolean isPitchRelative) {
        return ClientboundPlayerRotationPacketHandle.T.createNew.invoke(Float.valueOf(yaw), isYawRelative, Float.valueOf(pitch), isPitchRelative);
    }

    public abstract float getYaw();

    public abstract boolean isYawRelative();

    public abstract float getPitch();

    public abstract boolean isPitchRelative();

    public static ClientboundPlayerRotationPacketHandle createAbsolute(float yaw, float pitch) {
        return ClientboundPlayerRotationPacketHandle.createNew(yaw, false, pitch, false);
    }

    public static ClientboundPlayerRotationPacketHandle createRelative(float yaw, float pitch) {
        return ClientboundPlayerRotationPacketHandle.createNew(yaw, true, pitch, true);
    }

    public static final class ClientboundPlayerRotationPacketClass
    extends Template.Class<ClientboundPlayerRotationPacketHandle> {
        public final Template.StaticMethod<Boolean> isRelativeSupported = new Template.StaticMethod();
        public final Template.StaticMethod.Converted<ClientboundPlayerRotationPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Float> getYaw = new Template.Method();
        public final Template.Method<Boolean> isYawRelative = new Template.Method();
        public final Template.Method<Float> getPitch = new Template.Method();
        public final Template.Method<Boolean> isPitchRelative = new Template.Method();
    }
}

