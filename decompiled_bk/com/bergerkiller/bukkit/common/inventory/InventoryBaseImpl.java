/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.inventory.InventoryBase;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class InventoryBaseImpl
extends InventoryBase {
    private final ItemStack[] items;
    Location loc = null;

    public InventoryBaseImpl(int size) {
        this(new ItemStack[size], false);
    }

    public InventoryBaseImpl(Collection<ItemStack> contents) {
        this(contents, true);
    }

    public InventoryBaseImpl(Collection<ItemStack> contents, boolean clone) {
        this(contents.toArray(new ItemStack[contents.size()]), false);
        if (clone) {
            for (int i = 0; i < this.items.length; ++i) {
                this.items[i] = ItemUtil.cloneItem(this.items[i]);
            }
        }
    }

    public InventoryBaseImpl(ItemStack[] contents) {
        this(contents, true);
    }

    public InventoryBaseImpl(ItemStack[] contents, boolean clone) {
        this.items = clone ? ItemUtil.cloneItems(contents) : contents;
    }

    @Override
    public int getSize() {
        return this.items.length;
    }

    @Override
    public void setItem(int index, ItemStack item) {
        this.items[index] = item;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items[index];
    }

    @Override
    public ItemStack[] getContents() {
        return this.items;
    }

    @Override
    public ItemStack[] getStorageContents() {
        return new ItemStack[0];
    }

    @Override
    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {
    }

    @Override
    public Location getLocation() {
        return this.loc;
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }
}

