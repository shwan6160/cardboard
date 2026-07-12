/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class OverwritingCircularBuffer<E>
implements Iterable<E> {
    private static final Object EOF_SENTINEL = new Object();
    private final Object[] values;
    private final AtomicInteger index = new AtomicInteger();
    private final AtomicInteger written = new AtomicInteger();

    public static <E> OverwritingCircularBuffer<E> create(int capacity) {
        return new OverwritingCircularBuffer<E>(capacity);
    }

    private OverwritingCircularBuffer(int capacity) {
        this.values = new Object[capacity];
        Arrays.fill(this.values, EOF_SENTINEL);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<E> values() {
        Object[] copy;
        int endIndex;
        int capacity = this.values.length;
        OverwritingCircularBuffer overwritingCircularBuffer = this;
        synchronized (overwritingCircularBuffer) {
            do {
                endIndex = Math.floorMod(this.index.get(), capacity);
                copy = (Object[])this.values.clone();
            } while (endIndex != Math.floorMod(this.index.get(), capacity) || endIndex != Math.floorMod(this.written.get(), capacity));
        }
        if (copy[endIndex] == EOF_SENTINEL) {
            return Collections.emptyList();
        }
        int startIndex = endIndex;
        while (copy[startIndex = Math.floorMod(startIndex + 1, capacity)] == EOF_SENTINEL) {
        }
        if (startIndex == endIndex) {
            return Collections.singletonList(copy[endIndex]);
        }
        ArrayList<Object> result = new ArrayList<Object>(Math.floorMod(endIndex - startIndex + 1, capacity));
        int curr = startIndex - 1;
        do {
            curr = Math.floorMod(curr + 1, capacity);
            result.add(copy[curr]);
        } while (curr != endIndex);
        return Collections.unmodifiableList(result);
    }

    public int capacity() {
        return this.values.length;
    }

    public void clear() {
        Arrays.fill(this.values, EOF_SENTINEL);
    }

    public void add(E value) {
        boolean normalize;
        Object[] values = this.values;
        int currIndex = this.index.incrementAndGet();
        boolean bl = normalize = currIndex >= values.length;
        if (normalize) {
            currIndex = Math.floorMod(currIndex, values.length);
        }
        values[currIndex] = value;
        this.written.incrementAndGet();
        if (normalize) {
            this.normalizeIndex();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return this.values().iterator();
    }

    public String toString() {
        return this.values().toString();
    }

    private synchronized void normalizeIndex() {
        int currIndex;
        int len = this.values.length;
        while ((currIndex = this.index.get()) >= len && !this.index.compareAndSet(currIndex, Math.floorMod(currIndex, len))) {
        }
        while ((currIndex = this.written.get()) >= len && !this.written.compareAndSet(currIndex, Math.floorMod(currIndex, len))) {
        }
    }
}

