/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.util.ClassInstanceMultiMapHandle;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class EntitySliceProxy_1_8<E>
extends AbstractList<E> {
    private final ClassInstanceMultiMapHandle handle;
    private final List<E> listValues;

    public EntitySliceProxy_1_8(ClassInstanceMultiMapHandle handle) {
        this.handle = handle;
        this.listValues = new ArrayList(handle.size());
        Iterator<E> iter = this.handleIter();
        while (iter.hasNext()) {
            this.listValues.add(iter.next());
        }
    }

    public ClassInstanceMultiMapHandle getHandle() {
        return this.handle;
    }

    @Override
    public boolean add(E e) {
        if (!this.handle.add(e)) {
            return false;
        }
        this.listValues.add(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!this.handle.remove(o)) {
            return false;
        }
        this.listValues.remove(o);
        return true;
    }

    @Override
    public void add(int index, E element) {
        if (!this.handle.add(element)) {
            throw new RuntimeException("Failed to insert new element to Entity Slice");
        }
        this.listValues.add(index, element);
    }

    @Override
    public E set(int index, E element) {
        E old = this.remove(index);
        this.add(index, element);
        return old;
    }

    @Override
    public E remove(int index) {
        E result = this.get(index);
        this.remove(result);
        return result;
    }

    @Override
    public void clear() {
        while (!this.isEmpty()) {
            this.remove(this.get(0));
        }
    }

    @Override
    public int size() {
        return this.handle.size();
    }

    @Override
    public boolean isEmpty() {
        return this.handle.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        Iterator<E> iter = this.handleIter();
        while (iter.hasNext()) {
            if (!iter.next().equals(o)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        return this.listValues.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.listValues.toArray(a);
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
    public E get(int index) {
        return this.listValues.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.listValues.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.listValues.lastIndexOf(o);
    }

    private Iterator<E> handleIter() {
        return this.handle.iterator();
    }
}

