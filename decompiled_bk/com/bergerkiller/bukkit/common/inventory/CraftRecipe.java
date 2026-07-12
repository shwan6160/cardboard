/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.bukkit.common.inventory.InventoryBaseImpl;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.RecipeHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftRecipe {
    private final CraftInputSlot[] inputSlots;
    private final ItemStack[] input;
    private final ItemStack[] output;

    private CraftRecipe(Collection<CraftInputSlot> unmodifiedIngredients, ItemStack output) {
        ArrayList<CraftInputSlot> inputSlotsList = new ArrayList<CraftInputSlot>(unmodifiedIngredients.size());
        for (CraftInputSlot unmodInput : unmodifiedIngredients) {
            boolean merged = false;
            for (int i = 0; i < inputSlotsList.size(); ++i) {
                CraftInputSlot mergedSlot = ((CraftInputSlot)inputSlotsList.get(i)).tryMergeWith(unmodInput);
                merged = mergedSlot != null;
                if (!merged) continue;
                inputSlotsList.set(i, mergedSlot);
                break;
            }
            if (merged) continue;
            inputSlotsList.add(unmodInput);
        }
        this.inputSlots = inputSlotsList.toArray(new CraftInputSlot[inputSlotsList.size()]);
        ArrayList<ItemStack> inputItemsList = new ArrayList<ItemStack>(inputSlotsList.size());
        for (CraftInputSlot itemSlot : inputSlotsList) {
            ItemStack item = itemSlot.getDefaultChoice();
            if (LogicUtil.nullOrEmpty(item)) continue;
            item = item.clone();
            boolean create = true;
            for (ItemStack newitem : inputItemsList) {
                if (!ItemUtil.equalsIgnoreAmount(item, newitem)) continue;
                ItemUtil.addAmount(newitem, item.getAmount());
                create = false;
                break;
            }
            if (!create) continue;
            inputItemsList.add(item);
        }
        this.input = inputItemsList.toArray(new ItemStack[inputItemsList.size()]);
        ArrayList<ItemStack> newoutput = new ArrayList<ItemStack>(1);
        newoutput.add(output.clone());
        for (ItemStack stack : inputItemsList) {
            if (!BlockUtil.isType(stack, Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.MILK_BUCKET)) continue;
            newoutput.add(new ItemStack(Material.BUCKET, stack.getAmount()));
        }
        this.output = newoutput.toArray(new ItemStack[0]);
    }

    @Deprecated
    public ItemStack getInput(int index) {
        return this.getInput()[index];
    }

    @Deprecated
    public ItemStack[] getInput() {
        return this.input;
    }

    public CraftInputSlot[] getInputSlots() {
        return this.inputSlots;
    }

    public ItemStack[] getOutput() {
        return this.output;
    }

    @Deprecated
    public int getInputSize() {
        int count = 0;
        for (ItemStack item : this.getInput()) {
            count += item.getAmount();
        }
        return count;
    }

    public int getOutputSize() {
        int count = 0;
        for (ItemStack item : this.output) {
            count += item.getAmount();
        }
        return count;
    }

    public boolean containsInput(Inventory inventory) {
        return this.testCraftOnce(ItemUtil.cloneInventory(inventory), false);
    }

    public int craftItems(Inventory inventory, int itemlimit) {
        int lim = MathUtil.floor((double)itemlimit / (double)this.output[0].getAmount());
        return this.craft(inventory, lim) * this.output[0].getAmount();
    }

    public boolean craft(Inventory inventory) {
        return this.craft(inventory, 1) == 1;
    }

    public int craft(Inventory inventory, int limit) {
        int amount;
        if (!this.containsInput(inventory)) {
            return 0;
        }
        ItemStack[] items = inventory.getContents();
        int size = items.length;
        InventoryBaseImpl inventoryClone = new InventoryBaseImpl(items, true);
        for (amount = 0; amount < limit && this.testCraftOnce(inventoryClone, true); ++amount) {
            for (int i = 0; i < size; ++i) {
                ItemStack newItem = inventoryClone.getItem(i);
                items[i] = LogicUtil.nullOrEmpty(newItem) ? null : newItem.clone();
            }
        }
        inventory.setContents(items);
        return amount;
    }

    private boolean testCraftOnce(Inventory inventory, boolean addOutputItems) {
        for (CraftInputSlot craftInputSlot : this.inputSlots) {
            if (craftInputSlot.getChoices().length != 1 || craftInputSlot.takeFrom(inventory)) continue;
            return false;
        }
        for (CraftInputSlot craftInputSlot : this.inputSlots) {
            if (craftInputSlot.getChoices().length <= 1 || craftInputSlot.takeFrom(inventory)) continue;
            return false;
        }
        if (addOutputItems) {
            for (CraftInputSlot craftInputSlot : this.output) {
                CommonItemStack cloned = CommonItemStack.copyOf((ItemStack)craftInputSlot);
                cloned.transferTo(inventory, -1);
                if (cloned.isEmpty()) continue;
                return false;
            }
        }
        return true;
    }

    @Deprecated
    public static CraftRecipe create(Object recipe) {
        return CraftRecipe.create(RecipeHandle.createHandle(recipe));
    }

    public static CraftRecipe create(RecipeHandle recipe) {
        ItemStack output = recipe.getOutput();
        List<CraftInputSlot> ingredients = recipe.getIngredients();
        if (ingredients != null) {
            return CraftRecipe.createSlots(ingredients, output);
        }
        return null;
    }

    public static CraftRecipe createSlots(Collection<CraftInputSlot> ingredients, ItemStack output) {
        if (LogicUtil.nullOrEmpty(ingredients) || LogicUtil.nullOrEmpty(output)) {
            return null;
        }
        CraftRecipe rval = new CraftRecipe(ingredients, output);
        if (rval.input.length == 1 && rval.output.length == 1 && rval.input[0].getType() == rval.output[0].getType()) {
            return null;
        }
        return rval;
    }

    @Deprecated
    public static CraftRecipe create(Collection<ItemStack> inputs, ItemStack output) {
        if (LogicUtil.nullOrEmpty(inputs) || LogicUtil.nullOrEmpty(output)) {
            return null;
        }
        ArrayList<CraftInputSlot> slots = new ArrayList<CraftInputSlot>();
        for (ItemStack input : inputs) {
            slots.add(new CraftInputSlot(new ItemStack[]{input}));
        }
        return CraftRecipe.createSlots(slots, output);
    }
}

