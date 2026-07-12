/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.math.Vector3;
import org.bukkit.block.BlockFace;

public class Quad
implements Comparable<Quad>,
Cloneable {
    public Vector3 p0;
    public Vector3 p1;
    public Vector3 p2;
    public Vector3 p3;
    public BlockFace face;
    public MapTexture texture;

    public Quad() {
        this.p0 = new Vector3();
        this.p1 = new Vector3();
        this.p2 = new Vector3();
        this.p3 = new Vector3();
        this.face = BlockFace.UP;
        this.texture = null;
    }

    protected Quad(Quad quad) {
        this.p0 = quad.p0.clone();
        this.p1 = quad.p1.clone();
        this.p2 = quad.p2.clone();
        this.p3 = quad.p3.clone();
        this.face = quad.face;
        this.texture = quad.texture;
    }

    public Quad(BlockFace face, Vector3 from, Vector3 to, MapTexture texture) {
        this();
        this.texture = texture;
        this.face = face;
        if (face == BlockFace.UP) {
            this.p1.x = from.x;
            this.p1.z = to.z;
            this.p2.x = to.x;
            this.p2.z = to.z;
            this.p3.x = to.x;
            this.p3.z = from.z;
            this.p0.x = from.x;
            this.p0.z = from.z;
            this.p2.y = this.p3.y = to.y;
            this.p1.y = this.p3.y;
            this.p0.y = this.p3.y;
        }
        if (face == BlockFace.DOWN) {
            this.p1.x = from.x;
            this.p1.z = from.z;
            this.p2.x = to.x;
            this.p2.z = from.z;
            this.p3.x = to.x;
            this.p3.z = to.z;
            this.p0.x = from.x;
            this.p0.z = to.z;
            this.p2.y = this.p3.y = from.y;
            this.p1.y = this.p3.y;
            this.p0.y = this.p3.y;
        }
        if (face == BlockFace.SOUTH) {
            this.p1.x = from.x;
            this.p1.y = from.y;
            this.p2.x = to.x;
            this.p2.y = from.y;
            this.p3.x = to.x;
            this.p3.y = to.y;
            this.p0.x = from.x;
            this.p0.y = to.y;
            this.p2.z = this.p3.z = to.z;
            this.p1.z = this.p3.z;
            this.p0.z = this.p3.z;
        }
        if (face == BlockFace.NORTH) {
            this.p1.x = to.x;
            this.p1.y = from.y;
            this.p2.x = from.x;
            this.p2.y = from.y;
            this.p3.x = from.x;
            this.p3.y = to.y;
            this.p0.x = to.x;
            this.p0.y = to.y;
            this.p2.z = this.p3.z = from.z;
            this.p1.z = this.p3.z;
            this.p0.z = this.p3.z;
        }
        if (face == BlockFace.EAST) {
            this.p1.y = from.y;
            this.p1.z = to.z;
            this.p2.y = from.y;
            this.p2.z = from.z;
            this.p3.y = to.y;
            this.p3.z = from.z;
            this.p0.y = to.y;
            this.p0.z = to.z;
            this.p2.x = this.p3.x = to.x;
            this.p1.x = this.p3.x;
            this.p0.x = this.p3.x;
        }
        if (face == BlockFace.WEST) {
            this.p1.y = from.y;
            this.p1.z = from.z;
            this.p2.y = from.y;
            this.p2.z = to.z;
            this.p3.y = to.y;
            this.p3.z = to.z;
            this.p0.y = to.y;
            this.p0.z = from.z;
            this.p2.x = this.p3.x = from.x;
            this.p1.x = this.p3.x;
            this.p0.x = this.p3.x;
        }
    }

    public final void mergePoints(Quad quad) {
        this.mergePoint(quad.p0);
        this.mergePoint(quad.p1);
        this.mergePoint(quad.p2);
        this.mergePoint(quad.p3);
    }

    private final void mergePoint(Vector3 p) {
        if (this.p0.equals(p)) {
            this.p0 = p;
        }
        if (this.p1.equals(p)) {
            this.p1 = p;
        }
        if (this.p2.equals(p)) {
            this.p2 = p;
        }
        if (this.p3.equals(p)) {
            this.p3 = p;
        }
    }

    public double depth() {
        double d = this.p0.y;
        if (this.p1.y < d) {
            d = this.p1.y;
        }
        if (this.p2.y < d) {
            d = this.p2.y;
        }
        if (this.p3.y < d) {
            d = this.p3.y;
        }
        return d;
    }

    @Override
    public int compareTo(Quad o) {
        return Double.compare(this.depth(), o.depth());
    }

    public Quad clone() {
        return new Quad(this);
    }
}

