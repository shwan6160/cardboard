/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitForever;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatus;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathConnection;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import java.util.Collections;
import java.util.List;

public class GroupActionWaitPathFinding
extends GroupActionWaitForever {
    private final SignActionEvent info;
    private final PathNode from;
    private final String destination;
    private final RailJunction fromJunction;
    private int failCounter = 0;

    public GroupActionWaitPathFinding(SignActionEvent info, PathNode from, String destination) {
        this.info = info;
        this.from = from;
        this.destination = destination;
        this.fromJunction = info.getEnterJunction();
    }

    @Override
    public boolean update() {
        if (this.getTrainCarts().getPathProvider().isProcessing()) {
            if (this.failCounter++ == 20) {
                Localization.PATHING_BUSY.broadcast(this.getGroup(), new String[0]);
            }
            return super.update();
        }
        PathConnection conn = this.from.findConnection(this.destination);
        if (conn != null) {
            this.info.setRailsFromTo(this.fromJunction, conn.junctionName);
        } else {
            Localization.PATHING_FAILED.broadcast(this.getGroup(), this.destination);
        }
        return true;
    }

    @Override
    public List<TrainStatus> getStatusInfo() {
        return Collections.singletonList(TrainStatus.WaitingForRouting.CALCULATING);
    }
}

