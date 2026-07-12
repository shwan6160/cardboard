/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections.octree;

import com.bergerkiller.bukkit.common.collections.octree.Octree;
import com.bergerkiller.bukkit.common.collections.octree.OctreeIterator;
import java.util.BitSet;

public class OctreeDefragmentIterator<T>
extends OctreeIterator<T> {
    private final int[] remapping;
    private final BitSet data_entries;
    private int counter;

    public OctreeDefragmentIterator(Octree<T> tree) {
        super(tree);
        this.remapping = new int[tree.table_size];
        this.data_entries = new BitSet(tree.table_size << 3);
        this.counter = 1;
    }

    public int getTableSize() {
        return this.remapping.length << 3;
    }

    public int getRemapped(int node) {
        return this.remapping[node >>> 3] << 3 | node & 7;
    }

    public boolean isStoringDataValues(int node) {
        return this.data_entries.get(node >>> 3);
    }

    @Override
    protected OctreeIterator.Intersection intersect() {
        if (this.isDataNode()) {
            this.data_entries.set(this.getParentNode() >>> 3);
        } else {
            this.remapping[this.getNode() >>> 3] = this.counter++;
        }
        return OctreeIterator.Intersection.PARTIAL;
    }
}

