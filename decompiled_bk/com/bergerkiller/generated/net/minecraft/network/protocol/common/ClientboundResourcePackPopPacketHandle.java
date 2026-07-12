/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Optional;
import java.util.UUID;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket")
public abstract class ClientboundResourcePackPopPacketHandle
extends Template.Handle {
    public static final ClientboundResourcePackPopPacketClass T = Template.Class.create(ClientboundResourcePackPopPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundResourcePackPopPacketHandle createHandle(Object handleInstance) {
        return (ClientboundResourcePackPopPacketHandle)T.createHandle(handleInstance);
    }

    public abstract Optional<UUID> getId();

    public static final class ClientboundResourcePackPopPacketClass
    extends Template.Class<ClientboundResourcePackPopPacketHandle> {
        public final Template.Method<Optional<UUID>> getId = new Template.Method();
    }
}

