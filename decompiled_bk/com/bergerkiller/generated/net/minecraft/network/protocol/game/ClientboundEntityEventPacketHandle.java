/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundEntityEventPacket")
public abstract class ClientboundEntityEventPacketHandle
extends PacketHandle {
    public static final ClientboundEntityEventPacketClass T = Template.Class.create(ClientboundEntityEventPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundEntityEventPacketHandle createHandle(Object handleInstance) {
        return (ClientboundEntityEventPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract byte getEventId();

    public abstract void setEventId(byte var1);

    public static final class ClientboundEntityEventPacketClass
    extends Template.Class<ClientboundEntityEventPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Byte eventId = new Template.Field.Byte();
    }
}

