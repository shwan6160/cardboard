/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.pathfinding;

import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.pathfinding.PathNavigateEvent;

public class PathNavigateEventBaseImpl
implements PathNavigateEvent {
    private RailState railState;
    private RailPath railPath;
    private RailPath.Position nextPosition;
    private double currentDistance;
    private boolean abortNavigation;

    @Override
    public void resetToInitialState(RailState railState, RailPath railPath, double currentDistance) {
        if (railState == null) {
            throw new IllegalArgumentException("Rail state cannot be null");
        }
        if (railPath == null) {
            throw new IllegalArgumentException("Rail path cannot be null");
        }
        this.railState = railState;
        this.railPath = railPath;
        this.nextPosition = null;
        this.currentDistance = currentDistance;
        this.abortNavigation = false;
    }

    @Override
    public double currentDistance() {
        return this.currentDistance;
    }

    @Override
    public boolean isNavigationAborted() {
        return this.abortNavigation;
    }

    @Override
    public void abortNavigation() {
        this.abortNavigation = true;
    }

    @Override
    public RailState railState() {
        return this.railState;
    }

    @Override
    public RailPath railPath() {
        return this.railPath;
    }

    @Override
    public RailPath.Position getSwitchedPosition() {
        return this.nextPosition;
    }

    @Override
    public boolean hasSwitchedPosition() {
        return this.nextPosition != null;
    }

    @Override
    public void setSwitchedPosition(RailPath.Position nextPosition) {
        this.nextPosition = nextPosition;
    }
}

