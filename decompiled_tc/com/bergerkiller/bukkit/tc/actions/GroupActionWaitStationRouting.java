/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.Station;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.actions.GroupAction;
import com.bergerkiller.bukkit.tc.actions.WaitAction;
import com.bergerkiller.bukkit.tc.actions.registry.ActionRegistry;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.ActionTracker;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatus;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.pathfinding.PathConnection;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class GroupActionWaitStationRouting
extends GroupAction
implements WaitAction {
    private final Station station;
    private final RailPiece rails;
    private final boolean trainIsCentered;
    private boolean discoveryStarted = false;
    private TrainStatus.WaitingForRouting status = TrainStatus.WaitingForRouting.CALCULATING;

    public GroupActionWaitStationRouting(Station station, RailPiece rails, boolean trainIsCentered) {
        this.station = station;
        this.rails = rails;
        this.trainIsCentered = trainIsCentered;
    }

    @Override
    public boolean isMovementSuppressed() {
        return true;
    }

    public boolean isTrainCentered() {
        return this.trainIsCentered;
    }

    public Station getStation() {
        return this.station;
    }

    @Override
    public boolean update() {
        if (!this.trainIsCentered) {
            this.station.centerTrain();
            this.station.waitTrainKeepLeversDown(0L);
            this.getGroup().getActions().addAction(new GroupActionWaitStationRouting(this.station, this.rails, true)).addTag(this.station.getTag());
            return true;
        }
        String destination = this.getGroup().getProperties().getDestination();
        if (destination.isEmpty()) {
            return this.tryFallback(TrainStatus.WaitingForRouting.NO_DESTINATION);
        }
        if (this.getTrainCarts().getPathProvider().isProcessing()) {
            this.status = TrainStatus.WaitingForRouting.CALCULATING;
            return false;
        }
        PathNode node = this.getTrainCarts().getPathProvider().getWorld(this.rails.world()).getNodeAtRail(this.rails.block());
        if (node == null && !this.discoveryStarted) {
            this.discoveryStarted = true;
            this.getTrainCarts().getPathProvider().discoverFromRail(new BlockLocation(this.rails.block()));
            return false;
        }
        if (node == null) {
            return this.tryFallback(TrainStatus.WaitingForRouting.NO_ROUTE);
        }
        if (node.getNames().contains(destination)) {
            this.status = TrainStatus.WaitingForRouting.AT_DESTINATION;
            return false;
        }
        PathConnection connection = node.findConnection(destination);
        if (connection == null) {
            return this.tryFallback(TrainStatus.WaitingForRouting.NO_ROUTE);
        }
        Vector launchVector = null;
        for (RailJunction junction : this.rails.getJunctions()) {
            if (!junction.name().equals(connection.junctionName)) continue;
            launchVector = junction.position().getMotion();
            break;
        }
        if (launchVector == null) {
            return this.tryFallback(TrainStatus.WaitingForRouting.NO_ROUTE);
        }
        this.prepareLaunchTo(Util.vecToFace(launchVector, false));
        return true;
    }

    @Override
    public List<TrainStatus> getStatusInfo() {
        return Collections.singletonList(this.status);
    }

    private boolean tryFallback(TrainStatus.WaitingForRouting failStatus) {
        if (this.station.getNextDirection() != Direction.NONE) {
            this.prepareLaunchTo(this.station.getNextDirectionFace());
            return true;
        }
        this.status = failStatus;
        return false;
    }

    private void prepareLaunchTo(BlockFace direction) {
        if (!this.trainIsCentered && !this.station.getSignInfo().getMember().isDirectionTo(direction)) {
            this.station.centerTrain();
        }
        this.station.setLevers(false);
        this.station.launchTo(direction);
    }

    public static class Serializer
    implements ActionRegistry.Serializer<GroupActionWaitStationRouting> {
        private final TrainCarts plugin;

        public Serializer(TrainCarts plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean save(GroupActionWaitStationRouting action, OfflineDataBlock data, ActionTracker tracker) throws IOException {
            byte[] signData = this.plugin.getTrackedSignLookup().serializeUniqueKey(action.getStation().getSignInfo().getTrackedSign().getUniqueKey());
            if (signData == null) {
                return false;
            }
            if (action.getStation().getSignInfo().isCartSign()) {
                data.addChild("cart-station-member", stream -> StreamUtil.writeUUID((DataOutputStream)stream, (UUID)((CommonMinecart)action.getStation().getSignInfo().getMember().getEntity()).getUniqueId()));
            }
            data.addChild("wait-station-routing", stream -> {
                Util.writeByteArray(stream, signData);
                stream.writeBoolean(action.isTrainCentered());
            });
            return true;
        }

        @Override
        public GroupActionWaitStationRouting load(OfflineDataBlock data, ActionTracker tracker) throws IOException {
            boolean trainIsCentered;
            byte[] signData;
            try (DataInputStream stream = data.findChildOrThrow("wait-station-routing").readData();){
                signData = Util.readByteArray(stream);
                trainIsCentered = stream.readBoolean();
            }
            Object uniqueKey = this.plugin.getTrackedSignLookup().deserializeUniqueKey(signData);
            if (uniqueKey == null) {
                return null;
            }
            RailLookup.TrackedSign trackedSign = this.plugin.getTrackedSignLookup().getTrackedSign(uniqueKey);
            if (trackedSign == null || trackedSign.isRemoved()) {
                return null;
            }
            RailPiece rail = trackedSign.getRail();
            if (rail.isNone()) {
                return null;
            }
            SignActionEvent event = new SignActionEvent(trackedSign);
            if (event.isCartSign()) {
                UUID memberUUID;
                try (DataInputStream stream = data.findChildOrThrow("cart-station-member").readData();){
                    memberUUID = StreamUtil.readUUID((DataInputStream)stream);
                }
                MinecartMember<?> member = null;
                for (MinecartMember<?> memberOfGroup : tracker.getGroupOwner()) {
                    if (!memberUUID.equals(((CommonMinecart)memberOfGroup.getEntity()).getUniqueId())) continue;
                    member = memberOfGroup;
                    break;
                }
                if (member == null) {
                    return null;
                }
                event.setMember(member);
                event.setAction(SignActionType.MEMBER_ENTER);
            } else {
                event.setGroup(tracker.getGroupOwner());
                event.setAction(SignActionType.GROUP_ENTER);
            }
            Station station = new Station(event);
            return new GroupActionWaitStationRouting(station, rail, trainIsCentered);
        }
    }
}

