/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.NoSuchElementException;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class TrackMovingPoint {
    public Block current;
    public Block next;
    public RailPiece currentRailPiece;
    public RailPiece nextRailPiece;
    public Vector currentDirection;
    public Vector nextDirection;
    public Location currentLocation;
    public Location nextLocation;
    public RailType currentRail;
    public RailType nextRail;
    private boolean hasNext;
    private final TrackWalkingPoint walkingPoint;
    @Deprecated
    public Block currentTrack;
    @Deprecated
    public Block nextTrack;

    public TrackMovingPoint(Location startPos, Vector motionVector) {
        this(new TrackWalkingPoint(startPos, motionVector));
    }

    public TrackMovingPoint(RailState state) {
        this(new TrackWalkingPoint(state));
    }

    @Deprecated
    public TrackMovingPoint(Block startBlock, BlockFace startDirection) {
        this(new TrackWalkingPoint(startBlock, startDirection));
    }

    private TrackMovingPoint(TrackWalkingPoint walkingPoint) {
        this.walkingPoint = walkingPoint;
        if (this.walkingPoint.state.railType() != RailType.NONE) {
            this.currentRailPiece = this.nextRailPiece = this.walkingPoint.state.railPiece();
            this.currentTrack = this.nextTrack = (this.current = (this.next = this.walkingPoint.state.railBlock()));
            this.currentDirection = this.nextDirection = this.walkingPoint.state.enterDirection();
            this.currentLocation = this.nextLocation = this.walkingPoint.state.positionLocation();
            this.currentRail = this.nextRail = this.walkingPoint.state.railType();
            this.hasNext = true;
        } else {
            this.currentRailPiece = this.nextRailPiece = RailPiece.NONE;
            this.next = null;
            this.current = null;
            this.nextTrack = null;
            this.currentTrack = null;
            this.currentDirection = this.nextDirection = new Vector();
            this.nextLocation = null;
            this.currentLocation = null;
            this.currentRail = this.nextRail = RailType.NONE;
            this.hasNext = false;
        }
    }

    public void setLoopFilter(boolean enabled) {
        this.walkingPoint.setLoopFilter(enabled);
    }

    public boolean hasNext() {
        return this.hasNext;
    }

    public void clearNext() {
        this.hasNext = false;
    }

    public void next() {
        this.next(true);
    }

    public void next(boolean allowNext) {
        if (!this.hasNext()) {
            throw new NoSuchElementException("No next element is available");
        }
        this.current = this.next;
        this.currentRailPiece = this.nextRailPiece;
        this.currentTrack = this.nextTrack;
        this.currentDirection = this.nextDirection;
        this.currentLocation = this.nextLocation;
        this.currentRail = this.nextRail;
        this.hasNext = false;
        if (!allowNext) {
            return;
        }
        if (!this.walkingPoint.moveFull()) {
            return;
        }
        this.next = this.walkingPoint.state.railBlock();
        this.nextRailPiece = this.walkingPoint.state.railPiece();
        this.nextTrack = this.next;
        this.nextRail = this.walkingPoint.state.railType();
        this.nextDirection = this.walkingPoint.state.enterDirection();
        this.nextLocation = this.walkingPoint.state.positionLocation();
        this.hasNext = true;
    }

    public RailState getState() {
        return this.walkingPoint.state;
    }
}

