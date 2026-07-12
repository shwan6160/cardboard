/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.Location
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.chest;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableMember;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class TrainChestExtendableTrain {
    public final MinecartMember<?> member;
    public final RailState startState;

    public TrainChestExtendableTrain(MinecartMember<?> member, RailState startState) {
        this.member = member;
        this.startState = startState;
    }

    public static TrainChestExtendableTrain findOccupied(List<SpawnableGroup.OccupiedLocation> occupiedLocations, SpawnableMember connectedMember) {
        if (occupiedLocations.isEmpty()) {
            return null;
        }
        SpawnableGroup.OccupiedLocation firstOccupied = occupiedLocations.get(0);
        return TrainChestExtendableTrain.findEndOfTrain(firstOccupied.member, firstOccupied.spawnLocation.forward.clone().multiply(-1.0), connectedMember);
    }

    public static TrainChestExtendableTrain find(RailState startState, double searchDistance, SpawnableMember connectedMember) {
        if (startState.railPiece().isNone()) {
            return null;
        }
        TrackWalkingPoint p = new TrackWalkingPoint(startState);
        do {
            List<MinecartMember<?>> members;
            if ((members = p.state.railPiece().members()).isEmpty()) continue;
            MinecartMember<?> bestMember = members.get(0);
            if (members.size() >= 2) {
                double lowestDistanceSq = Double.MAX_VALUE;
                Location startLoc = startState.positionLocation();
                for (MinecartMember<?> member : members) {
                    double distanceSq = ((CommonMinecart)member.getEntity()).loc.distanceSquared(startLoc);
                    if (!(distanceSq < lowestDistanceSq)) continue;
                    lowestDistanceSq = distanceSq;
                    bestMember = member;
                }
            }
            return TrainChestExtendableTrain.findEndOfTrain(bestMember, p.state.motionVector().clone().multiply(-1.0), connectedMember);
        } while (p.moveStep(searchDistance - p.movedTotal));
        return null;
    }

    private static TrainChestExtendableTrain findEndOfTrain(MinecartMember<?> member, Vector spawnDirection, SpawnableMember connectedMember) {
        RailState memberStartState;
        if (spawnDirection.dot(member.getRailTracker().getMotionVector()) >= 0.0) {
            member = member.getGroup().head();
            memberStartState = member.getRailTracker().getState().clone();
        } else {
            member = member.getGroup().tail();
            memberStartState = member.getRailTracker().getState().cloneAndInvertMotion();
        }
        double extraDistance = 0.5 * (double)((CommonMinecart)member.getEntity()).getWidth() + member.getCartCouplerLength() + connectedMember.getCartCouplerLength();
        TrackWalkingPoint p = new TrackWalkingPoint(memberStartState);
        p.skipFirst();
        if (!p.move(extraDistance)) {
            return null;
        }
        return new TrainChestExtendableTrain(member, p.state);
    }
}

