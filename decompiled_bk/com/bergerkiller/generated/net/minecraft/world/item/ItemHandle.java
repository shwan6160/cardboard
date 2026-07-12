/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.world.item;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.world.item.Item")
public abstract class ItemHandle
extends Template.Handle {
    public static final ItemClass T = Template.Class.create(ItemClass.class, Common.TEMPLATE_RESOLVER);

    public static ItemHandle createHandle(Object handleInstance) {
        return (ItemHandle)T.createHandle(handleInstance);
    }

    public static Iterable<?> getRegistry() {
        return (Iterable)ItemHandle.T.getRegistry.invoker.invoke(null);
    }

    public abstract int getMaxStackSize();

    public abstract void setMaxStackSize(int var1);

    public abstract int getMaxDurability();

    public abstract boolean usesDurability();

    public abstract String getInternalName(ItemStack var1);

    public static ItemHandle fromMaterial(Material material) {
        return ItemHandle.createHandle(CraftMagicNumbersHandle.getItemFromMaterial(material));
    }

    public static final class ItemClass
    extends Template.Class<ItemHandle> {
        public final Template.StaticMethod<Iterable<?>> getRegistry = new Template.StaticMethod();
        public final Template.Method<Integer> getMaxStackSize = new Template.Method();
        public final Template.Method<Void> setMaxStackSize = new Template.Method();
        public final Template.Method<Integer> getMaxDurability = new Template.Method();
        public final Template.Method<Boolean> usesDurability = new Template.Method();
        public final Template.Method.Converted<String> getInternalName = new Template.Method.Converted();
    }
}

