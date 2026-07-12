/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.util;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingIterator;
import java.util.Collection;
import java.util.Iterator;

public class ConvertingCollection<T>
implements Collection<T> {
    private final Collection<Object> base;
    protected final DuplexConverter<Object, T> converter;

    public ConvertingCollection(Collection<?> collection, DuplexConverter<?, T> converterPair) {
        this.base = collection;
        this.converter = converterPair;
    }

    public DuplexConverter<Object, T> getElementConverter() {
        return this.converter;
    }

    public Collection<Object> getBase() {
        return this.base;
    }

    @Override
    public int size() {
        return this.base.size();
    }

    @Override
    public boolean isEmpty() {
        return this.base.isEmpty();
    }

    @Override
    public void clear() {
        this.base.clear();
    }

    @Override
    public boolean add(T e) {
        return this.base.add(this.converter.convertReverse(e));
    }

    @Override
    public boolean remove(Object o) {
        return this.base.remove(this.converter.convertReverse(o));
    }

    @Override
    public boolean contains(Object o) {
        return this.base.contains(this.converter.convertReverse(o));
    }

    @Override
    public Iterator<T> iterator() {
        return new ConvertingIterator<T>(this.base.iterator(), this.converter);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.base.containsAll(new ConvertingCollection<Object>(c, this.converter.reverse()));
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.base.addAll(new ConvertingCollection<Object>(c, this.converter.reverse()));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.base.removeAll(new ConvertingCollection<Object>(c, this.converter.reverse()));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return MountiplexUtil.retainAll(this, c);
    }

    @Override
    public Object[] toArray() {
        return MountiplexUtil.toArray(this);
    }

    @Override
    public <K> K[] toArray(K[] array) {
        return MountiplexUtil.toArray(this, array);
    }
}

