/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundChatPacket")
public abstract class ServerboundChatPacketHandle
extends PacketHandle {
    public static final ServerboundChatPacketClass T = Template.Class.create(ServerboundChatPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundChatPacketHandle createHandle(Object handleInstance) {
        return (ServerboundChatPacketHandle)T.createHandle(handleInstance);
    }

    public abstract String getMessage();

    public abstract void setMessage(String var1);

    public static final class ServerboundChatPacketClass
    extends Template.Class<ServerboundChatPacketHandle> {
        public final Template.Field<String> message = new Template.Field();
    }
}

