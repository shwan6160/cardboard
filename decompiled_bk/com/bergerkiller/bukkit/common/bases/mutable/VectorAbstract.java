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
import com.bergerkiller.bukkit.common.bases.mutable.DoubleAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorXZAbstract;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public abstract class VectorAbstract {
    public final VectorXZAbstract xz = new VectorXZAbstract(){

        @Override
        public double getX() {
            return VectorAbstract.this.getX();
        }

        @Override
        public double getZ() {
            return VectorAbstract.this.getZ();
        }

        @Override
        public VectorXZAbstract setX(double x) {
            VectorAbstract.this.setX(x);
            return this;
        }

        @Override
        public VectorXZAbstract setZ(double z) {
            VectorAbstract.this.setZ(z);
            return this;
        }
    };
    public final DoubleAbstract x = new DoubleAbstract(){

        @Override
        public double get() {
            return VectorAbstract.this.getX();
        }

        @Override
        public DoubleAbstract set(double value) {
            VectorAbstract.this.setX(value);
            return this;
        }
    };
    public final DoubleAbstract y = new DoubleAbstract(){

        @Override
        public double get() {
            return VectorAbstract.this.getY();
        }

        @Override
        public DoubleAbstract set(double value) {
            VectorAbstract.this.setY(value);
            return this;
        }
    };
    public final DoubleAbstract z = new DoubleAbstract(){

        @Override
        public double get() {
            return VectorAbstract.this.getZ();
        }

        @Override
        public DoubleAbstract set(double value) {
            VectorAbstract.this.setZ(value);
            return this;
        }
    };

    public abstract double getX();

    public abstract double getY();

    public abstract double getZ();

    public abstract VectorAbstract setX(double var1);

    public abstract VectorAbstract setY(double var1);

    public abstract VectorAbstract setZ(double var1);

    public VectorAbstract set(double x, double y, double z) {
        return this.setX(x).setY(y).setZ(z);
    }

    public VectorAbstract setZero() {
        return this.set(0.0, 0.0, 0.0);
    }

    public VectorAbstract set(VectorAbstract value) {
        return this.set(value.getX(), value.getY(), value.getZ());
    }

    public VectorAbstract set(Vector value) {
        return this.set(value.getX(), value.getY(), value.getZ());
    }

    public IntVector3 floor() {
        return new IntVector3(this.x.getFloor(), this.y.getFloor(), this.z.getFloor());
    }

    public IntVector3 block() {
        return new IntVector3(this.x.block(), this.y.block(), this.z.block());
    }

    public Vector vector() {
        return new Vector(this.getX(), this.getY(), this.getZ());
    }

    public VectorAbstract add(double x, double y, double z) {
        return this.set(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public VectorAbstract add(Vector vector) {
        return this.add(vector.getX(), vector.getY(), vector.getZ());
    }

    public VectorAbstract add(VectorAbstract value) {
        return this.add(value.getX(), value.getY(), value.getZ());
    }

    public VectorAbstract add(BlockFace face, double length) {
        return this.add(length * (double)face.getModX(), length * (double)face.getModY(), length * (double)face.getModZ());
    }

    public VectorAbstract add(VectorAbstract value, double length) {
        return this.add(length * value.getX(), length * value.getY(), length * value.getZ());
    }

    public VectorAbstract subtract(double x, double y, double z) {
        return this.set(this.getX() - x, this.getY() - y, this.getZ() - z);
    }

    public VectorAbstract subtract(Vector vector) {
        return this.subtract(vector.getX(), vector.getY(), vector.getZ());
    }

    public VectorAbstract subtract(VectorAbstract value) {
        return this.subtract(value.getX(), value.getY(), value.getZ());
    }

    public VectorAbstract subtract(BlockFace face, double length) {
        return this.subtract(length * (double)face.getModX(), length * (double)face.getModY(), length * (double)face.getModZ());
    }

    public VectorAbstract subtract(VectorAbstract value, double length) {
        return this.subtract(length * value.getX(), length * value.getY(), length * value.getZ());
    }

    public VectorAbstract multiply(double mx, double my, double mz) {
        return this.set(this.getX() * mx, this.getY() * my, this.getZ() * mz);
    }

    public VectorAbstract multiply(Vector vector) {
        return this.multiply(vector.getX(), vector.getY(), vector.getZ());
    }

    public VectorAbstract divide(double dx, double dy, double dz) {
        return this.set(this.getX() / dx, this.getY() / dy, this.getZ() / dz);
    }

    public VectorAbstract divide(Vector vector) {
        return this.divide(vector.getX(), vector.getY(), vector.getZ());
    }

    public VectorAbstract multiply(double factor) {
        return this.multiply(factor, factor, factor);
    }

    public VectorAbstract divide(double factor) {
        return this.divide(factor, factor, factor);
    }

    public VectorAbstract fixNaN() {
        this.x.fixNaN();
        this.y.fixNaN();
        this.z.fixNaN();
        return this;
    }

    public VectorAbstract fixNaN(double defx, double defy, double defz) {
        this.x.fixNaN(defx);
        this.y.fixNaN(defy);
        this.z.fixNaN(defz);
        return this;
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
        return new Vector(x - this.getX(), y - this.getY(), z - this.getZ());
    }

    public Vector offsetTo(Location l) {
        return this.offsetTo(l.getX(), l.getY(), l.getZ());
    }

    public Vector offsetTo(VectorAbstract other) {
        return this.offsetTo(other.getX(), other.getY(), other.getZ());
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

