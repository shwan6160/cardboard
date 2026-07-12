/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.tc.actions.Action;
import com.bergerkiller.bukkit.tc.actions.GroupAction;
import com.bergerkiller.bukkit.tc.actions.GroupActionRefill;
import com.bergerkiller.bukkit.tc.actions.GroupActionSizzle;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitDelay;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitForever;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitState;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitTicks;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitTill;
import com.bergerkiller.bukkit.tc.actions.MemberAction;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunch;
import com.bergerkiller.bukkit.tc.actions.MemberActionWaitOccupied;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.ActionTracker;

public class ActionTrackerGroup
extends ActionTracker {
    private final MinecartGroup owner;

    public ActionTrackerGroup(MinecartGroup owner) {
        this.owner = owner;
    }

    @Override
    public MinecartGroup getOwner() {
        return this.owner;
    }

    @Override
    public MinecartGroup getGroupOwner() {
        return this.owner;
    }

    @Override
    public void doTick() {
        super.doTick();
        for (MinecartMember<?> member : this.owner) {
            member.getActions().doTick();
        }
    }

    public void launchReset() {
        MemberActionWaitOccupied waitOccupied;
        Action action = this.getCurrentAction();
        if (action instanceof MemberActionLaunch && action.elapsedTicks() == 0) {
            MemberActionLaunch launchAction = (MemberActionLaunch)action;
            this.getOwner().setForwardForce(launchAction.getTargetVelocity());
        } else if (action instanceof MemberActionWaitOccupied && !Double.isNaN((waitOccupied = (MemberActionWaitOccupied)action).getPostWaitLaunchForce())) {
            this.getOwner().setForwardForce(waitOccupied.getPostWaitLaunchForce());
        }
        this.clear();
    }

    @Override
    public void clear() {
        super.clear();
        for (MinecartMember<?> member : this.owner) {
            member.getActions().clear();
        }
    }

    @Override
    public <T extends Action> T addAction(T action) {
        if (action instanceof GroupAction) {
            ((GroupAction)action).setGroup(this.owner);
        } else if (action instanceof MemberAction && ((MemberAction)action).getMember() == null) {
            throw new RuntimeException("Can not add member action without a member set beforehand!");
        }
        return super.addAction(action);
    }

    public GroupActionWaitDelay addActionWait(long delay) {
        return this.addAction(new GroupActionWaitDelay(delay));
    }

    public GroupActionWaitTill addActionWaitTill(long time) {
        return this.addAction(new GroupActionWaitTill(time));
    }

    public GroupActionWaitTicks addActionWaitTicks(int ticks) {
        return this.addAction(new GroupActionWaitTicks(ticks));
    }

    public GroupActionWaitForever addActionWaitForever() {
        return this.addAction(new GroupActionWaitForever());
    }

    public GroupActionWaitState addActionWaitState() {
        return this.addAction(new GroupActionWaitState());
    }

    public GroupActionSizzle addActionSizzle() {
        return this.addAction(new GroupActionSizzle());
    }

    public GroupActionRefill addActionRefill() {
        return this.addAction(new GroupActionRefill());
    }
}

