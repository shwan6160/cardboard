/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.world.item.crafting.Recipe")
public abstract class RecipeHandle
extends Template.Handle {
    public static final RecipeClass T = Template.Class.create(RecipeClass.class, Common.TEMPLATE_RESOLVER);

    public static RecipeHandle createHandle(Object handleInstance) {
        return (RecipeHandle)T.createHandle(handleInstance);
    }

    public abstract ItemStack getOutput();

    public abstract List<CraftInputSlot> getIngredients();

    public static final class RecipeClass
    extends Template.Class<RecipeHandle> {
        public final Template.Method.Converted<ItemStack> getOutput = new Template.Method.Converted();
        public final Template.Method.Converted<List<CraftInputSlot>> getIngredients = new Template.Method.Converted();
    }
}

