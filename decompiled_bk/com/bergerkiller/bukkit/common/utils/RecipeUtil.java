/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.inventory.CraftRecipe;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.RecipeHandle;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.RecipeManagerHandle;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.RecipesFurnaceHandle;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.SmeltingRecipeHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingSet;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RecipeUtil {
    private static final EnumMap<Material, Integer> fuelTimes = new EnumMap(Material.class);

    public static Set<Material> getFuelItems() {
        return fuelTimes.keySet();
    }

    public static Map<Material, Integer> getFuelTimes() {
        return fuelTimes;
    }

    public static int getFuelTime(Material material) {
        if (!CraftMagicNumbersHandle.isItemMaterial(material)) {
            return 0;
        }
        return RecipeUtil.getFuelTime(new ItemStack(material, 1));
    }

    public static int getFuelTime(ItemStack item) {
        if (item == null) {
            return 0;
        }
        return item.getAmount() * AbstractFurnaceBlockEntityHandle.fuelTime(CommonNMS.getHandle(item));
    }

    public static boolean isFuelItem(Material material) {
        return fuelTimes.containsKey(material);
    }

    public static boolean isFuelItem(ItemStack item) {
        return item != null && RecipeUtil.isFuelItem(item.getType());
    }

    public static boolean isHeatableItem(Material material) {
        return !ItemUtil.isEmpty(RecipeUtil.getFurnaceResult(material));
    }

    public static boolean isHeatableItem(ItemStack item) {
        return !ItemUtil.isEmpty(RecipeUtil.getFurnaceResult(item));
    }

    public static ItemStack getFurnaceResult(Material cookedType) {
        if (!CraftMagicNumbersHandle.isItemMaterial(cookedType)) {
            return null;
        }
        return RecipeUtil.getFurnaceResult(new ItemStack(cookedType, 1));
    }

    public static ItemStack getFurnaceResult(ItemStack cooked) {
        if (SmeltingRecipeHandle.T.isAvailable()) {
            for (SmeltingRecipeHandle recipe : SmeltingRecipeHandle.getRecipes()) {
                if (recipe.getIngredient().match(cooked) == null) continue;
                return recipe.getOutput();
            }
            return null;
        }
        ItemStackHandle input = CommonNMS.getHandle(cooked);
        if (input == null) {
            return null;
        }
        return (ItemStack)Conversion.toItemStack.convert(RecipesFurnaceHandle.getInstance().getResult(input));
    }

    public static Collection<ItemStack> getHeatableItemStacks() {
        return RecipesFurnaceHandle.getInstance().getRecipes().keySet();
    }

    @Deprecated
    public static Set<Integer> getHeatableItems() {
        DuplexConverter conv = DuplexConverter.pair(Conversion.toItemId, Conversion.toItemStackHandle);
        Map recipes = (Map)((Template.Field)RecipesFurnaceHandle.T.recipes.raw).get(RecipesFurnaceHandle.getInstance().getRaw());
        return new ConvertingSet<Integer>(recipes.keySet(), conv);
    }

    public static CraftRecipe[] getCraftingRequirements(Material type, int data) {
        ArrayList<CraftRecipe> poss = new ArrayList<CraftRecipe>(2);
        for (RecipeHandle rec : RecipeUtil.getCraftRecipes()) {
            CraftRecipe crec;
            ItemStack item = rec.getOutput();
            if (item == null || type != null && item.getType() != type || data != -1 && MaterialUtil.getRawData(item) != data || (crec = CraftRecipe.create(rec)) == null) continue;
            poss.add(crec);
        }
        return poss.toArray(new CraftRecipe[0]);
    }

    public static void craftItems(ItemParser parser, Inventory source) {
        if (parser.hasType()) {
            int limit = parser.hasAmount() ? parser.getAmount() : Integer.MAX_VALUE;
            RecipeUtil.craftItems(parser.getType(), parser.getData(), source, limit);
        }
    }

    public static void craftItems(Material type, int data, Inventory source, int limit) {
        for (CraftRecipe rec : RecipeUtil.getCraftingRequirements(type, data)) {
            limit -= rec.craftItems(source, limit);
        }
    }

    private static Iterable<RecipeHandle> getCraftRecipes() {
        return RecipeManagerHandle.getRecipes();
    }

    static {
        for (Material material : MaterialsByName.getAllMaterials()) {
            ItemStackHandle item;
            try {
                item = ItemStackHandle.newInstance(material);
            }
            catch (Throwable t) {
                continue;
            }
            int fuel = (Integer)((Template.StaticMethod)AbstractFurnaceBlockEntityHandle.T.fuelTime.raw).invoke(item.getRaw());
            if (fuel <= 0) continue;
            fuelTimes.put(material, fuel);
        }
        for (Material legacyMaterial : CommonLegacyMaterials.getAllLegacyMaterials()) {
            Material modernType = BlockData.fromMaterial(legacyMaterial).getType();
            Integer modernFuelValue = fuelTimes.get(modernType);
            if (modernFuelValue == null) continue;
            fuelTimes.put(legacyMaterial, modernFuelValue);
        }
    }
}

