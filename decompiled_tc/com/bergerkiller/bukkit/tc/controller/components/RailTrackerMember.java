/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  org.bukkit.block.Block
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.components.RailTracker;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicAir;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicGround;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackIterator;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class RailTrackerMember
extends RailTracker {
    private final MinecartMember<?> owner;
    private RailTracker.TrackedRail lastRail;
    private RailTracker.TrackedRail rail;
    private RailLogic lastRailLogic;
    private RailLogic railLogic;
    private boolean railLogicSnapshotted = false;

    public RailTrackerMember(MinecartMember<?> owner) {
        this.owner = owner;
        this.lastRail = this.rail = new RailTracker.TrackedRail(owner);
        this.lastRailLogic = this.railLogic = RailLogicGround.INSTANCE;
    }

    public void onAttached() {
        this.lastRail = this.rail = RailTracker.TrackedRail.create(this.owner, false);
        this.railLogic = null;
        this.lastRailLogic = null;
        this.railLogicSnapshotted = false;
    }

    @Override
    public boolean isOnRails(Block railsBlock) {
        return this.owner.getGroup().getRailTracker().getMemberFromRails(railsBlock) == this.owner;
    }

    public RailTracker.TrackedRail getRail() {
        return this.rail;
    }

    public RailTracker.TrackedRailWalker getTrackedRailWalker() {
        MinecartMember<?> owner = this.owner;
        return new RailTracker.TrackedRailWalker(owner.getGroup().getRailTracker().getRailInformation(), this.getRail());
    }

    @Deprecated
    public TrackIterator getTrackIterator() {
        return new TrackIterator(this.rail.state.railBlock(), this.owner.getDirectionTo());
    }

    public boolean isTrainSplit() {
        return this.rail.disconnected;
    }

    public Vector getMotionVector() {
        return this.rail.state.motionVector();
    }

    public RailType getRailType() {
        return this.rail.state.railType();
    }

    public RailType getLastRailType() {
        return this.lastRail.state.railType();
    }

    public Block getBlock() {
        return this.rail.state.railBlock();
    }

    public Block getMinecartPos() {
        return this.rail.minecartBlock;
    }

    public RailState getState() {
        return this.rail.state;
    }

    public IntVector3 getBlockPos() {
        return this.rail.state.railPiece().blockPosition();
    }

    public Block getLastBlock() {
        return this.lastRail.state.railBlock();
    }

    public RailLogic getRailLogic() {
        if (this.railLogicSnapshotted && this.railLogic != null) {
            return this.railLogic;
        }
        try {
            return this.rail.state.loadRailLogic();
        }
        catch (Throwable t) {
            RailType.handleCriticalError(this.rail.state.railType(), t);
            RailState state = this.rail.state.clone();
            state.setRailPiece(RailPiece.create(RailType.NONE, state.positionBlock()));
            state.initEnterDirection();
            this.rail = new RailTracker.TrackedRail(this.rail.member, state, this.rail.disconnected);
            return RailLogicAir.INSTANCE;
        }
    }

    public RailLogic getLastLogic() {
        if (this.lastRailLogic == null) {
            this.lastRailLogic = this.getRailLogic();
        }
        return this.lastRailLogic;
    }

    public boolean hasBlockChanged() {
        Block a = this.lastRail.state.railBlock();
        Block b = this.rail.state.railBlock();
        return a.getX() != b.getX() || a.getY() != b.getY() || a.getZ() != b.getZ();
    }

    public void setLiveRailLogic() {
        this.railLogicSnapshotted = false;
    }

    public void snapshotRailLogic() {
        this.railLogicSnapshotted = false;
        this.railLogic = this.getRailLogic();
        this.railLogicSnapshotted = true;
    }

    public void updateLast() {
        this.lastRail = this.rail;
        this.lastRailLogic = this.getRailLogic();
        this.owner.vertToSlope = false;
    }

    public void refresh(RailTracker.TrackedRail newInfo) {
        this.rail = newInfo;
        this.railLogic = null;
        this.railLogicSnapshotted = false;
    }
}

