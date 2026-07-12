/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundLevelEventPacket")
public abstract class ClientboundLevelEventPacketHandle
extends PacketHandle {
    public static final ClientboundLevelEventPacketClass T = Template.Class.create(ClientboundLevelEventPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundLevelEventPacketHandle createHandle(Object handleInstance) {
        return (ClientboundLevelEventPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getEffectId();

    public abstract void setEffectId(int var1);

    public abstract IntVector3 getPosition();

    public abstract void setPosition(IntVector3 var1);

    public abstract int getData();

    public abstract void setData(int var1);

    public abstract boolean isGlobalEvent();

    public abstract void setGlobalEvent(boolean var1);

    public static final class ClientboundLevelEventPacketClass
    extends Template.Class<ClientboundLevelEventPacketHandle> {
        public final Template.Field.Integer effectId = new Template.Field.Integer();
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted();
        public final Template.Field.Integer data = new Template.Field.Integer();
        public final Template.Field.Boolean globalEvent = new Template.Field.Boolean();
    }
}

