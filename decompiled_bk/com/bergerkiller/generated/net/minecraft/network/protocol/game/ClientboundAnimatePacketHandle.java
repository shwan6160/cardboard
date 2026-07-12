/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundAnimatePacket")
public abstract class ClientboundAnimatePacketHandle
extends PacketHandle {
    public static final ClientboundAnimatePacketClass T = Template.Class.create(ClientboundAnimatePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundAnimatePacketHandle createHandle(Object handleInstance) {
        return (ClientboundAnimatePacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract int getAction();

    public abstract void setAction(int var1);

    public static final class ClientboundAnimatePacketClass
    extends Template.Class<ClientboundAnimatePacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Integer action = new Template.Field.Integer();
    }
}

