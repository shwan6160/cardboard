/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.CollectionBasics
 *  com.bergerkiller.bukkit.common.collections.List2DIterator
 *  com.bergerkiller.bukkit.common.collections.List2DListIterator
 */
package com.bergerkiller.bukkit.tc.utils.modlist;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.collections.List2DIterator;
import com.bergerkiller.bukkit.common.collections.List2DListIterator;
import com.bergerkiller.bukkit.tc.utils.modlist.ModificationTrackedList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ModificationTrackedList2D<E>
extends AbstractList<E>
implements ModificationTrackedList<E> {
    private final ArrayList<List<E>> lists = new ArrayList();

    public void resetLists() {
        this.lists.clear();
        ++this.modCount;
    }

    public void addListIfNotEmpty(List<E> list) {
        if (!list.isEmpty()) {
            this.lists.add(list);
            ++this.modCount;
        }
    }

    public void removeList(List<E> list) {
        Iterator<List<E>> iter = this.lists.iterator();
        while (iter.hasNext()) {
            if (iter.next() != list) continue;
            iter.remove();
            ++this.modCount;
            break;
        }
    }

    @Override
    public int getModCount() {
        return this.modCount;
    }

    @Override
    public boolean add(E e) {
        Iterator<List<E>> iter = this.lists.iterator();
        List<E> rval = null;
        while (iter.hasNext()) {
            rval = iter.next();
        }
        ++this.modCount;
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
        return new List2DIterator(this.lists);
    }

    @Override
    public Object[] toArray() {
        return CollectionBasics.toArray((Collection)this);
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return CollectionBasics.toArray((Collection)this, (Object[])array);
    }

    @Override
    public boolean remove(Object o) {
        for (List<E> list : this.lists) {
            if (!list.remove(o)) continue;
            ++this.modCount;
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
        if (c.isEmpty()) {
            return true;
        }
        ++this.modCount;
        return CollectionBasics.getEntry(this.lists, (int)index).addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (List<E> list : this.lists) {
            changed |= list.removeAll(c);
        }
        if (changed) {
            ++this.modCount;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return CollectionBasics.retainAll((Collection)this, c);
    }

    @Override
    public void clear() {
        for (List<E> list : this.lists) {
            list.clear();
        }
    }

    @Override
    public E get(int index) {
        return (E)CollectionBasics.getEntry(this.lists, (int)index).get();
    }

    @Override
    public E set(int index, E element) {
        ++this.modCount;
        return (E)CollectionBasics.getEntry(this.lists, (int)index).set(element);
    }

    @Override
    public void add(int index, E element) {
        ++this.modCount;
        CollectionBasics.getEntry(this.lists, (int)index).add(element);
    }

    @Override
    public E remove(int index) {
        ++this.modCount;
        return (E)CollectionBasics.getEntry(this.lists, (int)index).remove();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E element : c) {
            changed |= this.add(element);
        }
        if (changed) {
            ++this.modCount;
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
        return new List2DListIterator(this.lists);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new List2DListIterator(this.lists, index);
    }
}

