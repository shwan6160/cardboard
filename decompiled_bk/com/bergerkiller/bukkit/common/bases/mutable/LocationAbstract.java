/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class LocationAbstract
extends VectorAbstract {
    public abstract World getWorld();

    public abstract LocationAbstract setWorld(World var1);

    @Override
    public abstract LocationAbstract setX(double var1);

    @Override
    public abstract LocationAbstract setY(double var1);

    @Override
    public abstract LocationAbstract setZ(double var1);

    public abstract float getYaw();

    public abstract float getPitch();

    public abstract LocationAbstract setYaw(float var1);

    public abstract LocationAbstract setPitch(float var1);

    public LocationAbstract setLocZero() {
        super.setZero();
        return this;
    }

    @Override
    public LocationAbstract setZero() {
        return this.setLocZero().setYaw(0.0f).setPitch(0.0f);
    }

    public LocationAbstract set(Location value) {
        this.set(value.getX(), value.getY(), value.getZ());
        return this.setWorld(value.getWorld()).setYaw(value.getYaw()).setPitch(value.getPitch());
    }

    public LocationAbstract set(LocationAbstract value) {
        this.set(value.getX(), value.getY(), value.getZ());
        return this.setWorld(value.getWorld()).setYaw(value.getYaw()).setPitch(value.getPitch());
    }

    @Override
    public LocationAbstract set(double x, double y, double z) {
        super.set(x, y, z);
        return this;
    }

    public LocationAbstract set(double x, double y, double z, float yaw, float pitch) {
        return this.set(x, y, z).setRotation(yaw, pitch);
    }

    public LocationAbstract setRotation(float yaw, float pitch) {
        return this.setYaw(yaw).setPitch(pitch);
    }

    public Location toLocation() {
        return new Location(this.getWorld(), this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
    }

    public Block toBlock() {
        return this.getWorld().getBlockAt(this.x.block(), this.y.block(), this.z.block());
    }

    public LocationAbstract addYaw(float yaw) {
        return this.setYaw(this.getYaw() + yaw);
    }

    public LocationAbstract addPitch(float pitch) {
        return this.setPitch(this.getPitch() + pitch);
    }

    public float getYawDifference(float yawcomparer) {
        return MathUtil.getAngleDifference(this.getYaw(), yawcomparer);
    }

    public float getYawDifference(LocationAbstract location) {
        return this.getYawDifference(location.getYaw());
    }

    public float getYawDifference(CommonEntity<?> entity) {
        return this.getYawDifference(entity.loc);
    }

    public float getPitchDifference(float pitchcomparer) {
        return MathUtil.getAngleDifference(this.getPitch(), pitchcomparer);
    }

    public float getPitchDifference(LocationAbstract location) {
        return this.getPitchDifference(location.getPitch());
    }

    public float getPitchDifference(CommonEntity<?> entity) {
        return this.getPitchDifference(entity.loc);
    }

    @Override
    public String toString() {
        World w = this.getWorld();
        return "{world=" + (w == null ? "null" : w.getName()) + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + "}";
    }
}

