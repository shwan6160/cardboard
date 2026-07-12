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
import java.util.List;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket")
public abstract class ClientboundContainerSetContentPacketHandle
extends PacketHandle {
    public static final ClientboundContainerSetContentPacketClass T = Template.Class.create(ClientboundContainerSetContentPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundContainerSetContentPacketHandle createHandle(Object handleInstance) {
        return (ClientboundContainerSetContentPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getWindowId();

    public abstract void setWindowId(int var1);

    public abstract List<ItemStack> getItems();

    public abstract void setItems(List<ItemStack> var1);

    public static final class ClientboundContainerSetContentPacketClass
    extends Template.Class<ClientboundContainerSetContentPacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Converted<List<ItemStack>> items = new Template.Field.Converted();
    }
}

