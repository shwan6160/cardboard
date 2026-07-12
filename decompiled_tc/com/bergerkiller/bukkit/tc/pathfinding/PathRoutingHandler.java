/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.pathfinding;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.pathfinding.PathNavigateEventBaseImpl;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.pathfinding.PathPredictEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathProvider;
import com.bergerkiller.bukkit.tc.pathfinding.PathRailInfo;
import com.bergerkiller.bukkit.tc.pathfinding.PathWorld;
import org.bukkit.World;

public interface PathRoutingHandler {
    public void process(PathRouteEvent var1);

    default public void predict(PathPredictEvent event) {
    }

    public static class PathRouteEvent
    extends PathNavigateEventBaseImpl {
        private final PathProvider provider;
        private final PathWorld world;
        private PathNode nodeAtRail;

        public PathRouteEvent(PathProvider provider, World world) {
            this.provider = provider;
            this.world = provider.getWorld(world);
        }

        @Override
        public void resetToInitialState(RailState railState, RailPath railPath, double currentDistance) {
            super.resetToInitialState(railState, railPath, currentDistance);
            this.nodeAtRail = null;
        }

        public PathProvider provider() {
            return this.provider;
        }

        public PathNode createNode() {
            if (this.nodeAtRail == null) {
                this.nodeAtRail = this.world.getOrCreateAtRail(new BlockLocation(this.railBlock()));
            }
            return this.nodeAtRail;
        }

        public PathNode getLastSetNode() {
            return this.nodeAtRail;
        }

        public PathRailInfo getRailInfo() {
            if (this.isBlocked()) {
                return PathRailInfo.BLOCKED;
            }
            if (this.getLastSetNode() != null) {
                return PathRailInfo.NODE;
            }
            return PathRailInfo.NONE;
        }
    }
}

