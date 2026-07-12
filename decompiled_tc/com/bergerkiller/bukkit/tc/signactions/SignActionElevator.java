/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignActionMode;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.BlockTimeoutMap;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class SignActionElevator
extends TrainCartsSignAction {
    public static final SignActionElevator INSTANCE = new SignActionElevator();
    public final BlockTimeoutMap ignoreTimes = new BlockTimeoutMap();

    public SignActionElevator() {
        super("elevator");
    }

    public ElevatorRail findNextElevator(RailPiece from, BlockFace direction, int elevatorCount) {
        block0: while ((from = Util.findNextRailPiece(from.block(), direction)) != null) {
            for (RailLookup.TrackedSign sign : from.signs()) {
                if (sign.getAction() != this) continue;
                if (--elevatorCount > 0) continue block0;
                return new ElevatorRail(from, sign);
            }
        }
        return null;
    }

    private static double getTrackDistance(RailState state) {
        TrackWalkingPoint p = new TrackWalkingPoint(state);
        p.setLoopFilter(true);
        p.skipFirst();
        p.move(16.0);
        return p.movedTotal;
    }

    @Override
    public void execute(SignActionEvent info) {
        if (info.getMode() == SignActionMode.NONE || !info.hasRailedMember() || !info.isPowered()) {
            return;
        }
        if (!info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_CHANGE)) {
            return;
        }
        if (this.ignoreTimes.isMarked(info.getRails(), 1000L)) {
            return;
        }
        boolean forced = false;
        BlockFace mode = BlockFace.UP;
        if (info.isLine(2, "down")) {
            mode = BlockFace.DOWN;
            forced = true;
        } else if (info.isLine(2, "up")) {
            forced = true;
        }
        int elevatorCount = ParseUtil.parseInt((String)info.getLine(2), (int)1);
        ElevatorRail nextElevator = this.findNextElevator(info.getRailPiece(), mode, elevatorCount);
        if (!forced && nextElevator == null) {
            nextElevator = this.findNextElevator(info.getRailPiece(), mode.getOppositeFace(), elevatorCount);
        }
        if (nextElevator == null) {
            return;
        }
        this.ignoreTimes.mark(nextElevator.rail.block());
        RailState spawnState = nextElevator.findSpawnState(info);
        info.getGroup().teleportAndGo(spawnState.railBlock(), spawnState.motionVector());
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create().setPermission(Permission.BUILD_ENTER).setName("train elevator").setDescription("teleport trains vertically").setTraincartsWIKIHelp("TrainCarts/Signs/Elevator").handle(event);
    }

    public static class ElevatorRail {
        public final RailPiece rail;
        public final RailLookup.TrackedSign sign;

        public ElevatorRail(RailPiece rail, RailLookup.TrackedSign sign) {
            this.rail = rail;
            this.sign = sign;
        }

        public RailState findSpawnState(SignActionEvent info) {
            RailState spawnState = RailState.getSpawnState(this.rail);
            Direction launchDirection = Direction.parse(info.getLine(3));
            if (launchDirection != Direction.NONE) {
                if (spawnState.position().motDot(launchDirection.getDirection(info.getFacing(), info.getCartEnterFace())) < 0.0) {
                    spawnState.position().invertMotion();
                }
                return spawnState;
            }
            Vector signForward = FaceUtil.faceToVector((BlockFace)this.sign.getFacing());
            double dot = signForward.dot(spawnState.motionVector());
            if (Math.abs(dot) > 0.707106781) {
                if (dot < 0.0) {
                    spawnState.position().invertMotion();
                }
                return spawnState;
            }
            RailState spawnStateReverse = spawnState.cloneAndInvertMotion();
            if (SignActionElevator.getTrackDistance(spawnStateReverse) > SignActionElevator.getTrackDistance(spawnState)) {
                return spawnStateReverse;
            }
            return spawnState;
        }
    }
}

