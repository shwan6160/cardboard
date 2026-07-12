/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.inventory.InventoryBaseImpl
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.inventory.InventoryBaseImpl;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.statements.StatementItems;
import java.util.ArrayList;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StatementPlayerHand
extends StatementItems {
    @Override
    public boolean match(String text) {
        return text.startsWith("playerhand");
    }

    @Override
    public boolean matchArray(String text) {
        return text.equals("ph");
    }

    private void addItems(MinecartMember<?> member, ArrayList<ItemStack> itemsList) {
        for (Player player : ((CommonMinecart)member.getEntity()).getPlayerPassengers()) {
            ItemStack item1 = HumanHand.getItemInMainHand((HumanEntity)player);
            ItemStack item2 = HumanHand.getItemInOffHand((HumanEntity)player);
            if (!LogicUtil.nullOrEmpty((ItemStack)item1)) {
                itemsList.add(item1);
            }
            if (LogicUtil.nullOrEmpty((ItemStack)item2)) continue;
            itemsList.add(item2);
        }
    }

    @Override
    public Inventory getInventory(MinecartMember<?> member) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        this.addItems(member, items);
        return new InventoryBaseImpl(items, false);
    }

    @Override
    public Inventory getInventory(MinecartGroup group) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (MinecartMember<?> member : group) {
            this.addItems(member, items);
        }
        return new InventoryBaseImpl(items, false);
    }
}

