/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.inventory.ItemStack;

@Template.Optional
@Template.InstanceType(value="net.minecraft.world.item.crafting.Ingredient")
public abstract class IngredientHandle
extends Template.Handle {
    public static final IngredientClass T = Template.Class.create(IngredientClass.class, Common.TEMPLATE_RESOLVER);

    public static IngredientHandle createHandle(Object handleInstance) {
        return (IngredientHandle)T.createHandle(handleInstance);
    }

    public abstract List<ItemStack> getChoices();

    public abstract void setChoices(List<ItemStack> var1);

    public static Object createRawRecipeItemStack(List<ItemStack> choices) {
        Object raw = T.newInstanceNull();
        IngredientHandle.T.setChoices.invoke(raw, choices);
        return raw;
    }

    public static final class IngredientClass
    extends Template.Class<IngredientHandle> {
        public final Template.Method.Converted<List<ItemStack>> getChoices = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setChoices = new Template.Method.Converted();
    }
}

