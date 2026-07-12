/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.Location
 *  org.bukkit.event.Event
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.components.RailTracker;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatus;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatusProvider;
import com.bergerkiller.bukkit.tc.events.MutexZoneConflictEvent;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZone;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCacheWorld;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlot;
import com.bergerkiller.bukkit.tc.utils.ForwardChunkArea;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

public class ObstacleTracker
implements TrainStatusProvider {
    private final MinecartGroup group;
    private double waitDistanceLastSpeedLimit = Double.MAX_VALUE;
    private double waitDistanceLastTrainSpeed = Double.MAX_VALUE;
    private int waitRemainingTicks = Integer.MAX_VALUE;
    private ObstacleSpeedLimit lastObstacleSpeedLimit = ObstacleSpeedLimit.NONE;
    private List<MutexZone> enteredMutexZones = Collections.emptyList();
    private int tickCounter = 0;

    public ObstacleTracker(MinecartGroup group) {
        this.group = group;
    }

    public double getSpeedLimit() {
        return this.waitDistanceLastSpeedLimit;
    }

    public int getTickCounter() {
        return this.tickCounter;
    }

    public void update(double trainSpeed) {
        double deceleration;
        double acceleration;
        double speedDiff;
        TrainProperties properties = this.group.getProperties();
        ++this.tickCounter;
        for (MutexZone newMutexZone : this.group.head().railLookup().getMutexZones().getNewZones()) {
            this.hardEnterNewMutexZoneIfInside(newMutexZone);
        }
        double searchAheadDistance = Math.max(1.0, properties.getSpeedLimit() + 0.5);
        if (properties.getWaitDeceleration() > 0.0) {
            double speedLimitLastTick = this.waitDistanceLastSpeedLimit == Double.MAX_VALUE ? properties.getSpeedLimit() : this.waitDistanceLastSpeedLimit;
            double maxProjectedSpeed = Math.min(trainSpeed, speedLimitLastTick);
            searchAheadDistance += 0.5 * (maxProjectedSpeed * maxProjectedSpeed) / properties.getWaitDeceleration();
        }
        double speedLimitLastTick = this.waitDistanceLastSpeedLimit == Double.MAX_VALUE ? properties.getSpeedLimit() : this.waitDistanceLastSpeedLimit;
        double trainSpeedLastTick = this.waitDistanceLastTrainSpeed == Double.MAX_VALUE ? trainSpeed : this.waitDistanceLastTrainSpeed;
        double baseSpeedLimitThisTick = Math.min(speedLimitLastTick, trainSpeedLastTick);
        this.waitDistanceLastTrainSpeed = trainSpeed;
        boolean checkTrains = properties.getWaitDistance() > 0.0;
        ObstacleSpeedLimit newDesiredSpeed = this.getDesiredSpeedLimit(searchAheadDistance, properties.getWaitDeceleration(), checkTrains, true, properties.getWaitDistance());
        if (this.waitDistanceLastSpeedLimit <= 1.0E-6 && newDesiredSpeed.speed <= 1.0E-6) {
            this.waitRemainingTicks = 0;
            this.waitDistanceLastSpeedLimit = newDesiredSpeed.speed;
            return;
        }
        if (this.waitRemainingTicks != Integer.MAX_VALUE) {
            double delay = properties.getWaitDelay();
            if (delay <= 0.0) {
                this.waitRemainingTicks = Integer.MAX_VALUE;
            } else {
                if (++this.waitRemainingTicks >= MathUtil.ceil((double)(delay * 20.0))) {
                    this.waitRemainingTicks = Integer.MAX_VALUE;
                }
                this.waitDistanceLastSpeedLimit = 0.0;
                return;
            }
        }
        if (newDesiredSpeed.speed >= properties.getSpeedLimit()) {
            if (this.waitDistanceLastSpeedLimit >= newDesiredSpeed.speed) {
                this.waitDistanceLastSpeedLimit = Double.MAX_VALUE;
            }
            if (this.waitDistanceLastSpeedLimit != Double.MAX_VALUE) {
                double acceleration2 = properties.getWaitAcceleration();
                if (acceleration2 > 0.0) {
                    this.waitDistanceLastSpeedLimit += acceleration2;
                    if (this.waitDistanceLastSpeedLimit >= properties.getSpeedLimit()) {
                        this.waitDistanceLastSpeedLimit = Double.MAX_VALUE;
                    }
                } else {
                    this.waitDistanceLastSpeedLimit = Double.MAX_VALUE;
                }
            }
            return;
        }
        if (this.waitDistanceLastSpeedLimit == Double.MAX_VALUE) {
            this.waitDistanceLastSpeedLimit = properties.getSpeedLimit();
        }
        this.waitDistanceLastSpeedLimit = (speedDiff = newDesiredSpeed.speed - this.waitDistanceLastSpeedLimit) >= 0.0 ? ((acceleration = properties.getWaitAcceleration()) <= 0.0 || acceleration >= speedDiff ? newDesiredSpeed.speed : (this.waitDistanceLastSpeedLimit += acceleration)) : ((deceleration = properties.getWaitDeceleration()) <= 0.0 || deceleration >= -speedDiff || newDesiredSpeed.instant ? newDesiredSpeed.speed : (newDesiredSpeed.speed > baseSpeedLimitThisTick ? newDesiredSpeed.speed : baseSpeedLimitThisTick - deceleration));
    }

    private void hardEnterNewMutexZoneIfInside(MutexZone newMutexZone) {
        boolean isNearby = false;
        for (MinecartMember<?> member : this.group) {
            int radius;
            IntVector3 blockPos = member.getRailTracker().getState().positionOfflineBlock().getPosition();
            if (!newMutexZone.isNearby(blockPos, radius = (int)(3.0 * (double)((CommonMinecart)member.getEntity()).getWidth()))) continue;
            isNearby = true;
            break;
        }
        if (!isNearby) {
            return;
        }
        List<RailTracker.TrackedRail> rails = this.group.getRailTracker().getRailInformation();
        if (rails.isEmpty()) {
            return;
        }
        MutexZone[] zones = new MutexZone[]{newMutexZone};
        RailPath.Position firstPosition = rails.get((int)0).state.position();
        MutexZoneCacheWorld.MovingPoint movingPoint = new MutexZoneCacheWorld.MovingPoint((cx, cz) -> zones, MathUtil.toChunk((double)firstPosition.posX), MathUtil.toChunk((double)firstPosition.posZ));
        boolean isInsideZone = false;
        for (RailTracker.TrackedRail rail : rails) {
            RailPath path;
            if (rail.state.railPiece().isNone() || (path = rail.getPath()).isEmpty()) continue;
            RailPath.Position start = path.getStartPosition();
            RailPath.Position end = path.getEndPosition();
            start.makeAbsolute(rail.state.railBlock());
            end.makeAbsolute(rail.state.railBlock());
            MutexZoneCacheWorld.MutexZoneResult result = movingPoint.get(start, end);
            if (result == null || result.zone != newMutexZone || !(result.distance <= 0.0)) continue;
            isInsideZone = true;
            break;
        }
        if (!isInsideZone) {
            return;
        }
        newMutexZone.onUsed(this.group);
        MutexZoneSlot.LoadedEnteredGroup entered = newMutexZone.slot.track(this.group, 0.0);
        for (RailTracker.TrackedRail rail : rails) {
            if (rail.state.railPiece().isNone()) continue;
            entered.enter(newMutexZone.type, rail.state.railPiece().blockPosition(), true);
        }
    }

    @Override
    public List<TrainStatus> getStatusInfo() {
        if (!this.lastObstacleSpeedLimit.hasLimit() && this.enteredMutexZones.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<TrainStatus> statuses = new ArrayList<TrainStatus>();
        if (!this.enteredMutexZones.isEmpty()) {
            IdentityHashMap<MutexZoneSlot, List> zones = new IdentityHashMap<MutexZoneSlot, List>();
            for (MutexZone mutexZone : this.enteredMutexZones) {
                zones.compute(mutexZone.slot, (s, curr_zones) -> {
                    ArrayList<MutexZone> newZones = new ArrayList<MutexZone>();
                    if (curr_zones != null) {
                        newZones.addAll((Collection<MutexZone>)curr_zones);
                    }
                    newZones.add(zone);
                    return newZones;
                });
            }
            for (Map.Entry entry : zones.entrySet()) {
                MutexZoneSlot.LoadedEnteredGroup entered = ((MutexZoneSlot)entry.getKey()).findEntered(this.group);
                statuses.add(new TrainStatus.EnteredMutexZone((MutexZoneSlot)entry.getKey(), (List)entry.getValue(), entered));
            }
        }
        if (this.lastObstacleSpeedLimit.hasLimit()) {
            statuses.add(this.lastObstacleSpeedLimit.getStatus());
        } else if (this.waitRemainingTicks != Integer.MAX_VALUE) {
            double remaining = this.group.getProperties().getWaitDelay() - (double)this.waitRemainingTicks * 0.05;
            statuses.add(new TrainStatus.WaitingForDelay(remaining));
        }
        return statuses;
    }

    private ObstacleSpeedLimit getDesiredSpeedLimit(double searchAheadDistance, double deceleration, boolean checkTrains, boolean checkRailObstacles, double trainDistance) {
        ObstacleFinder finder = new ObstacleFinder(Math.min(2000.0, searchAheadDistance), checkTrains, checkRailObstacles, trainDistance);
        List<Obstacle> obstacles = finder.search();
        this.enteredMutexZones = finder.enteredMutexZones;
        this.lastObstacleSpeedLimit = ObstacleTracker.minimumSpeedLimit(obstacles, deceleration);
        return this.lastObstacleSpeedLimit;
    }

    public List<Obstacle> findObstaclesAhead(double distance, boolean checkTrains, boolean checkRailObstacles, double trainDistance) {
        return new ObstacleFinder(distance, checkTrains, checkRailObstacles, trainDistance).search();
    }

    public static ObstacleSpeedLimit minimumSpeedLimit(Iterable<Obstacle> obstacles, double deceleration) {
        ObstacleSpeedLimit min = ObstacleSpeedLimit.NONE;
        for (Obstacle obstacle : obstacles) {
            ObstacleSpeedLimit limit = obstacle.findSpeedLimit(deceleration);
            if (!(limit.speed < min.speed)) continue;
            min = limit;
        }
        return min;
    }

    public static ObstacleSpeedLimit minimumSpeedLimit(Iterable<ObstacleSpeedLimit> limits) {
        ObstacleSpeedLimit min = ObstacleSpeedLimit.NONE;
        for (ObstacleSpeedLimit limit : limits) {
            if (!(limit.speed < min.speed)) continue;
            min = limit;
        }
        return min;
    }

    public static class ObstacleSpeedLimit {
        public static final ObstacleSpeedLimit NONE = new ObstacleSpeedLimit(null, Double.MAX_VALUE, false);
        public final Obstacle obstacle;
        public final double speed;
        public final boolean instant;

        public ObstacleSpeedLimit(Obstacle obstacle, double speed, boolean instant) {
            this.obstacle = obstacle;
            this.speed = speed;
            this.instant = instant;
        }

        public TrainStatus getStatus() {
            return this.obstacle.createStatus(this);
        }

        public boolean hasLimit() {
            return this.speed != Double.MAX_VALUE;
        }

        public boolean isStopped() {
            return this.instant && this.speed <= 0.0;
        }

        public String toString() {
            if (this.obstacle == null) {
                return "{NONE}";
            }
            return "{speed=" + this.speed + ", instant=" + this.instant + ", obstacle=" + this.obstacle.getClass().getSimpleName() + "}";
        }
    }

    private class ObstacleFinder {
        final double distance;
        final boolean checkTrains;
        final boolean checkRailObstacles;
        final double trainDistance;
        final double selfCartOffset;
        double waitDistance;
        final double mutexHardDistance;
        final double mutexSoftDistance;
        final double checkDistance;
        double closestHardRailObstacle = Double.MAX_VALUE;
        double lastRailSpeedLimit = Double.MAX_VALUE;
        MutexZone currentMutex = null;
        MutexZoneSlot.LoadedEnteredGroup currentMutexGroup = null;
        boolean currentMutexHard = false;
        double currentMutexSpacing = 0.0;
        public List<MutexZone> enteredMutexZones = Collections.emptyList();
        List<Obstacle> obstacles = new ArrayList<Obstacle>();

        public ObstacleFinder(double distance, boolean checkTrains, boolean checkRailObstacles, double trainDistance) {
            this.distance = distance;
            this.checkTrains = checkTrains;
            this.checkRailObstacles = checkRailObstacles;
            this.trainDistance = trainDistance;
            this.selfCartOffset = 0.5 * (double)((CommonMinecart)ObstacleTracker.this.group.head().getEntity()).getWidth();
            this.waitDistance = distance + trainDistance;
            this.mutexHardDistance = 0.0;
            this.mutexSoftDistance = 2.0 + distance;
            this.checkDistance = this.selfCartOffset + Math.max(this.mutexSoftDistance, this.waitDistance) + 1.0;
        }

        public List<Obstacle> search() {
            if (ObstacleTracker.this.group.isEmpty()) {
                ObstacleTracker.this.group.getChunkArea().getForwardChunkArea().reset();
                return Collections.emptyList();
            }
            ForwardChunkArea forwardChunks = null;
            if (ObstacleTracker.this.group.getProperties().isKeepingChunksLoaded()) {
                forwardChunks = ObstacleTracker.this.group.getChunkArea().getForwardChunkArea();
                forwardChunks.begin(ObstacleTracker.this.group.getWorld());
            } else {
                ObstacleTracker.this.group.getChunkArea().getForwardChunkArea().reset();
            }
            MutexZoneCacheWorld.MovingPoint mutexZones = ObstacleTracker.this.group.head().railLookup().getMutexZones().track(((CommonMinecart)((ObstacleTracker)ObstacleTracker.this).group.head().getEntity()).loc.block());
            if (this.distance <= 0.0 && this.trainDistance <= 0.0 && (!this.checkRailObstacles || !mutexZones.isNear())) {
                return Collections.emptyList();
            }
            RailState startState = ObstacleTracker.this.group.head().discoverRail();
            startState.setMember(null);
            TrackWalkingPoint iter = new TrackWalkingPoint(startState);
            if (ObstacleTracker.this.group.getProperties().isWaitPredicted()) {
                iter.setFollowPredictedPath(ObstacleTracker.this.group.head());
            }
            while ((iter.movedTotal <= this.checkDistance + this.currentMutexSpacing || iter.getPredictedRemainingBlockDistance() > 0.0) && iter.moveFull()) {
                double distanceFromFront = iter.movedTotal - this.selfCartOffset;
                if (forwardChunks != null) {
                    forwardChunks.addBlock(iter.state.railBlock());
                }
                if (this.checkRailObstacles) {
                    double railSpeedLimit;
                    boolean checkForNewHardObstacles;
                    MutexZone prevMutex = this.currentMutex;
                    if (this.currentMutex != null && !this.currentMutex.containsBlock(iter.state.positionOfflineBlock().getPosition())) {
                        this.currentMutex = null;
                        this.currentMutexSpacing = 0.0;
                    }
                    boolean bl = checkForNewHardObstacles = distanceFromFront < this.closestHardRailObstacle;
                    if (checkForNewHardObstacles && (railSpeedLimit = iter.getPredictedSpeedLimit()) < this.lastRailSpeedLimit) {
                        this.lastRailSpeedLimit = railSpeedLimit;
                        this.obstacles.add(new RailObstacle(distanceFromFront, railSpeedLimit, iter.state.railPiece()));
                        if (railSpeedLimit <= 0.0) {
                            this.closestHardRailObstacle = distanceFromFront;
                            checkForNewHardObstacles = false;
                        }
                    }
                    if (this.currentMutex == null) {
                        MutexZoneCacheWorld.MutexZoneResult newMutexResult;
                        boolean checkForNewMutexes;
                        boolean bl2 = checkForNewMutexes = checkForNewHardObstacles && distanceFromFront < this.mutexSoftDistance;
                        if ((prevMutex != null || checkForNewMutexes) && (newMutexResult = mutexZones.get(iter)) != null) {
                            boolean accept;
                            double distanceToMutex = distanceFromFront + newMutexResult.distance;
                            if (prevMutex != null && prevMutex.slot == newMutexResult.zone.slot) {
                                accept = true;
                            } else {
                                boolean bl3 = accept = checkForNewMutexes && distanceToMutex < this.mutexSoftDistance;
                            }
                            if (accept) {
                                newMutexResult.zone.onUsed(ObstacleTracker.this.group);
                                this.currentMutex = newMutexResult.zone;
                                this.currentMutexSpacing = this.currentMutex.getSpacing(ObstacleTracker.this.group);
                                this.currentMutexGroup = newMutexResult.zone.slot.track(ObstacleTracker.this.group, distanceToMutex);
                                boolean bl4 = this.currentMutexHard = this.currentMutexGroup.distanceToMutex <= this.mutexHardDistance;
                            }
                        }
                    }
                    if (this.currentMutex != null) {
                        this.updateCurrentMutex(iter);
                    }
                }
                if (!this.checkTrains) continue;
                Location state_position = null;
                Location member_position = null;
                for (MinecartMember<?> member : iter.state.railPiece().members()) {
                    Vector delta;
                    if (member.isUnloaded() || ((CommonMinecart)member.getEntity()).isRemoved() || member.getGroup() == ObstacleTracker.this.group) continue;
                    if (state_position == null) {
                        state_position = iter.state.positionLocation();
                    }
                    if (member_position == null) {
                        member_position = ((CommonMinecart)member.getEntity()).getLocation();
                    } else {
                        ((CommonMinecart)member.getEntity()).getLocation(member_position);
                    }
                    if (iter.movedTotal == 0.0 && (delta = new Vector(member_position.getX() - state_position.getX(), member_position.getY() - state_position.getY(), member_position.getZ() - state_position.getZ())).dot(iter.state.motionVector()) < 0.0) continue;
                    double distanceToMember = member_position.distance(state_position) - (double)((CommonMinecart)member.getEntity()).getWidth() * 0.5;
                    Vector member_velocity = ((CommonMinecart)member.getEntity()).getVelocity();
                    double speedAhead = Math.min(member_velocity.length(), ((CommonMinecart)member.getEntity()).getMaxSpeed());
                    if (speedAhead < 0.0) {
                        speedAhead = 0.0;
                    }
                    if (speedAhead > 1.0E-6 && iter.state.position().motDot(member_velocity) < 0.0) {
                        this.obstacles.add(new TrainObstacle(distanceFromFront + distanceToMember, this.trainDistance, 0.0, member));
                        continue;
                    }
                    this.obstacles.add(new TrainObstacle(distanceFromFront + distanceToMember, this.trainDistance, speedAhead, member));
                }
            }
            if (this.currentMutex != null) {
                double enabledLoopFilterLimit = iter.movedTotal + 64.0;
                while (!this.currentMutexGroup.isOccupiedFully() && iter.moveFull()) {
                    IntVector3 currBlockPos;
                    if (iter.movedTotal >= enabledLoopFilterLimit) {
                        enabledLoopFilterLimit = Double.MAX_VALUE;
                        iter.setLoopFilter(true);
                    }
                    if (forwardChunks != null) {
                        forwardChunks.addBlock(iter.state.railBlock());
                    }
                    if (!this.currentMutex.containsBlock(currBlockPos = iter.state.positionOfflineBlock().getPosition())) {
                        MutexZoneCacheWorld.MutexZoneResult otherMutex = mutexZones.get(iter);
                        if (otherMutex == null || otherMutex.zone.slot != this.currentMutex.slot) break;
                        this.currentMutex = otherMutex.zone;
                        this.currentMutexSpacing = this.currentMutex.getSpacing(ObstacleTracker.this.group);
                        otherMutex.zone.onUsed(ObstacleTracker.this.group);
                    }
                    if (this.updateCurrentMutex(iter)) continue;
                    break;
                }
            }
            return this.obstacles;
        }

        private boolean updateCurrentMutex(TrackWalkingPoint iter) {
            MutexZoneSlot.EnterResult result = this.currentMutexGroup.enter(this.currentMutex.type, iter.state.railPiece().blockPosition(), this.currentMutexHard);
            if (!this.enteredMutexZones.contains(this.currentMutex)) {
                if (this.enteredMutexZones.isEmpty()) {
                    this.enteredMutexZones = new ArrayList<MutexZone>();
                }
                this.enteredMutexZones.add(this.currentMutex);
            }
            double currentMutexDistance = this.currentMutexGroup.distanceToMutex - this.currentMutexSpacing;
            if (result.isOccupied()) {
                if (currentMutexDistance < this.closestHardRailObstacle) {
                    this.closestHardRailObstacle = currentMutexDistance;
                    this.obstacles.add(new MutexZoneObstacle(currentMutexDistance, 0.0, this.currentMutex));
                }
                if (result == MutexZoneSlot.EnterResult.OCCUPIED_DISCOVER) {
                    return true;
                }
                this.currentMutex = null;
                this.currentMutexGroup = null;
                this.currentMutexSpacing = 0.0;
                return false;
            }
            if (result.isConflict()) {
                if (result == MutexZoneSlot.EnterResult.CONFLICT) {
                    MutexZoneConflictEvent conflict = this.currentMutexGroup.getConflict();
                    if (TCConfig.logMutexConflicts) {
                        Logger l = ObstacleTracker.this.group.getTrainCarts().getLogger();
                        l.log(Level.WARNING, "[Mutex] Train '" + ObstacleTracker.this.group.getProperties().getTrainName() + "' is in violation inside mutex '" + conflict.getMutexZoneSlot().getNameWithoutWorldUUID() + "' crossing train '" + conflict.getGroupCrossed().getProperties().getTrainName() + "' at rail " + conflict.getRailPosition());
                    }
                    CommonUtil.callEvent((Event)conflict);
                }
                return true;
            }
            return result == MutexZoneSlot.EnterResult.SUCCESS || result != MutexZoneSlot.EnterResult.IGNORED;
        }
    }

    public static abstract class Obstacle {
        public final double distance;
        public final double speed;

        public Obstacle(double distance, double speed) {
            this.distance = distance;
            this.speed = speed;
        }

        public boolean isObstacleMoving() {
            return false;
        }

        protected abstract TrainStatus createStatus(ObstacleSpeedLimit var1);

        public ObstacleSpeedLimit findSpeedLimit(double deceleration) {
            if (this.distance > -1.0E-6 && this.distance < 1.0E-6) {
                return new ObstacleSpeedLimit(this, Math.max(0.0, this.speed), true);
            }
            if (this.distance <= 0.0) {
                if (this.isObstacleMoving()) {
                    return new ObstacleSpeedLimit(this, Math.max(0.0, this.speed + this.distance), true);
                }
                return new ObstacleSpeedLimit(this, Math.max(0.0, this.speed), true);
            }
            if (deceleration <= 0.0 || deceleration == Double.MAX_VALUE) {
                return new ObstacleSpeedLimit(this, Math.max(0.0, this.speed + this.distance), true);
            }
            double startSpeed = Math.sqrt(2.0 * deceleration * this.distance);
            int numSlowdownTicks = MathUtil.ceil((double)(startSpeed / deceleration));
            while ((double)((numSlowdownTicks + 1) * numSlowdownTicks) * 0.5 * deceleration > this.distance) {
                --numSlowdownTicks;
            }
            startSpeed = numSlowdownTicks == 0 ? this.distance + this.speed : (double)numSlowdownTicks * deceleration + this.speed;
            return new ObstacleSpeedLimit(this, Math.max(0.0, startSpeed), false);
        }
    }

    public static class RailObstacle
    extends Obstacle {
        public final RailPiece rail;

        public RailObstacle(double distance, double speed, RailPiece rail) {
            super(distance, speed);
            this.rail = rail;
        }

        @Override
        protected TrainStatus createStatus(ObstacleSpeedLimit speedLimit) {
            if (speedLimit.isStopped()) {
                return new TrainStatus.WaitingAtRailBlock(this.rail);
            }
            return new TrainStatus.ApproachingRailSpeedTrap(this.rail, this.distance, this.speed);
        }
    }

    public static class MutexZoneObstacle
    extends Obstacle {
        public final MutexZone zone;

        public MutexZoneObstacle(double distance, double speed, MutexZone zone) {
            super(distance, speed);
            this.zone = zone;
        }

        @Override
        protected TrainStatus createStatus(ObstacleSpeedLimit speedLimit) {
            if (speedLimit.isStopped()) {
                return new TrainStatus.WaitingForMutexZone(this.zone);
            }
            return new TrainStatus.ApproachingMutexZone(this.zone, this.distance, this.speed);
        }
    }

    public static class TrainObstacle
    extends Obstacle {
        public final double fullDistance;
        public final MinecartMember<?> member;

        public TrainObstacle(double fullDistance, double spaceDistance, double speed, MinecartMember<?> member) {
            super(fullDistance - spaceDistance, speed);
            this.fullDistance = fullDistance;
            this.member = member;
        }

        @Override
        public boolean isObstacleMoving() {
            return true;
        }

        @Override
        protected TrainStatus createStatus(ObstacleSpeedLimit speedLimit) {
            if (speedLimit.isStopped()) {
                return new TrainStatus.WaitingForTrain(this.member, this.fullDistance);
            }
            return new TrainStatus.FollowingTrain(this.member, this.fullDistance, speedLimit.speed);
        }
    }
}

