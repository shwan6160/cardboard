/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.bases.mutable.LocationAbstract
 *  com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.bases.mutable.LocationAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.utils.BlockIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class RailPath {
    private static final double SMALL_ADVANCE_MIN_MOT = 1.0E-6;
    public static final RailPath EMPTY = new RailPath(new Point[0]);
    private final Point[] points;
    private final Segment[] segments;
    private final double totalDistance;

    private RailPath(Point[] points) {
        this.points = points;
        if (points.length < 2) {
            this.segments = new Segment[0];
            this.totalDistance = 0.0;
        } else {
            int i;
            double distance = 0.0;
            this.segments = new Segment[points.length - 1];
            for (i = 0; i < this.segments.length; ++i) {
                this.segments[i] = new Segment(points[i], points[i + 1]);
                distance += this.segments[i].l;
            }
            for (i = 0; i < this.segments.length - 1; ++i) {
                this.segments[i].next = this.segments[i + 1];
                this.segments[i + 1].prev = this.segments[i];
            }
            for (i = 0; i < this.segments.length; ++i) {
                this.segments[i].postinit();
            }
            this.totalDistance = distance;
        }
    }

    public double getTotalDistance() {
        return this.totalDistance;
    }

    public Point[] getPoints() {
        return this.points;
    }

    public Segment[] getSegments() {
        return this.segments;
    }

    public Position getStartPosition() {
        Segment firstSegment = this.segments[0];
        Position p = new Position();
        p.relative = true;
        p.posX = firstSegment.p0.x;
        p.posY = firstSegment.p0.y;
        p.posZ = firstSegment.p0.z;
        p.wheelSegment = firstSegment;
        p.wheelTheta = 0.0;
        p.motX = -firstSegment.mot.getX();
        p.motY = -firstSegment.mot.getY();
        p.motZ = -firstSegment.mot.getZ();
        return p;
    }

    public Position getEndPosition() {
        Segment lastSegment = this.segments[this.segments.length - 1];
        Position p = new Position();
        p.relative = true;
        p.posX = lastSegment.p1.x;
        p.posY = lastSegment.p1.y;
        p.posZ = lastSegment.p1.z;
        p.wheelSegment = lastSegment;
        p.wheelTheta = 1.0;
        p.motX = lastSegment.mot.getX();
        p.motY = lastSegment.mot.getY();
        p.motZ = lastSegment.mot.getZ();
        return p;
    }

    public Position getEndOfPath(Block railBlock, Position position) {
        Segment s = position.wheelSegment;
        if (s == null) {
            throw new IllegalArgumentException("Input position was never moved or snapped to a path!");
        }
        Position end = position.motDot(s.p_offset) > 0.0 ? this.getEndPosition() : this.getStartPosition();
        end.makeAbsolute(railBlock);
        return end;
    }

    public boolean isEmpty() {
        return this.segments.length == 0;
    }

    public ProximityInfo getProximityInfo(Vector position, Vector motionVector) {
        ProximityInfo info = new ProximityInfo();
        for (int i = 0; i < this.segments.length; ++i) {
            double tmpTheta;
            double tmpDistSquared;
            Segment tmpSegment = this.segments[i];
            if (tmpSegment.isZeroLength() || !((tmpDistSquared = tmpSegment.calcDistanceSquared(position, tmpTheta = tmpSegment.calcTheta(position))) < info.distanceSquared)) continue;
            info.distanceSquared = tmpDistSquared;
            info.canMoveForward = tmpTheta < tmpSegment.end_theta_threshold && i == 0 ? tmpSegment.mot.dot(motionVector) >= 0.0 : (1.0 - tmpTheta < tmpSegment.end_theta_threshold && i == this.segments.length - 1 ? tmpSegment.mot.dot(motionVector) <= 0.0 : true);
        }
        return info;
    }

    public double distanceSquared(Vector position) {
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.segments.length; ++i) {
            double tmpTheta;
            double tmpDistSquared;
            Segment tmpSegment = this.segments[i];
            if (tmpSegment.isZeroLength() || !((tmpDistSquared = tmpSegment.calcDistanceSquared(position, tmpTheta = tmpSegment.calcTheta(position))) < closestDistance)) continue;
            closestDistance = tmpDistSquared;
        }
        return closestDistance;
    }

    public Segment findSegment(Vector position) {
        if (this.segments.length == 0) {
            return null;
        }
        if (this.segments.length == 1) {
            return this.segments[0];
        }
        Segment s = null;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.segments.length; ++i) {
            double tmpTheta;
            double tmpDistSquared;
            Segment tmpSegment = this.segments[i];
            if (tmpSegment.isZeroLength() || !((tmpDistSquared = tmpSegment.calcDistanceSquared(position, tmpTheta = tmpSegment.calcTheta(position))) < closestDistance)) continue;
            closestDistance = tmpDistSquared;
            s = tmpSegment;
        }
        return s;
    }

    public Segment findSegment(Vector position, Block rails) {
        if (this.segments.length == 0) {
            return null;
        }
        if (this.segments.length == 1) {
            return this.segments[0];
        }
        Vector relPos = position.clone();
        relPos.setX(relPos.getX() - (double)rails.getX());
        relPos.setY(relPos.getY() - (double)rails.getY());
        relPos.setZ(relPos.getZ() - (double)rails.getZ());
        return this.findSegment(relPos);
    }

    public void snap(Position position, Block railsBlock) {
        this.move(position, railsBlock, 0.0);
    }

    public double move(RailState state, double distance) {
        return this.move(state.position(), state.railBlock(), distance);
    }

    public double move(Position position, Block railBlock, double distance) {
        position.assertAbsolute();
        position.makeRelative(railBlock);
        double result = this.moveRelative(position, distance);
        position.makeAbsolute(railBlock);
        return result;
    }

    @Deprecated
    public double move(Vector position, Vector direction, Block railsBlock, double distance) {
        position.setX(position.getX() - (double)railsBlock.getX());
        position.setY(position.getY() - (double)railsBlock.getY());
        position.setZ(position.getZ() - (double)railsBlock.getZ());
        double result = this.moveRelative(position, direction, distance);
        position.setX(position.getX() + (double)railsBlock.getX());
        position.setY(position.getY() + (double)railsBlock.getY());
        position.setZ(position.getZ() + (double)railsBlock.getZ());
        return result;
    }

    @Deprecated
    public double moveRelative(Vector position, Vector direction, double distance) {
        Position tmp = new Position();
        tmp.relative = true;
        tmp.posX = position.getX();
        tmp.posY = position.getY();
        tmp.posZ = position.getZ();
        tmp.motX = direction.getX();
        tmp.motY = direction.getY();
        tmp.motZ = direction.getZ();
        double result = this.moveRelative(tmp, distance);
        position.setX(tmp.posX);
        position.setY(tmp.posY);
        position.setZ(tmp.posZ);
        direction.setX(tmp.motX);
        direction.setY(tmp.motY);
        direction.setZ(tmp.motZ);
        return result;
    }

    /*
     * Enabled aggressive block sorting
     */
    public double moveRelative(Position position, double distance) {
        position.assertRelative();
        if (this.segments.length == 0) {
            return 0.0;
        }
        if (this.segments.length == 1) {
            double remainingDistance;
            Segment s = this.segments[0];
            if (s.isZeroLength()) {
                s.calcPosition(position, 0.0);
                return 0.0;
            }
            double theta = s.calcTheta(position);
            s.calcPosition(position, theta);
            int order = s.calcDirection(position);
            if (order == 1) {
                double remainingDistance2;
                if (theta >= 1.0) {
                    return 0.0;
                }
                if (theta < 0.0) {
                    theta = 0.0;
                }
                if (distance >= (remainingDistance2 = s.l * (1.0 - theta))) {
                    s.calcPosition(position, 1.0);
                    return remainingDistance2;
                }
                s.calcPosition(position, theta + distance * s.linv);
                return distance;
            }
            if (theta <= 0.0) {
                return 0.0;
            }
            if (theta > 1.0) {
                theta = 1.0;
            }
            if (distance >= (remainingDistance = s.l * theta)) {
                s.calcPosition(position, 0.0);
                return remainingDistance;
            }
            s.calcPosition(position, theta - distance * s.linv);
            return distance;
        }
        double theta = 0.0;
        Segment s = null;
        int segmentIndex = -1;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.segments.length; ++i) {
            double tmpTheta;
            double tmpDistSquared;
            Segment tmpSegment = this.segments[i];
            if (tmpSegment.isZeroLength() || !((tmpDistSquared = tmpSegment.calcDistanceSquared(position, tmpTheta = tmpSegment.calcTheta(position))) < closestDistance)) continue;
            closestDistance = tmpDistSquared;
            theta = tmpTheta;
            s = tmpSegment;
            segmentIndex = i;
        }
        if (s == null) {
            return 0.0;
        }
        if (distance <= 0.0) {
            s.calcPosition(position, theta);
            s.calcDirection(position);
            return 0.0;
        }
        int order = s.calcDirection(position);
        double moved = 0.0;
        while (distance > 0.0) {
            s.calcPosition(position, theta);
            if (!s.isZeroLength()) {
                if (order == 1) {
                    if (theta < 0.0) {
                        theta = 0.0;
                    }
                    if (theta < 1.0) {
                        double remainingDistance = s.l * (1.0 - theta);
                        if (!(distance >= remainingDistance)) {
                            s.calcPosition(position, theta + distance * s.linv);
                            moved += distance;
                            return moved;
                        }
                        s.calcPosition(position, 1.0);
                        moved += remainingDistance;
                        distance -= remainingDistance;
                    }
                } else {
                    if (theta > 1.0) {
                        theta = 1.0;
                    }
                    if (theta > 0.0) {
                        double remainingDistance = s.l * theta;
                        if (!(distance >= remainingDistance)) {
                            s.calcPosition(position, theta - distance * s.linv);
                            moved += distance;
                            return moved;
                        }
                        s.calcPosition(position, 0.0);
                        moved += remainingDistance;
                        distance -= remainingDistance;
                    }
                }
            }
            if ((segmentIndex += order) < 0) return moved;
            if (segmentIndex >= this.segments.length) {
                return moved;
            }
            s = this.segments[segmentIndex];
            theta = s.calcTheta(position);
            Vector mot = s.mot;
            if (order > 0) {
                position.motX = mot.getX();
                position.motY = mot.getY();
                position.motZ = mot.getZ();
                continue;
            }
            position.motX = -mot.getX();
            position.motY = -mot.getY();
            position.motZ = -mot.getZ();
        }
        return moved;
    }

    public void forAllBlocks(IntVector3 railsBlock, Consumer<IntVector3> blockConsumer) {
        BlockIterator iter = null;
        IntVector3 last = null;
        for (Segment segment : this.segments) {
            IntVector3 containedInsideBlock = segment.containedInsideBlock;
            if (containedInsideBlock != null) {
                containedInsideBlock = containedInsideBlock.add(railsBlock);
                if (last != null && last.isSame(containedInsideBlock)) continue;
                last = containedInsideBlock;
                blockConsumer.accept(containedInsideBlock);
                continue;
            }
            if (iter == null) {
                iter = new BlockIterator(railsBlock, segment);
            } else {
                iter.reset(railsBlock, segment);
            }
            if (!iter.next()) continue;
            IntVector3 firstBlock = iter.block();
            if (last == null || !last.isSame(firstBlock)) {
                last = firstBlock;
                blockConsumer.accept(firstBlock);
            }
            if (iter.next()) {
                do {
                    last = iter.block();
                    blockConsumer.accept(last);
                } while (iter.next());
                continue;
            }
            segment.containedInsideBlock = firstBlock.subtract(railsBlock);
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RailPath) {
            RailPath other = (RailPath)o;
            if (other.points.length != this.points.length) {
                return false;
            }
            for (int i = 0; i < this.points.length; ++i) {
                Point p1 = this.points[i];
                Point p2 = other.points[i];
                if (p1.x == p2.x && p1.y == p2.y && p1.z == p2.z) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("RailPath[npoints=").append(this.points.length + "]:");
        for (Point p : this.points) {
            str.append("\n  - ").append(p.toString());
        }
        return str.toString();
    }

    public String stringifyEndPoints(Block railBlock) {
        if (this.isEmpty()) {
            return "RailPath{EMPTY}";
        }
        Position a = this.getStartPosition();
        Position b = this.getEndPosition();
        a.makeAbsolute(railBlock);
        b.makeAbsolute(railBlock);
        return "RailPath{[ " + a.posX + " / " + a.posY + " / " + a.posZ + " ] => [ " + b.posX + " / " + b.posY + " / " + b.posZ + " ]}";
    }

    public static RailPath create(Vector ... pointVectors) {
        Point[] points = new Point[pointVectors.length];
        for (int i = 0; i < pointVectors.length; ++i) {
            Vector v = pointVectors[i];
            points[i] = new Point(v.getX(), v.getY(), v.getZ());
        }
        return RailPath.create(points);
    }

    public static RailPath create(Point ... points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Paths must have at least 2 points");
        }
        return new RailPath(points);
    }

    public static RailPath offset(RailPath original_path, Vector position_offset) {
        if (original_path.isEmpty()) {
            return EMPTY;
        }
        Point[] originalPoints = original_path.getPoints();
        Point[] points_offset = new Point[originalPoints.length];
        for (int i = 0; i < originalPoints.length; ++i) {
            Point original = originalPoints[i];
            points_offset[i] = new Point(original.x + position_offset.getX(), original.y + position_offset.getY(), original.z + position_offset.getZ(), original.up_x, original.up_y, original.up_z);
        }
        return RailPath.create(points_offset);
    }

    public static class Point {
        public final double x;
        public final double y;
        public final double z;
        public final double up_x;
        public final double up_y;
        public final double up_z;

        public Point(Vector v) {
            this(v.getX(), v.getY(), v.getZ());
        }

        public Point(Vector v, Vector up) {
            this(v.getX(), v.getY(), v.getZ(), up.getX(), up.getY(), up.getZ());
        }

        public Point(Vector v, double up_x, double up_y, double up_z) {
            this(v.getX(), v.getY(), v.getZ(), up_x, up_y, up_z);
        }

        public Point(Vector v, BlockFace face) {
            this(v.getX(), v.getY(), v.getZ(), face);
        }

        public Point(double x, double y, double z) {
            this(x, y, z, BlockFace.UP);
        }

        public Point(double x, double y, double z, BlockFace face) {
            this(x, y, z, face.getModX(), face.getModY(), face.getModZ());
        }

        public Point(double x, double y, double z, double up_x, double up_y, double up_z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.up_x = up_x;
            this.up_y = up_y;
            this.up_z = up_z;
        }

        public boolean isVertical() {
            double EP = 1.0E-5;
            return this.x >= -1.0E-5 && this.x <= 1.0E-5 && this.z >= -1.0E-5 && this.z <= 1.0E-5;
        }

        public double distanceSquared(Vector position) {
            double dx = position.getX() - this.x;
            double dy = position.getY() - this.y;
            double dz = position.getZ() - this.z;
            return dx * dx + dy * dy + dz * dz;
        }

        public double distanceSquared(Position position) {
            double dx = position.posX - this.x;
            double dy = position.posY - this.y;
            double dz = position.posZ - this.z;
            return dx * dx + dy * dy + dz * dz;
        }

        public double dot(Vector vector) {
            return this.x * vector.getX() + this.y * vector.getY() + this.z * vector.getZ();
        }

        public final Vector up() {
            return new Vector(this.up_x, this.up_y, this.up_z);
        }

        public final Vector toVector() {
            return new Vector(this.x, this.y, this.z);
        }

        public final void toVector(Vector v) {
            v.setX(this.x);
            v.setY(this.y);
            v.setZ(this.z);
        }

        public final Location getLocation(Block railsBlock) {
            return new Location(railsBlock.getWorld(), (double)railsBlock.getX() + this.x, (double)railsBlock.getY() + this.y, (double)railsBlock.getZ() + this.z);
        }

        public String toString() {
            return "[v={" + this.x + "/" + this.y + "/" + this.z + "} up={" + this.up_x + "/" + this.up_y + "/" + this.up_z + "}]";
        }
    }

    public static class Segment {
        public final Point p0;
        public final Point p1;
        public final Vector p_offset;
        public final Vector mot;
        private final Vector mot_dt;
        public final Quaternion p0_orientation;
        public final Quaternion p1_orientation;
        public final boolean has_changing_up_orientation;
        public final boolean has_vertical_slope;
        private final double end_theta_threshold;
        public final double l;
        public final double ls;
        public final double linv;
        private Segment prev;
        private Segment next;
        @Deprecated
        public final Point dt;
        @Deprecated
        public final Point dt_norm;
        private IntVector3 containedInsideBlock = null;

        public Segment(Point p0, Point p1) {
            this.p_offset = new Vector(p1.x - p0.x, p1.y - p0.y, p1.z - p0.z);
            this.ls = this.p_offset.lengthSquared();
            this.l = Math.sqrt(this.ls);
            if (this.l <= 1.0E-20) {
                this.linv = 0.0;
                this.mot = new Vector();
                this.mot_dt = new Vector();
            } else {
                this.linv = 1.0 / this.l;
                this.mot = this.p_offset.clone().multiply(this.linv);
                this.mot_dt = this.p_offset.clone().multiply(1.0 / this.ls);
            }
            this.has_vertical_slope = this.mot.getY() < -1.0E-10 || this.mot.getY() > 1.0E-10;
            this.end_theta_threshold = Math.min(0.001, 1.0E-4 * this.linv);
            this.dt = new Point(this.p_offset);
            this.dt_norm = new Point(this.mot);
            Vector up0 = this.mot.clone().crossProduct(p0.up()).crossProduct(this.mot).normalize();
            Vector up1 = this.mot.clone().crossProduct(p1.up()).crossProduct(this.mot).normalize();
            this.p0 = new Point(p0.x, p0.y, p0.z, up0.getX(), up0.getY(), up0.getZ());
            this.p1 = new Point(p1.x, p1.y, p1.z, up1.getX(), up1.getY(), up1.getZ());
            this.has_changing_up_orientation = up0.distanceSquared(up1) > 1.0E-6;
            this.p0_orientation = new Quaternion();
            this.p1_orientation = new Quaternion();
        }

        public void postinit() {
            Quaternion mid_up0 = Quaternion.fromLookDirection((Vector)this.mot, (Vector)this.p0.up());
            if (this.prev == null || this.prev.isZeroLength()) {
                this.p0_orientation.setTo(mid_up0);
            } else {
                Quaternion prev_up = Quaternion.fromLookDirection((Vector)this.prev.mot, (Vector)this.prev.p1.up());
                this.p0_orientation.setTo(Quaternion.slerp((Quaternion)prev_up, (Quaternion)mid_up0, (double)0.5));
            }
            Quaternion mid_up1 = Quaternion.fromLookDirection((Vector)this.mot, (Vector)this.p1.up());
            if (this.next == null || this.next.isZeroLength()) {
                this.p1_orientation.setTo(mid_up1);
            } else {
                Quaternion next_up = Quaternion.fromLookDirection((Vector)this.next.mot, (Vector)this.next.p0.up());
                this.p1_orientation.setTo(Quaternion.slerp((Quaternion)next_up, (Quaternion)mid_up1, (double)0.5));
            }
        }

        public final boolean isZeroLength() {
            return this.l <= 1.0E-5;
        }

        public final Location getLocation(Block railsBlock, double theta) {
            return new Location(railsBlock.getWorld(), (double)railsBlock.getX() + this.p0.x + this.p_offset.getX() * theta, (double)railsBlock.getY() + this.p0.y + this.p_offset.getY() * theta, (double)railsBlock.getZ() + this.p0.z + this.p_offset.getZ() * theta);
        }

        private final int isHeadingToPrev(Position position) {
            if (this.prev != null) {
                double dot = position.motDot(this.prev.mot);
                if (dot < -1.0E-7) {
                    return 1;
                }
                if (dot > 1.0E-7) {
                    return -1;
                }
                return this.prev.isHeadingToPrev(position);
            }
            return 0;
        }

        private final int isHeadingToNext(Position position) {
            if (this.next != null) {
                double dot = position.motDot(this.next.mot);
                if (dot > 1.0E-7) {
                    return 1;
                }
                if (dot < -1.0E-7) {
                    return -1;
                }
                return this.next.isHeadingToNext(position);
            }
            return 0;
        }

        public final int calcDirection(Position position) {
            Vector mot = this.mot;
            double dot = position.motDot(mot);
            if (dot <= 1.0E-8 && dot >= -1.0E-8) {
                int order = this.isHeadingToPrev(position) - this.isHeadingToNext(position);
                if (order > 0) {
                    dot = -1.0;
                } else if (order < 0) {
                    dot = 1.0;
                } else {
                    dot = this.p1.distanceSquared(position) - this.p0.distanceSquared(position);
                    if (!(dot <= 1.0E-8) || dot >= -1.0E-8) {
                        // empty if block
                    }
                    if (position.reverse) {
                        dot = -dot;
                    }
                }
            }
            if (dot >= 0.0) {
                position.motX = mot.getX();
                position.motY = mot.getY();
                position.motZ = mot.getZ();
                return 1;
            }
            position.motX = -mot.getX();
            position.motY = -mot.getY();
            position.motZ = -mot.getZ();
            return -1;
        }

        public final double calcDistanceSquared(Vector position) {
            return this.calcDistanceSquared(position, this.calcTheta(position));
        }

        public final double calcDistanceSquared(Vector position, double theta) {
            Vector segmentPosition = new Vector();
            this.calcPosition(segmentPosition, theta);
            segmentPosition.subtract(position);
            return segmentPosition.lengthSquared();
        }

        public final double calcDistanceSquared(Position position, double theta) {
            Vector segmentPosition = new Vector();
            this.calcPosition(segmentPosition, theta);
            segmentPosition.setX(segmentPosition.getX() - position.posX);
            segmentPosition.setY(segmentPosition.getY() - position.posY);
            segmentPosition.setZ(segmentPosition.getZ() - position.posZ);
            return segmentPosition.lengthSquared();
        }

        public final double calcDistanceSquared(double x, double y, double z) {
            return this.calcDistanceSquared(x, y, z, this.calcTheta(x, y, z));
        }

        public final double calcDistanceSquared(double x, double y, double z, double theta) {
            double dz;
            double dy;
            double dx;
            if (theta <= 0.0) {
                dx = this.p0.x;
                dy = this.p0.y;
                dz = this.p0.z;
            } else if (theta >= 1.0) {
                dx = this.p1.x;
                dy = this.p1.y;
                dz = this.p1.z;
            } else {
                dx = this.p0.x + this.p_offset.getX() * theta;
                dy = this.p0.y + this.p_offset.getY() * theta;
                dz = this.p0.z + this.p_offset.getZ() * theta;
            }
            dx -= x;
            dy -= y;
            dz -= z;
            dx *= dx;
            dy *= dy;
            dz *= dz;
            return dx + dy + dz;
        }

        public void calcPosition(Vector position, double theta) {
            if (theta <= 0.0) {
                this.p0.toVector(position);
            } else if (theta >= 1.0) {
                this.p1.toVector(position);
            } else {
                position.setX(this.p0.x + this.p_offset.getX() * theta);
                position.setY(this.p0.y + this.p_offset.getY() * theta);
                position.setZ(this.p0.z + this.p_offset.getZ() * theta);
            }
        }

        public void calcPosition(Position position, double theta) {
            position.wheelSegment = this;
            position.wheelTheta = theta;
            if (theta <= 0.0) {
                position.posX = this.p0.x;
                position.posY = this.p0.y;
                position.posZ = this.p0.z;
            } else if (theta >= 1.0) {
                position.posX = this.p1.x;
                position.posY = this.p1.y;
                position.posZ = this.p1.z;
            } else {
                position.posX = this.p0.x + this.p_offset.getX() * theta;
                position.posY = this.p0.y + this.p_offset.getY() * theta;
                position.posZ = this.p0.z + this.p_offset.getZ() * theta;
            }
        }

        public Quaternion calcWheelOrientation(double theta) {
            if (theta <= 0.0) {
                return this.p0_orientation;
            }
            if (theta >= 1.0) {
                return this.p1_orientation;
            }
            return Quaternion.slerp((Quaternion)this.p0_orientation, (Quaternion)this.p1_orientation, (double)theta);
        }

        public final double calcTheta(Vector position) {
            return this.calcTheta(position.getX(), position.getY(), position.getZ());
        }

        public final double calcTheta(Position position) {
            return this.calcTheta(position.posX, position.posY, position.posZ);
        }

        public final double calcTheta(double x, double y, double z) {
            Point p0 = this.p0;
            Vector mot = this.mot_dt;
            return -((p0.x - x) * mot.getX() + (p0.y - y) * mot.getY() + (p0.z - z) * mot.getZ());
        }
    }

    public static final class Position {
        public double posX;
        public double posY;
        public double posZ;
        public double motX;
        public double motY;
        public double motZ;
        private Segment wheelSegment;
        private double wheelTheta;
        public boolean reverse = false;
        public boolean relative = true;

        public void makeRelative(Block railBlock) {
            if (!this.relative) {
                this.relative = true;
                this.posX -= (double)railBlock.getX();
                this.posY -= (double)railBlock.getY();
                this.posZ -= (double)railBlock.getZ();
            }
        }

        public void makeAbsolute(Block railBlock) {
            if (this.relative) {
                this.relative = false;
                this.posX += (double)railBlock.getX();
                this.posY += (double)railBlock.getY();
                this.posZ += (double)railBlock.getZ();
            }
        }

        public final void assertRelative() {
            if (!this.relative) {
                throw new IllegalStateException("Rail Position must be in relative coordinates");
            }
        }

        public final void assertAbsolute() {
            if (this.relative) {
                throw new IllegalStateException("Rail Position must be in absolute world coordinates");
            }
        }

        public void smallAdvance() {
            if (this.motX > 1.0E-6) {
                this.posX = Math.nextUp(this.posX);
            } else if (this.motX < -1.0E-6) {
                this.posX = Math.nextDown(this.posX);
            }
            if (this.motY > 1.0E-6) {
                this.posY = Math.nextUp(this.posY);
            } else if (this.motY < -1.0E-6) {
                this.posY = Math.nextDown(this.posY);
            }
            if (this.motZ > 1.0E-6) {
                this.posZ = Math.nextUp(this.posZ);
            } else if (this.motZ < -1.0E-6) {
                this.posZ = Math.nextDown(this.posZ);
            }
        }

        public Quaternion getWheelOrientation() {
            Segment s = this.wheelSegment;
            if (s == null) {
                return Quaternion.fromLookDirection((Vector)this.getMotion(), (Vector)new Vector(0, 1, 0));
            }
            return s.calcWheelOrientation(this.wheelTheta);
        }

        public void move(double distance) {
            this.posX += distance * this.motX;
            this.posY += distance * this.motY;
            this.posZ += distance * this.motZ;
        }

        public double distance(Position position) {
            if (this.relative) {
                position.assertRelative();
            } else {
                position.assertAbsolute();
            }
            return MathUtil.distance((double)this.posX, (double)this.posY, (double)this.posZ, (double)position.posX, (double)position.posY, (double)position.posZ);
        }

        public double distance(Location location) {
            this.assertAbsolute();
            return MathUtil.distance((double)this.posX, (double)this.posY, (double)this.posZ, (double)location.getX(), (double)location.getY(), (double)location.getZ());
        }

        public double distanceSquared(Location location) {
            this.assertAbsolute();
            return MathUtil.distanceSquared((double)this.posX, (double)this.posY, (double)this.posZ, (double)location.getX(), (double)location.getY(), (double)location.getZ());
        }

        public double distance(LocationAbstract location) {
            this.assertAbsolute();
            return MathUtil.distance((double)this.posX, (double)this.posY, (double)this.posZ, (double)location.getX(), (double)location.getY(), (double)location.getZ());
        }

        public double distanceSquaredAtRail(Block railBlock, Position pos) {
            if (pos.relative == this.relative) {
                return MathUtil.distanceSquared((double)this.posX, (double)this.posY, (double)this.posZ, (double)pos.posX, (double)pos.posY, (double)pos.posZ);
            }
            if (this.relative) {
                return MathUtil.distanceSquared((double)this.posX, (double)this.posY, (double)this.posZ, (double)(pos.posX - (double)railBlock.getX()), (double)(pos.posY - (double)railBlock.getY()), (double)(pos.posZ - (double)railBlock.getZ()));
            }
            return MathUtil.distanceSquared((double)(this.posX - (double)railBlock.getX()), (double)(this.posY - (double)railBlock.getY()), (double)(this.posZ - (double)railBlock.getZ()), (double)pos.posX, (double)pos.posY, (double)pos.posZ);
        }

        public double distanceSquared(Position pos) {
            if (pos.relative != this.relative) {
                throw new IllegalStateException("Self and pos must both be relative or both be absolute");
            }
            return MathUtil.distanceSquared((double)this.posX, (double)this.posY, (double)this.posZ, (double)pos.posX, (double)pos.posY, (double)pos.posZ);
        }

        public double distanceSquared(LocationAbstract pos) {
            if (this.relative) {
                throw new IllegalStateException("Self position must be absolute");
            }
            return pos.distanceSquared(this.posX, this.posY, this.posZ);
        }

        public Location toLocation(World world) {
            this.assertAbsolute();
            return new Location(world, this.posX, this.posY, this.posZ);
        }

        public Location toLocation(Block railsBlock) {
            if (this.relative) {
                return new Location(railsBlock.getWorld(), (double)railsBlock.getX() + this.posX, (double)railsBlock.getY() + this.posY, (double)railsBlock.getZ() + this.posZ);
            }
            return new Location(railsBlock.getWorld(), this.posX, this.posY, this.posZ);
        }

        public void getLocation(Location location) {
            this.assertAbsolute();
            location.setX(this.posX);
            location.setY(this.posY);
            location.setZ(this.posZ);
        }

        public void setLocation(Location location) {
            this.relative = false;
            this.posX = location.getX();
            this.posY = location.getY();
            this.posZ = location.getZ();
        }

        public void setLocation(LocationAbstract location) {
            this.relative = false;
            this.posX = location.getX();
            this.posY = location.getY();
            this.posZ = location.getZ();
        }

        public void setLocationMidOf(Block block) {
            this.relative = false;
            this.posX = (double)block.getX() + 0.5;
            this.posY = (double)block.getY() + 0.5;
            this.posZ = (double)block.getZ() + 0.5;
        }

        public BlockFace getMotionFace() {
            return Util.vecToFace(this.motX, this.motY, this.motZ, false);
        }

        public BlockFace getMotionFaceWithSubCardinal() {
            return Util.vecToFace(this.motX, this.motY, this.motZ, true);
        }

        public double motDot(Position pos) {
            return this.motX * pos.motX + this.motY * pos.motY + this.motZ * pos.motZ;
        }

        public double motDot(Vector v) {
            return this.motX * v.getX() + this.motY * v.getY() + this.motZ * v.getZ();
        }

        public double motDot(BlockFace face) {
            return this.motX * (double)face.getModX() + this.motY * (double)face.getModY() + this.motZ * (double)face.getModZ();
        }

        public double motDot(Point point) {
            return this.motX * point.x + this.motY * point.y + this.motZ * point.z;
        }

        public double motDot(double dx, double dy, double dz) {
            return this.motX * dx + this.motY * dy + this.motZ * dz;
        }

        public double motLength() {
            return Math.sqrt(this.motLengthSquared());
        }

        public double motLengthSquared() {
            return this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ;
        }

        public Vector getMotion() {
            return new Vector(this.motX, this.motY, this.motZ);
        }

        public Vector getMotion(Vector v) {
            v.setX(this.motX);
            v.setY(this.motY);
            v.setZ(this.motZ);
            return v;
        }

        public void setMotion(VectorAbstract movement) {
            this.motX = movement.getX();
            this.motY = movement.getY();
            this.motZ = movement.getZ();
        }

        public void setMotion(Vector movement) {
            if (Double.isNaN(movement.getX())) {
                throw new IllegalArgumentException("Motion vector is NaN");
            }
            this.motX = movement.getX();
            this.motY = movement.getY();
            this.motZ = movement.getZ();
        }

        public void setMotion(BlockFace movement) {
            this.motX = movement.getModX();
            this.motY = movement.getModY();
            this.motZ = movement.getModZ();
        }

        public void invertMotion() {
            this.motX = -this.motX;
            this.motY = -this.motY;
            this.motZ = -this.motZ;
        }

        public void normalizeMotion() {
            double n = MathUtil.getNormalizationFactor((double)this.motX, (double)this.motY, (double)this.motZ);
            if (Double.isInfinite(n)) {
                this.motX = 0.0;
                this.motY = -1.0;
                this.motZ = 0.0;
            } else {
                this.motX *= n;
                this.motY *= n;
                this.motZ *= n;
            }
        }

        public void copyTo(Position p) {
            p.posX = this.posX;
            p.posY = this.posY;
            p.posZ = this.posZ;
            p.motX = this.motX;
            p.motY = this.motY;
            p.motZ = this.motZ;
            p.wheelSegment = this.wheelSegment;
            p.wheelTheta = this.wheelTheta;
            p.reverse = this.reverse;
            p.relative = this.relative;
        }

        public Position clone() {
            Position p = new Position();
            this.copyTo(p);
            return p;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Position) {
                Position other = (Position)o;
                return this.posX == other.posX && this.posY == other.posY && this.posZ == other.posZ && this.motX == other.motX && this.motY == other.motY && this.motZ == other.motZ && this.relative == other.relative && this.reverse == other.reverse;
            }
            return false;
        }

        public String toString() {
            return (this.relative ? "{rel_pos={" : "{pos={") + MathUtil.round((double)this.posX, (int)4) + "/" + MathUtil.round((double)this.posY, (int)4) + "/" + MathUtil.round((double)this.posZ, (int)4) + "}, mot={" + MathUtil.round((double)this.motX, (int)4) + "/" + MathUtil.round((double)this.motY, (int)4) + "/" + MathUtil.round((double)this.motZ, (int)4) + "}, f=" + this.getMotionFace().name() + "}";
        }

        public static Position fromPosDir(Vector position, Vector direction) {
            Position p = new Position();
            p.relative = false;
            p.posX = position.getX();
            p.posY = position.getY();
            p.posZ = position.getZ();
            p.motX = direction.getX();
            p.motY = direction.getY();
            p.motZ = direction.getZ();
            return p;
        }

        public static Position fromTo(Location from, Location to) {
            Position p = new Position();
            p.relative = false;
            p.posX = from.getX();
            p.posY = from.getY();
            p.posZ = from.getZ();
            p.motX = to.getX() - p.posX;
            p.motY = to.getY() - p.posY;
            p.motZ = to.getZ() - p.posZ;
            return p;
        }

        public static Position fromLocation(Location positionWithDirection) {
            Position p = new Position();
            p.relative = false;
            p.posX = positionWithDirection.getX();
            p.posY = positionWithDirection.getY();
            p.posZ = positionWithDirection.getZ();
            Vector dir = positionWithDirection.getDirection();
            p.motX = dir.getX();
            p.motY = dir.getY();
            p.motZ = dir.getZ();
            return p;
        }
    }

    public static class ProximityInfo
    implements Comparable<ProximityInfo> {
        private static final double DIST_DIFF_SQ_THRESHOLD = 1.0E-6;
        public double distanceSquared = Double.MAX_VALUE;
        public boolean canMoveForward = false;

        @Override
        public int compareTo(ProximityInfo o) {
            double diffDistSq = this.distanceSquared - o.distanceSquared;
            if (diffDistSq > 1.0E-6) {
                return 1;
            }
            if (diffDistSq < -1.0E-6) {
                return -1;
            }
            return Boolean.compare(o.canMoveForward, this.canMoveForward);
        }
    }

    public static class Builder {
        private List<Point> points = new ArrayList<Point>(3);
        private double default_up_x = 0.0;
        private double default_up_y = 1.0;
        private double default_up_z = 0.0;

        public Builder up(BlockFace up) {
            return this.up(up.getModX(), up.getModY(), up.getModZ());
        }

        public Builder up(double up_x, double up_y, double up_z) {
            this.default_up_x = up_x;
            this.default_up_y = up_y;
            this.default_up_z = up_z;
            return this;
        }

        public Builder add(double x, double y, double z) {
            return this.add(new Point(x, y, z, this.default_up_x, this.default_up_y, this.default_up_z));
        }

        public Builder add(double x, double y, double z, double up_x, double up_y, double up_z) {
            return this.add(new Point(x, y, z, up_x, up_y, up_z));
        }

        public Builder add(double x, double y, double z, BlockFace face) {
            return this.add(new Point(x, y, z, face));
        }

        public Builder add(Vector point) {
            return this.add(new Point(point, this.default_up_x, this.default_up_y, this.default_up_z));
        }

        public Builder add(Vector point, double up_x, double up_y, double up_z) {
            return this.add(new Point(point, up_x, up_y, up_z));
        }

        public Builder add(Vector point, BlockFace face) {
            return this.add(new Point(point, face));
        }

        public Builder add(Point point) {
            this.points.add(point);
            return this;
        }

        public RailPath build() {
            return RailPath.create(this.points.toArray(new Point[this.points.size()]));
        }
    }
}

