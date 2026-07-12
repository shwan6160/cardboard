/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.filtering.Filter;
import com.bergerkiller.bukkit.common.filtering.FilterBundle;
import com.bergerkiller.bukkit.common.filtering.FilterNull;
import com.bergerkiller.bukkit.common.filtering.FilterType;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilteredCollection<E>
implements Collection<E> {
    private final Collection<E> base;
    protected Filter<E> filter;

    public FilteredCollection(Collection<E> base, Filter<E> filter) {
        this.base = base;
        this.filter = filter;
    }

    @Override
    public Iterator<E> iterator() {
        return new FilteredIterator(this);
    }

    public Filter<E> getFilter() {
        return this.filter;
    }

    public void setFilter(Filter<E> filter) {
        this.filter = filter;
    }

    @Override
    public int size() {
        int size = 0;
        for (E value : this.base) {
            if (this.filter.isFiltered(value)) continue;
            ++size;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        if (!this.base.isEmpty()) {
            for (E value : this.base) {
                if (this.filter.isFiltered(value)) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return this.base.contains(o);
    }

    @Override
    public Object[] toArray() {
        return CollectionBasics.toArray(this);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return CollectionBasics.toArray(this, a);
    }

    @Override
    public boolean add(E e) {
        if (this.filter.isFiltered(e)) {
            return false;
        }
        this.base.add(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Iterator<E> iter = this.base.iterator();
        while (iter.hasNext()) {
            E value = iter.next();
            if (!LogicUtil.bothNullOrEqual(value, o) || this.filter.isFiltered(value)) continue;
            iter.remove();
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return CollectionBasics.containsAll(this, c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return CollectionBasics.addAll(this, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.size() > this.size()) {
            return CollectionBasics.removeAll(this, c);
        }
        Iterator<E> iter = this.base.iterator();
        boolean removed = false;
        while (iter.hasNext()) {
            E value = iter.next();
            if (!c.contains(value) || this.filter.isFiltered(value)) continue;
            iter.remove();
            removed = true;
        }
        return removed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return CollectionBasics.retainAll(this, c);
    }

    @Override
    public void clear() {
        Iterator<E> iter = this.base.iterator();
        if (!this.filter.isFiltered(iter.next())) {
            iter.remove();
        }
    }

    public static <T> FilteredCollection<T> create(Collection<T> base, Filter<T> filter) {
        return new FilteredCollection<T>(base, filter);
    }

    public static <T> FilteredCollection<T> createBundleFilter(Collection<T> base, Filter<?> ... filters) {
        return FilteredCollection.create(base, new FilterBundle(filters));
    }

    public static <T> FilteredCollection<T> createBundleFilter(Collection<T> base, Collection<Filter<?>> filters) {
        return FilteredCollection.create(base, new FilterBundle(filters));
    }

    public static <T> FilteredCollection<T> createNullFilter(Collection<T> base) {
        return FilteredCollection.create(base, FilterNull.INSTANCE);
    }

    public static <T> FilteredCollection<T> createTypeFilter(Collection<T> base, Class<?> type) {
        return FilteredCollection.create(base, new FilterType(type));
    }

    private static class FilteredIterator<T>
    implements Iterator<T> {
        private final Iterator<T> base;
        private final FilteredCollection<T> collection;
        private boolean hasNext;
        private T next;

        public FilteredIterator(FilteredCollection<T> collection) {
            this.base = ((FilteredCollection)collection).base.iterator();
            this.collection = collection;
            this.genNext();
        }

        private void genNext() {
            while (this.base.hasNext()) {
                this.next = this.base.next();
                if (this.collection.filter.isFiltered(this.next)) continue;
                this.hasNext = true;
                return;
            }
            this.hasNext = false;
        }

        @Override
        public boolean hasNext() {
            return this.hasNext;
        }

        @Override
        public T next() {
            if (!this.hasNext) {
                throw new NoSuchElementException("No new elements are available");
            }
            T next = this.next;
            this.genNext();
            return next;
        }

        @Override
        public void remove() {
            this.base.remove();
        }
    }
}

