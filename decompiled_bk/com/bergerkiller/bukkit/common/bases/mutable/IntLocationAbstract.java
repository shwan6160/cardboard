/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.bases.mutable.IntVectorAbstract;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class IntLocationAbstract
extends IntVectorAbstract {
    public abstract World getWorld();

    public abstract IntLocationAbstract setWorld(World var1);

    @Override
    public abstract IntLocationAbstract setX(int var1);

    @Override
    public abstract IntLocationAbstract setY(int var1);

    @Override
    public abstract IntLocationAbstract setZ(int var1);

    public abstract int getYaw();

    public abstract int getPitch();

    public abstract IntLocationAbstract setYaw(int var1);

    public abstract IntLocationAbstract setPitch(int var1);

    public IntLocationAbstract setLocZero() {
        super.setZero();
        return this;
    }

    @Override
    public IntLocationAbstract setZero() {
        return this.setLocZero().setYaw(0).setPitch(0);
    }

    public IntLocationAbstract set(IntLocationAbstract value) {
        super.set(value.getX(), value.getY(), value.getZ());
        return this.setWorld(value.getWorld()).setYaw(value.getYaw()).setPitch(value.getPitch());
    }

    @Override
    public IntLocationAbstract set(int x, int y, int z) {
        super.set(x, y, z);
        return this;
    }

    public IntLocationAbstract set(int x, int y, int z, int yaw, int pitch) {
        return this.set(x, y, z).setRotation(yaw, pitch);
    }

    public IntLocationAbstract setRotation(int yaw, int pitch) {
        return this.setYaw(yaw).setPitch(pitch);
    }

    public Location toLocation() {
        return new Location(this.getWorld(), (double)this.getX(), (double)this.getY(), (double)this.getZ(), (float)this.getYaw(), (float)this.getPitch());
    }

    public IntLocationAbstract addYaw(int yaw) {
        return this.setYaw(this.getYaw() + yaw);
    }

    public IntLocationAbstract addPitch(int pitch) {
        return this.setPitch(this.getPitch() + pitch);
    }

    public float getYawDifference(int yawcomparer) {
        return MathUtil.getAngleDifference(this.getYaw(), yawcomparer);
    }

    public float getYawDifference(IntLocationAbstract location) {
        return this.getYawDifference(location.getYaw());
    }

    public float getPitchDifference(int pitchcomparer) {
        return MathUtil.getAngleDifference(this.getPitch(), pitchcomparer);
    }

    public float getPitchDifference(IntLocationAbstract location) {
        return this.getPitchDifference(location.getPitch());
    }

    @Override
    public String toString() {
        World w = this.getWorld();
        return "{world=" + (w == null ? "null" : w.getName()) + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + "}";
    }
}

