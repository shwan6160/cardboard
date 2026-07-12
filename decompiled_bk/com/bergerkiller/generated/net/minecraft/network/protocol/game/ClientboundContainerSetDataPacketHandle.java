/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket")
public abstract class ClientboundContainerSetDataPacketHandle
extends PacketHandle {
    public static final ClientboundContainerSetDataPacketClass T = Template.Class.create(ClientboundContainerSetDataPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundContainerSetDataPacketHandle createHandle(Object handleInstance) {
        return (ClientboundContainerSetDataPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getWindowId();

    public abstract void setWindowId(int var1);

    public abstract int getId();

    public abstract void setId(int var1);

    public abstract int getValue();

    public abstract void setValue(int var1);

    public static final class ClientboundContainerSetDataPacketClass
    extends Template.Class<ClientboundContainerSetDataPacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Integer id = new Template.Field.Integer();
        public final Template.Field.Integer value = new Template.Field.Integer();
    }
}

