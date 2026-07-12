/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket")
public abstract class ClientboundCustomPayloadPacketHandle
extends PacketHandle {
    public static final ClientboundCustomPayloadPacketClass T = Template.Class.create(ClientboundCustomPayloadPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundCustomPayloadPacketHandle createHandle(Object handleInstance) {
        return (ClientboundCustomPayloadPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundCustomPayloadPacketHandle createNew(String channel, byte[] message) {
        return ClientboundCustomPayloadPacketHandle.T.createNew.invoke(channel, message);
    }

    public abstract String getChannel();

    public abstract byte[] getMessage();

    @Override
    public PacketType getPacketType() {
        return PacketType.OUT_CUSTOM_PAYLOAD;
    }

    public static final class ClientboundCustomPayloadPacketClass
    extends Template.Class<ClientboundCustomPayloadPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundCustomPayloadPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<String> getChannel = new Template.Method();
        public final Template.Method<byte[]> getMessage = new Template.Method();
    }
}

