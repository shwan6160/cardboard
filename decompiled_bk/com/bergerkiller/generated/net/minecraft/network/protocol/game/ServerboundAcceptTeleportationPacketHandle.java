/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket")
public abstract class ServerboundAcceptTeleportationPacketHandle
extends PacketHandle {
    public static final ServerboundAcceptTeleportationPacketClass T = Template.Class.create(ServerboundAcceptTeleportationPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundAcceptTeleportationPacketHandle createHandle(Object handleInstance) {
        return (ServerboundAcceptTeleportationPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getTeleportId();

    public abstract void setTeleportId(int var1);

    public static final class ServerboundAcceptTeleportationPacketClass
    extends Template.Class<ServerboundAcceptTeleportationPacketHandle> {
        public final Template.Field.Integer teleportId = new Template.Field.Integer();
    }
}

