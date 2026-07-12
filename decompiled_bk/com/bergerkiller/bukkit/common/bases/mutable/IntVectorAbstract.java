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
import com.bergerkiller.bukkit.common.bases.mutable.IntegerAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public abstract class IntVectorAbstract {
    public final IntegerAbstract x = new IntegerAbstract(){

        @Override
        public int get() {
            return IntVectorAbstract.this.getX();
        }

        @Override
        public IntegerAbstract set(int value) {
            IntVectorAbstract.this.setX(value);
            return this;
        }
    };
    public final IntegerAbstract y = new IntegerAbstract(){

        @Override
        public int get() {
            return IntVectorAbstract.this.getY();
        }

        @Override
        public IntegerAbstract set(int value) {
            IntVectorAbstract.this.setY(value);
            return this;
        }
    };
    public final IntegerAbstract z = new IntegerAbstract(){

        @Override
        public int get() {
            return IntVectorAbstract.this.getZ();
        }

        @Override
        public IntegerAbstract set(int value) {
            IntVectorAbstract.this.setZ(value);
            return this;
        }
    };

    public abstract int getX();

    public abstract int getY();

    public abstract int getZ();

    public abstract IntVectorAbstract setX(int var1);

    public abstract IntVectorAbstract setY(int var1);

    public abstract IntVectorAbstract setZ(int var1);

    public IntVectorAbstract setZero() {
        return this.setX(0).setY(0).setZ(0);
    }

    public IntVectorAbstract set(int x, int y, int z) {
        return this.setX(x).setY(y).setZ(z);
    }

    public IntVectorAbstract set(IntVectorAbstract value) {
        return this.set(value.getX(), value.getY(), value.getZ());
    }

    public IntVectorAbstract set(IntVector3 value) {
        return this.set(value.x, value.y, value.z);
    }

    public IntVector3 vector() {
        return new IntVector3(this.getX(), this.getY(), this.getZ());
    }

    public IntVectorAbstract add(int x, int y, int z) {
        return this.setX(this.getX() + x).setY(this.getY() + y).setZ(this.getZ() + z);
    }

    public IntVectorAbstract add(IntVector3 vector) {
        return this.add(vector.x, vector.y, vector.z);
    }

    public IntVectorAbstract add(IntVectorAbstract value) {
        return this.add(value.getX(), value.getY(), value.getZ());
    }

    public IntVectorAbstract add(BlockFace face, int length) {
        return this.add(length * face.getModX(), length * face.getModY(), length * face.getModZ());
    }

    public IntVectorAbstract add(IntVectorAbstract value, int length) {
        return this.add(length * value.getX(), length * value.getY(), length * value.getZ());
    }

    public IntVectorAbstract subtract(int x, int y, int z) {
        return this.setX(this.getX() - x).setY(this.getY() - y).setZ(this.getZ() - z);
    }

    public IntVectorAbstract subtract(IntVector3 vector) {
        return this.subtract(vector.x, vector.y, vector.z);
    }

    public IntVectorAbstract subtract(IntVectorAbstract value) {
        return this.subtract(value.getX(), value.getY(), value.getZ());
    }

    public IntVectorAbstract subtract(BlockFace face, int length) {
        return this.subtract(length * face.getModX(), length * face.getModY(), length * face.getModZ());
    }

    public IntVectorAbstract subtract(IntVectorAbstract value, int length) {
        return this.subtract(length * value.getX(), length * value.getY(), length * value.getZ());
    }

    public IntVectorAbstract multiply(int mx, int my, int mz) {
        return this.setX(this.getX() * mx).setY(this.getY() * my).setZ(this.getZ() * mz);
    }

    public IntVectorAbstract multiply(IntVector3 vector) {
        return this.multiply(vector.x, vector.y, vector.z);
    }

    public IntVectorAbstract divide(int dx, int dy, int dz) {
        return this.setX(this.getX() / dx).setY(this.getY() / dy).setZ(this.getZ() / dz);
    }

    public IntVectorAbstract divide(IntVector3 vector) {
        return this.divide(vector.x, vector.y, vector.z);
    }

    public IntVectorAbstract multiply(int factor) {
        return this.multiply(factor, factor, factor);
    }

    public IntVectorAbstract divide(int factor) {
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

