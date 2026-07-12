/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.Location
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BlockIterator {
    private double dx;
    private double dy;
    private double dz;
    private double px;
    private double py;
    private double pz;
    private int bx;
    private int by;
    private int bz;
    private double remaining;
    private boolean first;
    private int skipEndX;
    private int skipEndY;
    private int skipEndZ;
    private int bdx;
    private int bdy;
    private int bdz;
    private double min;

    public BlockIterator() {
    }

    public BlockIterator(IntVector3 rails, RailPath.Segment segment) {
        this.reset(rails, segment);
    }

    public BlockIterator(Location loc, Vector direction, double distance) {
        this(loc.getX(), loc.getY(), loc.getZ(), direction.getX(), direction.getY(), direction.getZ(), distance);
    }

    public BlockIterator(double x, double y, double z, double dx, double dy, double dz, double distance) {
        IntVector3 rails = IntVector3.blockOf((double)x, (double)y, (double)z);
        this.reset(rails, x - (double)rails.x, y - (double)rails.y, z - (double)rails.z, dx, dy, dz, distance);
    }

    public BlockIterator(IntVector3 rails, double x, double y, double z, double dx, double dy, double dz, double distance) {
        this.reset(rails, x, y, z, dx, dy, dz, distance);
    }

    public void reset(IntVector3 rails, double x, double y, double z, double dx, double dy, double dz, double distance) {
        int floor_x = MathUtil.floor((double)x);
        int floor_y = MathUtil.floor((double)y);
        int floor_z = MathUtil.floor((double)z);
        this.bx = rails.x + floor_x;
        this.by = rails.y + floor_y;
        this.bz = rails.z + floor_z;
        this.px = x - (double)floor_x;
        this.py = y - (double)floor_y;
        this.pz = z - (double)floor_z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.skipEndX = Integer.MIN_VALUE;
        this.skipEndY = Integer.MIN_VALUE;
        this.skipEndZ = Integer.MIN_VALUE;
        this.remaining = distance;
        this.first = true;
    }

    public void reset(IntVector3 rails, RailPath.Segment segment) {
        boolean e_beyond_z;
        this.reset(rails, segment.p0.x, segment.p0.y, segment.p0.z, segment.mot.getX(), segment.mot.getY(), segment.mot.getZ(), segment.l);
        int s_rel_bx = this.bx - rails.x;
        int s_rel_by = this.by - rails.y;
        int s_rel_bz = this.bz - rails.z;
        if ((double)s_rel_bx == segment.p0.x && segment.mot.getX() < 0.0 || (double)s_rel_by == segment.p0.y && segment.mot.getY() < 0.0 || (double)s_rel_bz == segment.p0.z && segment.mot.getZ() < 0.0) {
            this.first = false;
        }
        int e_rel_bx = MathUtil.floor((double)segment.p1.x);
        int e_rel_by = MathUtil.floor((double)segment.p1.y);
        int e_rel_bz = MathUtil.floor((double)segment.p1.z);
        boolean e_beyond_x = (double)e_rel_bx == segment.p1.x;
        boolean e_beyond_y = (double)e_rel_by == segment.p1.y;
        boolean bl = e_beyond_z = (double)e_rel_bz == segment.p1.z;
        if (e_beyond_x || e_beyond_y || e_beyond_z) {
            this.skipEndX = rails.x + e_rel_bx;
            this.skipEndY = rails.y + e_rel_by;
            this.skipEndZ = rails.z + e_rel_bz;
            if (e_beyond_x && segment.mot.getX() < 0.0) {
                --this.skipEndX;
            }
            if (e_beyond_y && segment.mot.getY() < 0.0) {
                --this.skipEndY;
            }
            if (e_beyond_z && segment.mot.getZ() < 0.0) {
                --this.skipEndZ;
            }
        }
    }

    public IntVector3 block() {
        return new IntVector3(this.bx, this.by, this.bz);
    }

    public boolean next() {
        if (this.first) {
            this.first = false;
            return true;
        }
        this.min = Double.MAX_VALUE;
        if (this.dx > 1.0E-10) {
            this.add((1.0 - this.px) / this.dx, 1, 0, 0);
        } else if (this.dx < -1.0E-10) {
            this.add(this.px / -this.dx, -1, 0, 0);
        }
        if (this.dy > 1.0E-10) {
            this.add((1.0 - this.py) / this.dy, 0, 1, 0);
        } else if (this.dy < -1.0E-10) {
            this.add(this.py / -this.dy, 0, -1, 0);
        }
        if (this.dz > 1.0E-10) {
            this.add((1.0 - this.pz) / this.dz, 0, 0, 1);
        } else if (this.dz < -1.0E-10) {
            this.add(this.pz / -this.dz, 0, 0, -1);
        }
        if (this.min > this.remaining) {
            return false;
        }
        this.remaining -= this.min;
        this.px += this.dx * this.min;
        this.py += this.dy * this.min;
        this.pz += this.dz * this.min;
        this.bx += this.bdx;
        this.by += this.bdy;
        this.bz += this.bdz;
        this.px -= (double)this.bdx;
        this.py -= (double)this.bdy;
        this.pz -= (double)this.bdz;
        return this.bx != this.skipEndX || this.by != this.skipEndY || this.bz != this.skipEndZ;
    }

    private void add(double value, int bdx, int bdy, int bdz) {
        if (value < this.min) {
            this.min = value;
            this.bdx = bdx;
            this.bdy = bdy;
            this.bdz = bdz;
        }
    }
}

