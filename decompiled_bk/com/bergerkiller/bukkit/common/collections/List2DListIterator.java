/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class List2DListIterator<E>
implements ListIterator<E> {
    private final Collection<List<E>> lists;
    private int index;

    public List2DListIterator(Collection<List<E>> lists) {
        this(lists, 0);
    }

    public List2DListIterator(Collection<List<E>> lists, int index) {
        this.lists = lists;
        this.index = index;
    }

    private CollectionBasics.ListEntry<E> get() {
        return CollectionBasics.getEntry(this.lists, this.index);
    }

    private int size() {
        int size = 0;
        for (List<E> list : this.lists) {
            size += list.size();
        }
        return size;
    }

    @Override
    public boolean hasNext() {
        return this.index < this.size() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return this.index > 0;
    }

    @Override
    public E next() {
        try {
            E e = this.get().get();
            return e;
        }
        finally {
            ++this.index;
        }
    }

    @Override
    public void remove() {
        this.get().remove();
    }

    @Override
    public E previous() {
        --this.index;
        return this.get().get();
    }

    @Override
    public int nextIndex() {
        return this.index;
    }

    @Override
    public int previousIndex() {
        return this.index - 1;
    }

    @Override
    public void set(E e) {
        this.get().set(e);
    }

    @Override
    public void add(E e) {
        this.get().add(e);
    }
}

