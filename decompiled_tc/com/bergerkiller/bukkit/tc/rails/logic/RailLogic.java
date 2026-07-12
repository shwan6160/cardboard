/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.rails.logic;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public abstract class RailLogic {
    protected final boolean alongZ;
    protected final boolean alongX;
    protected final boolean alongY;
    protected final boolean curved;
    private final BlockFace horizontalDir;
    private RailPath railPath;

    public RailLogic(BlockFace horizontalDirection) {
        this.horizontalDir = horizontalDirection;
        this.alongX = FaceUtil.isAlongX((BlockFace)horizontalDirection);
        this.alongZ = FaceUtil.isAlongZ((BlockFace)horizontalDirection);
        this.alongY = FaceUtil.isAlongY((BlockFace)horizontalDirection);
        this.curved = !this.alongZ && !this.alongY && !this.alongX;
        this.railPath = null;
    }

    public BlockFace getDirection() {
        return this.horizontalDir;
    }

    public boolean isSloped() {
        return false;
    }

    public boolean hasVerticalMovement() {
        return false;
    }

    public boolean isUpsideDown() {
        return false;
    }

    public double getGravityMultiplier(MinecartMember<?> member) {
        return 0.015625;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "@" + this.getDirection();
    }

    public void onSpacingUpdate(MinecartMember<?> member, Vector velocity, Vector factor) {
        double motLen = velocity.length();
        if (motLen > 0.0) {
            double f = TCConfig.cartDistanceForcer * factor.dot(velocity);
            f = MathUtil.clamp((double)f, (double)-1.0, (double)1.0);
            velocity.multiply(f += 1.0);
        }
        if (TCConfig.cartDistanceForcerConstant > 0.0) {
            velocity.add(factor.clone().multiply(TCConfig.cartDistanceForcerConstant));
        }
    }

    public double getForwardVelocity(MinecartMember<?> member) {
        double dot;
        CommonEntity e = member.getEntity();
        RailPath.Segment segment = this.getPath().findSegment(((CommonMinecart)member.getEntity()).loc.vector(), member.getBlock());
        if (segment != null) {
            RailPath.Position pos = new RailPath.Position();
            pos.setMotion(member.getDirection());
            segment.calcDirection(pos);
            dot = pos.motDot(((CommonMinecart)member.getEntity()).vel.vector());
        } else {
            BlockFace direction = member.getDirection();
            dot = e.vel.getX() * FaceUtil.cos((BlockFace)direction) + e.vel.getY() * (double)direction.getModY() + e.vel.getZ() * FaceUtil.sin((BlockFace)direction);
        }
        return MathUtil.invert((double)e.vel.length(), (dot < 0.0 ? 1 : 0) != 0);
    }

    public void setForwardVelocity(MinecartMember<?> member, double force) {
        RailPath.Segment segment = this.getPath().findSegment(((CommonMinecart)member.getEntity()).loc.vector(), member.getBlock());
        if (segment != null) {
            RailPath.Position pos = new RailPath.Position();
            pos.setMotion(member.getRailTracker().getMotionVector());
            segment.calcDirection(pos);
            ((CommonMinecart)member.getEntity()).vel.set(pos.motX * force, pos.motY * force, pos.motZ * force);
            return;
        }
        CommonEntity e = member.getEntity();
        if (force == 0.0) {
            e.vel.setZero();
        } else if (!this.hasVerticalMovement() || !member.isMovingVerticalOnly()) {
            e.vel.setX(force * FaceUtil.cos((BlockFace)member.getDirection()));
            e.vel.setY(0.0);
            e.vel.setZ(force * FaceUtil.sin((BlockFace)member.getDirection()));
        } else {
            e.vel.set(0.0, force * (double)member.getDirection().getModY(), 0.0);
        }
    }

    @Deprecated
    public BlockFace getMovementDirection(BlockFace endDirection) {
        return endDirection;
    }

    public BlockFace getMovementDirection(Block railsBlock, Block positionBlock, BlockFace endDirection) {
        RailPath path = this.getPath();
        if (path.isEmpty()) {
            return endDirection;
        }
        RailPath.Position position = new RailPath.Position();
        position.setLocationMidOf(positionBlock);
        position.posX -= 0.5 * (double)endDirection.getModX();
        position.posY -= 0.5 * (double)endDirection.getModY();
        position.posZ -= 0.5 * (double)endDirection.getModZ();
        position.setMotion(endDirection);
        path.snap(position, railsBlock);
        return Util.vecToFace(position.motX, position.motY, position.motZ, true);
    }

    public void onPathAdjust(RailState state) {
    }

    public RailPath getPath() {
        if (this.railPath == null) {
            this.railPath = this.createPath();
        }
        return this.railPath;
    }

    protected RailPath createPath() {
        return RailPath.EMPTY;
    }

    public void onUpdateOrientation(MinecartMember<?> member, Quaternion orientation) {
        member.setOrientation(orientation);
    }

    public void onPreMove(MinecartMember<?> member) {
        member.snapToPath(this.getPath());
    }

    public void onPostMove(MinecartMember<?> member) {
    }

    public void onGravity(MinecartMember<?> member, double gravityFactorSquared) {
        CommonMinecart e = (CommonMinecart)member.getEntity();
        Block block = member.getRailTracker().getBlock();
        RailPath.Segment segment = this.getPath().findSegment(e.loc.vector(), block);
        if (segment == null) {
            e.vel.y.subtract(gravityFactorSquared * this.getGravityMultiplier(member));
        } else if (segment.has_vertical_slope) {
            double f = gravityFactorSquared * this.getGravityMultiplier(member) * segment.mot.getY();
            e.vel.subtract(segment.mot.getX() * f, segment.mot.getY() * f, segment.mot.getZ() * f);
        }
    }
}

