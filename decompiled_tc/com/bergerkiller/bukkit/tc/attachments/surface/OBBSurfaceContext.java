/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntCuboid
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.surface;

import com.bergerkiller.bukkit.common.bases.IntCuboid;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

class OBBSurfaceContext {
    public final Vector position;
    public final Quaternion orientation;
    public final Vector normal;
    public final Vector halfSize;
    public final Vector playerPos;
    public final boolean isFullyClipped;
    public final boolean isWall;
    public final boolean isBackSide;
    public final Vector planeMin;
    public final Vector planeMax;
    public final IntCuboid cuboid;
    public BlockFace face;
    public Vector rayDir;
    public final Vector planePos = new Vector();
    public final Vector projectedPos = new Vector();
    public final Vector projectedPosOnPlane = new Vector();

    public OBBSurfaceContext(OrientedBoundingBox surfaceBbox, Vector playerPos, int playerViewRange) {
        Vector[] points;
        this.position = surfaceBbox.getPosition();
        this.halfSize = surfaceBbox.getSize().clone().multiply(0.5);
        this.orientation = surfaceBbox.getOrientation();
        this.normal = this.orientation.upVector();
        this.playerPos = playerPos;
        this.planeMin = new Vector(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        this.planeMax = new Vector(-1.7976931348623157E308, -1.7976931348623157E308, -1.7976931348623157E308);
        for (Vector p : points = new Vector[]{new Vector(-this.halfSize.getX(), 0.0, -this.halfSize.getZ()), new Vector(this.halfSize.getX(), 0.0, -this.halfSize.getZ()), new Vector(-this.halfSize.getX(), 0.0, this.halfSize.getZ()), new Vector(this.halfSize.getX(), 0.0, this.halfSize.getZ())}) {
            this.orientation.transformPoint(p);
            p.add(this.position);
            this.planeMin.setX(Math.min(this.planeMin.getX(), p.getX()));
            this.planeMin.setY(Math.min(this.planeMin.getY(), p.getY()));
            this.planeMin.setZ(Math.min(this.planeMin.getZ(), p.getZ()));
            this.planeMax.setX(Math.max(this.planeMax.getX(), p.getX()));
            this.planeMax.setY(Math.max(this.planeMax.getY(), p.getY()));
            this.planeMax.setZ(Math.max(this.planeMax.getZ(), p.getZ()));
        }
        boolean bl = this.isWall = Math.abs(this.normal.getY()) < 0.6;
        this.isBackSide = this.isWall ? playerPos.clone().subtract(this.position).dot(this.normal) < 0.0 : (playerPos.getY() > this.planeMax.getY() - 1.0 ? this.normal.getY() < 0.0 : (playerPos.getY() < this.planeMin.getY() ? this.normal.getY() > 0.0 : playerPos.clone().subtract(this.position).dot(this.normal) < -0.5));
        IntVector3 playerPosBlock = IntVector3.blockOf((Vector)playerPos);
        IntCuboid viewRangeBox = playerViewRange == Integer.MAX_VALUE ? IntCuboid.ALL : IntCuboid.create((IntVector3)playerPosBlock.subtract(playerViewRange, playerViewRange, playerViewRange), (IntVector3)playerPosBlock.add(playerViewRange + 1, playerViewRange + 1, playerViewRange + 1));
        IntVector3 cuboidMin = IntVector3.of((int)Math.max(this.planeMin.getBlockX(), viewRangeBox.min.x), (int)Math.max(this.planeMin.getBlockY(), viewRangeBox.min.y), (int)Math.max(this.planeMin.getBlockZ(), viewRangeBox.min.z));
        IntVector3 cuboidMax = IntVector3.of((int)Math.min(this.planeMax.getBlockX() + 1, viewRangeBox.max.x), (int)Math.min(this.planeMax.getBlockY() + 1, viewRangeBox.max.y), (int)Math.min(this.planeMax.getBlockZ() + 1, viewRangeBox.max.z));
        this.isFullyClipped = cuboidMax.x <= cuboidMin.x || cuboidMax.y <= cuboidMin.y || cuboidMax.z <= cuboidMin.z;
        this.cuboid = this.isFullyClipped ? IntCuboid.ZERO : IntCuboid.create((IntVector3)cuboidMin, (IntVector3)cuboidMax);
    }

    public void initProjector(BlockFace face) {
        this.face = face;
        this.rayDir = FaceUtil.faceToVector((BlockFace)face);
    }

    public boolean project(double originX, double originY, double originZ) {
        MathUtil.setVector((Vector)this.planePos, (double)(this.position.getX() - originX), (double)(this.position.getY() - originY), (double)(this.position.getZ() - originZ));
        double denom = this.rayDir.dot(this.normal);
        if (Math.abs(denom) < 1.0E-6) {
            return false;
        }
        double t = this.planePos.dot(this.normal) / denom;
        MathUtil.setVector((Vector)this.projectedPos, (double)(originX + this.rayDir.getX() * t), (double)(originY + this.rayDir.getY() * t), (double)(originZ + this.rayDir.getZ() * t));
        Vector projectedPosOnPlane = this.projectedPosOnPlane;
        MathUtil.setVector((Vector)projectedPosOnPlane, (Vector)this.projectedPos);
        projectedPosOnPlane.subtract(this.position);
        this.orientation.invTransformPoint(projectedPosOnPlane);
        return !(Math.abs(projectedPosOnPlane.getX()) - 0.5 > this.halfSize.getX()) && !(Math.abs(projectedPosOnPlane.getZ()) - 0.5 > this.halfSize.getZ());
    }
}

