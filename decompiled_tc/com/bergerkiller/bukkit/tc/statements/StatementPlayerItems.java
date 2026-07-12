/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.statements.StatementItems;
import org.bukkit.inventory.Inventory;

public class StatementPlayerItems
extends StatementItems {
    @Override
    public boolean match(String text) {
        return text.startsWith("playeritems");
    }

    @Override
    public boolean matchArray(String text) {
        return text.equals("pi");
    }

    @Override
    public Inventory getInventory(MinecartMember<?> member) {
        return member.getPlayerInventory();
    }

    @Override
    public Inventory getInventory(MinecartGroup group) {
        return group.getPlayerInventory();
    }
}

