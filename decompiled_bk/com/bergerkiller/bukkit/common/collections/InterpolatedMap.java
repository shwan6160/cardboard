/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class InterpolatedMap {
    private final List<Entry> entries = new ArrayList<Entry>();

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public double get(double key) {
        ListIterator<Entry> listiter = this.entries.listIterator();
        Entry entry = null;
        while (listiter.hasNext()) {
            entry = listiter.next();
            if (entry.key == key) {
                return entry.value;
            }
            if (!(entry.key > key)) continue;
            if (listiter.hasPrevious()) {
                Entry previous = listiter.previous();
                double stage = (key - previous.key) / (entry.key - previous.key);
                return MathUtil.lerp(previous.value, entry.value, stage);
            }
            return entry.value;
        }
        if (entry == null) {
            return 0.0;
        }
        return entry.value;
    }

    public void clear() {
        this.entries.clear();
    }

    public void put(double key, double value) {
        ListIterator<Entry> listiter = this.entries.listIterator();
        Entry newEntry = new Entry(key, value);
        if (this.entries.isEmpty()) {
            this.entries.add(newEntry);
            return;
        }
        while (listiter.hasNext()) {
            Entry entry = listiter.next();
            if (key < entry.key) {
                if (listiter.hasPrevious()) {
                    listiter.previous();
                    listiter.add(newEntry);
                } else {
                    this.entries.add(0, newEntry);
                }
                return;
            }
            if (key != entry.key) continue;
            listiter.set(newEntry);
            return;
        }
        this.entries.add(newEntry);
    }

    private static class Entry {
        public final double key;
        public final double value;

        public Entry(double key, double value) {
            this.key = key;
            this.value = value;
        }
    }
}

