/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.bases.mutable.LongAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public abstract class LongVectorAbstract {
    public final LongAbstract x = new LongAbstract(){

        @Override
        public long get() {
            return LongVectorAbstract.this.getX();
        }

        @Override
        public LongAbstract set(long value) {
            LongVectorAbstract.this.setX(value);
            return this;
        }
    };
    public final LongAbstract y = new LongAbstract(){

        @Override
        public long get() {
            return LongVectorAbstract.this.getY();
        }

        @Override
        public LongAbstract set(long value) {
            LongVectorAbstract.this.setY(value);
            return this;
        }
    };
    public final LongAbstract z = new LongAbstract(){

        @Override
        public long get() {
            return LongVectorAbstract.this.getZ();
        }

        @Override
        public LongAbstract set(long value) {
            LongVectorAbstract.this.setZ(value);
            return this;
        }
    };

    public abstract long getX();

    public abstract long getY();

    public abstract long getZ();

    public abstract LongVectorAbstract setX(long var1);

    public abstract LongVectorAbstract setY(long var1);

    public abstract LongVectorAbstract setZ(long var1);

    public LongVectorAbstract setZero() {
        return this.setX(0L).setY(0L).setZ(0L);
    }

    public LongVectorAbstract set(long x, long y, long z) {
        return this.setX(x).setY(y).setZ(z);
    }

    public LongVectorAbstract set(LongVectorAbstract value) {
        return this.set(value.getX(), value.getY(), value.getZ());
    }

    public LongVectorAbstract set(IntVector3 value) {
        return this.set(value.x, value.y, value.z);
    }

    public IntVector3 vector() {
        return new IntVector3(this.getX(), this.getY(), this.getZ());
    }

    public LongVectorAbstract add(long x, long y, long z) {
        return this.setX(this.getX() + x).setY(this.getY() + y).setZ(this.getZ() + z);
    }

    public LongVectorAbstract add(IntVector3 vector) {
        return this.add(vector.x, vector.y, vector.z);
    }

    public LongVectorAbstract add(LongVectorAbstract value) {
        return this.add(value.getX(), value.getY(), value.getZ());
    }

    public LongVectorAbstract add(BlockFace face, long length) {
        return this.add(length * (long)face.getModX(), length * (long)face.getModY(), length * (long)face.getModZ());
    }

    public LongVectorAbstract add(LongVectorAbstract value, long length) {
        return this.add(length * value.getX(), length * value.getY(), length * value.getZ());
    }

    public LongVectorAbstract subtract(long x, long y, long z) {
        return this.setX(this.getX() - x).setY(this.getY() - y).setZ(this.getZ() - z);
    }

    public LongVectorAbstract subtract(IntVector3 vector) {
        return this.subtract(vector.x, vector.y, vector.z);
    }

    public LongVectorAbstract subtract(LongVectorAbstract value) {
        return this.subtract(value.getX(), value.getY(), value.getZ());
    }

    public LongVectorAbstract subtract(BlockFace face, long length) {
        return this.subtract(length * (long)face.getModX(), length * (long)face.getModY(), length * (long)face.getModZ());
    }

    public LongVectorAbstract subtract(LongVectorAbstract value, long length) {
        return this.subtract(length * value.getX(), length * value.getY(), length * value.getZ());
    }

    public LongVectorAbstract multiply(long mx, long my, long mz) {
        return this.setX(this.getX() * mx).setY(this.getY() * my).setZ(this.getZ() * mz);
    }

    public LongVectorAbstract multiply(IntVector3 vector) {
        return this.multiply(vector.x, vector.y, vector.z);
    }

    public LongVectorAbstract divide(long dx, long dy, long dz) {
        return this.setX(this.getX() / dx).setY(this.getY() / dy).setZ(this.getZ() / dz);
    }

    public LongVectorAbstract divide(IntVector3 vector) {
        return this.divide(vector.x, vector.y, vector.z);
    }

    public LongVectorAbstract multiply(long factor) {
        return this.multiply(factor, factor, factor);
    }

    public LongVectorAbstract divide(long factor) {
        return this.divide(factor, factor, factor);
    }

    public double length() {
        return MathUtil.length(this.getX(), this.getY(), this.getZ());
    }

    public double lengthSquared() {
        return MathUtil.lengthSquared(this.getX(), this.getY(), this.getZ());
    }

    public double distance(double x, double y, double z) {
        return MathUtil.distance(this.getX(), this.getY(), this.getZ(), x, y, z);
    }

    public double distanceSquared(double x, double y, double z) {
        return MathUtil.distanceSquared(this.getX(), this.getY(), this.getZ(), x, y, z);
    }

    public double distance(VectorAbstract other) {
        return this.distance(other.getX(), other.getY(), other.getZ());
    }

    public double distanceSquared(VectorAbstract other) {
        return this.distanceSquared(other.getX(), other.getY(), other.getZ());
    }

    public double distance(Location other) {
        return this.distance(other.getX(), other.getY(), other.getZ());
    }

    public double distanceSquared(Location other) {
        return this.distanceSquared(other.getX(), other.getY(), other.getZ());
    }

    public double distance(Vector other) {
        return this.distance(other.getX(), other.getY(), other.getZ());
    }

    public double distanceSquared(Vector other) {
        return this.distanceSquared(other.getX(), other.getY(), other.getZ());
    }

    public double distance(Entity other) {
        return this.distance(EntityUtil.getLocX(other), EntityUtil.getLocY(other), EntityUtil.getLocZ(other));
    }

    public double distanceSquared(Entity other) {
        return this.distanceSquared(EntityUtil.getLocX(other), EntityUtil.getLocY(other), EntityUtil.getLocZ(other));
    }

    public double distance(Block block) {
        return this.distance((double)block.getX() + 0.5, (double)block.getY() + 0.5, (double)block.getZ() + 0.5);
    }

    public double distanceSquared(Block block) {
        return this.distanceSquared((double)block.getX() + 0.5, (double)block.getY() + 0.5, (double)block.getZ() + 0.5);
    }

    public double distance(CommonEntity<?> other) {
        return this.distance(other.loc);
    }

    public double distanceSquared(CommonEntity<?> other) {
        return this.distanceSquared(other.loc);
    }

    public Vector offsetTo(double x, double y, double z) {
        return new Vector(x - (double)this.getX(), y - (double)this.getY(), z - (double)this.getZ());
    }

    public Vector offsetTo(Location l) {
        return this.offsetTo(l.getX(), l.getY(), l.getZ());
    }

    public Vector offsetTo(CommonEntity<?> entity) {
        return this.offsetTo((Entity)entity.getEntity());
    }

    public Vector offsetTo(Entity e) {
        return this.offsetTo(EntityUtil.getLocX(e), EntityUtil.getLocY(e), EntityUtil.getLocZ(e));
    }

    public String toString() {
        return "{x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "}";
    }
}

