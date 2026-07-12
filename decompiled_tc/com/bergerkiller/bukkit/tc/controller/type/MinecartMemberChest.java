/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartChest
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.inventory.ItemParser
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.Effect
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.controller.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecartChest;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.persistence.MinecartInventoryPersistentCartAttribute;
import com.bergerkiller.bukkit.tc.exception.GroupUnloadedException;
import com.bergerkiller.bukkit.tc.exception.MemberMissingException;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MinecartMemberChest
extends MinecartMember<CommonMinecartChest> {
    public MinecartMemberChest(TrainCarts plugin) {
        super(plugin);
        this.addPersistentCartAttribute(new MinecartInventoryPersistentCartAttribute());
    }

    @Override
    public void onAttached() {
        super.onAttached();
    }

    public boolean hasItem(ItemParser item) {
        if (item == null) {
            return false;
        }
        if (item.hasData()) {
            return this.hasItem(item.getType(), item.getData());
        }
        return this.hasItem(item.getType());
    }

    public boolean hasItem(Material type, int data) {
        for (ItemStack stack : ((CommonMinecartChest)this.entity).getInventory()) {
            if (LogicUtil.nullOrEmpty((ItemStack)stack) || stack.getType() != type || stack.getDurability() != data) continue;
            return true;
        }
        return false;
    }

    public boolean hasItem(Material type) {
        for (ItemStack stack : ((CommonMinecartChest)this.entity).getInventory()) {
            if (LogicUtil.nullOrEmpty((ItemStack)stack) || stack.getType() != type) continue;
            return true;
        }
        return false;
    }

    public boolean hasItems() {
        for (ItemStack stack : ((CommonMinecartChest)this.entity).getInventory()) {
            if (stack == null) continue;
            return true;
        }
        return false;
    }

    @Override
    public void onPhysicsPostMove() throws MemberMissingException, GroupUnloadedException {
        super.onPhysicsPostMove();
        if (this.getProperties().canPickup()) {
            Inventory inv = ((CommonMinecartChest)this.entity).getInventory();
            for (Entity e : ((CommonMinecartChest)this.entity).getNearbyEntities(TCConfig.itemPickupRadius)) {
                if (!(e instanceof Item) || EntityUtil.isIgnored((Entity)e)) continue;
                CommonItemStack stack = CommonItemStack.of((ItemStack)((Item)e).getItemStack());
                double distance = ((CommonMinecartChest)this.entity).loc.distanceSquared(e);
                if (stack.testTransferTo(inv) != stack.getAmount()) continue;
                if (distance < 0.7) {
                    stack.transferAllTo(inv);
                    ((CommonMinecartChest)this.entity).getWorld().playEffect(((CommonMinecartChest)this.entity).getLocation(), Effect.CLICK1, 0);
                    if (stack.getAmount() != 0) continue;
                    e.remove();
                    continue;
                }
                double factor = distance > 1.0 ? 0.8 : (distance > 0.75 ? 0.5 : 0.1);
                this.push(e, -factor / distance);
            }
        }
    }

    public void onItemSet(int index, ItemStack item) {
        super.onItemSet(index, item);
        this.onPropertiesChanged();
    }
}

