/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket")
public abstract class ClientboundTakeItemEntityPacketHandle
extends PacketHandle {
    public static final ClientboundTakeItemEntityPacketClass T = Template.Class.create(ClientboundTakeItemEntityPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundTakeItemEntityPacketHandle createHandle(Object handleInstance) {
        return (ClientboundTakeItemEntityPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getCollectedItemId();

    public abstract void setCollectedItemId(int var1);

    public abstract int getCollectorEntityId();

    public abstract void setCollectorEntityId(int var1);

    public static final class ClientboundTakeItemEntityPacketClass
    extends Template.Class<ClientboundTakeItemEntityPacketHandle> {
        public final Template.Field.Integer collectedItemId = new Template.Field.Integer();
        public final Template.Field.Integer collectorEntityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer amount = new Template.Field.Integer();
    }
}

