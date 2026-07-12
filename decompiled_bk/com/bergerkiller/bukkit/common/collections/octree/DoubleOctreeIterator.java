/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections.octree;

import com.bergerkiller.bukkit.common.collections.octree.DoubleOctree;
import com.bergerkiller.bukkit.common.collections.octree.OctreeIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleOctreeIterator<T>
implements Iterator<T> {
    private final OctreeIterator<DoubleOctree.Entry<T>> base_iter;
    private DoubleOctree.Entry<T> last_returned_parent;
    private DoubleOctree.Entry<T> last_returned;

    public DoubleOctreeIterator(OctreeIterator<DoubleOctree.Entry<T>> baseIterator) {
        this.base_iter = baseIterator;
        this.last_returned_parent = null;
        this.last_returned = null;
    }

    public void reset() {
        this.base_iter.reset();
        this.last_returned = null;
        this.last_returned_parent = null;
    }

    public int getBlockX() {
        return this.base_iter.getX();
    }

    public int getBlockY() {
        return this.base_iter.getY();
    }

    public int getBlockZ() {
        return this.base_iter.getZ();
    }

    public double getX() {
        return this.last_returned.getX();
    }

    public double getY() {
        return this.last_returned.getY();
    }

    public double getZ() {
        return this.last_returned.getZ();
    }

    @Override
    public boolean hasNext() {
        if (this.last_returned != null) {
            return this.last_returned.next != null || this.base_iter.hasNext();
        }
        return this.last_returned_parent != null && this.last_returned_parent.next != null || this.base_iter.hasNext();
    }

    @Override
    public T next() {
        return this.nextEntry().getValue();
    }

    public DoubleOctree.Entry<T> nextEntry() {
        if (this.last_returned == null) {
            this.last_returned = this.last_returned_parent != null ? this.last_returned_parent.next : this.base_iter.next();
            return this.last_returned;
        }
        this.last_returned_parent = this.last_returned;
        this.last_returned = this.last_returned.next;
        if (this.last_returned == null) {
            this.last_returned_parent = null;
            this.last_returned = this.base_iter.next();
        }
        return this.last_returned;
    }

    public DoubleOctree.Entry<T> getEntry() {
        return this.last_returned;
    }

    @Override
    public void remove() {
        if (this.last_returned == null) {
            throw new NoSuchElementException("No call was done to next() or nextEntry()");
        }
        if (this.last_returned_parent == null) {
            if (this.last_returned.next == null) {
                this.base_iter.remove();
            } else {
                this.base_iter.put(this.last_returned.next);
            }
        } else {
            this.last_returned_parent.next = this.last_returned.next;
            if (this.last_returned_parent.next == null) {
                this.last_returned_parent = null;
            }
        }
        this.last_returned = null;
    }
}

