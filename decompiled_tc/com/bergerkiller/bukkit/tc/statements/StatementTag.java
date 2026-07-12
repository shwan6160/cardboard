/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.Statement;

public class StatementTag
extends Statement {
    @Override
    public int priority() {
        return -99999;
    }

    @Override
    public boolean match(String text) {
        return true;
    }

    @Override
    public boolean matchArray(String text) {
        return true;
    }

    @Override
    public boolean handle(MinecartMember<?> member, String tag, SignActionEvent event) {
        return this.handleArray(member, StatementTag.parseArray(tag), event);
    }

    @Override
    public boolean handleArray(MinecartMember<?> member, String[] tags, SignActionEvent event) {
        for (String tag : tags) {
            if (!member.getProperties().matchTag(tag)) continue;
            return true;
        }
        return false;
    }
}

