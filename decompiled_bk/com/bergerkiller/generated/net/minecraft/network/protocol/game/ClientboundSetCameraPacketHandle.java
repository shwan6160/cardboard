/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetCameraPacket")
public abstract class ClientboundSetCameraPacketHandle
extends PacketHandle {
    public static final ClientboundSetCameraPacketClass T = Template.Class.create(ClientboundSetCameraPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetCameraPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetCameraPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundSetCameraPacketHandle createNew() {
        return ClientboundSetCameraPacketHandle.T.createNew.invoke();
    }

    public static ClientboundSetCameraPacketHandle createNew(int entityId) {
        ClientboundSetCameraPacketHandle packet = ClientboundSetCameraPacketHandle.createNew();
        packet.setEntityId(entityId);
        return packet;
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public static final class ClientboundSetCameraPacketClass
    extends Template.Class<ClientboundSetCameraPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.StaticMethod.Converted<ClientboundSetCameraPacketHandle> createNew = new Template.StaticMethod.Converted();
    }
}

