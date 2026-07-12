/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.InventoryClickType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundContainerClickPacket")
public abstract class ServerboundContainerClickPacketHandle
extends PacketHandle {
    public static final ServerboundContainerClickPacketClass T = Template.Class.create(ServerboundContainerClickPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundContainerClickPacketHandle createHandle(Object handleInstance) {
        return (ServerboundContainerClickPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getWindowId();

    public abstract void setWindowId(int var1);

    public abstract short getSlot();

    public abstract void setSlot(short var1);

    public abstract byte getButton();

    public abstract void setButton(byte var1);

    public abstract InventoryClickType getMode();

    public abstract void setMode(InventoryClickType var1);

    public static final class ServerboundContainerClickPacketClass
    extends Template.Class<ServerboundContainerClickPacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Converted<Short> slot = new Template.Field.Converted();
        public final Template.Field.Converted<Byte> button = new Template.Field.Converted();
        public final Template.Field.Converted<InventoryClickType> mode = new Template.Field.Converted();
    }
}

