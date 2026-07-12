/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundContainerClosePacket")
public abstract class ServerboundContainerClosePacketHandle
extends PacketHandle {
    public static final ServerboundContainerClosePacketClass T = Template.Class.create(ServerboundContainerClosePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundContainerClosePacketHandle createHandle(Object handleInstance) {
        return (ServerboundContainerClosePacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getWindowId();

    public abstract void setWindowId(int var1);

    public static final class ServerboundContainerClosePacketClass
    extends Template.Class<ServerboundContainerClosePacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
    }
}

