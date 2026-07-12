/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.Statement;
import java.util.List;
import org.bukkit.entity.Player;

public class StatementPassenger
extends Statement {
    @Override
    public boolean match(String text) {
        return text.startsWith("passenger") || text.startsWith("player");
    }

    @Override
    public boolean handle(MinecartMember<?> member, String text, SignActionEvent event) {
        return text.toLowerCase().startsWith("player") ? ((CommonMinecart)member.getEntity()).hasPlayerPassenger() : ((CommonMinecart)member.getEntity()).hasPassenger();
    }

    @Override
    public boolean handle(MinecartGroup group, String text, SignActionEvent event) {
        int count = 0;
        boolean playermode = text.toLowerCase().startsWith("player");
        for (MinecartMember<?> member : group) {
            if (playermode) {
                count += ((CommonMinecart)member.getEntity()).getPlayerPassengers().size();
                continue;
            }
            count += ((CommonMinecart)member.getEntity()).getPassengers().size();
        }
        return Util.evaluate(count, text);
    }

    @Override
    public boolean matchArray(String text) {
        return text.equals("p");
    }

    @Override
    public boolean handleArray(MinecartMember<?> member, String[] names, SignActionEvent event) {
        List playerPassengers = ((CommonMinecart)member.getEntity()).getPlayerPassengers();
        if (!playerPassengers.isEmpty()) {
            for (Player player : playerPassengers) {
                String pname = player.getName();
                for (String name : names) {
                    if (!Util.matchText(pname, name)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int priority() {
        return -1;
    }
}

