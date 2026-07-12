/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.RecipeHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class NMSRecipe {
    public static final ClassTemplate<?> T = ClassTemplate.create(RecipeHandle.T.getType());

    @Deprecated
    public static ItemStack getOutput(Object iRecipe) {
        return RecipeHandle.T.getOutput.invoke(iRecipe);
    }

    @Deprecated
    public static List<CraftInputSlot> getInputSlots(RecipeHandle iRecipe) {
        return iRecipe == null ? null : iRecipe.getIngredients();
    }

    @Deprecated
    public static List<ItemStack> getInputItems(Object iRecipe) {
        return NMSRecipe.getInputItems(RecipeHandle.createHandle(iRecipe));
    }

    @Deprecated
    public static List<ItemStack> getInputItems(RecipeHandle iRecipe) {
        List<CraftInputSlot> slots = NMSRecipe.getInputSlots(iRecipe);
        if (slots == null) {
            return null;
        }
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (CraftInputSlot slot : slots) {
            items.add(slot.getDefaultChoice());
        }
        return items;
    }
}

