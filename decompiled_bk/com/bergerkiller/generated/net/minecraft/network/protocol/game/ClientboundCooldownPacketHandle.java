/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundCooldownPacket")
public abstract class ClientboundCooldownPacketHandle
extends PacketHandle {
    public static final ClientboundCooldownPacketClass T = Template.Class.create(ClientboundCooldownPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundCooldownPacketHandle createHandle(Object handleInstance) {
        return (ClientboundCooldownPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getCooldown();

    public abstract void setCooldown(int var1);

    public static final class ClientboundCooldownPacketClass
    extends Template.Class<ClientboundCooldownPacketHandle> {
        public final Template.Field.Integer cooldown = new Template.Field.Integer();
    }
}

