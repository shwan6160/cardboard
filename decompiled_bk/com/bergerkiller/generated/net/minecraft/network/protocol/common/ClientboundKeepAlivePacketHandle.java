/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.common.ClientboundKeepAlivePacket")
public abstract class ClientboundKeepAlivePacketHandle
extends PacketHandle {
    public static final ClientboundKeepAlivePacketClass T = Template.Class.create(ClientboundKeepAlivePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundKeepAlivePacketHandle createHandle(Object handleInstance) {
        return (ClientboundKeepAlivePacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundKeepAlivePacketHandle createNew(long key) {
        return ClientboundKeepAlivePacketHandle.T.createNew.invoke(key);
    }

    public abstract long getKey();

    public abstract void setKey(long var1);

    public static final class ClientboundKeepAlivePacketClass
    extends Template.Class<ClientboundKeepAlivePacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundKeepAlivePacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Long> getKey = new Template.Method();
        public final Template.Method<Void> setKey = new Template.Method();
    }
}

