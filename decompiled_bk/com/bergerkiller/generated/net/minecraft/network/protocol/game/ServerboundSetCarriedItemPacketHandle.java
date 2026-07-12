/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket")
public abstract class ServerboundSetCarriedItemPacketHandle
extends PacketHandle {
    public static final ServerboundSetCarriedItemPacketClass T = Template.Class.create(ServerboundSetCarriedItemPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundSetCarriedItemPacketHandle createHandle(Object handleInstance) {
        return (ServerboundSetCarriedItemPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getItemInHandIndex();

    public abstract void setItemInHandIndex(int var1);

    public static final class ServerboundSetCarriedItemPacketClass
    extends Template.Class<ServerboundSetCarriedItemPacketHandle> {
        public final Template.Field.Integer itemInHandIndex = new Template.Field.Integer();
    }
}

