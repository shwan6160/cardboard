/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundTabListPacket")
public abstract class ClientboundTabListPacketHandle
extends PacketHandle {
    public static final ClientboundTabListPacketClass T = Template.Class.create(ClientboundTabListPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundTabListPacketHandle createHandle(Object handleInstance) {
        return (ClientboundTabListPacketHandle)T.createHandle(handleInstance);
    }

    public abstract ChatText getHeader();

    public abstract void setHeader(ChatText var1);

    public abstract ChatText getFooter();

    public abstract void setFooter(ChatText var1);

    public static final class ClientboundTabListPacketClass
    extends Template.Class<ClientboundTabListPacketHandle> {
        public final Template.Field.Converted<ChatText> header = new Template.Field.Converted();
        public final Template.Field.Converted<ChatText> footer = new Template.Field.Converted();
    }
}

