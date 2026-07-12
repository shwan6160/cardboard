/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket")
public abstract class ClientboundSetEquipmentPacketHandle
extends PacketHandle {
    public static final ClientboundSetEquipmentPacketClass T = Template.Class.create(ClientboundSetEquipmentPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetEquipmentPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetEquipmentPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundSetEquipmentPacketHandle createNew(OwnerType ownerType, int entityId, EquipmentSlot slot, ItemStack itemStack) {
        return ClientboundSetEquipmentPacketHandle.T.createNew.invoke((Object)ownerType, entityId, slot, itemStack);
    }

    public abstract int getSlotCount();

    public abstract EquipmentSlot getEquipmentSlot(OwnerType var1, int var2);

    public abstract void setEquipmentSlot(OwnerType var1, int var2, EquipmentSlot var3);

    public abstract ItemStack getItemStack(int var1);

    public abstract void setItemStack(int var1, ItemStack var2);

    @Deprecated
    public static ClientboundSetEquipmentPacketHandle createNew(int entityId, EquipmentSlot slot, ItemStack itemStack) {
        return ClientboundSetEquipmentPacketHandle.createNew(OwnerType.NON_PLAYER, entityId, slot, itemStack);
    }

    @Deprecated
    public EquipmentSlot getEquipmentSlot(int index) {
        return this.getEquipmentSlot(OwnerType.NON_PLAYER, index);
    }

    @Deprecated
    public void setEquipmentSlot(int index, EquipmentSlot slot) {
        this.setEquipmentSlot(OwnerType.NON_PLAYER, index, slot);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public static final class ClientboundSetEquipmentPacketClass
    extends Template.Class<ClientboundSetEquipmentPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.StaticMethod.Converted<ClientboundSetEquipmentPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Integer> getSlotCount = new Template.Method();
        public final Template.Method.Converted<EquipmentSlot> getEquipmentSlot = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setEquipmentSlot = new Template.Method.Converted();
        public final Template.Method.Converted<ItemStack> getItemStack = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setItemStack = new Template.Method.Converted();
    }

    public static enum OwnerType {
        NON_PLAYER(false),
        PLAYER(true);

        private final boolean isPlayer;

        private OwnerType(boolean isPlayer) {
            this.isPlayer = isPlayer;
        }

        public boolean isPlayer() {
            return this.isPlayer;
        }
    }
}

