/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace;
import com.bergerkiller.bukkit.tc.actions.GroupAction;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberFurnace;

public class GroupActionRefill
extends GroupAction {
    @Override
    public void start() {
        for (MinecartMember<?> member : this.getGroup()) {
            if (!(member instanceof MinecartMemberFurnace)) continue;
            ((CommonMinecartFurnace)((MinecartMemberFurnace)member).getEntity()).setFuelTicks(3600);
        }
    }
}

