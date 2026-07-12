/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.Statement;
import org.bukkit.entity.Player;

public class StatementPermission
extends Statement {
    @Override
    public boolean match(String text) {
        return false;
    }

    @Override
    public boolean matchArray(String text) {
        return text.equals("pm") || text.equals("perm");
    }

    @Override
    public boolean handleArray(MinecartGroup group, String[] text, SignActionEvent event) {
        for (MinecartMember<?> member : group) {
            if (this.handleArray(member, text, event)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean handleArray(MinecartMember<?> member, String[] text, SignActionEvent event) {
        if (((CommonMinecart)member.getEntity()).hasPlayerPassenger()) {
            for (Player player : ((CommonMinecart)member.getEntity()).getPlayerPassengers()) {
                for (String perm : text) {
                    if (player.hasPermission(perm)) continue;
                    return false;
                }
            }
        }
        return true;
    }
}

