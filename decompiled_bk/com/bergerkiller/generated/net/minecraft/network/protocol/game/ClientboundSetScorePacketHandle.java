/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetScorePacket")
public abstract class ClientboundSetScorePacketHandle
extends PacketHandle {
    public static final ClientboundSetScorePacketClass T = Template.Class.create(ClientboundSetScorePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetScorePacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetScorePacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundSetScorePacketHandle createNew(String name, String objectiveName, int score) {
        return ClientboundSetScorePacketHandle.T.createNew.invoke(name, objectiveName, score);
    }

    public abstract String getName();

    public abstract void setName(String var1);

    public abstract String getObjName();

    public abstract void setObjName(String var1);

    public abstract int getValue();

    public abstract void setValue(int var1);

    public static final class ClientboundSetScorePacketClass
    extends Template.Class<ClientboundSetScorePacketHandle> {
        public final Template.Field<String> name = new Template.Field();
        public final Template.Field<String> objName = new Template.Field();
        public final Template.Field.Integer value = new Template.Field.Integer();
        public final Template.StaticMethod.Converted<ClientboundSetScorePacketHandle> createNew = new Template.StaticMethod.Converted();
    }
}

