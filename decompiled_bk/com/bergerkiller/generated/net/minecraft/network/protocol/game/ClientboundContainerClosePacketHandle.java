/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundContainerClosePacket")
public abstract class ClientboundContainerClosePacketHandle
extends PacketHandle {
    public static final ClientboundContainerClosePacketClass T = Template.Class.create(ClientboundContainerClosePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundContainerClosePacketHandle createHandle(Object handleInstance) {
        return (ClientboundContainerClosePacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getWindowId();

    public abstract void setWindowId(int var1);

    public static final class ClientboundContainerClosePacketClass
    extends Template.Class<ClientboundContainerClosePacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
    }
}

