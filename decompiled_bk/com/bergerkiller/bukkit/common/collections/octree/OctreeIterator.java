/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections.octree;

import com.bergerkiller.bukkit.common.collections.octree.Octree;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class OctreeIterator<T>
implements Iterator<T> {
    private static final int COORD_NOT_DIRTY = -1;
    protected final Octree<T> tree;
    protected final int[] index;
    private int x;
    private int y;
    private int z;
    private int coordDirtyDepth = 32;
    private int depth;
    private IteratorState state;
    private int skipIntersectionBelowDepth;

    public OctreeIterator(Octree<T> tree) {
        this.tree = tree;
        this.index = new int[33];
        this.reset();
    }

    public void reset() {
        this.depth = this.index.length - 1;
        this.index[this.depth] = 0;
        this.skipIntersectionBelowDepth = -1;
        this.state = IteratorState.INITIAL;
        this.coordDirtyDepth = 32;
    }

    public boolean isInitialState() {
        return this.state == IteratorState.INITIAL;
    }

    public int getX() {
        if (this.coordDirtyDepth != -1) {
            this.syncCoord();
        }
        return this.x;
    }

    public int getY() {
        if (this.coordDirtyDepth != -1) {
            this.syncCoord();
        }
        return this.y;
    }

    public int getZ() {
        if (this.coordDirtyDepth != -1) {
            this.syncCoord();
        }
        return this.z;
    }

    protected int getNode() {
        return this.index[this.depth] & 0xFFFFFFF8;
    }

    protected int getParentNode() {
        return this.index[this.depth + 1] & 0xFFFFFFF8;
    }

    protected boolean isDataNode() {
        return this.depth == 0;
    }

    protected int getScale() {
        return 1 << this.depth;
    }

    protected Intersection intersect() {
        return Intersection.INSIDE;
    }

    @Override
    public boolean hasNext() {
        return this.search() != 0;
    }

    @Override
    public T next() {
        int dataIndex = this.search();
        if (dataIndex == 0) {
            throw new NoSuchElementException("No more elements in Octal Search Tree");
        }
        Object result = this.tree.values.getAt(dataIndex);
        this.state = IteratorState.FIND_NEXT;
        return result;
    }

    @Override
    public void remove() {
        if (this.state != IteratorState.FIND_NEXT) {
            throw new NoSuchElementException("Next must be called before elements can be removed");
        }
        this.tree.values.removeAt(this.index[0] >>> 3);
        int node = this.index[++this.depth];
        while (true) {
            this.tree.table[node] = 0;
            if (this.tree.clean(node | 7) || this.depth == this.index.length - 1) break;
            this.tree.deallocate(node & 0xFFFFFFF8);
            node = this.index[++this.depth];
        }
    }

    public T get() {
        if (this.state != IteratorState.FIND_NEXT) {
            throw new NoSuchElementException("Next must be called before get() can be called");
        }
        return this.tree.values.getAt(this.index[0] >>> 3);
    }

    public T put(T value) {
        if (this.state != IteratorState.FIND_NEXT) {
            throw new NoSuchElementException("Next must be called before get() can be called");
        }
        int index = this.index[0] >>> 3;
        Object oldValue = this.tree.values.getAt(index);
        this.tree.values.setAt(index, value);
        return oldValue;
    }

    protected int search() {
        if (this.state == IteratorState.INITIAL) {
            block7: {
                this.state = IteratorState.READY;
                if (this.depth == 32) {
                    --this.depth;
                    int parent = 0;
                    do {
                        int node_index;
                        if ((node_index = this.tree.table[parent]) == 0) continue;
                        parent = parent & 0xFFFFFFF8 | node_index & 7;
                        this.index[31] = node_index &= 0xFFFFFFF8;
                        this.index[32] = parent;
                        this.coordDirtyDepth = 32;
                        Intersection intersection = this.intersect();
                        if (intersection != Intersection.PARTIAL) {
                            if (intersection != Intersection.INSIDE) continue;
                            this.skipIntersectionBelowDepth = this.depth;
                            this.findFirstValueSkipIntersection(node_index);
                            return this.index[0] >> 3;
                        }
                        break block7;
                    } while ((++parent & 7) != 0);
                    this.end();
                    return 0;
                }
            }
            this.findDeeper();
        } else if (this.state == IteratorState.FIND_NEXT) {
            this.state = IteratorState.READY;
            if (this.findNextSibling()) {
                this.findDeeper();
            }
        }
        return this.index[0] >>> 3;
    }

    private void findFirstValueSkipIntersection(int parent) {
        if (this.depth > this.coordDirtyDepth) {
            this.coordDirtyDepth = this.depth;
        }
        while (this.depth > 0) {
            int node_index = this.tree.table[parent];
            this.index[this.depth] = parent | node_index & 7;
            parent = node_index & 0xFFFFFFF8;
            --this.depth;
        }
        this.index[this.depth] = parent;
    }

    private void findDeeper() {
        int parent = this.index[this.depth--] & 0xFFFFFFF8;
        while (true) {
            int node_index;
            if ((node_index = this.tree.table[parent]) != 0) {
                Intersection intersection;
                parent = parent & 0xFFFFFFF8 | node_index & 7;
                this.index[this.depth + 1] = parent;
                this.index[this.depth] = node_index &= 0xFFFFFFF8;
                if (this.depth + 1 > this.coordDirtyDepth) {
                    this.coordDirtyDepth = this.depth + 1;
                }
                if ((intersection = this.intersect()) == Intersection.PARTIAL) {
                    if (this.depth == 0) {
                        return;
                    }
                    parent = node_index;
                    --this.depth;
                    continue;
                }
                if (intersection == Intersection.INSIDE) {
                    this.skipIntersectionBelowDepth = this.depth;
                    this.findFirstValueSkipIntersection(node_index);
                    return;
                }
                if ((++parent & 7) != 0) continue;
            }
            if (!this.findNextSibling()) {
                return;
            }
            parent = this.index[this.depth--] & 0xFFFFFFF8;
        }
    }

    private boolean findNextSibling() {
        int node_index;
        int parent = this.index[++this.depth];
        if (this.depth <= this.skipIntersectionBelowDepth) {
            while (true) {
                if ((++parent & 7) != 0 && (node_index = this.tree.table[parent]) != 0) {
                    if (this.depth > this.coordDirtyDepth) {
                        this.coordDirtyDepth = this.depth;
                    }
                    this.index[this.depth--] = parent & 0xFFFFFFF8 | node_index & 7;
                    this.findFirstValueSkipIntersection(node_index & 0xFFFFFFF8);
                    return false;
                }
                if (this.depth == this.skipIntersectionBelowDepth) {
                    this.skipIntersectionBelowDepth = -1;
                    --parent;
                    break;
                }
                parent = this.index[++this.depth];
            }
        }
        while (true) {
            if ((++parent & 7) != 0 && (node_index = this.tree.table[parent]) != 0) {
                parent &= 0xFFFFFFF8;
                parent |= node_index & 7;
                node_index &= 0xFFFFFFF8;
                if (this.depth > this.coordDirtyDepth) {
                    this.coordDirtyDepth = this.depth;
                }
                this.index[this.depth] = parent;
                this.index[--this.depth] = node_index;
                Intersection intersection = this.intersect();
                if (intersection == Intersection.INSIDE) {
                    this.skipIntersectionBelowDepth = this.depth;
                    this.findFirstValueSkipIntersection(node_index);
                    return false;
                }
                if (intersection == Intersection.PARTIAL) {
                    return this.depth > 0;
                }
                ++this.depth;
                continue;
            }
            if (++this.depth == this.index.length) {
                this.end();
                return false;
            }
            parent = this.index[this.depth];
        }
    }

    private void end() {
        this.index[0] = 0;
    }

    private void syncCoord() {
        int depth = this.depth;
        int x_updated = 0;
        int y_updated = 0;
        int z_updated = 0;
        for (int n = this.coordDirtyDepth; n >= depth + 1; --n) {
            int node = this.index[n];
            x_updated <<= 1;
            y_updated <<= 1;
            z_updated <<= 1;
            x_updated |= node & 1;
            y_updated |= (node & 2) >>> 1;
            z_updated |= (node & 4) >>> 2;
        }
        int keepMask = this.coordDirtyDepth == 32 ? 0 : -(1 << this.coordDirtyDepth);
        this.x = this.x & keepMask | x_updated << depth;
        this.y = this.y & keepMask | y_updated << depth;
        this.z = this.z & keepMask | z_updated << depth;
        this.coordDirtyDepth = -1;
    }

    private static enum IteratorState {
        READY,
        INITIAL,
        FIND_NEXT;

    }

    protected static enum Intersection {
        INSIDE,
        OUTSIDE,
        PARTIAL;

    }
}

