/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket")
public abstract class ClientboundContainerSetSlotPacketHandle
extends PacketHandle {
    public static final ClientboundContainerSetSlotPacketClass T = Template.Class.create(ClientboundContainerSetSlotPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundContainerSetSlotPacketHandle createHandle(Object handleInstance) {
        return (ClientboundContainerSetSlotPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundContainerSetSlotPacketHandle createNew(int containerId, int slot, ItemStack item) {
        return ClientboundContainerSetSlotPacketHandle.T.createNew.invoke(containerId, slot, item);
    }

    public abstract int getWindowId();

    public abstract void setWindowId(int var1);

    public abstract int getSlot();

    public abstract void setSlot(int var1);

    public abstract ItemStack getItem();

    public abstract void setItem(ItemStack var1);

    public static final class ClientboundContainerSetSlotPacketClass
    extends Template.Class<ClientboundContainerSetSlotPacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Integer slot = new Template.Field.Integer();
        public final Template.Field.Converted<ItemStack> item = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<ClientboundContainerSetSlotPacketHandle> createNew = new Template.StaticMethod.Converted();
    }
}

