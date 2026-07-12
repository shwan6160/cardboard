/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.math.VectorList;
import com.bergerkiller.bukkit.common.math.VectorListMutable;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class OrientedBoundingBox {
    private final Vector position = new Vector();
    private final Vector radius = new Vector();
    private final Quaternion orientation = new Quaternion();
    private boolean is_orientation_set = false;
    private VectorList cachedVertices = null;

    public OrientedBoundingBox() {
    }

    public OrientedBoundingBox(Vector position, Vector size, Quaternion orientation) {
        this.setPosition(position);
        this.setSize(size);
        this.setOrientation(orientation);
    }

    public static OrientedBoundingBox naturalFromTo(Vector p1, Vector p2) {
        OrientedBoundingBox box = new OrientedBoundingBox();
        box.setPosition(p1.clone().add(p2).multiply(0.5));
        box.setSize(Math.abs(p2.getX() - p1.getX()), Math.abs(p2.getY() - p1.getY()), Math.abs(p2.getZ() - p1.getZ()));
        return box;
    }

    public static OrientedBoundingBox fromAABB(Block block, AABBHandle aabb) {
        OrientedBoundingBox box = OrientedBoundingBox.fromAABB(aabb);
        box.addPosition(block.getX(), block.getY(), block.getZ());
        return box;
    }

    public static OrientedBoundingBox fromAABB(Vector position, AABBHandle aabb) {
        OrientedBoundingBox box = OrientedBoundingBox.fromAABB(aabb);
        box.addPosition(position.getX(), position.getY(), position.getZ());
        return box;
    }

    public static OrientedBoundingBox fromAABB(AABBHandle aabb) {
        OrientedBoundingBox box = new OrientedBoundingBox();
        box.setPosition(0.5 * (aabb.getMinX() + aabb.getMaxX()), 0.5 * (aabb.getMinY() + aabb.getMaxY()), 0.5 * (aabb.getMinZ() + aabb.getMaxZ()));
        box.setSize(aabb.getMaxX() - aabb.getMinX(), aabb.getMaxY() - aabb.getMinY(), aabb.getMaxZ() - aabb.getMinZ());
        return box;
    }

    public Vector getPosition() {
        return this.position.clone();
    }

    public Vector getSize() {
        return this.radius.clone().multiply(2.0);
    }

    public Quaternion getOrientation() {
        return this.orientation;
    }

    public void setPosition(Vector pos) {
        MathUtil.setVector(this.position, pos);
        this.cachedVertices = null;
    }

    public void setPosition(double x, double y, double z) {
        MathUtil.setVector(this.position, x, y, z);
        this.cachedVertices = null;
    }

    public void addPosition(double dx, double dy, double dz) {
        MathUtil.addToVector(this.position, dx, dy, dz);
        this.cachedVertices = null;
    }

    public void setSize(Vector size) {
        this.setSize(size.getX(), size.getY(), size.getZ());
    }

    public void setSize(double sx, double sy, double sz) {
        MathUtil.setVector(this.radius, 0.5 * sx, 0.5 * sy, 0.5 * sz);
        this.cachedVertices = null;
    }

    public void setOrientation(Quaternion orientation) {
        if (orientation == null) {
            this.is_orientation_set = false;
            this.orientation.setIdentity();
        } else {
            this.is_orientation_set = true;
            this.orientation.setTo(orientation);
        }
        this.cachedVertices = null;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OrientedBoundingBox) {
            OrientedBoundingBox other = (OrientedBoundingBox)o;
            return this.orientation.equals(other.orientation) && this.position.equals((Object)other.position) && this.radius.equals((Object)other.radius);
        }
        return false;
    }

    public String toString() {
        return "OBB{pos=" + this.position + ", size=" + this.radius.clone().multiply(2.0) + ", ori=" + this.orientation + "}";
    }

    public boolean isInside(Vector point) {
        return this.isInside(point.getX(), point.getY(), point.getZ());
    }

    public boolean isInside(double x, double y, double z) {
        Vector localPoint = new Vector(x, y, z).subtract(this.position);
        if (this.is_orientation_set) {
            this.orientation.invTransformPoint(localPoint);
        }
        return this.testLocalPointInside(localPoint.getX(), localPoint.getY(), localPoint.getZ());
    }

    public HitTestResult distanceToPoint(Vector point) {
        Vector localPoint = point.clone().subtract(this.position);
        if (this.is_orientation_set) {
            this.orientation.invTransformPoint(localPoint);
        }
        Vector rad = this.radius;
        Vector closestPoint = new Vector(MathUtil.clamp(localPoint.getX(), -rad.getX(), rad.getX()), MathUtil.clamp(localPoint.getY(), -rad.getY(), rad.getY()), MathUtil.clamp(localPoint.getZ(), -rad.getZ(), rad.getZ()));
        if (localPoint.equals((Object)closestPoint)) {
            MathUtil.setVector(closestPoint, point);
            return HitTestResult.inside(closestPoint);
        }
        Vector normal = new Vector();
        double dx = Math.abs(closestPoint.getX() - localPoint.getX());
        double dy = Math.abs(closestPoint.getY() - localPoint.getY());
        double dz = Math.abs(closestPoint.getZ() - localPoint.getZ());
        if (dx > dy && dx > dz) {
            normal.setX(Math.signum(localPoint.getX()));
        } else if (dy > dz) {
            normal.setY(Math.signum(localPoint.getY()));
        } else {
            normal.setZ(Math.signum(localPoint.getZ()));
        }
        if (this.is_orientation_set) {
            this.orientation.transformPoint(closestPoint);
            this.orientation.transformPoint(normal);
        }
        closestPoint.add(this.position);
        return new HitTestResult(closestPoint, normal, point.distance(closestPoint));
    }

    public double hitTest(double startX, double startY, double startZ, double dirX, double dirY, double dirZ) {
        return this.performHitTest(startX, startY, startZ, dirX, dirY, dirZ).distance();
    }

    public double hitTest(Vector startPosition, Vector startDirection) {
        return this.performHitTest(startPosition, startDirection).distance();
    }

    public double hitTest(Location eyeLocation) {
        return this.performHitTest(eyeLocation).distance();
    }

    public HitTestResult performHitTest(double startX, double startY, double startZ, double dirX, double dirY, double dirZ) {
        double pz;
        double py;
        if (this.is_orientation_set) {
            return this.performHitTest(new Vector(startX, startY, startZ), new Vector(dirX, dirY, dirZ));
        }
        Vector midPos = this.position;
        double px = startX - midPos.getX();
        if (this.testLocalPointInside(px, py = startY - midPos.getY(), pz = startZ - midPos.getZ())) {
            return HitTestResult.inside(new Vector(startX, startY, startZ));
        }
        HitTestResult result = this.hitTestBase(px, py, pz, dirX, dirY, dirZ);
        if (result.success()) {
            if (result.inside()) {
                result.position.setX(startX);
                result.position.setY(startY);
                result.position.setZ(startZ);
            } else {
                result.position.setX(startX + result.distance() * dirX);
                result.position.setY(startY + result.distance() * dirY);
                result.position.setZ(startZ + result.distance() * dirZ);
                if (this.is_orientation_set) {
                    this.orientation.transformPoint(result.normal);
                }
            }
        }
        return result;
    }

    public HitTestResult performHitTest(Vector startPosition, Vector startDirection) {
        HitTestResult result;
        Vector p = startPosition.clone().subtract(this.position);
        if (this.is_orientation_set) {
            this.orientation.invTransformPoint(p);
        }
        if (this.testLocalPointInside(p.getX(), p.getY(), p.getZ())) {
            return HitTestResult.inside(startPosition);
        }
        Vector d = startDirection;
        if (this.is_orientation_set) {
            d = d.clone();
            this.orientation.invTransformPoint(d);
        }
        if ((result = this.hitTestBase(p, d)).success()) {
            if (result.inside()) {
                result.position.copy(startPosition);
            } else {
                result.position.setX(startPosition.getX() + result.distance() * startDirection.getX());
                result.position.setY(startPosition.getY() + result.distance() * startDirection.getY());
                result.position.setZ(startPosition.getZ() + result.distance() * startDirection.getZ());
                if (this.is_orientation_set) {
                    this.orientation.transformPoint(result.normal);
                }
            }
        }
        return result;
    }

    public HitTestResult performHitTest(Location eyeLocation) {
        return this.performHitTest(eyeLocation.toVector(), eyeLocation.getDirection());
    }

    private HitTestResult hitTestBase(Vector localPos, Vector localDir) {
        return this.hitTestBase(localPos.getX(), localPos.getY(), localPos.getZ(), localDir.getX(), localDir.getY(), localDir.getZ());
    }

    private HitTestResult hitTestBase(double localPosX, double localPosY, double localPosZ, double localDirX, double localDirY, double localDirZ) {
        double ERR = 1.0E-6;
        double min_distance = Double.MAX_VALUE;
        BlockFace min_dir = null;
        Vector rad = this.radius;
        for (BlockFace dir : FaceUtil.BLOCK_SIDES) {
            double f;
            double c;
            double b;
            double a;
            if (dir.getModX() != 0) {
                a = rad.getX() * (double)dir.getModX();
                b = localPosX;
                c = localDirX;
            } else if (dir.getModY() != 0) {
                a = rad.getY() * (double)dir.getModY();
                b = localPosY;
                c = localDirY;
            } else {
                a = rad.getZ() * (double)dir.getModZ();
                b = localPosZ;
                c = localDirZ;
            }
            if (c == 0.0 || (f = (a - b) / c) < 0.0 || f > min_distance || Math.abs(localPosX + f * localDirX) - rad.getX() > 1.0E-6 || Math.abs(localPosY + f * localDirY) - rad.getY() > 1.0E-6 || Math.abs(localPosZ + f * localDirZ) - rad.getZ() > 1.0E-6) continue;
            min_distance = f;
            min_dir = dir;
        }
        if (min_distance == Double.MAX_VALUE) {
            return HitTestResult.MISSED;
        }
        if (min_distance <= 0.0) {
            return HitTestResult.inside(new Vector());
        }
        return new HitTestResult(new Vector(), FaceUtil.faceToVector(min_dir), min_distance);
    }

    private boolean testLocalPointInside(double x, double y, double z) {
        Vector rad = this.radius;
        return Math.abs(x) <= rad.getX() && Math.abs(y) <= rad.getY() && Math.abs(z) <= rad.getZ();
    }

    public boolean hasOverlap(OrientedBoundingBox other) {
        return VectorList.areVerticesOverlapping(this.getVertices(), other.getVertices(), OrientedBoundingBox.createSeparatingAxisIterator(this.getOrientation(), other.getOrientation()));
    }

    public boolean isInside(OrientedBoundingBox other) {
        VectorList.VectorIterator iter = other.getVertices().vectorIterator();
        while (iter.advance()) {
            if (this.isInside(iter.x(), iter.y(), iter.z())) continue;
            return false;
        }
        return true;
    }

    public VectorList getVertices() {
        VectorList cached = this.cachedVertices;
        if (cached == null) {
            VectorListMutable box = VectorListMutable.createBoxVerticesWithHalfSize(this.radius);
            if (this.is_orientation_set) {
                box.rotate(this.orientation);
            }
            box.translate(this.position);
            this.cachedVertices = cached = box.immutable();
        }
        return cached;
    }

    public static VectorList.VectorIterator createSeparatingAxisIterator(Quaternion box1Ori, Quaternion box2Ori) {
        Vector box2Forward;
        Vector box2Up;
        Vector box2Right;
        boolean isMisaligned = !box1Ori.equals(box2Ori);
        VectorListMutable all = VectorListMutable.create(isMisaligned ? 15 : 9);
        VectorListMutable.MutableVectorIterator consumer = all.vectorIterator();
        Vector box1Right = box1Ori.rightVector();
        Vector box1Up = box1Ori.upVector();
        Vector box1Forward = box1Ori.forwardVector();
        if (isMisaligned) {
            box2Right = box2Ori.rightVector();
            box2Up = box2Ori.upVector();
            box2Forward = box2Ori.forwardVector();
        } else {
            box2Right = box1Right;
            box2Up = box1Up;
            box2Forward = box1Forward;
        }
        consumer.acceptVector(box1Right);
        consumer.acceptVector(box1Up);
        consumer.acceptVector(box1Forward);
        if (isMisaligned) {
            consumer.acceptVector(box2Right);
            consumer.acceptVector(box2Up);
            consumer.acceptVector(box2Forward);
            OrientedBoundingBox.tryCrossProduct(box1Right, box2Right, consumer);
            OrientedBoundingBox.tryCrossProduct(box1Up, box2Up, consumer);
            OrientedBoundingBox.tryCrossProduct(box1Forward, box2Forward, consumer);
        }
        OrientedBoundingBox.tryCrossProduct(box1Right, box2Up, consumer);
        OrientedBoundingBox.tryCrossProduct(box1Right, box2Forward, consumer);
        OrientedBoundingBox.tryCrossProduct(box1Up, box2Forward, consumer);
        OrientedBoundingBox.tryCrossProduct(box1Up, box2Right, consumer);
        OrientedBoundingBox.tryCrossProduct(box1Forward, box2Up, consumer);
        OrientedBoundingBox.tryCrossProduct(box1Forward, box2Right, consumer);
        return all.vectorIterator(0, consumer.index() + 1);
    }

    private static void tryCrossProduct(Vector left, Vector right, VectorList.VectorConsumer consumer) {
        double z;
        double y;
        double x = left.getY() * right.getZ() - right.getY() * left.getZ();
        double lengthSquared = x * x + (y = left.getZ() * right.getX() - right.getZ() * left.getX()) * y + (z = left.getX() * right.getY() - right.getX() * left.getY()) * z;
        if (lengthSquared >= 1.0E-6) {
            double normFactor = MathUtil.getNormalizationFactorLS(lengthSquared);
            consumer.acceptVector(x * normFactor, y * normFactor, z * normFactor);
        }
    }

    public static class HitTestResult {
        public static final HitTestResult MISSED = new HitTestResult(null, null, Double.MAX_VALUE);
        private final Vector position;
        private final Vector normal;
        private final double distance;

        public HitTestResult(Vector position, Vector normal, double distance) {
            this.position = position;
            this.normal = normal;
            this.distance = distance;
        }

        public boolean success() {
            return this.distance != Double.MAX_VALUE;
        }

        public boolean inside() {
            return this.distance == 0.0;
        }

        public Vector position() {
            return this.position;
        }

        public Vector normal() {
            return this.normal;
        }

        public double distance() {
            return this.distance;
        }

        public static HitTestResult inside(Vector position) {
            return new HitTestResult(position, new Vector(), 0.0);
        }
    }
}

