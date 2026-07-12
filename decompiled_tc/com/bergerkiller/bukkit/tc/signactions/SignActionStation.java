/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Station;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitStationRouting;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathConnection;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.pathfinding.PathPredictEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.block.BlockFace;

public class SignActionStation
extends TrainCartsSignAction {
    public SignActionStation() {
        super("station");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (!info.isAction(SignActionType.REDSTONE_CHANGE, SignActionType.GROUP_ENTER, SignActionType.GROUP_LEAVE)) {
            return;
        }
        if (info.isAction(SignActionType.GROUP_LEAVE)) {
            if (info.getGroup().getActions().isWaitAction()) {
                info.getGroup().getActions().clear();
            }
            info.setLevers(false);
            return;
        }
        if (!info.hasRails() || !info.hasGroup() || info.getGroup().isEmpty()) {
            return;
        }
        MinecartGroup group = info.getGroup();
        Station station = new Station(info);
        if (station.getInstruction() == null) {
            if (info.isAction(SignActionType.REDSTONE_CHANGE) && info.getGroup().getActions().isCurrentActionTag(station.getTag())) {
                info.getGroup().getActions().clear();
            }
        } else if (station.getInstruction() == BlockFace.SELF) {
            MinecartMember<?> centerMember = station.getCenterPositionCart();
            if (info.isAction(SignActionType.REDSTONE_CHANGE) && (centerMember.isMovementControlled() || info.getGroup().isMoving())) {
                return;
            }
            group.getActions().launchReset();
            if (!station.isAutoRouting() && station.getNextDirection() == Direction.NONE) {
                station.centerTrain();
                station.waitTrain(Long.MAX_VALUE);
                return;
            }
            if (station.isAutoRouting()) {
                if (station.hasDelay()) {
                    station.centerTrain();
                    station.waitTrainKeepLeversDown(station.getDelay());
                }
                group.getActions().addAction(new GroupActionWaitStationRouting(station, info.getRailPiece(), station.hasDelay())).addTag(station.getTag());
                return;
            }
            BlockFace trainDirection = station.getNextDirectionFace();
            if (station.hasDelay()) {
                station.centerTrain();
                station.waitTrain(station.getDelay());
            } else if (!info.getMember().isDirectionTo(trainDirection)) {
                station.centerTrain();
            }
            station.launchTo(trainDirection);
        } else {
            group.getActions().launchReset();
            if (station.hasDelay() || group.head().isMoving() && !info.getMember().isDirectionTo(station.getInstruction())) {
                station.centerTrain();
            }
            if (station.hasDelay()) {
                station.waitTrain(station.getDelay());
            }
            station.launchTo(station.getInstruction());
        }
    }

    @Override
    public void predictPathFinding(SignActionEvent info, PathPredictEvent prediction) {
        RailJunction junction;
        PathConnection conn;
        Station.StationConfig stationConfig = new Station.StationConfig();
        stationConfig.setAutoModeUsingSign(info);
        if (!stationConfig.isAutoRouting()) {
            return;
        }
        stationConfig.setInstructionUsingSign(info);
        if (stationConfig.getInstruction() != BlockFace.SELF) {
            return;
        }
        PathNode node = PathNode.getOrCreate(info.getRails());
        if (node == null) {
            return;
        }
        node.addSwitcher();
        if (info.getTrainCarts().getPathProvider().isProcessing()) {
            prediction.setSpeedLimit(0.0);
            return;
        }
        String destination = prediction.group().getProperties().getDestination();
        if (!LogicUtil.nullOrEmpty((String)destination) && !node.containsName(destination) && (conn = node.findConnection(destination)) != null && (junction = info.findJunction(conn.junctionName)) != null) {
            prediction.setSwitchedJunction(junction);
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create().setPermission(Permission.BUILD_STATION).setName("station").setDescription("stop, wait and launch trains").setTraincartsWIKIHelp("TrainCarts/Signs/Station").handle(event);
    }

    @Override
    public boolean overrideFacing() {
        return true;
    }

    @Override
    public boolean isRailSwitcher(SignActionEvent info) {
        for (String part : info.getLine(3).split(" ")) {
            if (!part.equalsIgnoreCase("route")) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getDescriptiveOutputName(SignActionEvent event) {
        return "Train waiting on station";
    }
}

