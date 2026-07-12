/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.InventoryBase
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.itemanimation;

import com.bergerkiller.bukkit.common.inventory.InventoryBase;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.tc.itemanimation.ItemAnimation;
import com.bergerkiller.bukkit.tc.utils.GroundItemsInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemAnimatedInventory
extends InventoryBase {
    private final Inventory source;
    private final ItemStack[] original;
    private Object other;
    private Object self;

    public ItemAnimatedInventory(Inventory inventory, Object self, Object other) {
        this.other = other;
        this.self = self;
        this.source = inventory;
        this.original = ItemUtil.getClonedContents((Inventory)inventory);
    }

    public static Inventory convert(Inventory inventory, Object self, Object other) {
        return new ItemAnimatedInventory(inventory, self, other);
    }

    public void setItem(int index, ItemStack newitem) {
        ItemStack olditem = this.original[index];
        this.source.setItem(index, newitem);
        Object self = this.getSelfAt(index);
        this.original[index] = ItemUtil.cloneItem((ItemStack)newitem);
        if (olditem == null) {
            if (newitem != null) {
                ItemAnimation.start(this.other, self, newitem);
            }
        } else if (newitem == null) {
            ItemAnimation.start(self, this.other, olditem);
        } else if (ItemUtil.equalsIgnoreAmount((ItemStack)olditem, (ItemStack)newitem)) {
            ItemStack trans = ItemUtil.cloneItem((ItemStack)newitem);
            int newAmount = trans.getAmount() - olditem.getAmount();
            if (newAmount > 0) {
                trans.setAmount(newAmount);
                ItemAnimation.start(this.other, self, trans);
            } else if (newAmount < 0) {
                trans.setAmount(-newAmount);
                ItemAnimation.start(self, this.other, trans);
            }
        } else {
            ItemAnimation.start(self, this.other, olditem);
            ItemAnimation.start(this.other, self, newitem);
        }
    }

    public Object getSelfAt(int index) {
        if (this.source instanceof GroundItemsInventory) {
            return ((GroundItemsInventory)this.source).getEntity(index);
        }
        return this.self;
    }

    public ItemStack getItem(int index) {
        return this.source.getItem(index);
    }

    public int getSize() {
        return this.source.getSize();
    }
}

