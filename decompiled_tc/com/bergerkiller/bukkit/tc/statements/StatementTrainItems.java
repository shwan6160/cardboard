/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.entity.Entity
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.statements.StatementItems;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class StatementTrainItems
extends StatementItems {
    @Override
    public boolean match(String text) {
        return text.startsWith("items");
    }

    @Override
    public boolean matchArray(String text) {
        return text.equals("i");
    }

    @Override
    public Inventory getInventory(MinecartMember<?> member) {
        Entity entity = ((CommonMinecart)member.getEntity()).getEntity();
        if (entity instanceof InventoryHolder) {
            return ((InventoryHolder)entity).getInventory();
        }
        return null;
    }

    @Override
    public Inventory getInventory(MinecartGroup group) {
        return group.getInventory();
    }
}

