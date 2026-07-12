/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.util;

import com.bergerkiller.mountiplex.conversion.Converter;
import java.util.Iterator;

public class ConvertingIterator<T>
implements Iterator<T> {
    private final Iterator<?> iter;
    private final Converter<Object, T> converter;

    public ConvertingIterator(Iterator<?> iterator, Converter<?, T> converter) {
        this.iter = iterator;
        this.converter = converter;
    }

    @Override
    public boolean hasNext() {
        return this.iter.hasNext();
    }

    @Override
    public T next() {
        return this.converter.convert(this.iter.next());
    }

    @Override
    public void remove() {
        this.iter.remove();
    }
}

