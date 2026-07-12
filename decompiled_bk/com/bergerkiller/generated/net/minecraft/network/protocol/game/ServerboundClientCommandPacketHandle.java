/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundClientCommandPacket")
public abstract class ServerboundClientCommandPacketHandle
extends PacketHandle {
    public static final ServerboundClientCommandPacketClass T = Template.Class.create(ServerboundClientCommandPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundClientCommandPacketHandle createHandle(Object handleInstance) {
        return (ServerboundClientCommandPacketHandle)T.createHandle(handleInstance);
    }

    public abstract Object getAction();

    public abstract void setAction(Object var1);

    public static final class ServerboundClientCommandPacketClass
    extends Template.Class<ServerboundClientCommandPacketHandle> {
        public final Template.Field.Converted<Object> action = new Template.Field.Converted();
    }
}

