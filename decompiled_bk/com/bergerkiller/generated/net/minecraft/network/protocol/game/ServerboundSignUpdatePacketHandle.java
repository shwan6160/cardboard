/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundSignUpdatePacket")
public abstract class ServerboundSignUpdatePacketHandle
extends PacketHandle {
    public static final ServerboundSignUpdatePacketClass T = Template.Class.create(ServerboundSignUpdatePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundSignUpdatePacketHandle createHandle(Object handleInstance) {
        return (ServerboundSignUpdatePacketHandle)T.createHandle(handleInstance);
    }

    public abstract IntVector3 getPosition();

    public abstract void setPosition(IntVector3 var1);

    public abstract ChatText[] getLines();

    public abstract void setLines(ChatText[] var1);

    public static final class ServerboundSignUpdatePacketClass
    extends Template.Class<ServerboundSignUpdatePacketHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted();
        public final Template.Field.Converted<ChatText[]> lines = new Template.Field.Converted();
    }
}

