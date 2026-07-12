/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.Statement;

public class StatementRandom
extends Statement {
    @Override
    public boolean match(String text) {
        return text.startsWith("rand");
    }

    @Override
    public boolean matchArray(String text) {
        return text.startsWith("rand");
    }

    @Override
    public boolean requiresTrain() {
        return false;
    }

    private boolean handle(String ... text) {
        double chance = 0.5;
        if (text.length > 0) {
            chance = ParseUtil.parseDouble((String)text[0], (double)chance);
            if (text[0].endsWith("%")) {
                chance /= 100.0;
            }
            chance = MathUtil.clamp((double)chance, (double)0.0, (double)1.0);
        }
        return Math.random() < chance;
    }

    @Override
    public boolean handle(MinecartGroup group, String text, SignActionEvent event) {
        return this.handle(new String[0]);
    }

    @Override
    public boolean handle(MinecartMember<?> member, String text, SignActionEvent event) {
        return this.handle(new String[0]);
    }

    @Override
    public boolean handleArray(MinecartGroup group, String[] text, SignActionEvent event) {
        return this.handle(text);
    }

    @Override
    public boolean handleArray(MinecartMember<?> member, String[] text, SignActionEvent event) {
        return this.handle(text);
    }
}

