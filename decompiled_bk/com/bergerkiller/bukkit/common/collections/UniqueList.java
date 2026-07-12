/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class UniqueList<E>
extends ArrayList<E> {
    private static final long serialVersionUID = 1L;
    private final HashSet<E> uniqueElements;

    public UniqueList() {
        this(10);
    }

    public UniqueList(Collection<E> contents) {
        this(contents.size());
        this.addAll(contents);
    }

    public UniqueList(int capacity) {
        super(capacity);
        this.uniqueElements = new HashSet(capacity);
    }

    @Override
    public void clear() {
        super.clear();
        this.uniqueElements.clear();
    }

    @Override
    public boolean contains(Object o) {
        return this.uniqueElements.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.uniqueElements.containsAll(c);
    }

    @Override
    public boolean remove(Object o) {
        return this.uniqueElements.remove(o) && super.remove(o);
    }

    @Override
    public boolean add(E e) {
        return this.uniqueElements.add(e) && super.add(e);
    }

    @Override
    public E remove(int index) {
        Object removed = super.remove(index);
        this.uniqueElements.remove(removed);
        return removed;
    }

    @Override
    public E set(int index, E e) {
        Object oldValue = this.get(index);
        if (oldValue != e) {
            this.uniqueElements.remove(oldValue);
            if (this.uniqueElements.add(e)) {
                super.set(index, e);
            } else {
                this.uniqueElements.add(oldValue);
            }
        }
        return oldValue;
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        return CollectionBasics.addAll(this, elements);
    }

    @Override
    public boolean removeAll(Collection<?> elements) {
        if (this.size() <= 10) {
            return CollectionBasics.removeAll(this, elements);
        }
        boolean changed = this.uniqueElements.removeAll(elements);
        if (changed) {
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                if (this.uniqueElements.contains(iter.next())) continue;
                iter.remove();
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> elements) {
        return CollectionBasics.retainAll(this, elements);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; ++i) {
            this.uniqueElements.remove(this.get(i));
        }
        super.removeRange(fromIndex, toIndex);
    }
}

