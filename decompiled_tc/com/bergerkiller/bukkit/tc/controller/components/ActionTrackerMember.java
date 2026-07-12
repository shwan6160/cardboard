/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.Location
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.actions.Action;
import com.bergerkiller.bukkit.tc.actions.MemberAction;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunch;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunchDirection;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunchLocation;
import com.bergerkiller.bukkit.tc.actions.MemberActionWaitDistance;
import com.bergerkiller.bukkit.tc.actions.MemberActionWaitLocation;
import com.bergerkiller.bukkit.tc.actions.MemberActionWaitOccupied;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.ActionTracker;
import com.bergerkiller.bukkit.tc.utils.LauncherConfig;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class ActionTrackerMember
extends ActionTracker {
    private final MinecartMember<?> owner;

    public ActionTrackerMember(MinecartMember<?> owner) {
        this.owner = owner;
    }

    @Override
    public MinecartMember<?> getOwner() {
        return this.owner;
    }

    @Override
    public MinecartGroup getGroupOwner() {
        return this.owner.getGroup();
    }

    @Override
    public void clear() {
        super.clear();
        if (!this.owner.isUnloaded()) {
            this.owner.getGroup().getActions().removeActions(this.owner);
        }
    }

    @Override
    public <T extends Action> T addAction(T action) {
        if (action instanceof MemberAction) {
            ((MemberAction)action).setMember(this.owner);
        }
        return super.addAction(action);
    }

    public <A extends MemberAction> A addGroupAction(A action) {
        action.setMember(this.owner);
        return this.owner.getGroup().getActions().addAction(action);
    }

    public MemberActionWaitDistance addActionWaitDistance(double distance) {
        return this.addGroupAction(new MemberActionWaitDistance(distance));
    }

    public MemberActionWaitLocation addActionWaitLocation(Location location) {
        return this.addGroupAction(new MemberActionWaitLocation(location));
    }

    public MemberActionWaitLocation addActionWaitLocation(Location location, double radius) {
        return this.addGroupAction(new MemberActionWaitLocation(location, radius));
    }

    public MemberActionLaunch addActionLaunch(double distance, double targetvelocity) {
        MemberActionLaunch action = new MemberActionLaunch();
        action.initDistance(distance, targetvelocity);
        return this.addGroupAction(action);
    }

    public MemberActionLaunch addActionTimedLaunch(int timeTicks, double targetvelocity) {
        MemberActionLaunch action = new MemberActionLaunch();
        action.initTime(timeTicks, targetvelocity);
        return this.addGroupAction(action);
    }

    public MemberActionLaunch addActionLaunch(LauncherConfig config, double targetvelocity) {
        MemberActionLaunch action = new MemberActionLaunch();
        action.init(config, targetvelocity);
        return this.addGroupAction(action);
    }

    public MemberActionLaunchDirection addActionLaunch(BlockFace direction, double targetdistance, double targetvelocity) {
        MemberActionLaunchDirection action = new MemberActionLaunchDirection();
        action.initDistance(targetdistance, targetvelocity, direction);
        return this.addGroupAction(action);
    }

    public MemberActionLaunchDirection addActionTimedLaunch(BlockFace direction, int timeTicks, double targetvelocity) {
        MemberActionLaunchDirection action = new MemberActionLaunchDirection();
        action.initTime(timeTicks, targetvelocity, direction);
        return this.addGroupAction(action);
    }

    public MemberActionLaunchDirection addActionLaunch(BlockFace direction, LauncherConfig config, double targetvelocity) {
        MemberActionLaunchDirection action = new MemberActionLaunchDirection();
        action.init(config, targetvelocity, direction);
        return this.addGroupAction(action);
    }

    public MemberActionLaunchDirection addActionLaunch(BlockFace direction, LauncherConfig config, double targetvelocity, double targetspeedlimit) {
        MemberActionLaunchDirection action = new MemberActionLaunchDirection();
        action.init(config, targetvelocity, targetspeedlimit, direction);
        return this.addGroupAction(action);
    }

    public MemberActionLaunchLocation addActionLaunch(Location destination, double targetvelocity) {
        return this.addGroupAction(new MemberActionLaunchLocation(targetvelocity, destination));
    }

    public MemberActionLaunchLocation addActionLaunch(Vector offset, double targetvelocity) {
        return this.addActionLaunch(((CommonMinecart)this.owner.getEntity()).getLocation().add(offset), targetvelocity);
    }

    public MemberActionWaitOccupied addActionWaitOccupied(double maxDistance, long launchDelay, double launchDistance) {
        return this.addActionWaitOccupied(maxDistance, launchDelay, launchDistance, null, null);
    }

    public MemberActionWaitOccupied addActionWaitOccupied(double maxDistance, long launchDelay, double launchDistance, BlockFace launchDirection, Double launchVelocity) {
        return this.addGroupAction(new MemberActionWaitOccupied(maxDistance, launchDelay, launchDistance, launchDirection, launchVelocity));
    }
}

