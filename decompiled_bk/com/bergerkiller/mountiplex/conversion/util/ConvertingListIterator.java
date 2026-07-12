/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.util;

import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import java.util.ListIterator;

public class ConvertingListIterator<T>
implements ListIterator<T> {
    private final ListIterator<Object> iter;
    private final DuplexConverter<Object, T> converter;

    public ConvertingListIterator(ListIterator<?> listIterator, DuplexConverter<?, T> converter) {
        this.iter = listIterator;
        this.converter = converter;
    }

    @Override
    public boolean hasNext() {
        return this.iter.hasNext();
    }

    @Override
    public T next() {
        return (T)this.converter.convert(this.iter.next());
    }

    @Override
    public boolean hasPrevious() {
        return this.iter.hasPrevious();
    }

    @Override
    public T previous() {
        return (T)this.converter.convert(this.iter.previous());
    }

    @Override
    public int nextIndex() {
        return this.iter.nextIndex();
    }

    @Override
    public int previousIndex() {
        return this.iter.previousIndex();
    }

    @Override
    public void remove() {
        this.iter.remove();
    }

    @Override
    public void set(T e) {
        this.iter.set(this.converter.convertReverse(e));
    }

    @Override
    public void add(T e) {
        this.iter.add(this.converter.convertReverse(e));
    }
}

