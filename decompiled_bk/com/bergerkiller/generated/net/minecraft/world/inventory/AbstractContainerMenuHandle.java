/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.world.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.inventory.SlotHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.world.inventory.AbstractContainerMenu")
public abstract class AbstractContainerMenuHandle
extends Template.Handle {
    public static final AbstractContainerMenuClass T = Template.Class.create(AbstractContainerMenuClass.class, Common.TEMPLATE_RESOLVER);

    public static AbstractContainerMenuHandle createHandle(Object handleInstance) {
        return (AbstractContainerMenuHandle)T.createHandle(handleInstance);
    }

    public static AbstractContainerMenuHandle fromBukkit(InventoryView bukkitView) {
        return (AbstractContainerMenuHandle)AbstractContainerMenuHandle.T.fromBukkit.invoker.invoke(null, bukkitView);
    }

    public abstract InventoryView getBukkitView();

    public abstract List<ItemStack> getOldItems();

    public abstract void setOldItems(List<ItemStack> var1);

    public abstract List<SlotHandle> getSlots();

    public abstract void setSlots(List<SlotHandle> var1);

    public abstract int getWindowId();

    public abstract void setWindowId(int var1);

    public static final class AbstractContainerMenuClass
    extends Template.Class<AbstractContainerMenuHandle> {
        public final Template.Field.Converted<List<ItemStack>> oldItems = new Template.Field.Converted();
        public final Template.Field.Converted<List<SlotHandle>> slots = new Template.Field.Converted();
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.StaticMethod<AbstractContainerMenuHandle> fromBukkit = new Template.StaticMethod();
        public final Template.Method<InventoryView> getBukkitView = new Template.Method();
    }
}

