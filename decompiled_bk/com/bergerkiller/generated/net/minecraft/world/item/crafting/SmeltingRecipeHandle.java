/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.RecipeHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.world.item.crafting.SmeltingRecipe")
public abstract class SmeltingRecipeHandle
extends RecipeHandle {
    public static final SmeltingRecipeClass T = Template.Class.create(SmeltingRecipeClass.class, Common.TEMPLATE_RESOLVER);

    public static SmeltingRecipeHandle createHandle(Object handleInstance) {
        return (SmeltingRecipeHandle)T.createHandle(handleInstance);
    }

    public static Iterable<SmeltingRecipeHandle> getRecipes() {
        return SmeltingRecipeHandle.T.getRecipes.invoke();
    }

    public abstract CraftInputSlot getIngredient();

    public static final class SmeltingRecipeClass
    extends Template.Class<SmeltingRecipeHandle> {
        public final Template.StaticMethod.Converted<Iterable<SmeltingRecipeHandle>> getRecipes = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<CraftInputSlot> getIngredient = new Template.Method.Converted();
    }
}

