/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.utils.ItemUtil;
import java.util.List;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftInputSlot {
    private final ItemStack[] choices;

    public CraftInputSlot(List<ItemStack> choices) {
        this.choices = choices.toArray(new ItemStack[choices.size()]);
    }

    public CraftInputSlot(ItemStack[] choices) {
        this.choices = choices;
    }

    public ItemStack[] getChoices() {
        return this.choices;
    }

    public ItemStack getDefaultChoice() {
        return this.choices.length == 0 ? ItemUtil.emptyItem() : this.choices[0];
    }

    public ItemStack match(ItemStack item) {
        if (!ItemUtil.isEmpty(item)) {
            for (ItemStack choice : this.choices) {
                if (choice.getType() != item.getType() || choice.getDurability() != Short.MAX_VALUE && choice.getDurability() != item.getDurability()) continue;
                return choice;
            }
        }
        return null;
    }

    public boolean takeFrom(Inventory inventory) {
        int amountRemaining = Integer.MAX_VALUE;
        for (int i = 0; i < inventory.getSize() && amountRemaining > 0; ++i) {
            ItemStack item = inventory.getItem(i);
            ItemStack match = this.match(item);
            if (match == null || match.getAmount() <= 0 || item.getAmount() <= 0) continue;
            if (amountRemaining == Integer.MAX_VALUE) {
                amountRemaining = match.getAmount();
            }
            if (amountRemaining >= item.getAmount()) {
                amountRemaining -= item.getAmount();
                inventory.setItem(i, null);
                continue;
            }
            item.setAmount(item.getAmount() - amountRemaining);
            inventory.setItem(i, item);
            amountRemaining = 0;
        }
        return amountRemaining == 0;
    }

    public CraftInputSlot clone() {
        ItemStack[] clonedChoices = new ItemStack[this.choices.length];
        for (int i = 0; i < clonedChoices.length; ++i) {
            clonedChoices[i] = this.choices[i].clone();
        }
        return new CraftInputSlot(clonedChoices);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");
        for (int i = 0; i < this.choices.length; ++i) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(this.choices[i].toString());
        }
        result.append("}");
        return result.toString();
    }

    public CraftInputSlot tryMergeWith(CraftInputSlot other) {
        if (this.choices.length != 1 || other.choices.length != 1) {
            return null;
        }
        if (!ItemUtil.equalsIgnoreAmount(this.choices[0], other.choices[0])) {
            return null;
        }
        ItemStack item = this.choices[0].clone();
        ItemUtil.addAmount(item, other.choices[0].getAmount());
        return new CraftInputSlot(new ItemStack[]{item});
    }
}

