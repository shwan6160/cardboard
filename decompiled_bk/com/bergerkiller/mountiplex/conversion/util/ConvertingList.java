/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.util;

import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingCollection;
import com.bergerkiller.mountiplex.conversion.util.ConvertingListIterator;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class ConvertingList<T>
extends ConvertingCollection<T>
implements List<T> {
    public ConvertingList(List<?> list, DuplexConverter<?, T> converter) {
        super(list, converter);
    }

    public List<Object> getBase() {
        return (List)super.getBase();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return this.getBase().addAll(index, new ConvertingCollection(c, this.converter.reverse()));
    }

    @Override
    public T get(int index) {
        return (T)this.converter.convert(this.getBase().get(index));
    }

    @Override
    public T set(int index, T element) {
        return (T)this.converter.convert(this.getBase().set(index, this.converter.convertReverse(element)));
    }

    @Override
    public void add(int index, T element) {
        this.getBase().add(index, this.converter.convert(element));
    }

    @Override
    public T remove(int index) {
        return (T)this.converter.convert(this.getBase().remove(index));
    }

    @Override
    public int indexOf(Object o) {
        return this.getBase().indexOf(this.converter.convertReverse(o));
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.getBase().lastIndexOf(this.converter.convertReverse(o));
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ConvertingListIterator(this.getBase().listIterator(), this.converter);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ConvertingListIterator(this.getBase().listIterator(index), this.converter);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new ConvertingList<T>(this.getBase().subList(fromIndex, toIndex), this.converter);
    }
}

