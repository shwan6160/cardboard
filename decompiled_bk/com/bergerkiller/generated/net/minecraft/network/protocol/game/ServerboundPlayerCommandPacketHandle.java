/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket")
public abstract class ServerboundPlayerCommandPacketHandle
extends PacketHandle {
    public static final ServerboundPlayerCommandPacketClass T = Template.Class.create(ServerboundPlayerCommandPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundPlayerCommandPacketHandle createHandle(Object handleInstance) {
        return (ServerboundPlayerCommandPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getPlayerId();

    public abstract void setPlayerId(int var1);

    public abstract Object getAction();

    public abstract void setAction(Object var1);

    public abstract int getData();

    public abstract void setData(int var1);

    public static final class ServerboundPlayerCommandPacketClass
    extends Template.Class<ServerboundPlayerCommandPacketHandle> {
        public final Template.Field.Integer playerId = new Template.Field.Integer();
        public final Template.Field.Converted<Object> action = new Template.Field.Converted();
        public final Template.Field.Integer data = new Template.Field.Integer();
    }
}

