/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.rails.logic;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.standard.type.SlowdownMode;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class RailLogicAir
extends RailLogic {
    public static final RailLogicAir INSTANCE = new RailLogicAir();

    private RailLogicAir() {
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
            if (member.isMovingVerticalOnly() || member != member.getGroup().head() && member != member.getGroup().head()) {
                velocity.setY(velocity.getY() + f * factor.getY() * TCConfig.cartDistanceForcer);
            }
        }
    }

    @Override
    public void onUpdateOrientation(MinecartMember<?> member, Quaternion orientation) {
        CommonMinecart entity = (CommonMinecart)member.getEntity();
        Vector forward = new Vector(entity.getMovedX(), entity.getMovedY(), entity.getMovedZ());
        if (member.getGroup().size() > 1) {
            MinecartMember<?> m;
            boolean has_delta = false;
            double dx = 0.0;
            double dy = 0.0;
            double dz = 0.0;
            if (member != member.getGroup().head() && (m = member.getNeighbour(-1)).isDerailed()) {
                dx += ((CommonMinecart)m.getEntity()).loc.getX() - ((CommonMinecart)member.getEntity()).loc.getX();
                dy += ((CommonMinecart)m.getEntity()).loc.getY() - ((CommonMinecart)member.getEntity()).loc.getY();
                dz += ((CommonMinecart)m.getEntity()).loc.getZ() - ((CommonMinecart)member.getEntity()).loc.getZ();
                has_delta = true;
            }
            if (member != member.getGroup().tail() && (m = member.getNeighbour(1)).isDerailed()) {
                dx += ((CommonMinecart)member.getEntity()).loc.getX() - ((CommonMinecart)m.getEntity()).loc.getX();
                dy += ((CommonMinecart)member.getEntity()).loc.getY() - ((CommonMinecart)m.getEntity()).loc.getY();
                dz += ((CommonMinecart)member.getEntity()).loc.getZ() - ((CommonMinecart)m.getEntity()).loc.getZ();
                has_delta = true;
            }
            if (has_delta) {
                forward.setX(dx);
                forward.setY(dy);
                forward.setZ(dz);
            }
        } else if (!member.getGroup().getProperties().isSlowingDown(SlowdownMode.GRAVITY)) {
            forward.multiply(0.0);
        }
        if (forward.lengthSquared() <= 1.0E-8) {
            member.setOrientation(orientation);
        } else {
            if (forward.dot(orientation.forwardVector()) < 0.0) {
                forward.multiply(-1.0);
            }
            member.setOrientation(Quaternion.fromLookDirection((Vector)forward, (Vector)orientation.upVector()));
        }
    }

    public void onUpdateOrientation_old(MinecartMember<?> member, Quaternion orientation) {
        CommonMinecart entity = (CommonMinecart)member.getEntity();
        boolean upsideDown = MathUtil.getAngleDifference((float)entity.loc.getPitch(), (float)180.0f) < 89.0f;
        float newYaw = ((CommonMinecart)member.getEntity()).loc.getYaw();
        float newPitch = ((CommonMinecart)member.getEntity()).loc.getPitch();
        if (member.getGroup().size() <= 1) {
            boolean movedXZ;
            double movedX = entity.getMovedX();
            double movedY = entity.getMovedY();
            double movedZ = entity.getMovedZ();
            boolean bl = movedXZ = Math.abs(movedX) > 0.001 || Math.abs(movedZ) > 0.001;
            if (Math.abs(movedX) > 0.01 || Math.abs(movedZ) > 0.01) {
                newYaw = MathUtil.getLookAtYaw((double)movedX, (double)movedZ);
            }
            if (movedXZ && Math.abs(movedY) > 0.001) {
                newPitch = MathUtil.clamp((float)(-MathUtil.getLookAtPitch((double)(-movedX), (double)(-movedY), (double)(-movedZ))), (float)89.9f);
                if (upsideDown) {
                    newPitch += 180.0f;
                }
            }
        } else {
            MinecartMember<?> m;
            int n = 0;
            double dx = 0.0;
            double dy = 0.0;
            double dz = 0.0;
            if (member != member.getGroup().head()) {
                m = member.getNeighbour(-1);
                dx += ((CommonMinecart)m.getEntity()).loc.getX() - ((CommonMinecart)member.getEntity()).loc.getX();
                dy += ((CommonMinecart)m.getEntity()).loc.getY() - ((CommonMinecart)member.getEntity()).loc.getY();
                dz += ((CommonMinecart)m.getEntity()).loc.getZ() - ((CommonMinecart)member.getEntity()).loc.getZ();
                ++n;
            }
            if (member != member.getGroup().tail()) {
                m = member.getNeighbour(1);
                dx += ((CommonMinecart)member.getEntity()).loc.getX() - ((CommonMinecart)m.getEntity()).loc.getX();
                dy += ((CommonMinecart)member.getEntity()).loc.getY() - ((CommonMinecart)m.getEntity()).loc.getY();
                dz += ((CommonMinecart)member.getEntity()).loc.getZ() - ((CommonMinecart)m.getEntity()).loc.getZ();
                ++n;
            }
            dy /= (double)n;
            double[] dArray = new double[]{dx /= (double)n, dz /= (double)n};
            if (MathUtil.lengthSquared((double[])dArray) < 1.0E-4) {
                newPitch = MathUtil.getAngleDifference((float)newPitch, (float)90.0f) < MathUtil.getAngleDifference((float)newPitch, (float)-90.0f) ? 90.0f : -90.0f;
            } else {
                newYaw = MathUtil.getLookAtYaw((double)dx, (double)dz);
                newPitch = MathUtil.getLookAtPitch((double)dx, (double)dy, (double)dz);
            }
            if (upsideDown) {
                newPitch += 180.0f;
            }
        }
        member.setRotationWrap(newYaw, newPitch);
    }

    @Override
    public BlockFace getMovementDirection(BlockFace endDirection) {
        return endDirection;
    }

    @Override
    public double getForwardVelocity(MinecartMember<?> member) {
        CommonEntity e = member.getEntity();
        if (((CommonMinecart)member.getEntity()).vel.xz.lengthSquared() == 0.0) {
            double dot = e.vel.getY() * (double)member.getDirection().getModY();
            return MathUtil.invert((double)e.vel.length(), (dot < 0.0 ? 1 : 0) != 0);
        }
        return e.vel.length();
    }

    @Override
    public void setForwardVelocity(MinecartMember<?> member, double force) {
        if (member.isMovementControlled()) {
            super.setForwardVelocity(member, force);
        } else if (((CommonMinecart)member.getEntity()).vel.xz.lengthSquared() == 0.0) {
            Vector vel = ((CommonMinecart)member.getEntity()).vel.vector();
            MathUtil.setVectorLength((Vector)vel, (double)force);
            ((CommonMinecart)member.getEntity()).vel.set(vel);
        } else {
            Vector vel = ((CommonMinecart)member.getEntity()).vel.vector();
            MathUtil.setVectorLength((Vector)vel, (double)force);
            ((CommonMinecart)member.getEntity()).vel.set(vel);
        }
    }

    @Override
    public boolean hasVerticalMovement() {
        return true;
    }

    @Override
    public void onPreMove(MinecartMember<?> member) {
        CommonMinecart entity = (CommonMinecart)member.getEntity();
        TrainProperties trainProp = member.getGroup().getProperties();
        if (!member.isMovementControlled() && trainProp.isSlowingDown(SlowdownMode.FRICTION)) {
            Vector flyingMod = entity.getFlyingVelocityMod();
            if (member.getGroup().getUpdateStepCount() > 1) {
                double factor = member.getGroup().getUpdateSpeedFactor();
                flyingMod = new Vector(Math.pow(flyingMod.getX(), factor), Math.pow(flyingMod.getY(), factor), Math.pow(flyingMod.getZ(), factor));
            }
            entity.vel.multiply(flyingMod);
        }
    }
}

