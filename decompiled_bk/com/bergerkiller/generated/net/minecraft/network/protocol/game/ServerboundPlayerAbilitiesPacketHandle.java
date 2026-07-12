/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket")
public abstract class ServerboundPlayerAbilitiesPacketHandle
extends PacketHandle {
    public static final ServerboundPlayerAbilitiesPacketClass T = Template.Class.create(ServerboundPlayerAbilitiesPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundPlayerAbilitiesPacketHandle createHandle(Object handleInstance) {
        return (ServerboundPlayerAbilitiesPacketHandle)T.createHandle(handleInstance);
    }

    public abstract boolean isFlying();

    public abstract void setIsFlying(boolean var1);

    public static final class ServerboundPlayerAbilitiesPacketClass
    extends Template.Class<ServerboundPlayerAbilitiesPacketHandle> {
        public final Template.Field.Boolean isFlying = new Template.Field.Boolean();
    }
}

