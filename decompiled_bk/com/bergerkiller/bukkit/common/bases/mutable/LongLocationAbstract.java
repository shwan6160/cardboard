/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.bases.mutable.LongVectorAbstract;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class LongLocationAbstract
extends LongVectorAbstract {
    public abstract World getWorld();

    public abstract LongLocationAbstract setWorld(World var1);

    @Override
    public abstract LongLocationAbstract setX(long var1);

    @Override
    public abstract LongLocationAbstract setY(long var1);

    @Override
    public abstract LongLocationAbstract setZ(long var1);

    public abstract int getYaw();

    public abstract int getPitch();

    public abstract LongLocationAbstract setYaw(int var1);

    public abstract LongLocationAbstract setPitch(int var1);

    public LongLocationAbstract setLocZero() {
        super.setZero();
        return this;
    }

    @Override
    public LongLocationAbstract setZero() {
        return this.setLocZero().setYaw(0).setPitch(0);
    }

    public LongLocationAbstract set(LongLocationAbstract value) {
        super.set(value.getX(), value.getY(), value.getZ());
        return this.setWorld(value.getWorld()).setYaw(value.getYaw()).setPitch(value.getPitch());
    }

    @Override
    public LongLocationAbstract set(long x, long y, long z) {
        super.set(x, y, z);
        return this;
    }

    public LongLocationAbstract set(long x, long y, long z, int yaw, int pitch) {
        return this.set(x, y, z).setRotation(yaw, pitch);
    }

    public LongLocationAbstract setRotation(int yaw, int pitch) {
        return this.setYaw(yaw).setPitch(pitch);
    }

    public Location toLocation() {
        return new Location(this.getWorld(), (double)this.getX(), (double)this.getY(), (double)this.getZ(), (float)this.getYaw(), (float)this.getPitch());
    }

    public LongLocationAbstract addYaw(int yaw) {
        return this.setYaw(this.getYaw() + yaw);
    }

    public LongLocationAbstract addPitch(int pitch) {
        return this.setPitch(this.getPitch() + pitch);
    }

    public float getYawDifference(int yawcomparer) {
        return MathUtil.getAngleDifference(this.getYaw(), yawcomparer);
    }

    public float getYawDifference(LongLocationAbstract location) {
        return this.getYawDifference(location.getYaw());
    }

    public float getPitchDifference(int pitchcomparer) {
        return MathUtil.getAngleDifference(this.getPitch(), pitchcomparer);
    }

    public float getPitchDifference(LongLocationAbstract location) {
        return this.getPitchDifference(location.getPitch());
    }

    @Override
    public String toString() {
        World w = this.getWorld();
        return "{world=" + (w == null ? "null" : w.getName()) + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + "}";
    }
}

