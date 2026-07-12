/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundRotateHeadPacket")
public abstract class ClientboundRotateHeadPacketHandle
extends PacketHandle {
    public static final ClientboundRotateHeadPacketClass T = Template.Class.create(ClientboundRotateHeadPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundRotateHeadPacketHandle createHandle(Object handleInstance) {
        return (ClientboundRotateHeadPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundRotateHeadPacketHandle createNew(Entity entity, float headYaw) {
        return ClientboundRotateHeadPacketHandle.T.createNew.invoke(entity, Float.valueOf(headYaw));
    }

    public abstract float getHeadYaw();

    public abstract void setHeadYaw(float var1);

    public static ClientboundRotateHeadPacketHandle createNew() {
        return ClientboundRotateHeadPacketHandle.T.createNewEmpty.invoke();
    }

    public static ClientboundRotateHeadPacketHandle createNew(int entityId, float headYaw) {
        ClientboundRotateHeadPacketHandle packet = ClientboundRotateHeadPacketHandle.createNew();
        packet.setEntityId(entityId);
        packet.setHeadYaw(headYaw);
        return packet;
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public static final class ClientboundRotateHeadPacketClass
    extends Template.Class<ClientboundRotateHeadPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.StaticMethod.Converted<ClientboundRotateHeadPacketHandle> createNewEmpty = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ClientboundRotateHeadPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Float> getHeadYaw = new Template.Method();
        public final Template.Method<Void> setHeadYaw = new Template.Method();
    }
}

