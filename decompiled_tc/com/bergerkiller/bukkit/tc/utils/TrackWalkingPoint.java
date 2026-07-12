/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.pathfinding.PathNavigateEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathPredictEvent;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class TrackWalkingPoint {
    public final RailState state;
    public RailLogic currentRailLogic;
    public RailPath currentRailPath;
    public double moved = 0.0;
    public double movedTotal = 0.0;
    public FailReason failReason = FailReason.NONE;
    private Set<Block> loopFilter = null;
    private Vector lastLocation = null;
    private int _stuckCtr = 0;
    private boolean first = true;
    private boolean isAtEnd = false;
    private NavigatorWithEvent<?> navigator = null;

    public TrackWalkingPoint(RailState state) {
        state.position().assertAbsolute();
        this.state = state.clone();
        this.currentRailLogic = this.state.loadRailLogic();
        this.currentRailPath = this.currentRailLogic.getPath();
        if (this.isDerailed()) {
            this.failReason = FailReason.NO_RAIL;
        }
    }

    public TrackWalkingPoint(Location startPos, Vector motionVector) {
        this.state = new RailState();
        this.state.setRailPiece(RailPiece.createWorldPlaceholder(startPos.getWorld()));
        this.state.position().setMotion(motionVector);
        this.state.position().setLocation(startPos);
        RailType.loadRailInformation(this.state);
        this.currentRailLogic = this.state.loadRailLogic();
        this.currentRailPath = this.currentRailLogic.getPath();
        if (this.isDerailed()) {
            this.failReason = FailReason.NO_RAIL;
        }
    }

    public TrackWalkingPoint(Block startRail, BlockFace motionFace) {
        this(startRail, motionFace == null ? null : FaceUtil.faceToVector((BlockFace)motionFace));
    }

    public TrackWalkingPoint(Block startRail, Vector motion) {
        this.state = new RailState();
        this.state.position().relative = false;
        if (startRail != null) {
            this.state.setRailPiece(RailPiece.create(RailType.getType(startRail), startRail));
            this.state.position().setMotion(motion);
            this.state.position().setLocation(this.state.railType().getSpawnLocation(startRail, FaceUtil.vectorToBlockFace((Vector)motion, (boolean)false)));
            this.state.initEnterDirection();
            this.currentRailLogic = this.state.loadRailLogic();
            this.currentRailPath = this.currentRailLogic.getPath();
            if (this.isDerailed()) {
                this.failReason = FailReason.NO_RAIL;
            }
        } else {
            this.failReason = FailReason.NO_RAIL;
        }
    }

    public void setNavigator(Navigator<?> navigator) {
        if (navigator == null) {
            this.navigator = null;
            return;
        }
        NavigatorWithEvent<?> currentNav = this.navigator;
        if (currentNav == null || currentNav.navigator != navigator) {
            currentNav = new NavigatorWithEvent(navigator);
            this.navigator = currentNav;
            if (!this.isDerailed()) {
                currentNav.navigate(this.state, this.currentRailPath, this.movedTotal);
            }
            if (currentNav.event.isNavigationAborted()) {
                this.failReason = FailReason.NAVIGATION_ABORTED;
            }
        }
    }

    public void setFollowPredictedPath(MinecartMember<?> member) {
        this.setNavigator(member == null ? null : new Predictor(member));
    }

    public double getPredictedSpeedLimit() {
        NavigatorWithEvent<?> navigator = this.navigator;
        return navigator != null && navigator.event instanceof PathPredictEvent ? ((PathPredictEvent)navigator.event).getSpeedLimit() : Double.MAX_VALUE;
    }

    public double getPredictedRemainingBlockDistance() {
        NavigatorWithEvent<?> navigator = this.navigator;
        return navigator != null && navigator.navigator instanceof Predictor ? Math.max(0.0, ((Predictor)navigator.navigator).maxPredictorEndDistance - this.movedTotal) : 0.0;
    }

    public void skipFirst() {
        this.first = false;
    }

    public boolean moveStep(double limit) {
        double movedOnPath;
        if (this.isMovementAborted()) {
            return false;
        }
        if (this.isAtEnd) {
            this.moved = 0.0;
        } else {
            movedOnPath = this.currentRailPath.move(this.state, limit);
            this.state.initEnterDirection();
            this.moved = movedOnPath;
            this.movedTotal += movedOnPath;
            limit -= movedOnPath;
            if (limit > -1.0E-10 && limit < 1.0E-10) {
                this.failReason = FailReason.LIMIT_REACHED;
                return false;
            }
            this.isAtEnd = true;
        }
        if (!this.loadNextRail()) {
            return false;
        }
        movedOnPath = this.currentRailPath.move(this.state, limit);
        this.state.initEnterDirection();
        this.moved += movedOnPath;
        this.movedTotal += movedOnPath;
        limit -= movedOnPath;
        if (limit > -1.0E-10 && limit < 1.0E-10) {
            this.failReason = FailReason.LIMIT_REACHED;
            return false;
        }
        this.isAtEnd = true;
        return true;
    }

    public boolean moveFull() {
        if (this.isMovementAborted()) {
            return false;
        }
        if (this.first) {
            this.first = false;
            return true;
        }
        this.moved = this.currentRailPath.move(this.state, Double.MAX_VALUE);
        this.movedTotal += this.moved;
        this.state.initEnterDirection();
        this.isAtEnd = true;
        if (!this.loadNextRail()) {
            return false;
        }
        this.currentRailPath.snap(this.state.position(), this.state.railBlock());
        return true;
    }

    public boolean move(double distance) {
        if (this.isMovementAborted()) {
            return false;
        }
        if (this.first) {
            this.first = false;
            return true;
        }
        double remainingDistance = distance;
        int infCycleCtr = 0;
        do {
            double moved;
            if ((moved = this.currentRailPath.move(this.state, remainingDistance)) != 0.0 || remainingDistance <= 1.0E-4) {
                infCycleCtr = 0;
                if ((remainingDistance -= moved) <= 1.0E-5) {
                    this.moved = distance;
                    this.movedTotal += this.moved;
                    this.state.initEnterDirection();
                    return true;
                }
            } else if (++infCycleCtr > 100) {
                TrainCarts.plugin.log(Level.SEVERE, "[TrackWalkingPoint] Infinite rails loop detected at " + this.state.railBlock());
                TrainCarts.plugin.log(Level.SEVERE, "[TrackWalkingPoint] Rail Logic at rail is " + this.currentRailLogic);
                TrainCarts.plugin.log(Level.SEVERE, "[TrackWalkingPoint] Rail Type at rail is " + this.state.railType());
                this.moved = distance - remainingDistance;
                this.movedTotal += this.moved;
                this.failReason = FailReason.CYCLIC_PATH;
                this.state.initEnterDirection();
                return false;
            }
            this.isAtEnd = true;
        } while (this.loadNextRail());
        this.moved = distance - remainingDistance;
        this.movedTotal += this.moved;
        this.state.initEnterDirection();
        return false;
    }

    private boolean loadNextRail() {
        RailPath.Position position = this.state.position();
        NavigatorWithEvent<?> navigator = this.navigator;
        if (this.lastLocation == null) {
            this.lastLocation = new Vector(position.posX, position.posY, position.posZ);
            this._stuckCtr = 0;
        } else {
            if (this.lastLocation.getX() == position.posX && this.lastLocation.getY() == position.posY && this.lastLocation.getZ() == position.posZ) {
                this.failReason = FailReason.CYCLIC_PATH;
                if (++this._stuckCtr > 20) {
                    TrainCarts.plugin.log(Level.SEVERE, "[TrackWalkingPoint] Stuck on rails block " + this.state.railBlock());
                    TrainCarts.plugin.log(Level.SEVERE, "[TrackWalkingPoint] Rail Logic at rail is " + this.currentRailLogic);
                    TrainCarts.plugin.log(Level.SEVERE, "[TrackWalkingPoint] Rail Type at rail is " + this.state.railType());
                }
                return false;
            }
            this.lastLocation.setX(position.posX);
            this.lastLocation.setY(position.posY);
            this.lastLocation.setZ(position.posZ);
            this._stuckCtr = 0;
        }
        if (navigator != null && navigator.event.hasSwitchedPosition()) {
            RailPath.Position switchedPosition = navigator.event.getSwitchedPosition().clone();
            switchedPosition.makeAbsolute(navigator.event.railBlock());
            double distance = position.distance(switchedPosition);
            switchedPosition.copyTo(position);
            this.moved += distance;
            this.movedTotal += distance;
        }
        position.smallAdvance();
        Block prevRailBlock = this.state.railBlock();
        if (RailType.loadRailInformation(this.state) && this.loopFilter != null && !BlockUtil.equals((Block)this.state.railBlock(), (Block)prevRailBlock) && !this.loopFilter.add(this.state.railBlock())) {
            this.state.setRailPiece(this.state.railPiece().asNoneType());
            this.failReason = FailReason.LOOP_DETECTED;
        }
        if (this.state.railType() == RailType.NONE) {
            this.failReason = FailReason.NO_RAIL;
            return false;
        }
        this.currentRailLogic = this.state.loadRailLogic();
        this.currentRailPath = this.currentRailLogic.getPath();
        if (this.currentRailPath.isEmpty()) {
            this.failReason = FailReason.NO_RAIL;
            return false;
        }
        this.isAtEnd = true;
        if (navigator != null) {
            navigator.navigate(this.state, this.currentRailPath, this.movedTotal);
            if (navigator.event.isNavigationAborted()) {
                this.failReason = FailReason.NAVIGATION_ABORTED;
                return false;
            }
        }
        return true;
    }

    private boolean isDerailed() {
        return this.state.railType() == RailType.NONE || this.currentRailPath.isEmpty();
    }

    private boolean isMovementAborted() {
        if (this.isDerailed()) {
            return true;
        }
        NavigatorWithEvent<?> navigator = this.navigator;
        return navigator != null && navigator.event.isNavigationAborted();
    }

    public void setLoopFilter(boolean enabled) {
        HashSet hashSet = this.loopFilter = enabled ? new HashSet() : null;
        if (enabled && !this.isDerailed()) {
            this.loopFilter.add(this.state.railBlock());
        }
    }

    public boolean moveFindRail(Block railsBlock, double maxDistance) {
        double distance;
        this.movedTotal = 0.0;
        boolean startedOnRail = BlockUtil.equals((Block)this.state.railBlock(), (Block)railsBlock);
        if (!startedOnRail) {
            do {
                if (!this.moveFull()) {
                    return false;
                }
                if (!(this.movedTotal > maxDistance)) continue;
                this.failReason = FailReason.LIMIT_REACHED;
                return false;
            } while (!BlockUtil.equals((Block)this.state.railBlock(), (Block)railsBlock));
        }
        Location spawnLocation = this.state.railType().getSpawnLocation(railsBlock, this.state.enterFace());
        for (int i = 0; i < 10 && !((distance = this.state.position().distance(spawnLocation)) < 1.0E-4); ++i) {
            double moved = this.currentRailPath.move(this.state, distance);
            this.movedTotal += moved;
            if (!(moved < 1.0E-4)) continue;
            if (!startedOnRail) break;
            this.failReason = FailReason.LIMIT_REACHED;
            return false;
        }
        this.moved = this.movedTotal;
        return this.movedTotal <= maxDistance;
    }

    public static enum FailReason {
        NONE,
        NO_RAIL,
        CYCLIC_PATH,
        LOOP_DETECTED,
        LIMIT_REACHED,
        NAVIGATION_ABORTED;

    }

    private static class NavigatorWithEvent<E extends PathNavigateEvent> {
        public final Navigator<E> navigator;
        public final E event;

        public NavigatorWithEvent(Navigator<E> navigator) {
            this.navigator = navigator;
            this.event = navigator.createNewEvent();
        }

        public void navigate(RailState railState, RailPath railPath, double currentPosition) {
            this.event.resetToInitialState(railState, railPath, currentPosition);
            this.navigator.navigate(this.event);
        }
    }

    public static interface Navigator<E extends PathNavigateEvent> {
        public void navigate(E var1);

        public E createNewEvent();
    }

    private static class Predictor
    implements Navigator<PathPredictEvent> {
        private final MinecartMember<?> member;
        private List<BlockPredictor> activeBlockPredictors = Collections.emptyList();
        private Set<Object> usedBlockPredictorTokens = Collections.emptySet();
        private double maxPredictorEndDistance = 0.0;

        public Predictor(MinecartMember<?> member) {
            this.member = member;
        }

        @Override
        public PathPredictEvent createNewEvent() {
            return new PathPredictEvent(this.member.getTrainCarts().getPathProvider(), this.member);
        }

        @Override
        public void navigate(PathPredictEvent event) {
            double currentDistance = event.currentDistance();
            event.provider().predictRoutingHandler(event);
            boolean predictorsRemoved = false;
            Iterator<BlockPredictor> iter = this.activeBlockPredictors.iterator();
            while (iter.hasNext()) {
                BlockPredictor blockPredictor = iter.next();
                if (blockPredictor.handler.update(event, currentDistance - blockPredictor.startDistance) && !(currentDistance >= blockPredictor.endDistance)) continue;
                iter.remove();
                predictorsRemoved = true;
            }
            if (predictorsRemoved) {
                this.maxPredictorEndDistance = 0.0;
                for (BlockPredictor blockPredictor : this.activeBlockPredictors) {
                    this.maxPredictorEndDistance = Math.max(this.maxPredictorEndDistance, blockPredictor.endDistance);
                }
            }
            if (event.hasNewBlockTrackers()) {
                if (this.activeBlockPredictors.isEmpty()) {
                    this.activeBlockPredictors = new ArrayList<BlockPredictor>();
                }
                if (this.usedBlockPredictorTokens.isEmpty()) {
                    this.usedBlockPredictorTokens = new HashSet<Object>();
                }
                for (PathPredictEvent.ActiveBlockHandler activeHandler : event.getNewBlockTrackers()) {
                    if (!this.usedBlockPredictorTokens.add(activeHandler.token)) continue;
                    BlockPredictor blockPredictor = new BlockPredictor(currentDistance, activeHandler);
                    this.activeBlockPredictors.add(blockPredictor);
                    this.maxPredictorEndDistance = Math.max(this.maxPredictorEndDistance, blockPredictor.endDistance);
                }
            }
        }
    }

    private static class BlockPredictor {
        public final PathPredictEvent.BlockHandler handler;
        public final double startDistance;
        public final double endDistance;

        public BlockPredictor(double currentDistance, PathPredictEvent.ActiveBlockHandler activeHandler) {
            this.handler = activeHandler.handler;
            this.startDistance = currentDistance;
            this.endDistance = this.startDistance + activeHandler.maxDistance;
        }
    }
}

