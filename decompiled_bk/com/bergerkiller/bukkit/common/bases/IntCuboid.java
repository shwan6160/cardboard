/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public final class IntCuboid {
    public final IntVector3 min;
    public final IntVector3 max;
    public static final IntCuboid ZERO = new IntCuboid(IntVector3.ZERO, IntVector3.ZERO);
    public static final IntCuboid ALL = new IntCuboid(new IntVector3(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE), new IntVector3(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));

    private IntCuboid(IntVector3 min, IntVector3 max) {
        this.min = min;
        this.max = max;
    }

    public static IntCuboid create(IntVector3 min, IntVector3 max) {
        if (min == null) {
            throw new IllegalArgumentException("Minimum position is null");
        }
        if (max == null) {
            throw new IllegalArgumentException("Maximum position is null");
        }
        return new IntCuboid(min, max);
    }

    public static IntCuboid createWorldBorder(Location center, double size, int minY, int maxY) {
        double hsize = 0.5 * size;
        IntVector3 min = IntVector3.blockOf(center.getX() - hsize, minY, center.getZ() - hsize);
        IntVector3 max = IntVector3.blockOf(center.getX() + hsize + 1.0, maxY, center.getZ() + hsize + 1.0);
        return IntCuboid.create(min, max);
    }

    public boolean isMinXUnlimited() {
        return this.min.x == Integer.MIN_VALUE;
    }

    public boolean isMinYUnlimited() {
        return this.min.y == Integer.MIN_VALUE;
    }

    public boolean isMinZUnlimited() {
        return this.min.z == Integer.MIN_VALUE;
    }

    public boolean isMaxXUnlimited() {
        return this.max.x == Integer.MIN_VALUE;
    }

    public boolean isMaxYUnlimited() {
        return this.max.y == Integer.MIN_VALUE;
    }

    public boolean isMaxZUnlimited() {
        return this.max.z == Integer.MIN_VALUE;
    }

    public boolean contains(int x, int y, int z) {
        return x >= this.min.x && y >= this.min.y && z >= this.min.z && x < this.max.x && y < this.max.y && z < this.max.z;
    }

    public boolean contains(IntVector3 position) {
        return position.x >= this.min.x && position.y >= this.min.y && position.z >= this.min.z && position.x < this.max.x && position.y < this.max.y && position.z < this.max.z;
    }

    public boolean contains(Block block) {
        return this.contains(block.getX(), block.getY(), block.getZ());
    }

    public boolean contains(Vector position) {
        return this.contains(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    public boolean contains(Location location) {
        return this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public double distance(Vector position) {
        return Math.sqrt(this.distanceSquared(position));
    }

    public double distanceSquared(Vector position) {
        double dx = position.getX() < (double)this.min.x ? (double)this.min.x - position.getX() : (position.getX() >= (double)this.max.x ? position.getX() - (double)this.max.x : 0.0);
        double dy = position.getY() < (double)this.min.y ? (double)this.min.y - position.getY() : (position.getY() >= (double)this.max.y ? position.getY() - (double)this.max.y : 0.0);
        double dz = position.getZ() < (double)this.min.z ? (double)this.min.z - position.getZ() : (position.getZ() >= (double)this.max.z ? position.getZ() - (double)this.max.z : 0.0);
        return dx * dx + dy * dy + dz * dz;
    }

    public int hashCode() {
        return 31 * this.min.hashCode() + this.max.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof IntCuboid) {
            IntCuboid other = (IntCuboid)o;
            return this.min.equals(other.min) && this.max.equals(other.max);
        }
        return false;
    }

    public String toString() {
        return "{min=" + this.min + ", max=" + this.max + "}";
    }
}

