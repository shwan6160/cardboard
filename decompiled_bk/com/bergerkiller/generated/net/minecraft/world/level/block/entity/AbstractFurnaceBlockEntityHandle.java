/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity")
public abstract class AbstractFurnaceBlockEntityHandle
extends BlockEntityHandle {
    public static final AbstractFurnaceBlockEntityClass T = Template.Class.create(AbstractFurnaceBlockEntityClass.class, Common.TEMPLATE_RESOLVER);

    public static AbstractFurnaceBlockEntityHandle createHandle(Object handleInstance) {
        return (AbstractFurnaceBlockEntityHandle)T.createHandle(handleInstance);
    }

    public static int fuelTime(ItemStackHandle itemstack) {
        return AbstractFurnaceBlockEntityHandle.T.fuelTime.invoke(itemstack);
    }

    public static final class AbstractFurnaceBlockEntityClass
    extends Template.Class<AbstractFurnaceBlockEntityHandle> {
        public final Template.StaticMethod.Converted<Integer> fuelTime = new Template.StaticMethod.Converted();
    }
}

