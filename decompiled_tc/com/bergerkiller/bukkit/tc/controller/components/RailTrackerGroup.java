/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.components.RailTracker;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class RailTrackerGroup
extends RailTracker {
    private final MinecartGroup owner;
    private final ArrayList<RailTracker.TrackedRail> prevRails = new ArrayList();
    private final ArrayList<RailTracker.TrackedRail> rails = new ArrayList();

    public RailTrackerGroup(MinecartGroup owner) {
        this.owner = owner;
    }

    public void unload() {
        this.rails.forEach(RailTracker.TrackedRail::handleMemberRemove);
        this.rails.clear();
        this.prevRails.clear();
    }

    public void removeMemberRails(MinecartMember<?> member) {
        RailTrackerGroup.removeMemberRails(this.prevRails, member);
        RailTrackerGroup.removeMemberRails(this.rails, member);
    }

    private static void removeMemberRails(List<RailTracker.TrackedRail> rails, MinecartMember<?> member) {
        Iterator<RailTracker.TrackedRail> iter = rails.iterator();
        while (iter.hasNext()) {
            RailTracker.TrackedRail rail = iter.next();
            if (rail.member != member) continue;
            if (rail.memberAddedToRailPiece) {
                rail.handleMemberRemove();
            }
            iter.remove();
        }
    }

    public void reverseRailData() {
        Collections.reverse(this.rails);
        for (RailTracker.TrackedRail rail : this.rails) {
            rail.state.position().invertMotion();
            rail.state.initEnterDirection();
        }
    }

    public List<RailTracker.TrackedRail> getRailInformation() {
        return this.rails;
    }

    @Override
    public boolean isOnRails(Block railsBlock) {
        return this.getMemberFromRails(railsBlock) != null;
    }

    public MinecartMember<?> getMemberFromRails(Block railsBlock) {
        if (railsBlock.getWorld() != this.owner.getWorld()) {
            return null;
        }
        return this.getMemberFromRails(new IntVector3(railsBlock));
    }

    public MinecartMember<?> getMemberFromRails(IntVector3 railsBlockPosition) {
        for (RailTracker.TrackedRail info : this.rails) {
            if (!railsBlockPosition.equals((Object)info.state.railPiece().blockPosition())) continue;
            return info.member;
        }
        return null;
    }

    public void refresh() {
        this.prevRails.clear();
        this.prevRails.addAll(this.rails);
        this.rails.clear();
        this.refreshFrom(this.owner.size() - 1, false);
        if (TCConfig.railTrackerDebugEnabled) {
            double theta;
            Location loc;
            int i;
            ArrayList<RailTracker.TrackedRail> behindRails = new ArrayList<RailTracker.TrackedRail>();
            ArrayList<RailTracker.TrackedRail> midRails = new ArrayList<RailTracker.TrackedRail>(this.rails);
            ArrayList<RailTracker.TrackedRail> aheadRails = new ArrayList<RailTracker.TrackedRail>();
            this.calcWheelTracks();
            boolean gotToAhead = false;
            for (RailTracker.TrackedRail rail : this.rails) {
                if (midRails.contains(rail)) {
                    gotToAhead = true;
                    continue;
                }
                if (gotToAhead) {
                    aheadRails.add(rail);
                    continue;
                }
                behindRails.add(rail);
            }
            for (i = 0; i < behindRails.size(); ++i) {
                loc = ((RailTracker.TrackedRail)behindRails.get((int)i)).state.positionLocation();
                theta = (double)i / (double)(behindRails.size() - 1);
                Util.spawnDustParticle(loc, 0.5 * theta + 0.5, 0.0, 0.0);
            }
            for (i = 0; i < midRails.size(); ++i) {
                loc = ((RailTracker.TrackedRail)midRails.get((int)i)).state.positionLocation();
                theta = (double)i / (double)(midRails.size() - 1);
                Util.spawnDustParticle(loc, 0.5 * (1.0 - theta), 0.5 * theta, 1.0);
            }
            for (i = 0; i < aheadRails.size(); ++i) {
                loc = ((RailTracker.TrackedRail)aheadRails.get((int)i)).state.positionLocation();
                theta = (double)i / (double)(aheadRails.size() - 1);
                Util.spawnDustParticle(loc, 0.0, 0.5 * (1.0 - theta) + 0.5, 0.0);
            }
        } else {
            this.calcWheelTracks();
        }
        Collections.reverse(this.rails);
        this.owner.getSignTracker().updatePosition();
        if (this.prevRails.isEmpty() && !this.rails.isEmpty()) {
            for (MinecartMember member : this.owner) {
                RailLookup.removeMemberFromAll(member);
            }
            for (RailTracker.TrackedRail newRail : this.rails) {
                newRail.handleMemberAdd();
            }
        } else {
            for (RailTracker.TrackedRail prevRail : this.prevRails) {
                if (!prevRail.memberAddedToRailPiece) continue;
                MinecartMember<?> memberToFind = prevRail.member;
                Iterator<RailTracker.TrackedRail> newRailIter = this.rails.iterator();
                block7: while (newRailIter.hasNext()) {
                    RailTracker.TrackedRail newRail = newRailIter.next();
                    if (newRail.member != memberToFind) continue;
                    do {
                        if (prevRail.state.isSameRails(newRail.state)) {
                            prevRail.memberAddedToRailPiece = false;
                            newRail.memberAddedToRailPiece = true;
                        }
                        if (!newRailIter.hasNext()) break block7;
                        newRail = newRailIter.next();
                    } while (newRail.member == memberToFind);
                    break;
                }
                if (!prevRail.memberAddedToRailPiece) continue;
                prevRail.handleMemberRemove();
            }
            for (RailTracker.TrackedRail newRail : this.rails) {
                if (newRail.memberAddedToRailPiece) continue;
                newRail.handleMemberAdd();
            }
        }
    }

    private final void calcWheelTracks() {
        if (this.rails.isEmpty()) {
            return;
        }
        boolean hasPreviousMember = false;
        for (int i = 0; i < this.rails.size(); ++i) {
            RailTracker.TrackedRail rail = this.rails.get(i);
            if (rail.state.railType() == RailType.NONE) {
                if (!hasPreviousMember) continue;
                this.calcWheelTracksAhead(i - 1);
                hasPreviousMember = false;
                while (this.rails.get(i) != rail && i < this.rails.size()) {
                    ++i;
                }
                continue;
            }
            if (hasPreviousMember && !rail.disconnected) continue;
            this.calcWheelTracksBehind(i);
            hasPreviousMember = true;
            while (this.rails.get(i) != rail && i < this.rails.size()) {
                ++i;
            }
        }
        this.calcWheelTracksAhead(this.rails.size() - 1);
    }

    private final void calcWheelTracksAhead(int railIndex) {
        RailTracker.TrackedRail startInfo = this.rails.get(railIndex);
        MinecartMember<?> tail = startInfo.member;
        if (startInfo.state.railType() == RailType.NONE) {
            return;
        }
        if (!tail.getWheels().hasWheelDistance()) {
            return;
        }
        RailPath.Position position = startInfo.state.position().clone();
        double wheelDistance = position.motDot(tail.getOrientationForward()) > 0.0 ? tail.getWheels().front().getDistance() : tail.getWheels().back().getDistance();
        if (wheelDistance > 1.0E-5) {
            TrackWalkingPoint p = new TrackWalkingPoint(startInfo.state);
            int limit = 1000;
            do {
                if (p.moveStep(wheelDistance - p.movedTotal) && --limit == 0) {
                    this.owner.getTrainCarts().log(Level.WARNING, "Reached maximum loops refreshing front wheel position (train=" + this.owner.getProperties().getTrainName() + " x=" + ((CommonMinecart)tail.getEntity()).loc.getX() + " y=" + ((CommonMinecart)tail.getEntity()).loc.getY() + " z=" + ((CommonMinecart)tail.getEntity()).loc.getZ() + ")");
                    break;
                }
                this.rails.add(++railIndex, new RailTracker.TrackedRail(tail, p, false));
            } while (p.failReason == TrackWalkingPoint.FailReason.NONE);
        }
    }

    private final void calcWheelTracksBehind(int railIndex) {
        RailTracker.TrackedRail startInfo = this.rails.get(railIndex);
        MinecartMember<?> tail = startInfo.member;
        if (startInfo.state.railType() == RailType.NONE) {
            return;
        }
        if (!tail.getWheels().hasWheelDistance()) {
            return;
        }
        Vector movementDirection = startInfo.state.motionVector();
        movementDirection.multiply(-1.0);
        Vector ownDirection = tail.getOrientationForward();
        double wheelDistance = MathUtil.isHeadingTo((Vector)movementDirection, (Vector)ownDirection) ? tail.getWheels().front().getDistance() : tail.getWheels().back().getDistance();
        if (wheelDistance > 1.0E-5) {
            RailTracker.TrackedRail startRail;
            RailPath startPath;
            double startMoved;
            int i;
            RailPath.Position position = RailPath.Position.fromPosDir(((CommonMinecart)tail.getEntity()).loc.vector(), movementDirection);
            position.reverse = true;
            int prevRailStartIndex = -1;
            for (i = this.prevRails.size() - 1; i >= 0; --i) {
                if (!this.prevRails.get(i).isSameTrack(startInfo)) continue;
                prevRailStartIndex = i;
                break;
            }
            if (prevRailStartIndex == -1 && !this.prevRails.isEmpty()) {
                for (i = 0; i < this.prevRails.size(); ++i) {
                    if (this.prevRails.get((int)i).member != startInfo.member) continue;
                    RailTracker.TrackedRail prev = this.prevRails.get(i);
                    TrackWalkingPoint p = new TrackWalkingPoint(prev.state);
                    p.skipFirst();
                    if (!p.moveFull() || !p.state.isSameRails(startInfo.state) || !p.currentRailPath.equals(startInfo.getPath())) break;
                    this.prevRails.add(i, startInfo.clone());
                    prevRailStartIndex = i;
                    break;
                }
            }
            if (prevRailStartIndex != -1 && (wheelDistance -= (startMoved = (startPath = (startRail = this.prevRails.get(prevRailStartIndex)).getPath()).move(position, startRail.state.railBlock(), wheelDistance))) > 1.0E-10) {
                int order = startRail.state.position().motDot(position) > 0.0 ? -1 : 1;
                for (int prevRailIndex = prevRailStartIndex + order; prevRailIndex >= 0 && prevRailIndex < this.prevRails.size() && wheelDistance > 1.0E-4; prevRailIndex += order) {
                    RailTracker.TrackedRail rail = this.prevRails.get(prevRailIndex);
                    if (rail.isSameTrack(startInfo)) continue;
                    RailPath path = rail.getPath();
                    double moved = path.move(position, rail.state.railBlock(), wheelDistance);
                    wheelDistance -= moved;
                    rail = rail.changeMember(startInfo.member);
                    if (order < 0) {
                        rail = rail.invertMotionVector();
                    }
                    rail.cachedPath = path;
                    this.rails.add(railIndex, rail);
                    startInfo = rail;
                }
            }
            if (wheelDistance > 0.0) {
                RailState state = new RailState();
                state.setPosition(position);
                state.setMember(tail);
                state.setRailPiece(startInfo.state.railPiece());
                RailType.loadRailInformation(state);
                TrackWalkingPoint p = new TrackWalkingPoint(state);
                int limit = 1000;
                do {
                    if (p.moveStep(wheelDistance - p.movedTotal) && --limit == 1000) {
                        this.owner.getTrainCarts().log(Level.WARNING, "Reached maximum loops refreshing back wheel position (train=" + this.owner.getProperties().getTrainName() + " x=" + ((CommonMinecart)tail.getEntity()).loc.getX() + " y=" + ((CommonMinecart)tail.getEntity()).loc.getY() + " z=" + ((CommonMinecart)tail.getEntity()).loc.getZ() + ")");
                        break;
                    }
                    RailTracker.TrackedRail rail = new RailTracker.TrackedRail(tail, p, false);
                    rail = rail.invertMotionVector();
                    rail.cachedPath = p.currentRailPath;
                    this.rails.add(railIndex, rail);
                } while (p.failReason == TrackWalkingPoint.FailReason.NONE);
            }
            if (position != null) {
                // empty if block
            }
        }
    }

    private final void refreshFrom(int memberIndex, boolean disconnected) {
        Iterator<RailTracker.TrackedRail> iter;
        RailFinderResult result;
        RailFinder finder = new RailFinder(memberIndex, disconnected);
        if (finder.startIndex < 0) {
            finder.tail.getRailTracker().refresh(finder.startInfo);
            this.rails.add(finder.startInfo);
            return;
        }
        if (((RailFinder)finder).startInfo.state.railType() == RailType.NONE) {
            finder.tail.getRailTracker().refresh(finder.startInfo);
            this.rails.add(finder.startInfo);
            this.refreshFrom(finder.startIndex, false);
            return;
        }
        int remainingCnt = memberIndex;
        boolean isAbormal = false;
        if (this.rails.isEmpty()) {
            result = finder.test(finder.startInfo, this.rails);
            if (result.numMembers < remainingCnt && !result.endIsDerailed) {
                isAbormal = true;
                result.rails = new ArrayList<RailTracker.TrackedRail>(result.rails);
                this.rails.clear();
            }
        } else {
            result = finder.test(finder.startInfo);
            isAbormal = true;
        }
        if (isAbormal) {
            if (result.numMembers < remainingCnt && !result.endIsDerailed) {
                RailFinderResult alter = finder.test(finder.startInfo.invertMotionVector());
                if (alter.numMembers > result.numMembers) {
                    result = alter;
                }
            }
            this.rails.addAll(result.rails);
        }
        if (TCConfig.logTrainSplitting && result.status != RailFinderResult.Status.OK) {
            Logger logger = this.owner.getTrainCarts().getLogger();
            logger.warning("Train '" + this.owner.getProperties().getTrainName() + "' split apart because: " + result.status.getReason());
            logger.warning("Search start: " + result.failSearchStart);
            logger.warning("Search end: " + result.failSearchEnd);
            if (result.nextMemberIndex >= 0) {
                MinecartMember member = (MinecartMember)this.owner.get(result.nextMemberIndex);
                Location mloc = ((CommonMinecart)member.getEntity()).getLocation();
                logger.warning("Cart that could not be reached: cart #" + (result.nextMemberIndex + 1) + " of " + this.owner.size() + " [" + ((CommonMinecart)member.getEntity()).getUniqueId() + "] at x=" + mloc.getX() + " y=" + mloc.getY() + " z=" + mloc.getZ());
            }
        }
        if ((iter = result.rails.iterator()).hasNext()) {
            RailTracker.TrackedRail prev = iter.next();
            while (iter.hasNext()) {
                RailTracker.TrackedRail next = iter.next();
                if (prev.member != next.member) {
                    prev.member.getRailTracker().refresh(prev);
                }
                prev = next;
            }
            prev.member.getRailTracker().refresh(prev);
        }
        if (result.nextMemberIndex >= 0) {
            this.refreshFrom(result.nextMemberIndex, !result.endIsDerailed);
        }
    }

    private class RailFinder {
        private MinecartMember<?> tail;
        private final RailTracker.TrackedRail startInfo;
        private final int startIndex;

        public RailFinder(int index, boolean disconnected) {
            this.tail = (MinecartMember)RailTrackerGroup.this.owner.get(index);
            this.startInfo = RailTracker.TrackedRail.create(this.tail, disconnected);
            this.startIndex = index - 1;
        }

        public RailFinderResult test(RailTracker.TrackedRail moveInfo) {
            return this.test(moveInfo, new LinkedList<RailTracker.TrackedRail>());
        }

        /*
         * Enabled aggressive block sorting
         */
        public RailFinderResult test(RailTracker.TrackedRail moveInfo, List<RailTracker.TrackedRail> buffer) {
            RailFinderResult result = new RailFinderResult(this.startIndex, buffer);
            result.rails.add(moveInfo);
            MinecartMember nextMember = (MinecartMember)RailTrackerGroup.this.owner.get(result.nextMemberIndex);
            RailState nextPos = nextMember.discoverRail();
            if (nextPos.railType() == RailType.NONE) {
                result.status = RailFinderResult.Status.DERAILED;
                result.failSearchStart = moveInfo.state;
                result.failSearchEnd = nextPos;
                result.endIsDerailed = true;
                return result;
            }
            int moveLimitCtr = 0;
            int maximumDistanceBlocks = this.tail.getMaximumBlockDistance(nextMember);
            TrackWalkingPoint p = new TrackWalkingPoint(moveInfo.state);
            if (!p.moveFull()) return result;
            moveLimitCtr = 0;
            boolean isFirstBlock = true;
            int nrCachedRails = 0;
            while (true) {
                RailTracker.TrackedRail currInfo;
                block12: {
                    double curr_distance;
                    double initial_distance;
                    int cycle_limit;
                    block13: {
                        block10: {
                            block11: {
                                boolean useFastMethod;
                                if (!p.state.isSameRails(nextPos)) break block10;
                                boolean bl = useFastMethod = p.currentRailPath.getSegments().length <= 1;
                                if (!useFastMethod) break block11;
                                if (p.state.position().motDot(nextPos.motionVector()) < 0.0) {
                                    nextPos.position().invertMotion();
                                }
                                currInfo = new RailTracker.TrackedRail(nextMember, nextPos, false);
                                break block12;
                            }
                            double ERR_EPSILON = 1.0E-8;
                            cycle_limit = 10000;
                            curr_distance = initial_distance = p.state.position().distance(nextPos.position());
                            break block13;
                        }
                        if (isFirstBlock) {
                            isFirstBlock = false;
                        } else {
                            result.rails.add(new RailTracker.TrackedRail(nextMember, p, false));
                            ++nrCachedRails;
                        }
                        if (++moveLimitCtr <= maximumDistanceBlocks && p.moveFull()) continue;
                        while (nrCachedRails > 0) {
                            --nrCachedRails;
                            result.rails.remove(result.rails.size() - 1);
                        }
                        result.status = moveLimitCtr > maximumDistanceBlocks ? RailFinderResult.Status.LIMIT_REACHED : RailFinderResult.Status.END_OF_TRACK;
                        result.failSearchStart = result.rails.isEmpty() ? p.state : result.rails.get((int)(result.rails.size() - 1)).state;
                        result.failSearchEnd = p.state;
                        return result;
                    }
                    while (!(curr_distance <= 1.0E-8)) {
                        if (!p.move(curr_distance) || p.moved <= 1.0E-8) {
                            curr_distance = p.state.position().distance(nextPos.position());
                            break;
                        }
                        double new_distance = p.state.position().distance(nextPos.position());
                        if (new_distance >= curr_distance) break;
                        curr_distance = new_distance;
                        if (--cycle_limit > 0) continue;
                    }
                    if (curr_distance > 1.0E-8 && curr_distance > 0.5 * initial_distance) {
                        result.status = RailFinderResult.Status.DIVERGING;
                        result.failSearchStart = p.state;
                        result.failSearchEnd = nextPos;
                        return result;
                    }
                    RailState currInfoState = p.state.clone();
                    currInfoState.setRailPiece(nextPos.railPiece());
                    currInfo = new RailTracker.TrackedRail(nextMember, currInfoState, false);
                }
                ++result.numMembers;
                nrCachedRails = 0;
                result.rails.add(currInfo);
                if (--result.nextMemberIndex < 0) {
                    return result;
                }
                moveLimitCtr = 0;
                nextMember = (MinecartMember)RailTrackerGroup.this.owner.get(result.nextMemberIndex);
                nextPos = nextMember.discoverRail();
                maximumDistanceBlocks = currInfo.member.getMaximumBlockDistance(nextMember);
                isFirstBlock = true;
                if (nextPos.railType() == RailType.NONE) break;
            }
            result.status = RailFinderResult.Status.DERAILED;
            result.endIsDerailed = true;
            result.failSearchStart = p.state;
            result.failSearchEnd = nextPos;
            return result;
        }
    }

    public static class RailFinderResult {
        public Status status = Status.OK;
        public List<RailTracker.TrackedRail> rails;
        public int numMembers;
        public int nextMemberIndex;
        public boolean endIsDerailed;
        public RailState failSearchStart;
        public RailState failSearchEnd;

        public RailFinderResult(int nextMemberIndex, List<RailTracker.TrackedRail> buffer) {
            this.rails = buffer;
            this.numMembers = 0;
            this.nextMemberIndex = nextMemberIndex;
            this.endIsDerailed = false;
            this.failSearchStart = null;
            this.failSearchEnd = null;
        }

        public static enum Status {
            OK("OK"),
            DIVERGING("Path moving away from the next cart in the chain"),
            DERAILED("Next cart is derailed"),
            LIMIT_REACHED("Maximum distance reached searching next cart"),
            END_OF_TRACK("End of the rails reached before finding next cart");

            private final String reason;

            private Status(String reason) {
                this.reason = reason;
            }

            public String getReason() {
                return this.reason;
            }
        }
    }
}

