/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.tc.actions.MemberAction;
import com.bergerkiller.bukkit.tc.actions.WaitAction;
import com.bergerkiller.bukkit.tc.controller.components.ObstacleTracker;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatus;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import java.util.Collections;
import java.util.List;
import org.bukkit.block.BlockFace;

public class MemberActionWaitOccupied
extends MemberAction
implements WaitAction {
    private double maxDistance;
    private final long delay;
    private final double launchDistance;
    private final BlockFace launchDirection;
    private final Double launchVelocity;
    private BlockFace direction = null;
    private double launchforce = Double.NaN;
    private int counter = 20;
    private boolean breakCode = false;
    private RailLookup.TrackedSign toggleOutputOf = null;
    private ObstacleTracker.TrainObstacle trainObstacle = null;

    public MemberActionWaitOccupied(double maxDistance, long delay, double launchDistance, BlockFace launchDirection, Double launchVelocity) {
        this.maxDistance = maxDistance;
        this.delay = delay;
        this.launchDistance = launchDistance;
        this.launchDirection = launchDirection;
        this.launchVelocity = launchVelocity;
    }

    public MemberActionWaitOccupied setToggleOutputOf(RailLookup.TrackedSign sign) {
        this.toggleOutputOf = sign;
        if (!sign.isRemoved()) {
            sign.setOutput(true);
        }
        return this;
    }

    public RailLookup.TrackedSign getToggleOutputOf() {
        return this.toggleOutputOf;
    }

    public void adjustDistance(double maxDistance) {
        if (maxDistance > this.maxDistance) {
            this.maxDistance = maxDistance;
            this.breakCode = false;
        }
    }

    @Override
    public void bind() {
        if (this.direction == null) {
            this.direction = this.getMember().getDirection();
        }
        if (Double.isNaN(this.launchforce)) {
            this.launchforce = this.getGroup().getAverageForce();
        }
    }

    public double getPostWaitLaunchForce() {
        return this.launchforce;
    }

    @Override
    public void start() {
        if (this.handleOccupied()) {
            this.getGroup().stop(true);
            if (this.toggleOutputOf != null && !this.toggleOutputOf.isRemoved()) {
                this.toggleOutputOf.setOutput(true);
            }
        } else {
            this.breakCode = true;
        }
    }

    public boolean handleOccupied() {
        for (ObstacleTracker.Obstacle obstacle : this.getGroup().findObstaclesAhead(this.maxDistance, true, false)) {
            if (!(obstacle instanceof ObstacleTracker.TrainObstacle)) continue;
            this.trainObstacle = (ObstacleTracker.TrainObstacle)obstacle;
            return true;
        }
        this.trainObstacle = null;
        return false;
    }

    @Override
    public List<TrainStatus> getStatusInfo() {
        if (this.trainObstacle == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new TrainStatus.WaitingForTrain(this.trainObstacle.member, this.trainObstacle.fullDistance));
    }

    @Override
    public boolean update() {
        if (this.breakCode) {
            return true;
        }
        if (this.counter++ >= 20) {
            if (!this.handleOccupied()) {
                if (this.delay > 0L) {
                    this.getGroup().getActions().addActionWait(this.delay);
                }
                if (this.toggleOutputOf != null && !this.toggleOutputOf.isRemoved()) {
                    if (this.delay > 0L) {
                        this.toggleOutputOf.setOutput(false);
                        this.getGroup().getActions().addActionSetSignOutput(this.toggleOutputOf, false);
                    } else {
                        this.toggleOutputOf.setOutput(false);
                    }
                }
                if (this.launchVelocity != null && this.launchDirection != null) {
                    this.getMember().getActions().addActionLaunch(this.launchDirection, this.launchDistance, (double)this.launchVelocity);
                } else {
                    this.getMember().getActions().addActionLaunch(this.direction, this.launchDistance, this.launchforce);
                }
                return true;
            }
            this.counter = 0;
        }
        return false;
    }

    @Override
    public void cancel() {
        if (this.toggleOutputOf != null && !this.toggleOutputOf.isRemoved()) {
            this.toggleOutputOf.setOutput(false);
        }
    }

    @Override
    public boolean isMovementSuppressed() {
        return true;
    }
}

