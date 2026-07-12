/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.BeaconInventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.bukkit.common.inventory.InventoryBase;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.ItemStack;

public class BeaconInventory_1_14
extends InventoryBase
implements BeaconInventory {
    private final Object nmsTileEntityBeacon;
    private ItemStack item = null;

    public BeaconInventory_1_14(Object nmsTileEntityBeacon) {
        this.nmsTileEntityBeacon = nmsTileEntityBeacon;
        this.setMaxStackSize(0);
    }

    public ItemStack getItem() {
        return this.getItem(0);
    }

    public void setItem(ItemStack arg0) {
        this.setItem(0, arg0);
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.item;
    }

    @Override
    public void setItem(int index, ItemStack item) {
        this.item = item;
    }
}

