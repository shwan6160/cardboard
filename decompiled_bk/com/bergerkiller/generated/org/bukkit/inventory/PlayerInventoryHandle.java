/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.org.bukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="org.bukkit.inventory.PlayerInventory")
public abstract class PlayerInventoryHandle
extends InventoryHandle {
    public static final PlayerInventoryClass T = Template.Class.create(PlayerInventoryClass.class, Common.TEMPLATE_RESOLVER);

    public static PlayerInventoryHandle createHandle(Object handleInstance) {
        return (PlayerInventoryHandle)T.createHandle(handleInstance);
    }

    public abstract ItemStack getItemInMainHand();

    public abstract void setItemInMainHand(ItemStack var1);

    public abstract ItemStack getItemInOffHand();

    public abstract void setItemInOffHand(ItemStack var1);

    public abstract void setItem(EquipmentSlot var1, ItemStack var2);

    public abstract ItemStack getItem(EquipmentSlot var1);

    public static final class PlayerInventoryClass
    extends Template.Class<PlayerInventoryHandle> {
        public final Template.Method<ItemStack> getItemInMainHand = new Template.Method();
        public final Template.Method<Void> setItemInMainHand = new Template.Method();
        public final Template.Method<ItemStack> getItemInOffHand = new Template.Method();
        public final Template.Method<Void> setItemInOffHand = new Template.Method();
        public final Template.Method<Void> setItem = new Template.Method();
        public final Template.Method<ItemStack> getItem = new Template.Method();
    }
}

