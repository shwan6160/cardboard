/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetObjectivePacket")
public abstract class ClientboundSetObjectivePacketHandle
extends PacketHandle {
    public static final ClientboundSetObjectivePacketClass T = Template.Class.create(ClientboundSetObjectivePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetObjectivePacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetObjectivePacketHandle)T.createHandle(handleInstance);
    }

    public abstract String getName();

    public abstract void setName(String var1);

    public abstract ChatText getDisplayName();

    public abstract void setDisplayName(ChatText var1);

    public abstract Object getCriteria();

    public abstract void setCriteria(Object var1);

    public abstract int getAction();

    public abstract void setAction(int var1);

    public static final class ClientboundSetObjectivePacketClass
    extends Template.Class<ClientboundSetObjectivePacketHandle> {
        public final Template.Field<String> name = new Template.Field();
        public final Template.Field.Converted<ChatText> displayName = new Template.Field.Converted();
        public final Template.Field.Converted<Object> criteria = new Template.Field.Converted();
        public final Template.Field.Integer action = new Template.Field.Integer();
    }
}

