/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.statements;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.rails.direction.RailEnterDirection;
import com.bergerkiller.bukkit.tc.statements.Statement;
import org.bukkit.block.BlockFace;

public class StatementDirection
extends Statement {
    @Override
    public boolean match(String text) {
        return false;
    }

    @Override
    public boolean matchArray(String text) {
        return text.equals("ed");
    }

    @Override
    public boolean handle(MinecartMember<?> member, String text, SignActionEvent event) {
        return false;
    }

    @Override
    public boolean handle(MinecartGroup group, String text, SignActionEvent event) {
        return false;
    }

    @Override
    public boolean handleArray(MinecartGroup group, String[] directionNames, SignActionEvent event) {
        if (event.getGroup() == group) {
            return this.handleArray(event.getMember(), directionNames, event);
        }
        return this.handleArray(group.head(), directionNames, event);
    }

    @Override
    public boolean handleArray(MinecartMember<?> member, String[] directionNames, SignActionEvent event) {
        RailState enterState = null;
        if (member == null || event.getMember() == member) {
            enterState = event.getCartEnterState();
        }
        if (member != null && enterState == null && (enterState = member.getRailTracker().getState()) != null && enterState.railPiece().isNone()) {
            enterState = null;
        }
        if (enterState == null) {
            return false;
        }
        BlockFace forwardDirection = event.getFacing().getOppositeFace();
        for (String directionName : directionNames) {
            for (RailEnterDirection dir : RailEnterDirection.parseAll(event.getRailPiece(), forwardDirection, directionName)) {
                if (!dir.match(enterState)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasRequiredContext(MinecartMember<?> member, MinecartGroup group, SignActionEvent event) {
        if (event == null) {
            return false;
        }
        return member != null || group != null || event.getCartEnterState() != null;
    }

    @Override
    public boolean requiredEvent() {
        return true;
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}

