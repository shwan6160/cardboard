/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.world;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.world.Container")
public abstract class ContainerHandle
extends Template.Handle {
    public static final ContainerClass T = Template.Class.create(ContainerClass.class, Common.TEMPLATE_RESOLVER);

    public static ContainerHandle createHandle(Object handleInstance) {
        return (ContainerHandle)T.createHandle(handleInstance);
    }

    public abstract ItemStackHandle getItem(int var1);

    public abstract void setItem(int var1, ItemStackHandle var2);

    public abstract ItemStack splitStack(int var1, int var2);

    public abstract ItemStack splitWithoutUpdate(int var1);

    public abstract int getSize();

    public abstract void update();

    public abstract boolean canOpen(HumanEntity var1);

    public abstract boolean canStoreItem(int var1, ItemStack var2);

    public abstract List<ItemStackHandle> getContents();

    public abstract void clear();

    public static final class ContainerClass
    extends Template.Class<ContainerHandle> {
        public final Template.Method.Converted<ItemStackHandle> getItem = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setItem = new Template.Method.Converted();
        public final Template.Method.Converted<ItemStack> splitStack = new Template.Method.Converted();
        public final Template.Method.Converted<ItemStack> splitWithoutUpdate = new Template.Method.Converted();
        public final Template.Method<Integer> getSize = new Template.Method();
        public final Template.Method<Void> update = new Template.Method();
        public final Template.Method.Converted<Boolean> canOpen = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> canStoreItem = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method<Integer> getProperty = new Template.Method();
        @Template.Optional
        public final Template.Method<Void> setProperty = new Template.Method();
        @Template.Optional
        public final Template.Method<Integer> someFunction = new Template.Method();
        public final Template.Method.Converted<List<ItemStackHandle>> getContents = new Template.Method.Converted();
        public final Template.Method<Void> clear = new Template.Method();
        @Template.Optional
        public final Template.Method<Boolean> isNotEmptyOpt = new Template.Method();
    }
}

