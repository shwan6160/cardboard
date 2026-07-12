/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.common.ClientboundDisconnectPacket")
public abstract class ClientboundDisconnectPacketHandle
extends PacketHandle {
    public static final ClientboundDisconnectPacketClass T = Template.Class.create(ClientboundDisconnectPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundDisconnectPacketHandle createHandle(Object handleInstance) {
        return (ClientboundDisconnectPacketHandle)T.createHandle(handleInstance);
    }

    public abstract ChatText getReason();

    public abstract void setReason(ChatText var1);

    public static final class ClientboundDisconnectPacketClass
    extends Template.Class<ClientboundDisconnectPacketHandle> {
        public final Template.Field.Converted<ChatText> reason = new Template.Field.Converted();
    }
}

