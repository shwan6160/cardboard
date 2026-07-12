/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import java.util.UUID;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket")
public abstract class ClientboundPlayerInfoRemovePacketHandle
extends PacketHandle {
    public static final ClientboundPlayerInfoRemovePacketClass T = Template.Class.create(ClientboundPlayerInfoRemovePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundPlayerInfoRemovePacketHandle createHandle(Object handleInstance) {
        return (ClientboundPlayerInfoRemovePacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundPlayerInfoRemovePacketHandle createNew(List<UUID> profileIds) {
        return ClientboundPlayerInfoRemovePacketHandle.T.createNew.invoke(profileIds);
    }

    public abstract List<UUID> getProfileIds();

    public abstract void setProfileIds(List<UUID> var1);

    public static final class ClientboundPlayerInfoRemovePacketClass
    extends Template.Class<ClientboundPlayerInfoRemovePacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundPlayerInfoRemovePacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<List<UUID>> getProfileIds = new Template.Method();
        public final Template.Method<Void> setProfileIds = new Template.Method();
    }
}

