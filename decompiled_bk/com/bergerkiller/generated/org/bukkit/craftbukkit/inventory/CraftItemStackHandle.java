/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Template.InstanceType(value="org.bukkit.craftbukkit.inventory.CraftItemStack")
public abstract class CraftItemStackHandle
extends Template.Handle {
    public static final CraftItemStackClass T = Template.Class.create(CraftItemStackClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftItemStackHandle createHandle(Object handleInstance) {
        return (CraftItemStackHandle)T.createHandle(handleInstance);
    }

    public static Object asNMSCopy(ItemStack original) {
        return CraftItemStackHandle.T.asNMSCopy.invoker.invoke(null, original);
    }

    public static ItemStack asCraftCopy(ItemStack original) {
        return CraftItemStackHandle.T.asCraftCopy.invoke(original);
    }

    public static ItemStack asCraftMirror(Object nmsItemStack) {
        return CraftItemStackHandle.T.asCraftMirror.invoke(nmsItemStack);
    }

    public static ItemMeta deserializeItemMeta(Map<String, Object> values) {
        return (ItemMeta)CraftItemStackHandle.T.deserializeItemMeta.invoker.invoke(null, values);
    }

    public static boolean isCorruptedEntityTag(ItemMeta itemMeta) {
        return (Boolean)CraftItemStackHandle.T.isCorruptedEntityTag.invoker.invoke(null, itemMeta);
    }

    public abstract Object getHandle();

    public abstract void setHandle(Object var1);

    public static final class CraftItemStackClass
    extends Template.Class<CraftItemStackHandle> {
        public final Template.Field.Converted<Object> handle = new Template.Field.Converted();
        public final Template.StaticMethod<Object> asNMSCopy = new Template.StaticMethod();
        public final Template.StaticMethod.Converted<ItemStack> asCraftCopy = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ItemStack> asCraftMirror = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<ItemMeta> deserializeItemMeta = new Template.StaticMethod();
        @Template.Optional
        public final Template.StaticMethod<Object> deserializeSkullOwner = new Template.StaticMethod();
        @Template.Optional
        public final Template.StaticMethod<Object> deserializeCustomModelData = new Template.StaticMethod();
        @Template.Optional
        public final Template.StaticMethod.Converted<ItemStack> deserializeNBT = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<Boolean> isCorruptedEntityTag = new Template.StaticMethod();
    }
}

