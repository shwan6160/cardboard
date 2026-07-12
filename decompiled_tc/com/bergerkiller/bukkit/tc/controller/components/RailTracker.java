/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public abstract class RailTracker {
    public abstract boolean isOnRails(Block var1);

    public static class TrackedRailWalker {
        private final List<TrackedRail> rails;
        private TrackedRail currentRail;
        private int currentRailIndex;
        private int order;
        private final RailState state;

        public TrackedRailWalker(List<TrackedRail> rails, TrackedRail currentRail) {
            this.rails = rails;
            this.currentRail = currentRail;
            this.currentRailIndex = this.rails.indexOf(currentRail);
            this.order = -1;
            this.state = currentRail.state.clone();
            this.state.position().makeAbsolute(this.state.railBlock());
        }

        public RailState state() {
            return this.state;
        }

        public RailPath.Position position() {
            return this.state.position();
        }

        public void invertMotion() {
            this.state.position().invertMotion();
            this.order = -this.order;
        }

        public double move(double distance) {
            if (this.currentRailIndex == -1) {
                return 0.0;
            }
            double movedTotal = 0.0;
            double distanceRemaining = distance;
            while (true) {
                double moved = this.currentRail.getPath().move(this.state, distanceRemaining);
                movedTotal += moved;
                if (moved >= distanceRemaining) {
                    return distance;
                }
                this.currentRailIndex += this.order;
                if (this.currentRailIndex < 0) {
                    this.currentRailIndex = 0;
                    break;
                }
                if (this.currentRailIndex >= this.rails.size()) {
                    this.currentRailIndex = this.rails.size() - 1;
                    break;
                }
                this.currentRail = this.rails.get(this.currentRailIndex);
                this.state.setRailPiece(this.currentRail.state.railPiece());
                distanceRemaining = distance - movedTotal;
            }
            return movedTotal;
        }
    }

    public static class TrackedRail
    implements Cloneable {
        public final MinecartMember<?> member;
        public final Block minecartBlock;
        public final boolean disconnected;
        public RailPath cachedPath = null;
        public final RailState state;
        protected boolean memberAddedToRailPiece;

        public TrackedRail(MinecartMember<?> member, TrackWalkingPoint point, boolean disconnected) {
            this(member, point.state.clone(), disconnected);
        }

        public TrackedRail(MinecartMember<?> member, RailState state, boolean disconnected) {
            state.position().assertAbsolute();
            this.member = member;
            this.state = state;
            this.state.setMember(member);
            this.minecartBlock = state.positionBlock();
            this.disconnected = disconnected;
            this.memberAddedToRailPiece = false;
        }

        public TrackedRail(MinecartMember<?> member) {
            this.member = member;
            this.state = new RailState();
            this.state.setMember(member);
            this.state.setRailPiece(RailPiece.NONE);
            this.minecartBlock = null;
            this.disconnected = false;
            this.state.position().setMotion(new Vector(0, -1, 0));
            this.state.initEnterDirection();
            this.memberAddedToRailPiece = false;
        }

        public Location getMemberLocation() {
            return this.state.positionLocation();
        }

        public TrackedRail invertMotionVector() {
            RailState state = this.state.clone();
            RailPath.Position p = state.position();
            p.motX = -p.motX;
            p.motY = -p.motY;
            p.motZ = -p.motZ;
            state.initEnterDirection();
            return new TrackedRail(this.member, state, this.disconnected);
        }

        public TrackedRail changeMember(MinecartMember<?> member) {
            return new TrackedRail(member, this.state, this.disconnected);
        }

        public TrackedRail clone() {
            return new TrackedRail(this.member, this.state, this.disconnected);
        }

        void handleMemberRemove() {
            this.memberAddedToRailPiece = false;
            try {
                this.state.railPiece().mutableMembers().remove(this.member);
            }
            catch (RailLookup.RailTypeNotRegisteredException railTypeNotRegisteredException) {
                // empty catch block
            }
        }

        void handleMemberAdd() {
            this.memberAddedToRailPiece = true;
            List<MinecartMember<?>> members = this.state.railPiece().mutableMembers();
            if (!members.contains(this.member)) {
                members.add(this.member);
            }
        }

        public RailLogic getLogic() {
            return this.state.loadRailLogic();
        }

        public RailPath getPath() {
            if (this.cachedPath == null) {
                this.cachedPath = this.getLogic().getPath();
            }
            return this.cachedPath;
        }

        public boolean isSameTrack(TrackedRail other) {
            return this.state.isSameRails(other.state) && this.getPath().equals(other.getPath());
        }

        public static TrackedRail createDerailed(MinecartMember<?> member) {
            Location loc = ((CommonMinecart)member.getEntity()).getLocation();
            RailState state = new RailState();
            state.position().setLocation(loc);
            state.position().setMotion(((CommonMinecart)member.getEntity()).getVelocity());
            state.setRailPiece(RailPiece.create(RailType.NONE, loc.getBlock()));
            state.initEnterDirection();
            return new TrackedRail(member, state, false);
        }

        public static TrackedRail create(MinecartMember<?> member, boolean disconnected) {
            return new TrackedRail(member, member.discoverRail(), disconnected);
        }

        public String toString() {
            RailPath.Position pos = this.state.position();
            if (this.getPath().isEmpty()) {
                return "POS{x=" + pos.posX + ",y=" + pos.posY + ",z=" + pos.posZ + "} RAIL" + this.state.railPiece().blockPosition() + " EMPTY PATH";
            }
            RailPath.Position start = this.getPath().getStartPosition();
            RailPath.Position end = this.getPath().getEndPosition();
            start.makeAbsolute(this.state.railBlock());
            end.makeAbsolute(this.state.railBlock());
            return "POS{x=" + pos.posX + ",y=" + pos.posY + ",z=" + pos.posZ + "} RAIL" + this.state.railPiece().blockPosition() + " START{x=" + start.posX + ",y=" + start.posY + ",z=" + start.posZ + "} END{x=" + end.posX + ",y=" + end.posY + ",z=" + end.posZ + "}";
        }
    }
}

