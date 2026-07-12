/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.collections.List2DIterator;
import com.bergerkiller.bukkit.common.collections.List2DListIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class List2D<E>
implements List<E> {
    private final Collection<List<E>> lists;

    public List2D(List<E>[] lists) {
        if (lists == null) {
            throw new IllegalArgumentException("Array of lists can not be null");
        }
        if (lists.length == 0) {
            throw new IllegalArgumentException("Can not use an empty array of lists");
        }
        this.lists = Arrays.asList(lists);
    }

    public List2D(Collection<List<E>> lists) {
        if (lists == null) {
            throw new IllegalArgumentException("Collection of lists can not be null");
        }
        if (lists.isEmpty()) {
            throw new IllegalArgumentException("Can not use an empty collection of lists");
        }
        this.lists = lists;
    }

    @Override
    public boolean add(E e) {
        Iterator<List<E>> iter = this.lists.iterator();
        List<E> rval = null;
        while (iter.hasNext()) {
            rval = iter.next();
        }
        return rval.add(e);
    }

    @Override
    public int size() {
        int size = 0;
        for (List<E> list : this.lists) {
            size += list.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (List<E> list : this.lists) {
            if (list.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        for (List<E> list : this.lists) {
            if (!list.contains(o)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new List2DIterator<E>(this.lists);
    }

    @Override
    public Object[] toArray() {
        return CollectionBasics.toArray(this);
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return CollectionBasics.toArray(this, array);
    }

    @Override
    public boolean remove(Object o) {
        for (List<E> list : this.lists) {
            if (!list.remove(o)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (this.contains(o)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return CollectionBasics.getEntry(this.lists, index).addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (List<E> list : this.lists) {
            changed |= list.removeAll(c);
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return CollectionBasics.retainAll(this, c);
    }

    @Override
    public void clear() {
        for (List<E> list : this.lists) {
            list.clear();
        }
    }

    @Override
    public E get(int index) {
        return (E)CollectionBasics.getEntry(this.lists, index).get();
    }

    @Override
    public E set(int index, E element) {
        return (E)CollectionBasics.getEntry(this.lists, index).set(element);
    }

    @Override
    public void add(int index, E element) {
        CollectionBasics.getEntry(this.lists, index).add(element);
    }

    @Override
    public E remove(int index) {
        return (E)CollectionBasics.getEntry(this.lists, index).remove();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E element : c) {
            changed |= this.add(element);
        }
        return changed;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        for (List<E> list : this.lists) {
            int subIndex = list.indexOf(o);
            if (subIndex == -1) {
                index += list.size();
                continue;
            }
            return index + subIndex;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int rval = -1;
        int index = 0;
        for (List<E> list : this.lists) {
            int subIndex = list.lastIndexOf(o);
            if (subIndex == -1) {
                index += list.size();
                continue;
            }
            rval = index + subIndex;
        }
        return rval;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new List2DListIterator<E>(this.lists);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new List2DListIterator<E>(this.lists, index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Unimplemented feature");
    }
}

