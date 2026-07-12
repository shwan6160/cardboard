/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket")
public abstract class ClientboundUpdateAdvancementsPacketHandle
extends PacketHandle {
    public static final ClientboundUpdateAdvancementsPacketClass T = Template.Class.create(ClientboundUpdateAdvancementsPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundUpdateAdvancementsPacketHandle createHandle(Object handleInstance) {
        return (ClientboundUpdateAdvancementsPacketHandle)T.createHandle(handleInstance);
    }

    public abstract boolean isInitial();

    public abstract void setInitial(boolean var1);

    public static final class ClientboundUpdateAdvancementsPacketClass
    extends Template.Class<ClientboundUpdateAdvancementsPacketHandle> {
        public final Template.Field.Boolean initial = new Template.Field.Boolean();
    }
}

