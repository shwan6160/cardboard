/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.conversion.Conversion
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.entity.EntityType
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.Statement;
import java.util.Locale;
import org.bukkit.entity.EntityType;

public class StatementType
extends Statement {
    private boolean isSize(String text) {
        int index = Util.getOperatorIndex(text);
        if (index != -1) {
            text = text.substring(0, index);
        }
        return LogicUtil.contains((Object)text, (Object[])new String[]{"cartcount", "trainsize", "length", "count", "size"});
    }

    @Override
    public boolean match(String text) {
        return this.isSize(text) || Conversion.toMinecartType.convert((Object)text) != null;
    }

    @Override
    public boolean handle(MinecartMember<?> member, String text, SignActionEvent event) {
        return this.isSize(text.toLowerCase(Locale.ENGLISH)) || ((CommonMinecart)member.getEntity()).getType() == Conversion.toMinecartType.convert((Object)text);
    }

    @Override
    public boolean handle(MinecartGroup group, String text, SignActionEvent event) {
        if (this.isSize(text.toLowerCase(Locale.ENGLISH))) {
            return Util.evaluate(group.size(), text);
        }
        EntityType type = (EntityType)Conversion.toMinecartType.convert((Object)text);
        return type != null && Util.evaluate(group.size(type), text);
    }

    @Override
    public boolean matchArray(String text) {
        return false;
    }
}

