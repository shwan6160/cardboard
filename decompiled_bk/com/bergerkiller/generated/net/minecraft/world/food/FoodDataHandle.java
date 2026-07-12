/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.food;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.food.FoodData")
public abstract class FoodDataHandle
extends Template.Handle {
    public static final FoodDataClass T = Template.Class.create(FoodDataClass.class, Common.TEMPLATE_RESOLVER);

    public static FoodDataHandle createHandle(Object handleInstance) {
        return (FoodDataHandle)T.createHandle(handleInstance);
    }

    public abstract void loadFromNBT(CommonTagCompound var1);

    public abstract void saveToNBT(CommonTagCompound var1);

    public static final class FoodDataClass
    extends Template.Class<FoodDataHandle> {
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted();
        public final Template.Method.Converted<Void> saveToNBT = new Template.Method.Converted();
    }
}

