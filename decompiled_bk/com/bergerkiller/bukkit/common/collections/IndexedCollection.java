/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IndexedCollection<T>
extends AbstractCollection<T> {
    private Object[] _values;
    private int _freeIndex;
    private int _size;
    private int _reserved = 0;

    public IndexedCollection() {
        this.clear();
    }

    @Override
    public int size() {
        return this._size;
    }

    @Override
    public void clear() {
        this.clearAndReserve(this._reserved);
    }

    public void reserve(int reserved) {
        if (this._size != 0) {
            throw new IllegalStateException("Can not set an offset when collection is not empty");
        }
        if (this._reserved != reserved) {
            this.clearAndReserve(reserved);
        }
    }

    private void clearAndReserve(int newReserved) {
        int initialSize;
        for (initialSize = 1; initialSize <= newReserved; initialSize <<= 1) {
        }
        if (this._values == null) {
            this._values = new Object[initialSize];
        } else if (this._values.length != initialSize) {
            Object[] newValues = new Object[initialSize];
            for (int i = 0; i < newReserved; ++i) {
                newValues[i] = i < this._reserved ? this._values[i] : null;
            }
            this._values = newValues;
        }
        this.createFreeIndex(newReserved, this._values.length);
        this._size = 0;
        this._reserved = newReserved;
    }

    public void setAt(int index, T value) {
        this._values[index] = value;
    }

    public T getAt(int index) {
        return (T)this._values[index];
    }

    public void removeAt(int index) {
        FreeValue free = new FreeValue(this._freeIndex);
        this._freeIndex = index;
        this._values[index] = free;
        --this._size;
    }

    public int addAndGetIndex(T value) {
        int valueIndex = this._freeIndex;
        this._freeIndex = ((FreeValue)this._values[this._freeIndex]).next;
        this._values[valueIndex] = value;
        ++this._size;
        if (this._freeIndex < 0) {
            int firstIndex = this._values.length;
            this._values = Arrays.copyOf(this._values, this._values.length << 1);
            this.createFreeIndex(firstIndex, this._values.length);
        }
        return valueIndex;
    }

    private void createFreeIndex(int startIndex, int endIndex) {
        this._freeIndex = startIndex;
        for (int index = startIndex; index < endIndex - 1; ++index) {
            this._values[index] = new FreeValue(index + 1);
        }
        this._values[index] = new FreeValue(-1);
    }

    public int indexOf(Object value) {
        if (value == null) {
            for (int i = this._reserved; i < this._values.length; ++i) {
                if (this._values[i] != null) continue;
                return i;
            }
        } else {
            for (int i = this._reserved; i < this._values.length; ++i) {
                Object stored = this._values[i];
                if (stored == null || !stored.equals(value)) continue;
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean contains(Object value) {
        return this.indexOf(value) != -1;
    }

    @Override
    public boolean remove(Object value) {
        int index = this.indexOf(value);
        if (index != -1) {
            this.removeAt(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean add(T value) {
        this.addAndGetIndex(value);
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new IndexedCollectionIterator(this);
    }

    private static final class FreeValue {
        public final int next;

        public FreeValue(int next) {
            this.next = next;
        }

        public boolean equals(Object o) {
            return false;
        }
    }

    private static final class IndexedCollectionIterator<T>
    implements Iterator<T> {
        private static final int INDEX_FLAG_UNSET = -1;
        private static final int INDEX_FLAG_FINISHED = -2;
        private final IndexedCollection<T> collection;
        private int index;
        private int nextIndex;

        public IndexedCollectionIterator(IndexedCollection<T> collection) {
            this.collection = collection;
            this.index = -1;
            this.nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            if (this.nextIndex == -1) {
                this.findNext();
            }
            return this.nextIndex != -2;
        }

        @Override
        public T next() {
            if (this.nextIndex == -1) {
                this.findNext();
            }
            if (this.nextIndex == -2) {
                throw new NoSuchElementException("No more elements");
            }
            this.index = this.nextIndex;
            this.nextIndex = -1;
            return (T)((IndexedCollection)this.collection)._values[this.index];
        }

        @Override
        public void remove() {
            if (this.index == -1) {
                throw new NoSuchElementException("No element was previously iterated using next()");
            }
            this.collection.removeAt(this.index);
            if (this.nextIndex == -1) {
                this.findNext();
            }
            this.index = -1;
        }

        private void findNext() {
            this.nextIndex = this.index == -1 ? ((IndexedCollection)this.collection)._reserved - 1 : this.index;
            Object[] values = ((IndexedCollection)this.collection)._values;
            while (++this.nextIndex < values.length) {
                if (values[this.nextIndex] instanceof FreeValue) continue;
                return;
            }
            this.nextIndex = -2;
        }
    }
}

