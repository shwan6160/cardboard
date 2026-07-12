/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections.octree;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.octree.Octree;
import com.bergerkiller.bukkit.common.collections.octree.OctreeIterator;

public class OctreeCuboidIterator<T>
extends OctreeIterator<T> {
    private final IntVector3 min;
    private final IntVector3 max;
    public static int NUM_INTERSECTS = 0;

    public OctreeCuboidIterator(Octree<T> tree, IntVector3 min, IntVector3 max) {
        super(tree);
        this.min = min;
        this.max = max;
    }

    @Override
    protected OctreeIterator.Intersection intersect() {
        ++NUM_INTERSECTS;
        int min_x = this.getX();
        int min_y = this.getY();
        int min_z = this.getZ();
        int mask = this.getScale() - 1;
        int max_x = min_x | mask;
        int max_y = min_y | mask;
        int max_z = min_z | mask;
        if (min_x > this.max.x || min_y > this.max.y || min_z > this.max.z) {
            return OctreeIterator.Intersection.OUTSIDE;
        }
        if (max_x < this.min.x || max_y < this.min.y || max_z < this.min.z) {
            return OctreeIterator.Intersection.OUTSIDE;
        }
        if (min_x >= this.min.x && min_y >= this.min.y && min_z >= this.min.z && max_x <= this.max.x && max_y <= this.max.y && max_z <= this.max.z) {
            return OctreeIterator.Intersection.INSIDE;
        }
        return OctreeIterator.Intersection.PARTIAL;
    }
}

