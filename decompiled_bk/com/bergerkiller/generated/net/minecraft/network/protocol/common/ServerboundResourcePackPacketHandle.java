/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.common.ServerboundResourcePackPacket")
public abstract class ServerboundResourcePackPacketHandle
extends PacketHandle {
    public static final ServerboundResourcePackPacketClass T = Template.Class.create(ServerboundResourcePackPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundResourcePackPacketHandle createHandle(Object handleInstance) {
        return (ServerboundResourcePackPacketHandle)T.createHandle(handleInstance);
    }

    public abstract Object getStatus();

    public abstract void setStatus(Object var1);

    public static final class ServerboundResourcePackPacketClass
    extends Template.Class<ServerboundResourcePackPacketHandle> {
        public final Template.Field.Converted<Object> status = new Template.Field.Converted();
    }
}

