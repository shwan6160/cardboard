/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
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
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class RailLogicGround
extends RailLogic {
    public static final RailLogicGround INSTANCE = new RailLogicGround();

    private RailLogicGround() {
        super(BlockFace.SELF);
    }

    @Override
    public double getGravityMultiplier(MinecartMember<?> member) {
        return 0.04;
    }

    @Override
    public void onSpacingUpdate(MinecartMember<?> member, Vector velocity, Vector factor) {
        double motLen = velocity.length();
        if (motLen > 0.01) {
            double f = motLen / ((CommonMinecart)member.getEntity()).getMaxSpeed();
            velocity.setX(velocity.getX() + f * factor.getX() * TCConfig.cartDistanceForcer);
            velocity.setZ(velocity.getZ() + f * factor.getZ() * TCConfig.cartDistanceForcer);
        }
    }

    @Override
    public void onGravity(MinecartMember<?> member, double gravityFactorSquared) {
        CommonMinecart e = (CommonMinecart)member.getEntity();
        e.vel.y.subtract(gravityFactorSquared * this.getGravityMultiplier(member));
    }

    @Override
    public double getForwardVelocity(MinecartMember<?> member) {
        CommonEntity e = member.getEntity();
        BlockFace direction = member.getDirection();
        double vel = 0.0;
        vel += e.vel.getX() * FaceUtil.cos((BlockFace)direction);
        return vel += e.vel.getZ() * FaceUtil.sin((BlockFace)direction);
    }

    @Override
    public void setForwardVelocity(MinecartMember<?> member, double force) {
        CommonEntity e = member.getEntity();
        if (e.vel.getY() > 0.0) {
            Vector vel = e.vel.vector();
            MathUtil.setVectorLength((Vector)vel, (double)force);
            e.vel.set(vel);
        } else {
            e.vel.set(force * FaceUtil.cos((BlockFace)member.getDirection()), e.vel.getY(), force * FaceUtil.sin((BlockFace)member.getDirection()));
        }
    }

    @Override
    public void onUpdateOrientation(MinecartMember<?> member, Quaternion orientation) {
        boolean upsideDown;
        float oldyaw;
        CommonMinecart entity = (CommonMinecart)member.getEntity();
        double movedX = entity.getMovedX();
        double movedZ = entity.getMovedZ();
        float newyaw = oldyaw = entity.loc.getYaw();
        float newpitch = entity.loc.getPitch();
        boolean bl = upsideDown = newpitch <= -91.0f || newpitch >= 91.0f;
        if (Math.abs(movedX) > 0.01 || Math.abs(movedZ) > 0.01) {
            newyaw = MathUtil.getLookAtYaw((double)movedX, (double)movedZ);
        }
        if (upsideDown) {
            newpitch = MathUtil.wrapAngle((float)(newpitch + 180.0f));
        }
        newpitch = (double)Math.abs(newpitch) > 0.1 ? (float)((double)newpitch * 0.1) : 0.0f;
        if (upsideDown) {
            newpitch += 180.0f;
        }
        member.setRotationWrap(newyaw, newpitch);
    }

    @Override
    public BlockFace getMovementDirection(BlockFace endDirection) {
        return endDirection;
    }

    @Override
    public boolean hasVerticalMovement() {
        return true;
    }

    @Override
    public void onPreMove(MinecartMember<?> member) {
        if (!member.isMovementControlled()) {
            ((CommonMinecart)member.getEntity()).vel.multiply(((CommonMinecart)member.getEntity()).getDerailedVelocityMod());
        }
    }
}

