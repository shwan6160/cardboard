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

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.mutable.DoubleAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public abstract class VectorXZAbstract {
    public final DoubleAbstract x = new DoubleAbstract(){

        @Override
        public double get() {
            return VectorXZAbstract.this.getX();
        }

        @Override
        public DoubleAbstract set(double value) {
            VectorXZAbstract.this.setX(value);
            return this;
        }
    };
    public final DoubleAbstract z = new DoubleAbstract(){

        @Override
        public double get() {
            return VectorXZAbstract.this.getZ();
        }

        @Override
        public DoubleAbstract set(double value) {
            VectorXZAbstract.this.setZ(value);
            return this;
        }
    };

    public abstract double getX();

    public abstract double getZ();

    public abstract VectorXZAbstract setX(double var1);

    public abstract VectorXZAbstract setZ(double var1);

    public VectorXZAbstract setZero() {
        return this.setX(0.0).setZ(0.0);
    }

    public VectorXZAbstract set(double x, double z) {
        return this.setX(x).setZ(z);
    }

    public VectorXZAbstract set(VectorXZAbstract value) {
        return this.set(value.getX(), value.getZ());
    }

    public IntVector2 floor() {
        return new IntVector2(this.x.getFloor(), this.z.getFloor());
    }

    public IntVector2 toBlock() {
        return new IntVector2(this.x.block(), this.z.block());
    }

    public VectorXZAbstract add(double x, double z) {
        return this.setX(this.getX() + x).setZ(this.getZ() + z);
    }

    public VectorXZAbstract add(Vector vector) {
        return this.add(vector.getX(), vector.getZ());
    }

    public VectorXZAbstract add(VectorXZAbstract value) {
        return this.add(value.getX(), value.getZ());
    }

    public VectorXZAbstract add(VectorXZAbstract value, double length) {
        return this.add(length * value.getX(), length * value.getZ());
    }

    public VectorXZAbstract add(BlockFace face, double length) {
        return this.add(length * (double)face.getModX(), length * (double)face.getModZ());
    }

    public VectorXZAbstract subtract(double x, double z) {
        return this.setX(this.getX() - x).setZ(this.getZ() - z);
    }

    public VectorXZAbstract subtract(Vector vector) {
        return this.subtract(vector.getX(), vector.getZ());
    }

    public VectorXZAbstract subtract(VectorXZAbstract value) {
        return this.subtract(value.getX(), value.getZ());
    }

    public VectorXZAbstract subtract(BlockFace face, double length) {
        return this.subtract(length * (double)face.getModX(), length * (double)face.getModZ());
    }

    public VectorXZAbstract subtract(VectorXZAbstract value, double length) {
        return this.subtract(length * value.getX(), length * value.getZ());
    }

    public VectorXZAbstract multiply(double mx, double mz) {
        return this.setX(this.getX() * mx).setZ(this.getZ() * mz);
    }

    public VectorXZAbstract multiply(Vector vector) {
        return this.multiply(vector.getX(), vector.getZ());
    }

    public VectorXZAbstract multiply(double factor) {
        return this.multiply(factor, factor);
    }

    public VectorXZAbstract divide(double dx, double dz) {
        return this.setX(this.getX() / dx).setZ(this.getZ() / dz);
    }

    public VectorXZAbstract divide(Vector vector) {
        return this.divide(vector.getX(), vector.getZ());
    }

    public VectorXZAbstract divide(double factor) {
        return this.divide(factor, factor);
    }

    public VectorXZAbstract fixNaN() {
        this.x.fixNaN();
        this.z.fixNaN();
        return this;
    }

    public VectorXZAbstract fixNaN(double defx, double defz) {
        this.x.fixNaN(defx);
        this.z.fixNaN(defz);
        return this;
    }

    public IntVector2 chunk() {
        return new IntVector2(this.x.chunk(), this.z.chunk());
    }

    public double length() {
        return MathUtil.length(this.getX(), this.getZ());
    }

    public double lengthSquared() {
        return MathUtil.lengthSquared(this.getX(), this.getZ());
    }

    public double distance(double x, double z) {
        return MathUtil.distance(this.getX(), this.getZ(), x, z);
    }

    public double distanceSquared(double x, double z) {
        return MathUtil.distance(this.getX(), this.getZ(), x, z);
    }

    public double distance(VectorAbstract other) {
        return this.distance(other.getX(), other.getZ());
    }

    public double distanceSquared(VectorAbstract other) {
        return this.distanceSquared(other.getX(), other.getZ());
    }

    public double distance(VectorXZAbstract other) {
        return this.distance(other.getX(), other.getZ());
    }

    public double distanceSquared(VectorXZAbstract other) {
        return this.distanceSquared(other.getX(), other.getZ());
    }

    public double distance(Location other) {
        return this.distance(other.getX(), other.getZ());
    }

    public double distanceSquared(Location other) {
        return this.distanceSquared(other.getX(), other.getZ());
    }

    public double distance(Vector other) {
        return this.distance(other.getX(), other.getZ());
    }

    public double distanceSquared(Vector other) {
        return this.distanceSquared(other.getX(), other.getZ());
    }

    public double distance(Block block) {
        return this.distance((double)block.getX() + 0.5, (double)block.getZ() + 0.5);
    }

    public double distanceSquared(Block block) {
        return this.distanceSquared((double)block.getX() + 0.5, (double)block.getZ() + 0.5);
    }

    public double distance(Entity other) {
        return this.distance(EntityUtil.getLocX(other), EntityUtil.getLocZ(other));
    }

    public double distanceSquared(Entity other) {
        return this.distanceSquared(EntityUtil.getLocX(other), EntityUtil.getLocZ(other));
    }

    public double distance(CommonEntity<?> other) {
        return this.distance(other.loc);
    }

    public double distanceSquared(CommonEntity<?> other) {
        return this.distanceSquared(other.loc);
    }

    public String toString() {
        return "{x=" + this.getX() + ", z=" + this.getZ() + "}";
    }
}

