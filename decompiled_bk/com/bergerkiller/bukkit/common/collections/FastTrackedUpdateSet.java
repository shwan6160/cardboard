/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public final class FastTrackedUpdateSet<E> {
    private final List<Tracker<E>> trackers = new ArrayList<Tracker<E>>();
    private final Iterable<E> iterable;
    private boolean enabled;

    public FastTrackedUpdateSet() {
        TrackerIterator iter = new TrackerIterator();
        this.iterable = () -> iter;
        this.enabled = true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            this.clear();
        }
    }

    public Tracker<E> track(E value) {
        return new Tracker(this, value, this.enabled);
    }

    public Iterable<E> iterateAndClear() {
        return this.iterable;
    }

    public void forEachAndClear(Consumer<? super E> action) {
        this.iterable.forEach(action);
    }

    public void clear() {
        this.trackers.forEach(t -> {
            ((Tracker)t).addedToList = false;
            ((Tracker)t).isSet = false;
        });
        this.trackers.clear();
    }

    private final class TrackerIterator
    implements Iterator<E> {
        private FallbackIterator<Tracker<E>> iter;
        private Tracker<E> current;

        private TrackerIterator() {
        }

        private boolean startIterating() {
            if (this.iter == null) {
                if (FastTrackedUpdateSet.this.trackers.isEmpty()) {
                    return false;
                }
                this.iter = new FallbackIterator(FastTrackedUpdateSet.this.trackers);
                return this.prepNextValue();
            }
            return true;
        }

        private boolean prepNextValue() {
            while (this.iter.hasNext()) {
                Tracker t = this.iter.next();
                t.addedToList = false;
                if (!t.isSet) continue;
                t.isSet = false;
                this.current = t;
                return true;
            }
            this.iter = null;
            this.current = null;
            FastTrackedUpdateSet.this.trackers.clear();
            return false;
        }

        @Override
        public boolean hasNext() {
            return this.startIterating();
        }

        @Override
        public E next() {
            if (!this.startIterating()) {
                throw new NoSuchElementException();
            }
            try {
                Object object = this.current.value;
                return object;
            }
            finally {
                this.prepNextValue();
            }
        }
    }

    public static final class Tracker<E> {
        private final FastTrackedUpdateSet<E> owner;
        private final E value;
        private boolean tracked;
        private boolean addedToList;
        private boolean isSet;

        private Tracker(FastTrackedUpdateSet<E> owner, E value, boolean tracked) {
            this.owner = owner;
            this.value = value;
            this.addedToList = false;
            this.isSet = false;
            this.tracked = tracked;
        }

        public E getValue() {
            return this.value;
        }

        public boolean isSet() {
            return this.isSet;
        }

        public void set(boolean added) {
            if (!this.tracked) {
                return;
            }
            this.isSet = added;
            if (added && !this.addedToList) {
                this.addedToList = true;
                ((FastTrackedUpdateSet)this.owner).trackers.add(this);
            }
        }

        public void untrack() {
            this.tracked = false;
            this.isSet = false;
        }
    }

    private static final class FallbackIterator<E>
    implements Iterator<E> {
        private final List<E> list;
        private Iterator<E> iter;
        private int index;

        public FallbackIterator(List<E> list) {
            this.list = list;
            this.iter = list.iterator();
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            try {
                return this.iter.hasNext();
            }
            catch (ConcurrentModificationException ex) {
                this.iter = new ByIndexIterator();
                return this.iter.hasNext();
            }
        }

        @Override
        public E next() {
            try {
                return this.iter.next();
            }
            catch (ConcurrentModificationException ex) {
                this.iter = new ByIndexIterator();
                return this.iter.next();
            }
        }

        private final class ByIndexIterator
        implements Iterator<E> {
            private ByIndexIterator() {
            }

            @Override
            public boolean hasNext() {
                return FallbackIterator.this.index < FallbackIterator.this.list.size();
            }

            @Override
            public E next() {
                try {
                    return FallbackIterator.this.list.get(FallbackIterator.this.index++);
                }
                catch (IndexOutOfBoundsException ex) {
                    --FallbackIterator.this.index;
                    throw new NoSuchElementException();
                }
            }
        }
    }
}

