/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.Statement;
import org.bukkit.entity.Entity;

public class StatementMob
extends Statement {
    @Override
    public boolean match(String text) {
        return text.equals("mob") || text.equals("mobs");
    }

    @Override
    public boolean handle(MinecartMember<?> member, String text, SignActionEvent event) {
        for (Entity passenger : ((CommonMinecart)member.getEntity()).getPassengers()) {
            if (!EntityUtil.isMob((Entity)passenger)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean handle(MinecartGroup group, String text, SignActionEvent event) {
        int count = 0;
        for (MinecartMember<?> member : group) {
            for (Entity passenger : ((CommonMinecart)member.getEntity()).getPassengers()) {
                if (!EntityUtil.isMob((Entity)passenger)) continue;
                ++count;
            }
        }
        return Util.evaluate(count, text);
    }

    @Override
    public boolean matchArray(String text) {
        return text.equals("m");
    }

    public boolean hasMob(MinecartMember<?> member, String mob) {
        int idx = Util.getOperatorIndex(mob);
        if (idx == 0) {
            return false;
        }
        if (idx > 0) {
            mob = mob.substring(0, idx - 1);
        }
        for (Entity passenger : ((CommonMinecart)member.getEntity()).getPassengers()) {
            String mobname;
            if (!EntityUtil.isMob((Entity)passenger) || !(mobname = EntityUtil.getName((Entity)passenger)).contains(mob)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean handleArray(MinecartMember<?> member, String[] mobs, SignActionEvent event) {
        if (mobs.length == 0) {
            return this.handle(member, null, event);
        }
        for (int i = 0; i < mobs.length; ++i) {
            mobs[i] = mobs[i].replace("_", "").replace(" ", "");
        }
        for (String mob : mobs) {
            if (!this.hasMob(member, mob)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean handleArray(MinecartGroup group, String[] mobs, SignActionEvent event) {
        if (mobs.length == 0) {
            return this.handle(group, null, event);
        }
        for (int i = 0; i < mobs.length; ++i) {
            mobs[i] = mobs[i].replace("_", "").replace(" ", "");
        }
        for (String mob : mobs) {
            int count = 0;
            for (MinecartMember<?> member : group) {
                if (!this.hasMob(member, mob)) continue;
                ++count;
            }
            if (!Util.evaluate(count, mob)) continue;
            return true;
        }
        return false;
    }
}

