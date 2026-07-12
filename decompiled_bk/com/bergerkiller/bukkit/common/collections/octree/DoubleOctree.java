/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.collections.octree;

import com.bergerkiller.bukkit.common.bases.IntCuboid;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.octree.DoubleOctreeIterable;
import com.bergerkiller.bukkit.common.collections.octree.DoubleOctreeIterator;
import com.bergerkiller.bukkit.common.collections.octree.Octree;
import com.bergerkiller.bukkit.common.collections.octree.OctreeCuboidIterator;
import com.bergerkiller.bukkit.common.collections.octree.OctreePointIterator;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bukkit.util.Vector;

public class DoubleOctree<T>
implements DoubleOctreeIterable<T> {
    private final Octree<Entry<T>> tree = new Octree();
    private final DoubleOctreeIterator<T> remove_iter;

    public DoubleOctree() {
        this.remove_iter = new DoubleOctreeIterator(this.tree.remove_iter);
    }

    public void clear() {
        this.tree.clear();
    }

    @Override
    public DoubleOctreeIterator<T> iterator() {
        return new DoubleOctreeIterator(this.tree.iterator());
    }

    public Collection<T> values() {
        return new DoubleOctreeValues<T>(this.tree.values());
    }

    public DoubleOctreeIterable<T> cuboid(IntVector3 min, IntVector3 max) {
        return () -> new DoubleOctreeIterator<T>(new OctreeCuboidIterator<Entry<T>>(this.tree, min, max));
    }

    public DoubleOctreeIterable<T> cuboid(IntCuboid cuboid) {
        return this.cuboid(cuboid.min, cuboid.max.subtract(1, 1, 1));
    }

    public DoubleOctreeIterable<T> block(IntVector3 block) {
        return () -> new DoubleOctreeIterator<T>(new OctreePointIterator<Entry<T>>(this.tree, block));
    }

    public boolean contains(double x, double y, double z, Object value) {
        Entry<T> entry = this.tree.get(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
        if (entry != null) {
            do {
                int compare;
                if ((compare = entry.compareTo(x, y, z)) > 0) {
                    return false;
                }
                if (compare != 0 || !LogicUtil.bothNullOrEqual(entry.getValue(), value)) continue;
                return true;
            } while ((entry = entry.next) != null);
        }
        return false;
    }

    public Collection<T> get(double x, double y, double z) {
        Entry<T> firstEntry = this.getFirstEntry(x, y, z);
        return firstEntry == null ? Collections.emptyList() : new PositionCollection<T>(firstEntry);
    }

    public void add(double x, double y, double z, T value) {
        this.addEntry(new Entry<T>(x, y, z, value));
    }

    public boolean remove(double x, double y, double z, Object value) {
        Entry<T> entry;
        int compare;
        this.remove_iter.reset();
        this.tree.remove_iter.reset(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
        while (this.remove_iter.hasNext() && (compare = (entry = this.remove_iter.nextEntry()).compareTo(x, y, z)) <= 0) {
            if (compare == 0 && LogicUtil.bothNullOrEqual(entry.getValue(), value)) {
                this.remove_iter.remove();
                return true;
            }
            if (compare <= 0) continue;
            break;
        }
        return false;
    }

    public boolean move(double oldX, double oldY, double oldZ, T oldValue, double newX, double newY, double newZ, T newValue) {
        return this.moveEntry(new Entry<T>(oldX, oldY, oldZ, oldValue), new Entry<T>(newX, newY, newZ, newValue));
    }

    public boolean move(double oldX, double oldY, double oldZ, T value, double newX, double newY, double newZ) {
        return this.moveEntry(new Entry<T>(oldX, oldY, oldZ, value), new Entry<T>(newX, newY, newZ, value));
    }

    public boolean containsEntry(Entry<?> entry) {
        Entry<T> existing = this.getFirstEntry(entry.getX(), entry.getY(), entry.getZ());
        if (existing == null) {
            return false;
        }
        if (existing.valueEquals(entry)) {
            return true;
        }
        existing = existing.next;
        while (existing != null && existing.equalsCoord(entry.getX(), entry.getY(), entry.getZ())) {
            if (existing.valueEquals(entry)) {
                return true;
            }
            existing = existing.next;
        }
        return false;
    }

    private Entry<T> getFirstEntry(double x, double y, double z) {
        Entry<T> entry = this.tree.get(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
        int compare;
        while (entry != null && (compare = entry.compareTo(x, y, z)) <= 0) {
            if (compare == 0) {
                return entry;
            }
            entry = entry.next;
        }
        return null;
    }

    public void addEntry(Entry<? extends T> value) {
        int index = this.tree.getValueIndex(value.getBlockX(), value.getBlockY(), value.getBlockZ(), true);
        Entry<T> currentEntry = this.tree.getValueAtIndex(index);
        if (currentEntry == null) {
            this.tree.putValueAtIndex(index, value);
            return;
        }
        Entry<T> previous = null;
        do {
            int compare;
            if ((compare = currentEntry.compareTo(value)) >= 0) {
                if (previous == null) {
                    previous = value;
                    this.tree.putValueAtIndex(index, previous);
                } else {
                    previous.next = value;
                }
                value.next = currentEntry;
                return;
            }
            previous = currentEntry;
        } while ((currentEntry = currentEntry.next) != null);
        previous.next = value;
        value.next = null;
    }

    public boolean removeEntry(Entry<?> entry) {
        return this.remove(entry.getX(), entry.getY(), entry.getZ(), entry.getValue());
    }

    public boolean moveEntry(Entry<? extends T> oldEntry, Entry<? extends T> newEntry) {
        if (oldEntry == null) {
            if (newEntry != null) {
                this.addEntry(newEntry);
            }
            return true;
        }
        if (newEntry == null) {
            return this.removeEntry(oldEntry);
        }
        if (oldEntry.equals(newEntry)) {
            return this.containsEntry(oldEntry);
        }
        int oldBlockX = oldEntry.getBlockX();
        int oldBlockY = oldEntry.getBlockY();
        int oldBlockZ = oldEntry.getBlockZ();
        int newBlockX = newEntry.getBlockX();
        int newBlockY = newEntry.getBlockY();
        int newBlockZ = newEntry.getBlockZ();
        int oldIndex = this.tree.getValueIndex(oldBlockX, oldBlockY, oldBlockZ, false);
        if (oldIndex == 0) {
            return false;
        }
        if (oldBlockX == newBlockX && oldBlockY == newBlockY && oldBlockZ == newBlockZ) {
            return this.moveEntryInChain(oldIndex, oldEntry, newEntry);
        }
        Entry<T> currentEntry = this.tree.getValueAtIndex(oldIndex);
        int compare = currentEntry.compareTo(oldEntry);
        if (compare == 0 && currentEntry.valueEquals(oldEntry)) {
            if (currentEntry.next == null) {
                this.tree.remove(oldBlockX, oldBlockY, oldBlockZ);
            } else {
                this.tree.putValueAtIndex(oldIndex, currentEntry.next);
            }
        } else {
            if (compare > 0) {
                return false;
            }
            while (true) {
                if (currentEntry.next == null || (compare = currentEntry.next.compareTo(oldEntry)) > 0) {
                    return false;
                }
                if (compare == 0 && currentEntry.next.valueEquals(oldEntry)) {
                    currentEntry.next = currentEntry.next.next;
                    break;
                }
                currentEntry = currentEntry.next;
            }
        }
        this.addEntry(newEntry);
        return true;
    }

    private boolean moveEntryInChain(int index, Entry<T> oldEntry, Entry<T> newEntry) {
        Entry<T> entryAtIndex = this.tree.getValueAtIndex(index);
        int compare = entryAtIndex.compareTo(oldEntry);
        if (compare == 0 && entryAtIndex.valueEquals(oldEntry)) {
            Entry currentEntry = entryAtIndex.next;
            if (currentEntry == null || (compare = currentEntry.compareTo(newEntry)) > 0) {
                newEntry.next = currentEntry;
                this.tree.putValueAtIndex(index, newEntry);
                return true;
            }
            while (currentEntry.next != null && (compare = currentEntry.next.compareTo(newEntry)) < 0) {
                currentEntry = currentEntry.next;
            }
            newEntry.next = currentEntry.next;
            currentEntry.next = newEntry;
            this.tree.putValueAtIndex(index, entryAtIndex.next);
            return true;
        }
        if (compare > 0) {
            return false;
        }
        compare = entryAtIndex.compareTo(newEntry);
        if (compare >= 0) {
            Entry<T> currentEntry = entryAtIndex;
            while (currentEntry.next != null) {
                compare = currentEntry.next.compareTo(oldEntry);
                if (compare == 0 && currentEntry.next.valueEquals(oldEntry)) {
                    newEntry.next = entryAtIndex;
                    currentEntry.next = currentEntry.next.next;
                    this.tree.putValueAtIndex(index, newEntry);
                    return true;
                }
                if (compare > 0) {
                    return false;
                }
                currentEntry = currentEntry.next;
            }
            return false;
        }
        if (oldEntry.compareTo(newEntry) < 0) {
            Entry<T> entryBeforeOldEntry = null;
            Entry<T> currentEntry = entryAtIndex;
            while (true) {
                if (currentEntry.next == null || (compare = currentEntry.next.compareTo(oldEntry)) > 0) {
                    return false;
                }
                if (compare == 0 && currentEntry.next.valueEquals(oldEntry)) break;
                currentEntry = currentEntry.next;
            }
            entryBeforeOldEntry = currentEntry;
            currentEntry = currentEntry.next.next;
            if (currentEntry == null || (compare = currentEntry.compareTo(newEntry)) >= 0) {
                newEntry.next = currentEntry;
                entryBeforeOldEntry.next = newEntry;
                return true;
            }
            while (true) {
                if (currentEntry.next == null || (compare = currentEntry.next.compareTo(newEntry)) >= 0) {
                    newEntry.next = currentEntry.next;
                    entryBeforeOldEntry.next = entryBeforeOldEntry.next.next;
                    currentEntry.next = newEntry;
                    return true;
                }
                currentEntry = currentEntry.next;
            }
        }
        Entry<T> entryBeforeNewEntry = null;
        Entry<T> currentEntry = entryAtIndex;
        while (true) {
            if (currentEntry.next == null || (compare = currentEntry.next.compareTo(newEntry)) >= 0) break;
            currentEntry = currentEntry.next;
        }
        entryBeforeNewEntry = currentEntry;
        currentEntry = currentEntry.next;
        if (currentEntry == null || (compare = currentEntry.compareTo(oldEntry)) > 0) {
            return false;
        }
        if (compare == 0 && currentEntry.valueEquals(oldEntry)) {
            newEntry.next = currentEntry.next;
            entryBeforeNewEntry.next = newEntry;
            return true;
        }
        while (currentEntry.next != null && (compare = currentEntry.next.compareTo(oldEntry)) <= 0) {
            if (compare == 0 && currentEntry.next.valueEquals(oldEntry)) {
                newEntry.next = entryBeforeNewEntry.next;
                currentEntry.next = currentEntry.next.next;
                entryBeforeNewEntry.next = newEntry;
                return true;
            }
            currentEntry = currentEntry.next;
        }
        return false;
    }

    private static final class DoubleOctreeValues<T>
    extends AbstractCollection<T> {
        private final Collection<Entry<T>> rootEntries;

        public DoubleOctreeValues(Collection<Entry<T>> rootEntries) {
            this.rootEntries = rootEntries;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>(){
                private Iterator<Entry<T>> rootEntryIter;
                private Entry<T> nextEntry;
                {
                    this.rootEntryIter = rootEntries.iterator();
                    this.nextEntry = null;
                }

                @Override
                public boolean hasNext() {
                    if (this.nextEntry == null) {
                        if (this.rootEntryIter.hasNext()) {
                            this.nextEntry = this.rootEntryIter.next();
                        } else {
                            return false;
                        }
                    }
                    return true;
                }

                @Override
                public T next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException("No more elements inside the tree");
                    }
                    Object value = this.nextEntry.getValue();
                    this.nextEntry = this.nextEntry.next;
                    return value;
                }
            };
        }

        @Override
        public int size() {
            int size = 0;
            Iterator<T> iter = this.iterator();
            while (iter.hasNext()) {
                ++size;
                iter.next();
            }
            return size;
        }
    }

    public static final class Entry<T>
    implements Comparable<Entry<?>> {
        private final double x;
        private final double y;
        private final double z;
        private final T value;
        protected Entry<T> next;

        public Entry(Vector position, T value) {
            this(position.getX(), position.getY(), position.getZ(), value);
        }

        public Entry(double x, double y, double z, T value) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.value = value;
            this.next = null;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }

        public int getBlockX() {
            return MathUtil.floor(this.x);
        }

        public int getBlockY() {
            return MathUtil.floor(this.y);
        }

        public int getBlockZ() {
            return MathUtil.floor(this.z);
        }

        public Vector toVector() {
            return new Vector(this.x, this.y, this.z);
        }

        public double distanceSquared(double x, double y, double z) {
            return MathUtil.distanceSquared(this.x, this.y, this.z, x, y, z);
        }

        public double distanceSquared(Vector pos) {
            return this.distanceSquared(pos.getX(), pos.getY(), pos.getZ());
        }

        public double distance(double x, double y, double z) {
            return Math.sqrt(this.distanceSquared(x, y, z));
        }

        public double distance(Vector pos) {
            return this.distance(pos.getX(), pos.getY(), pos.getZ());
        }

        public T getValue() {
            return this.value;
        }

        public boolean valueEquals(Object value) {
            return LogicUtil.bothNullOrEqual(this.value, value);
        }

        public boolean valueEquals(Entry<?> entry) {
            return this == entry || this.valueEquals(entry.getValue());
        }

        @Override
        public int compareTo(Entry<?> other) {
            return this.compareTo(other.x, other.y, other.z);
        }

        public int compareTo(double x, double y, double z) {
            int comp = Double.compare(this.x, x);
            if (comp == 0 && (comp = Double.compare(this.y, y)) == 0) {
                comp = Double.compare(this.z, z);
            }
            return comp;
        }

        public boolean equalsCoord(double x, double y, double z) {
            return this.x == x && this.y == y && this.z == z;
        }

        public boolean equalsCoord(Vector pos) {
            return this.x == pos.getX() && this.y == pos.getY() && this.z == pos.getZ();
        }

        public boolean equalsBlockCoord(int x, int y, int z) {
            return this.getBlockX() == x && this.getBlockY() == y && this.getBlockZ() == z;
        }

        public boolean equalsBlockCoord(IntVector3 blockCoord) {
            return this.equalsBlockCoord(blockCoord.x, blockCoord.y, blockCoord.z);
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof Entry) {
                Entry otherEntry = (Entry)other;
                return this.getX() == otherEntry.getX() && this.getY() == otherEntry.getY() && this.getZ() == otherEntry.getZ() && this.valueEquals(otherEntry.getValue());
            }
            return false;
        }

        public int hashCode() {
            return Double.hashCode(this.x) + (Double.hashCode(this.z) << 8) + (Double.hashCode(this.y) << 16);
        }

        public String toString() {
            return "{x:" + this.x + ", y:" + this.y + ", z:" + this.z + ", value:" + this.value + "}";
        }

        public static <T> Entry<T> create(Vector pos, T value) {
            return new Entry<T>(pos, value);
        }

        public static <T> Entry<T> create(double x, double y, double z, T value) {
            return new Entry<T>(x, y, z, value);
        }
    }

    private static final class PositionCollection<T>
    extends AbstractCollection<T> {
        private final Entry<T> root;

        public PositionCollection(Entry<T> root) {
            this.root = root;
        }

        private Entry<T> getNext(Entry<T> entry) {
            Entry next = entry.next;
            if (next != null && next.getX() == this.root.getX() && next.getY() == this.root.getY() && next.getZ() == this.root.getZ()) {
                return next;
            }
            return null;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>(){
                Entry<T> next;
                {
                    this.next = root;
                }

                @Override
                public boolean hasNext() {
                    return this.next != null;
                }

                @Override
                public T next() {
                    if (this.next == null) {
                        throw new NoSuchElementException("No next element available");
                    }
                    Object result = this.next.getValue();
                    this.next = this.getNext(this.next);
                    return result;
                }
            };
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            int size = 0;
            Entry<T> entry = this.root;
            do {
                ++size;
            } while ((entry = this.getNext(entry)) != null);
            return size;
        }
    }
}

