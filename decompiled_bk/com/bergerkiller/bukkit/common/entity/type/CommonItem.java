/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Item
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class CommonItem
extends CommonEntity<Item> {
    public CommonItem(Item base) {
        super(base);
    }

    public int getPickupDelay() {
        return ((Item)this.entity).getPickupDelay();
    }

    public void setPickupDelay(int tickDelay) {
        ((Item)this.entity).setPickupDelay(tickDelay);
    }

    public ItemStack getItemStack() {
        return ((Item)this.entity).getItemStack();
    }

    public void setItemStack(ItemStack item) {
        ((Item)this.entity).setItemStack(item);
    }
}

