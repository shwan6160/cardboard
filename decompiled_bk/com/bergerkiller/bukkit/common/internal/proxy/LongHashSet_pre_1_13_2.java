/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.mountiplex.MountiplexUtil;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongHashSet_pre_1_13_2
implements Cloneable {
    private static final int INITIAL_SIZE = 3;
    private static final double LOAD_FACTOR = 0.75;
    private static final long FREE = 0L;
    private static final long REMOVED = Long.MIN_VALUE;
    private int freeEntries;
    private int elements;
    private long[] values;
    private int modCount;
    private boolean has_free_value;
    private boolean has_removed_value;

    public LongHashSet_pre_1_13_2() {
        this(3);
    }

    public LongHashSet_pre_1_13_2(int size) {
        this.values = new long[size == 0 ? 1 : size];
        this.elements = 0;
        this.freeEntries = this.values.length;
        this.modCount = 0;
        this.has_free_value = false;
        this.has_removed_value = false;
    }

    public LongIterator iterator() {
        return new LongIterator();
    }

    public int size() {
        return this.elements + (this.has_free_value ? 1 : 0) + (this.has_removed_value ? 1 : 0);
    }

    public boolean isEmpty() {
        return this.elements == 0 && !this.has_free_value && !this.has_removed_value;
    }

    public boolean contains(long value) {
        if (value == 0L) {
            return this.has_free_value;
        }
        if (value == Long.MIN_VALUE) {
            return this.has_removed_value;
        }
        int hash = this.hash(value);
        int index = (hash & Integer.MAX_VALUE) % this.values.length;
        int offset = 1;
        while (this.values[index] != 0L && (this.hash(this.values[index]) != hash || this.values[index] != value)) {
            index = (index + offset & Integer.MAX_VALUE) % this.values.length;
            if ((offset = offset * 2 + 1) != -1) continue;
            offset = 2;
        }
        return this.values[index] != 0L;
    }

    public boolean add(long value) {
        if (value == 0L) {
            if (this.has_free_value) {
                return false;
            }
            this.has_free_value = true;
            return true;
        }
        if (value == Long.MIN_VALUE) {
            if (this.has_removed_value) {
                return false;
            }
            this.has_removed_value = true;
            return true;
        }
        int hash = this.hash(value);
        int index = (hash & Integer.MAX_VALUE) % this.values.length;
        int offset = 1;
        int deletedix = -1;
        while (this.values[index] != 0L && (this.hash(this.values[index]) != hash || this.values[index] != value)) {
            if (this.values[index] == Long.MIN_VALUE) {
                deletedix = index;
            }
            index = (index + offset & Integer.MAX_VALUE) % this.values.length;
            if ((offset = offset * 2 + 1) != -1) continue;
            offset = 2;
        }
        if (this.values[index] == 0L) {
            if (deletedix != -1) {
                index = deletedix;
            } else {
                --this.freeEntries;
            }
            ++this.modCount;
            ++this.elements;
            this.values[index] = value;
            if (1.0 - (double)this.freeEntries / (double)this.values.length > 0.75) {
                this.rehash();
            }
            return true;
        }
        return false;
    }

    public boolean remove(long value) {
        if (value == 0L) {
            if (!this.has_free_value) {
                return false;
            }
            this.has_free_value = false;
            return true;
        }
        if (value == Long.MIN_VALUE) {
            if (!this.has_removed_value) {
                return false;
            }
            this.has_removed_value = false;
            return true;
        }
        int hash = this.hash(value);
        int index = (hash & Integer.MAX_VALUE) % this.values.length;
        int offset = 1;
        while (this.values[index] != 0L && (this.hash(this.values[index]) != hash || this.values[index] != value)) {
            index = (index + offset & Integer.MAX_VALUE) % this.values.length;
            if ((offset = offset * 2 + 1) != -1) continue;
            offset = 2;
        }
        if (this.values[index] != 0L) {
            this.values[index] = Long.MIN_VALUE;
            ++this.modCount;
            --this.elements;
            return true;
        }
        return false;
    }

    public void clear() {
        this.elements = 0;
        this.has_free_value = false;
        this.has_removed_value = false;
        for (int ix = 0; ix < this.values.length; ++ix) {
            this.values[ix] = 0L;
        }
        this.freeEntries = this.values.length;
        ++this.modCount;
    }

    public long[] toArray() {
        long[] result = new long[this.size()];
        long[] values = Arrays.copyOf(this.values, this.values.length);
        int pos = 0;
        if (this.has_free_value) {
            result[pos++] = 0L;
        }
        if (this.has_removed_value) {
            result[pos++] = Long.MIN_VALUE;
        }
        for (long value : values) {
            if (value == 0L || value == Long.MIN_VALUE) continue;
            result[pos++] = value;
        }
        return result;
    }

    public long popFirst() {
        if (this.has_free_value) {
            this.has_free_value = false;
            return 0L;
        }
        if (this.has_removed_value) {
            this.has_removed_value = false;
            return Long.MIN_VALUE;
        }
        for (long value : this.values) {
            if (value == 0L || value == Long.MIN_VALUE) continue;
            this.remove(value);
            return value;
        }
        throw new NoSuchElementException();
    }

    public long[] popAll() {
        long[] ret = this.toArray();
        this.clear();
        return ret;
    }

    private int hash(long value) {
        value ^= value >>> 33;
        value *= -49064778989728563L;
        value ^= value >>> 33;
        value *= -4265267296055464877L;
        value ^= value >>> 33;
        return (int)value;
    }

    private void rehash() {
        int gargagecells = this.values.length - (this.elements + this.freeEntries);
        if ((double)gargagecells / (double)this.values.length > 0.05) {
            this.rehash(this.values.length);
        } else {
            this.rehash(this.values.length * 2 + 1);
        }
    }

    private void rehash(int newCapacity) {
        long[] newValues = new long[newCapacity];
        for (long value : this.values) {
            if (value == 0L || value == Long.MIN_VALUE) continue;
            int hash = this.hash(value);
            int index = (hash & Integer.MAX_VALUE) % newCapacity;
            int offset = 1;
            while (newValues[index] != 0L) {
                index = (index + offset & Integer.MAX_VALUE) % newCapacity;
                if ((offset = offset * 2 + 1) != -1) continue;
                offset = 2;
            }
            newValues[index] = value;
        }
        this.values = newValues;
        this.freeEntries = this.values.length - this.elements;
    }

    public LongHashSet_pre_1_13_2 clone() {
        LongHashSet_pre_1_13_2 clone;
        try {
            clone = (LongHashSet_pre_1_13_2)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw MountiplexUtil.uncheckedRethrow(e);
        }
        clone.values = (long[])clone.values.clone();
        clone.modCount = 0;
        return clone;
    }

    public final class LongIterator
    implements Iterator<Long> {
        private static final int LAST_RETURNED_INITIAL = -1;
        private static final int LAST_RETURNED_END = -2;
        private static final int LAST_RETURNED_FREE = -3;
        private static final int LAST_RETURNED_REMOVED = -4;
        private int index = 0;
        private int lastReturned = -1;
        private int expectedModCount;

        public LongIterator() {
            while (this.index < LongHashSet_pre_1_13_2.this.values.length && (LongHashSet_pre_1_13_2.this.values[this.index] == 0L || LongHashSet_pre_1_13_2.this.values[this.index] == Long.MIN_VALUE)) {
                ++this.index;
            }
            this.expectedModCount = LongHashSet_pre_1_13_2.this.modCount;
        }

        @Override
        public boolean hasNext() {
            if (this.index != LongHashSet_pre_1_13_2.this.values.length) {
                return true;
            }
            if (LongHashSet_pre_1_13_2.this.has_free_value && this.lastReturned >= 0) {
                return true;
            }
            return LongHashSet_pre_1_13_2.this.has_removed_value && (this.lastReturned >= 0 || this.lastReturned == -3);
        }

        @Override
        public Long next() {
            return this.nextLong();
        }

        public long nextLong() {
            if (LongHashSet_pre_1_13_2.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            int length = LongHashSet_pre_1_13_2.this.values.length;
            if (this.index >= length) {
                if (LongHashSet_pre_1_13_2.this.has_free_value && this.lastReturned >= 0) {
                    this.lastReturned = -3;
                    return 0L;
                }
                if (LongHashSet_pre_1_13_2.this.has_removed_value && (this.lastReturned >= 0 || this.lastReturned == -3)) {
                    this.lastReturned = -4;
                    return Long.MIN_VALUE;
                }
                this.lastReturned = -2;
                throw new NoSuchElementException();
            }
            this.lastReturned = this.index++;
            while (this.index < length && (LongHashSet_pre_1_13_2.this.values[this.index] == 0L || LongHashSet_pre_1_13_2.this.values[this.index] == Long.MIN_VALUE)) {
                ++this.index;
            }
            if (LongHashSet_pre_1_13_2.this.values[this.lastReturned] == 0L) {
                return 0L;
            }
            return LongHashSet_pre_1_13_2.this.values[this.lastReturned];
        }

        @Override
        public void remove() {
            if (LongHashSet_pre_1_13_2.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.lastReturned == -3) {
                LongHashSet_pre_1_13_2.this.has_free_value = false;
            } else if (this.lastReturned == -4) {
                LongHashSet_pre_1_13_2.this.has_removed_value = false;
            } else {
                if (this.lastReturned == -1 || this.lastReturned == -2) {
                    throw new IllegalStateException();
                }
                if (LongHashSet_pre_1_13_2.this.values[this.lastReturned] != 0L && LongHashSet_pre_1_13_2.this.values[this.lastReturned] != Long.MIN_VALUE) {
                    ((LongHashSet_pre_1_13_2)LongHashSet_pre_1_13_2.this).values[this.lastReturned] = Long.MIN_VALUE;
                    LongHashSet_pre_1_13_2.this.elements--;
                    LongHashSet_pre_1_13_2.this.modCount++;
                    this.expectedModCount = LongHashSet_pre_1_13_2.this.modCount;
                }
            }
        }
    }
}

