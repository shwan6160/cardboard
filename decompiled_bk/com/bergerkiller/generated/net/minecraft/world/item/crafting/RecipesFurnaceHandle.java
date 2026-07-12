/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

@Template.Optional
@Template.InstanceType(value="net.minecraft.world.item.crafting.RecipesFurnace")
public abstract class RecipesFurnaceHandle
extends Template.Handle {
    public static final RecipesFurnaceClass T = Template.Class.create(RecipesFurnaceClass.class, Common.TEMPLATE_RESOLVER);

    public static RecipesFurnaceHandle createHandle(Object handleInstance) {
        return (RecipesFurnaceHandle)T.createHandle(handleInstance);
    }

    public static RecipesFurnaceHandle getInstance() {
        return RecipesFurnaceHandle.T.getInstance.invoke();
    }

    public abstract ItemStackHandle getResult(ItemStackHandle var1);

    public abstract Map<ItemStack, ItemStack> getRecipes();

    public abstract void setRecipes(Map<ItemStack, ItemStack> var1);

    public static final class RecipesFurnaceClass
    extends Template.Class<RecipesFurnaceHandle> {
        public final Template.Field.Converted<Map<ItemStack, ItemStack>> recipes = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<RecipesFurnaceHandle> getInstance = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<ItemStackHandle> getResult = new Template.Method.Converted();
    }
}

