/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.pathfinding;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.pathfinding.PathNavigateEventBaseImpl;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.pathfinding.PathProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathPredictEvent
extends PathNavigateEventBaseImpl {
    private final PathProvider provider;
    private final MinecartMember<?> member;
    private double speedLimit;
    private List<ActiveBlockHandler> newBlockHandlers = Collections.emptyList();

    public PathPredictEvent(PathProvider provider, MinecartMember<?> member) {
        this.provider = provider;
        this.member = member;
        this.speedLimit = Double.MAX_VALUE;
    }

    @Override
    public void resetToInitialState(RailState railState, RailPath railPath, double currentDistance) {
        super.resetToInitialState(railState, railPath, currentDistance);
        this.setSpeedLimit(Double.MAX_VALUE);
        this.newBlockHandlers = Collections.emptyList();
    }

    public MinecartGroup group() {
        return this.member.getGroup();
    }

    public MinecartMember<?> member() {
        return this.member;
    }

    public PathProvider provider() {
        return this.provider;
    }

    public PathNode pathNode() {
        RailState state = this.railState();
        return this.provider.getWorld(state.railWorld()).getNodeAtRail(state.railBlock());
    }

    public double getSpeedLimit() {
        return this.speedLimit;
    }

    public boolean hasSpeedLimit() {
        return this.speedLimit != Double.MAX_VALUE;
    }

    public void setSpeedLimit(double speedLimit) {
        this.speedLimit = speedLimit;
    }

    public void addSpeedLimit(double speedLimit) {
        if (speedLimit < this.speedLimit) {
            this.speedLimit = speedLimit;
        }
    }

    public void trackBlock(BlockHandler handler, Object token, double maxDistance) {
        if (this.newBlockHandlers.isEmpty()) {
            this.newBlockHandlers = new ArrayList<ActiveBlockHandler>();
        }
        this.newBlockHandlers.add(new ActiveBlockHandler(handler, token, maxDistance));
    }

    public boolean hasNewBlockTrackers() {
        return !this.newBlockHandlers.isEmpty();
    }

    public List<ActiveBlockHandler> getNewBlockTrackers() {
        return this.newBlockHandlers;
    }

    public static class ActiveBlockHandler {
        public final BlockHandler handler;
        public final Object token;
        public final double maxDistance;

        public ActiveBlockHandler(BlockHandler handler, Object token, double maxDistance) {
            this.handler = handler;
            this.token = token;
            this.maxDistance = maxDistance;
        }
    }

    public static interface BlockHandler {
        public boolean update(PathPredictEvent var1, double var2);
    }
}

