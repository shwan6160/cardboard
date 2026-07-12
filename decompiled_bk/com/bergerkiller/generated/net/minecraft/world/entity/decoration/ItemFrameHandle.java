/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.world.entity.decoration;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.HangingEntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.world.entity.decoration.ItemFrame")
public abstract class ItemFrameHandle
extends HangingEntityHandle {
    public static final ItemFrameClass T = Template.Class.create(ItemFrameClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<ItemStack> DATA_ITEM = DataWatcher.Key.Type.ITEMSTACK.createKey(ItemFrameHandle.T.DATA_ITEM, 8);

    public static ItemFrameHandle createHandle(Object handleInstance) {
        return (ItemFrameHandle)T.createHandle(handleInstance);
    }

    public abstract boolean getItemIsMap();

    public abstract UUID getItemMapDisplayDynamicOnlyUUID();

    public abstract UUID getItemMapDisplayUUID();

    public abstract ItemStack getItem();

    public abstract void setItem(ItemStack var1);

    public abstract void refreshItem();

    public abstract int getRotationOrdinal();

    public static ItemFrameHandle fromBukkit(ItemFrame itemFrame) {
        return ItemFrameHandle.createHandle(HandleConversion.toEntityHandle((Entity)itemFrame));
    }

    public static final class ItemFrameClass
    extends Template.Class<ItemFrameHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Object>> DATA_ITEM = new Template.StaticField.Converted();
        public final Template.Method<Boolean> getItemIsMap = new Template.Method();
        public final Template.Method<UUID> getItemMapDisplayDynamicOnlyUUID = new Template.Method();
        public final Template.Method<UUID> getItemMapDisplayUUID = new Template.Method();
        public final Template.Method.Converted<ItemStack> getItem = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setItem = new Template.Method.Converted();
        public final Template.Method<Void> refreshItem = new Template.Method();
        public final Template.Method<Integer> getRotationOrdinal = new Template.Method();
    }
}

