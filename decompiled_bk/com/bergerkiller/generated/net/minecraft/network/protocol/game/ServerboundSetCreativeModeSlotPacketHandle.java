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

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket")
public abstract class ServerboundSetCreativeModeSlotPacketHandle
extends PacketHandle {
    public static final ServerboundSetCreativeModeSlotPacketClass T = Template.Class.create(ServerboundSetCreativeModeSlotPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundSetCreativeModeSlotPacketHandle createHandle(Object handleInstance) {
        return (ServerboundSetCreativeModeSlotPacketHandle)T.createHandle(handleInstance);
    }

    public static ServerboundSetCreativeModeSlotPacketHandle createNew(int slotIndex, ItemStack item) {
        return ServerboundSetCreativeModeSlotPacketHandle.T.createNew.invoke(slotIndex, item);
    }

    public abstract ItemStack getItem();

    public abstract int getSlotIndex();

    public static final class ServerboundSetCreativeModeSlotPacketClass
    extends Template.Class<ServerboundSetCreativeModeSlotPacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundSetCreativeModeSlotPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<ItemStack> getItem = new Template.Method.Converted();
        public final Template.Method<Integer> getSlotIndex = new Template.Method();
    }
}

