/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.org.bukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="org.bukkit.inventory.InventoryView")
public abstract class InventoryViewHandle
extends Template.Handle {
    public static final InventoryViewClass T = Template.Class.create(InventoryViewClass.class, Common.TEMPLATE_RESOLVER);

    public static InventoryViewHandle createHandle(Object handleInstance) {
        return (InventoryViewHandle)T.createHandle(handleInstance);
    }

    public abstract void close();

    public abstract HumanEntity getPlayer();

    public abstract Inventory getTopInventory();

    public abstract Inventory getBottomInventory();

    public abstract ItemStack getItem(int var1);

    public abstract void setItem(int var1, ItemStack var2);

    public static final class InventoryViewClass
    extends Template.Class<InventoryViewHandle> {
        public final Template.Method<Void> close = new Template.Method();
        public final Template.Method<HumanEntity> getPlayer = new Template.Method();
        public final Template.Method<Inventory> getTopInventory = new Template.Method();
        public final Template.Method<Inventory> getBottomInventory = new Template.Method();
        public final Template.Method<ItemStack> getItem = new Template.Method();
        public final Template.Method<Void> setItem = new Template.Method();
    }
}

