/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket")
public abstract class ServerboundTeleportToEntityPacketHandle
extends PacketHandle {
    public static final ServerboundTeleportToEntityPacketClass T = Template.Class.create(ServerboundTeleportToEntityPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundTeleportToEntityPacketHandle createHandle(Object handleInstance) {
        return (ServerboundTeleportToEntityPacketHandle)T.createHandle(handleInstance);
    }

    public static final ServerboundTeleportToEntityPacketHandle createNew(UUID uuid) {
        return ServerboundTeleportToEntityPacketHandle.T.constr_uuid.newInstance(uuid);
    }

    public abstract UUID getUuid();

    public abstract void setUuid(UUID var1);

    public static final class ServerboundTeleportToEntityPacketClass
    extends Template.Class<ServerboundTeleportToEntityPacketHandle> {
        public final Template.Constructor.Converted<ServerboundTeleportToEntityPacketHandle> constr_uuid = new Template.Constructor.Converted();
        public final Template.Field<UUID> uuid = new Template.Field();
    }
}

