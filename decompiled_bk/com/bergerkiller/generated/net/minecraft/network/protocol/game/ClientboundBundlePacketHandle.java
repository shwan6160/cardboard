/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.function.Predicate;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundBundlePacket")
public abstract class ClientboundBundlePacketHandle
extends PacketHandle {
    public static final ClientboundBundlePacketClass T = Template.Class.create(ClientboundBundlePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundBundlePacketHandle createHandle(Object handleInstance) {
        return (ClientboundBundlePacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundBundlePacketHandle createNew(Iterable<Object> rawPackets) {
        return ClientboundBundlePacketHandle.T.createNew.invoke(rawPackets);
    }

    public abstract Iterable<Object> subPackets();

    public abstract void setSubPackets(Iterable<Object> var1);

    public abstract boolean filterSubPackets(Predicate<Object> var1);

    public static final class ClientboundBundlePacketClass
    extends Template.Class<ClientboundBundlePacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundBundlePacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<Iterable<Object>> subPackets = new Template.Method.Converted();
        public final Template.Method<Void> setSubPackets = new Template.Method();
        public final Template.Method<Boolean> filterSubPackets = new Template.Method();
    }
}

