/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.Statement;

public class StatementBoolean
extends Statement {
    public static final StatementBoolean INSTANCE = new StatementBoolean();
    public static final StatementBoolean EMPTY = new StatementBoolean();

    @Override
    public boolean match(String text) {
        return text.equals("true") || text.equals("false");
    }

    @Override
    public boolean handle(MinecartMember<?> member, String text, SignActionEvent event) {
        return text.equalsIgnoreCase("true");
    }

    @Override
    public boolean handle(MinecartGroup group, String text, SignActionEvent event) {
        return text.equalsIgnoreCase("true");
    }

    @Override
    public boolean requiresTrain() {
        return false;
    }

    @Override
    public boolean matchArray(String text) {
        return false;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public int priority() {
        return 1;
    }
}

