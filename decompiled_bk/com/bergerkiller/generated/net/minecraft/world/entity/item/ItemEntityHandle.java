/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.world.entity.item;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.world.entity.item.ItemEntity")
public abstract class ItemEntityHandle
extends EntityHandle {
    public static final ItemEntityClass T = Template.Class.create(ItemEntityClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<ItemStack> DATA_ITEM = DataWatcher.Key.Type.ITEMSTACK.createKey(ItemEntityHandle.T.DATA_ITEM, 10);

    public static ItemEntityHandle createHandle(Object handleInstance) {
        return (ItemEntityHandle)T.createHandle(handleInstance);
    }

    public static final ItemEntityHandle createNew(LevelHandle world, double x, double y, double z, ItemStackHandle itemstack) {
        return ItemEntityHandle.T.constr_world_x_y_z_itemstack.newInstance(world, x, y, z, itemstack);
    }

    public abstract ItemStackHandle getItemStack();

    public abstract void setItemStack(ItemStackHandle var1);

    public abstract int getAge();

    public abstract void setAge(int var1);

    public abstract int getPickupDelay();

    public abstract void setPickupDelay(int var1);

    public static final class ItemEntityClass
    extends Template.Class<ItemEntityHandle> {
        public final Template.Constructor.Converted<ItemEntityHandle> constr_world_x_y_z_itemstack = new Template.Constructor.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Object>> DATA_ITEM = new Template.StaticField.Converted();
        public final Template.Field.Integer age = new Template.Field.Integer();
        public final Template.Field.Integer pickupDelay = new Template.Field.Integer();
        public final Template.Method.Converted<ItemStackHandle> getItemStack = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setItemStack = new Template.Method.Converted();
    }
}

