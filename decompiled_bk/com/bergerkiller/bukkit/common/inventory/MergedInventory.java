/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.inventory.InventoryBase;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MergedInventory
extends InventoryBase {
    private final Inventory[] inventories;
    private final int size;
    Location loc = null;

    public MergedInventory(Inventory ... inventories) {
        this.inventories = inventories;
        int size = 0;
        for (Inventory inventory : inventories) {
            size += inventory.getSize();
        }
        this.size = size;
    }

    @Override
    public ItemStack[] getContents() {
        if (LogicUtil.nullOrEmpty(this.inventories)) {
            return new ItemStack[0];
        }
        if (this.inventories.length == 1) {
            return this.inventories[0].getContents();
        }
        ItemStack[] rval = new ItemStack[this.getSize()];
        int i = 0;
        for (Inventory inv : this.inventories) {
            ItemStack[] itemStackArray = inv.getContents();
            int n = itemStackArray.length;
            for (int j = 0; j < n; ++j) {
                ItemStack stack;
                rval[i] = stack = itemStackArray[j];
                ++i;
            }
        }
        return rval;
    }

    @Override
    public ItemStack[] getStorageContents() {
        return new ItemStack[0];
    }

    @Override
    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public ItemStack getItem(int index) {
        for (Inventory inventory : this.inventories) {
            int size = inventory.getSize();
            if (index < size) {
                return inventory.getItem(index);
            }
            index -= size;
        }
        return null;
    }

    @Override
    public void setItem(int index, ItemStack arg1) {
        for (Inventory inventory : this.inventories) {
            int size = inventory.getSize();
            if (index < size) {
                inventory.setItem(index, arg1);
                return;
            }
            index -= size;
        }
    }

    @Override
    public Location getLocation() {
        return this.loc;
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }
}

