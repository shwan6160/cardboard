/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.actions.Action;
import com.bergerkiller.bukkit.tc.actions.MemberActionWaitOccupied;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import org.bukkit.block.BlockFace;

public class SignActionWait
extends TrainCartsSignAction {
    public SignActionWait() {
        super("wait");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (info.isAction(SignActionType.GROUP_ENTER) && info.isPowered()) {
            double launchDistance;
            if (!info.hasRailedMember()) {
                return;
            }
            BlockFace launchDirection = null;
            String[] launchData = Util.splitBySeparator(info.getLine(3));
            Double launchVelocity = null;
            if (launchData.length == 3) {
                launchDistance = ParseUtil.parseDouble((String)launchData[0], (double)2.0);
                launchDirection = Direction.parse(launchData[1]).getDirectionLegacy(info.getFacing(), info.getCartEnterFace());
                launchVelocity = Util.parseVelocity(launchData[2], info.getGroup().getAverageForce());
            } else {
                launchDistance = launchData.length == 1 ? ParseUtil.parseDouble((String)launchData[0], (double)2.0) : 2.0;
            }
            String distanceData = info.getLine(1);
            if (distanceData.startsWith("waiter ")) {
                distanceData = distanceData.replaceFirst("waiter ", "");
            } else if (distanceData.startsWith("waiter")) {
                distanceData = distanceData.replaceFirst("waiter", "");
            } else if (distanceData.startsWith("wait ")) {
                distanceData = distanceData.replaceFirst("wait ", "");
            } else if (distanceData.startsWith("wait")) {
                distanceData = distanceData.replaceFirst("wait", "");
            }
            double distance = Double.NaN;
            if (distanceData.matches("[a-zA-Z]+")) {
                RailState state = info.getGroup().head().discoverRail();
                if (launchDirection != null) {
                    state.setMotionVector(FaceUtil.faceToVector((BlockFace)launchDirection));
                }
                TrackWalkingPoint walkingPoint = new TrackWalkingPoint(state);
                block0: while (walkingPoint.movedTotal < (double)TCConfig.maxDetectorLength && walkingPoint.moveFull()) {
                    for (RailLookup.TrackedSign sign : walkingPoint.state.railSigns()) {
                        if (sign.getRail().block().equals((Object)info.getRails())) continue;
                        SignActionEvent found = new SignActionEvent(sign, info.getGroup());
                        if (!found.isType(distanceData)) continue;
                        distance = walkingPoint.movedTotal;
                        break block0;
                    }
                }
                if (Double.isNaN(distance)) {
                    Localization.WAITER_TARGET_NOT_FOUND.broadcast(info.getGroup(), distanceData);
                } else {
                    info.setLine(1, "waiter" + String.valueOf(MathUtil.round((double)distance, (int)3)));
                }
            } else {
                distance = ParseUtil.parseDouble((String)info.getLine(1), (double)100.0);
            }
            long delay = ParseUtil.parseTime((String)info.getLine(2));
            if (info.getGroup().isObstacleAhead(distance, true, false)) {
                Action currentAction = info.getGroup().getActions().getCurrentAction();
                if (currentAction instanceof MemberActionWaitOccupied) {
                    MemberActionWaitOccupied waitOccupied = (MemberActionWaitOccupied)currentAction;
                    waitOccupied.adjustDistance(distance);
                } else {
                    info.getGroup().getActions().launchReset();
                    info.getMember().getActions().addActionWaitOccupied(distance, delay, launchDistance, launchDirection, launchVelocity).setToggleOutputOf(info.getTrackedSign());
                }
            }
        } else if (info.isAction(SignActionType.REDSTONE_OFF)) {
            info.setLevers(false);
            if (info.hasRailedMember()) {
                info.getGroup().getActions().clear();
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create().setPermission(Permission.BUILD_WAIT).setName("train waiter sign").setDescription("waits the train until the tracks ahead are clear").setTraincartsWIKIHelp("TrainCarts/Signs/Waiter").handle(event);
    }
}

