/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket")
public abstract class ServerboundContainerButtonClickPacketHandle
extends PacketHandle {
    public static final ServerboundContainerButtonClickPacketClass T = Template.Class.create(ServerboundContainerButtonClickPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundContainerButtonClickPacketHandle createHandle(Object handleInstance) {
        return (ServerboundContainerButtonClickPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getWindowId();

    public abstract void setWindowId(int var1);

    public abstract int getButtonId();

    public abstract void setButtonId(int var1);

    public static final class ServerboundContainerButtonClickPacketClass
    extends Template.Class<ServerboundContainerButtonClickPacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Integer buttonId = new Template.Field.Integer();
    }
}

