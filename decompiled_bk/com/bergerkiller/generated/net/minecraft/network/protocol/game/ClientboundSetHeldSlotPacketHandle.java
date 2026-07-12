/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket")
public abstract class ClientboundSetHeldSlotPacketHandle
extends PacketHandle {
    public static final ClientboundSetHeldSlotPacketClass T = Template.Class.create(ClientboundSetHeldSlotPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetHeldSlotPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetHeldSlotPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getItemInHandIndex();

    public abstract void setItemInHandIndex(int var1);

    public static final class ClientboundSetHeldSlotPacketClass
    extends Template.Class<ClientboundSetHeldSlotPacketHandle> {
        public final Template.Field.Integer itemInHandIndex = new Template.Field.Integer();
    }
}

