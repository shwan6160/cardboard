/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.common.ServerboundKeepAlivePacket")
public abstract class ServerboundKeepAlivePacketHandle
extends PacketHandle {
    public static final ServerboundKeepAlivePacketClass T = Template.Class.create(ServerboundKeepAlivePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundKeepAlivePacketHandle createHandle(Object handleInstance) {
        return (ServerboundKeepAlivePacketHandle)T.createHandle(handleInstance);
    }

    public abstract long getKey();

    public abstract void setKey(long var1);

    public static final class ServerboundKeepAlivePacketClass
    extends Template.Class<ServerboundKeepAlivePacketHandle> {
        public final Template.Method<Long> getKey = new Template.Method();
        public final Template.Method<Void> setKey = new Template.Method();
    }
}

