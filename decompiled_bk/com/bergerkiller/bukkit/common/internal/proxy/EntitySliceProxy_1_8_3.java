/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.util.ClassInstanceMultiMapHandle;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

public class EntitySliceProxy_1_8_3<E>
extends AbstractList<E> {
    private final ClassInstanceMultiMapHandle handle;
    private final List<E> listValues;

    public EntitySliceProxy_1_8_3(ClassInstanceMultiMapHandle handle) {
        this.handle = handle;
        this.listValues = ClassInstanceMultiMapHandle.T.listValues_1_8_3.get(handle.getRaw());
    }

    public ClassInstanceMultiMapHandle getHandle() {
        return this.handle;
    }

    @Override
    public boolean add(E e) {
        return this.handle.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.handle.remove(o);
    }

    @Override
    public void add(int index, E element) {
        if (!this.handle.add(element)) {
            throw new RuntimeException("Failed to insert new element to Entity Slice");
        }
        int oldIndex = this.listValues.lastIndexOf(element);
        if (oldIndex == -1) {
            throw new RuntimeException("Attempted to insert element to Entity Slice but it is now gone");
        }
        if (oldIndex != index) {
            this.listValues.remove(oldIndex);
            this.listValues.add(index, element);
        }
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
        return this.listValues.size();
    }

    @Override
    public boolean isEmpty() {
        return this.listValues.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.listValues.contains(o);
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
        return this.listValues.containsAll(c);
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
}

