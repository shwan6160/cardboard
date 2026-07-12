/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class CBCraftItemStack {
    public static final ClassTemplate<?> T = ClassTemplate.create("org.bukkit.craftbukkit.inventory.CraftItemStack");
    public static final FieldAccessor<Object> handle = T.selectField("net.minecraft.world.item.ItemStack handle");
    private static final SafeConstructor<?> constructor1 = T.getConstructor(ItemStackHandle.T.getType());

    public static ItemStack newInstanceFromHandle(Object nmsItemHandle) {
        return (ItemStack)constructor1.newInstance(nmsItemHandle);
    }
}

