/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeInstanceHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket")
public abstract class ClientboundUpdateAttributesPacketHandle
extends PacketHandle {
    public static final ClientboundUpdateAttributesPacketClass T = Template.Class.create(ClientboundUpdateAttributesPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundUpdateAttributesPacketHandle createHandle(Object handleInstance) {
        return (ClientboundUpdateAttributesPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundUpdateAttributesPacketHandle createNew(int entityId, Collection<AttributeInstanceHandle> attributes) {
        return ClientboundUpdateAttributesPacketHandle.T.createNew.invoke(entityId, attributes);
    }

    public static ClientboundUpdateAttributesPacketHandle createZeroMaxHealth(int entityId) {
        return ClientboundUpdateAttributesPacketHandle.T.createZeroMaxHealth.invoke(entityId);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public static final class ClientboundUpdateAttributesPacketClass
    extends Template.Class<ClientboundUpdateAttributesPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.StaticMethod.Converted<ClientboundUpdateAttributesPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ClientboundUpdateAttributesPacketHandle> createZeroMaxHealth = new Template.StaticMethod.Converted();
    }
}

