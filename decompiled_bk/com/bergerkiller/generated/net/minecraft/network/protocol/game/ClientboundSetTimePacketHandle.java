/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetTimePacket")
public abstract class ClientboundSetTimePacketHandle
extends PacketHandle {
    public static final ClientboundSetTimePacketClass T = Template.Class.create(ClientboundSetTimePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetTimePacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetTimePacketHandle)T.createHandle(handleInstance);
    }

    public abstract long getGameTime();

    public abstract void setGameTime(long var1);

    public static final class ClientboundSetTimePacketClass
    extends Template.Class<ClientboundSetTimePacketHandle> {
        public final Template.Field.Long gameTime = new Template.Field.Long();
    }
}

