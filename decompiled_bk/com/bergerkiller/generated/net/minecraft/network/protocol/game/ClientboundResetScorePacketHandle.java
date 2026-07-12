/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundResetScorePacket")
public abstract class ClientboundResetScorePacketHandle
extends PacketHandle {
    public static final ClientboundResetScorePacketClass T = Template.Class.create(ClientboundResetScorePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundResetScorePacketHandle createHandle(Object handleInstance) {
        return (ClientboundResetScorePacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundResetScorePacketHandle createNew(String name, String objectiveName) {
        return ClientboundResetScorePacketHandle.T.createNew.invoke(name, objectiveName);
    }

    public abstract String getName();

    public abstract void setName(String var1);

    public abstract String getObjName();

    public abstract void setObjName(String var1);

    public static final class ClientboundResetScorePacketClass
    extends Template.Class<ClientboundResetScorePacketHandle> {
        public final Template.Field<String> name = new Template.Field();
        public final Template.Field<String> objName = new Template.Field();
        public final Template.StaticMethod.Converted<ClientboundResetScorePacketHandle> createNew = new Template.StaticMethod.Converted();
    }
}

