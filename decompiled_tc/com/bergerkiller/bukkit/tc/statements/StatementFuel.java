/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberFurnace;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.Statement;

public class StatementFuel
extends Statement {
    @Override
    public boolean match(String text) {
        return text.equals("coal") || text.equals("fuel") || text.equals("fueled");
    }

    @Override
    public boolean handle(MinecartMember<?> member, String text, SignActionEvent event) {
        return member instanceof MinecartMemberFurnace && ((CommonMinecartFurnace)((MinecartMemberFurnace)member).getEntity()).hasFuel();
    }

    @Override
    public boolean matchArray(String text) {
        return false;
    }
}

