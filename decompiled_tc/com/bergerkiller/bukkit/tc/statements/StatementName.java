/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.statements.Statement;
import java.util.Collections;

public class StatementName
extends Statement {
    @Override
    public boolean match(String text) {
        return LogicUtil.contains((Object)text, (Object[])new String[]{"renamed", "rename", "ren", "name", "named"});
    }

    @Override
    public boolean matchArray(String text) {
        return text.equals("name") || text.equals("n");
    }

    @Override
    public boolean handle(MinecartGroup group, String text, SignActionEvent event) {
        return group.getProperties().isTrainRenamed();
    }

    @Override
    public boolean handleArray(MinecartGroup group, String[] text, SignActionEvent event) {
        TrainProperties prop = group.getProperties();
        for (String name : text) {
            if (!Util.matchText(Collections.singletonList(prop.getTrainName()), name)) continue;
            return true;
        }
        return false;
    }
}

