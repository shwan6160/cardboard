/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.util;

import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingIterator;
import java.util.Iterator;

public class ConvertingIterable<T>
implements Iterable<T> {
    private final Iterable<?> base;
    private final Converter<?, T> converter;

    public ConvertingIterable(Iterable<?> base, Converter<?, T> converter) {
        this.base = base;
        this.converter = converter;
    }

    public Converter<?, T> getConverter() {
        return this.converter;
    }

    @Override
    public Iterator<T> iterator() {
        return new ConvertingIterator<T>(this.base.iterator(), this.converter);
    }
}

