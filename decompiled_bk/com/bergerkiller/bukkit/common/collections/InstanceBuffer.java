/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public abstract class InstanceBuffer<E>
extends AbstractList<E> {
    private final ArrayList<E> buffer = new ArrayList();
    private int size = 0;

    public abstract E createElement();

    public E add() {
        this.ensureCapacity(this.size + 1);
        return this.buffer.get(this.size - 1);
    }

    public List<E> addAll(int amount) {
        int startIndex = this.size - 1;
        this.ensureCapacity(this.size + amount);
        return this.buffer.subList(startIndex, startIndex + amount);
    }

    @Override
    public E remove(int index) {
        E element = this.buffer.remove(index);
        --this.size;
        return element;
    }

    @Override
    public E get(int index) {
        this.ensureCapacity(index + 1);
        return this.buffer.get(index);
    }

    @Override
    public E set(int index, E element) {
        if (index < this.size) {
            return this.buffer.set(index, element);
        }
        this.size = index + 1;
        if (this.buffer.size() >= this.size) {
            this.buffer.set(index, element);
        } else {
            this.buffer.ensureCapacity(this.size);
            while (this.buffer.size() < this.size - 1) {
                this.buffer.add(this.createElement());
            }
            this.buffer.add(element);
        }
        return null;
    }

    @Override
    public void add(int index, E element) {
        if (index > this.size) {
            this.set(index, element);
        } else {
            this.buffer.add(index, element);
            ++this.size;
        }
    }

    private void ensureCapacity(int capacity) {
        if (capacity < this.size) {
            return;
        }
        this.size = capacity;
        this.buffer.ensureCapacity(capacity);
        while (this.buffer.size() < this.size) {
            this.buffer.add(this.createElement());
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.clear(false);
    }

    public void clear(boolean clearBuffer) {
        this.size = 0;
        if (clearBuffer) {
            this.buffer.clear();
        }
    }
}

