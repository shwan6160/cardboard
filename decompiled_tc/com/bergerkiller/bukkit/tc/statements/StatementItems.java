/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.ItemParser
 *  com.bergerkiller.bukkit.common.inventory.MergedInventory
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  org.bukkit.Material
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.inventory.MergedInventory;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.Statement;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class StatementItems
extends Statement {
    public abstract Inventory getInventory(MinecartMember<?> var1);

    public abstract Inventory getInventory(MinecartGroup var1);

    @Override
    public boolean handle(MinecartMember<?> member, String text, SignActionEvent event) {
        Inventory inventory = this.getInventory(member);
        if (inventory == null) {
            inventory = new MergedInventory(new Inventory[0]);
        }
        int count = ItemUtil.getItemCount((Inventory)inventory, null, (int)-1);
        return Util.evaluate(count, text);
    }

    @Override
    public boolean handle(MinecartGroup group, String text, SignActionEvent event) {
        Inventory inventory = this.getInventory(group);
        if (inventory == null) {
            inventory = new MergedInventory(new Inventory[0]);
        }
        int count = ItemUtil.getItemCount((Inventory)inventory, null, (int)-1);
        return Util.evaluate(count, text);
    }

    public boolean handleInventory(Inventory inv, String[] items) {
        if (inv == null) {
            return false;
        }
        for (String itemname : items) {
            int count;
            if (inv.getSize() == 0) {
                count = 0;
            } else {
                int opidx = Util.getOperatorIndex(itemname);
                String itemnamefixed = opidx > 0 ? itemname.substring(0, opidx) : itemname;
                for (ItemParser parser : Util.getParsers(itemnamefixed)) {
                    count = ItemUtil.getItemCount((Inventory)inv, (Material)parser.getType(), (int)parser.getData());
                    if (!(opidx == -1 ? (parser.hasAmount() ? count >= parser.getAmount() : count > 0) : Util.evaluate(count, itemname))) continue;
                    return true;
                }
                count = 0;
                for (ItemStack item : inv) {
                    if (item == null || !ItemUtil.hasDisplayName((ItemStack)item) || !ItemUtil.getDisplayName((ItemStack)item).equals(itemnamefixed)) continue;
                    count += item.getAmount();
                }
            }
            if (!Util.evaluate(count, itemname)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean handleArray(MinecartMember<?> member, String[] items, SignActionEvent event) {
        return this.handleInventory(this.getInventory(member), items);
    }

    @Override
    public boolean handleArray(MinecartGroup group, String[] items, SignActionEvent event) {
        return this.handleInventory(this.getInventory(group), items);
    }
}

