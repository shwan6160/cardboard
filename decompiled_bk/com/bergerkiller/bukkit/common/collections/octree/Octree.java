/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections.octree;

import com.bergerkiller.bukkit.common.bases.IntCuboid;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.IndexedCollection;
import com.bergerkiller.bukkit.common.collections.octree.OctreeCuboidIterator;
import com.bergerkiller.bukkit.common.collections.octree.OctreeDefragmentIterator;
import com.bergerkiller.bukkit.common.collections.octree.OctreeIterable;
import com.bergerkiller.bukkit.common.collections.octree.OctreeIterator;
import com.bergerkiller.bukkit.common.collections.octree.OctreePointIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Octree<T>
implements OctreeIterable<T> {
    protected int[] table;
    protected int table_size;
    protected final IndexedCollection<T> values = new IndexedCollection();
    private int deallocated_node_index;
    protected final OctreePointIterator<T> remove_iter;

    public Octree() {
        this.values.reserve(1);
        this.values.setAt(0, null);
        this.clear();
        this.remove_iter = new OctreePointIterator(this, 0, 0, 0);
    }

    public void clear() {
        this.values.clear();
        this.deallocated_node_index = 0;
        this.table_size = 1;
        this.table = new int[8];
    }

    protected int allocate() {
        if (this.deallocated_node_index == 0) {
            this.deallocated_node_index = this.table.length;
            this.table = Arrays.copyOf(this.table, this.table.length << 1);
            int end_node_index = this.table.length - 8;
            for (int node = this.deallocated_node_index; node < end_node_index; node += 8) {
                this.table[node + 1] = node + 8;
            }
        }
        int node = this.deallocated_node_index;
        this.deallocated_node_index = this.table[++node];
        this.table[node--] = 0;
        ++this.table_size;
        return node;
    }

    protected void deallocate(int node) {
        int next_deallocated_node_index = this.deallocated_node_index;
        this.deallocated_node_index = node;
        this.table[node] = 0;
        this.table[++node] = next_deallocated_node_index;
        this.table[++node] = 0;
        this.table[++node] = 0;
        this.table[++node] = 0;
        this.table[++node] = 0;
        this.table[++node] = 0;
        this.table[++node] = 0;
        --this.table_size;
    }

    public void compress() {
        int new_table_size;
        OctreeDefragmentIterator iter = new OctreeDefragmentIterator(this);
        while (iter.hasNext()) {
            iter.next();
        }
        for (new_table_size = 8; new_table_size < iter.getTableSize(); new_table_size <<= 1) {
        }
        int[] new_table = new int[new_table_size];
        for (int i = 0; i < this.table.length; i += 8) {
            if (this.table[i] == 0) continue;
            int new_pos = iter.getRemapped(i);
            if (iter.isStoringDataValues(i)) {
                System.arraycopy(this.table, i, new_table, new_pos, 8);
                continue;
            }
            for (int k = 0; k < 8; ++k) {
                new_table[new_pos + k] = iter.getRemapped(this.table[i + k]);
            }
        }
        if (new_table_size == iter.getTableSize()) {
            this.deallocated_node_index = 0;
        } else {
            this.deallocated_node_index = iter.getTableSize();
            int end_node_index = new_table_size - 8;
            for (int node = this.deallocated_node_index; node < end_node_index; node += 8) {
                new_table[node + 1] = node + 8;
            }
        }
        this.table = new_table;
    }

    private void compressIfNeeded() {
        if (this.table_size < this.table.length >> 9) {
            this.compress();
        }
    }

    public int getNodeCount() {
        return this.table_size;
    }

    public int size() {
        return this.values.size();
    }

    public Collection<T> values() {
        return Collections.unmodifiableCollection(this.values);
    }

    @Override
    public OctreeIterator<T> iterator() {
        return new OctreeIterator(this);
    }

    public OctreeIterable<T> cuboid(IntVector3 min, IntVector3 max) {
        return () -> new OctreeCuboidIterator(this, min, max);
    }

    public OctreeIterable<T> cuboid(IntCuboid cuboid) {
        return this.cuboid(cuboid.min, cuboid.max.subtract(1, 1, 1));
    }

    protected boolean clean(int parent) {
        int node = 0;
        do {
            int new_node;
            if ((new_node = this.table[parent]) != 0 && (new_node & 7) == (parent & 7)) {
                node = new_node;
                continue;
            }
            this.table[parent] = node;
        } while ((parent-- & 7) != 0);
        return node != 0;
    }

    public T remove(int x, int y, int z) {
        this.remove_iter.reset(x, y, z);
        if (this.remove_iter.hasNext()) {
            Object value = this.remove_iter.next();
            this.remove_iter.remove();
            this.compressIfNeeded();
            return value;
        }
        return null;
    }

    public boolean contains(int x, int y, int z) {
        return this.getValueIndex(x, y, z, false) != 0;
    }

    public T get(int x, int y, int z) {
        return this.getValueAtIndex(this.getValueIndex(x, y, z, false));
    }

    public T put(int x, int y, int z, T value) {
        return this.putValueAtIndex(this.getValueIndex(x, y, z, true), value);
    }

    public T putValueAtIndex(int index, T value) {
        T result = this.values.getAt(index);
        this.values.setAt(index, value);
        return result;
    }

    public T getValueAtIndex(int index) {
        return this.values.getAt(index);
    }

    public int getValueIndex(int x, int y, int z, boolean create) {
        if (create) {
            int index = 0;
            for (int n = 0; n < 31; ++n) {
                int subaddr = (x & Integer.MIN_VALUE) >>> 31 | (y & Integer.MIN_VALUE) >>> 30 | (z & Integer.MIN_VALUE) >>> 29;
                int next_index = this.table[index |= subaddr];
                if (next_index == 0 || (next_index & 7) != subaddr) {
                    next_index = this.allocate();
                    this.table[index] = next_index | subaddr;
                    this.clean(index);
                }
                index = next_index & 0xFFFFFFF8;
                x <<= 1;
                y <<= 1;
                z <<= 1;
            }
            int subaddr = (x & Integer.MIN_VALUE) >>> 31 | (y & Integer.MIN_VALUE) >>> 30 | (z & Integer.MIN_VALUE) >>> 29;
            int data_index = this.table[index |= subaddr];
            if (data_index == 0 || (data_index & 7) != subaddr) {
                data_index = this.values.addAndGetIndex(null);
                this.table[index] = data_index << 3 | subaddr;
                this.clean(index);
            } else {
                data_index >>>= 3;
            }
            return data_index;
        }
        int index = 0;
        for (int n = 0; n < 31; ++n) {
            int sub = (x & Integer.MIN_VALUE) >>> 31 | (y & Integer.MIN_VALUE) >>> 30 | (z & Integer.MIN_VALUE) >>> 29;
            int next_index = this.table[index |= sub];
            if (next_index == 0 || (next_index & 7) != sub) {
                return 0;
            }
            index = next_index & 0xFFFFFFF8;
            x <<= 1;
            y <<= 1;
            z <<= 1;
        }
        int subaddr = (x & Integer.MIN_VALUE) >>> 31 | (y & Integer.MIN_VALUE) >>> 30 | (z & Integer.MIN_VALUE) >>> 29;
        int data_index = this.table[index |= subaddr];
        if (data_index == 0 || (data_index & 7) != subaddr) {
            return 0;
        }
        return data_index >>>= 3;
    }
}

