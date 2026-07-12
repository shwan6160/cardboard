/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections.octree;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.octree.Octree;
import com.bergerkiller.bukkit.common.collections.octree.OctreeIterator;

public class OctreePointIterator<T>
extends OctreeIterator<T> {
    private int point_x;
    private int point_y;
    private int point_z;

    public OctreePointIterator(Octree<T> tree, IntVector3 point) {
        this(tree, point.x, point.y, point.z);
    }

    public OctreePointIterator(Octree<T> tree, int x, int y, int z) {
        super(tree);
        this.point_x = x;
        this.point_y = y;
        this.point_z = z;
    }

    public void reset(int x, int y, int z) {
        super.reset();
        this.point_x = x;
        this.point_y = y;
        this.point_z = z;
    }

    @Override
    protected OctreeIterator.Intersection intersect() {
        int min_x = this.getX();
        int min_y = this.getY();
        int min_z = this.getZ();
        int mask = this.getScale() - 1;
        int max_x = min_x | mask;
        int max_y = min_y | mask;
        int max_z = min_z | mask;
        if (min_x > this.point_x || min_y > this.point_y || min_z > this.point_z) {
            return OctreeIterator.Intersection.OUTSIDE;
        }
        if (max_x < this.point_x || max_y < this.point_y || max_z < this.point_z) {
            return OctreeIterator.Intersection.OUTSIDE;
        }
        return mask == 0 ? OctreeIterator.Intersection.INSIDE : OctreeIterator.Intersection.PARTIAL;
    }
}

