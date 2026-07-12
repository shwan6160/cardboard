/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.IgnoredFieldAccessor;

public class TranslatorFieldAccessor<T>
implements FieldAccessor<T> {
    private final FieldAccessor<Object> base;
    private final DuplexConverter<Object, T> converterPair;

    public TranslatorFieldAccessor(FieldAccessor<?> base, Converter<?, ?> setConverter, Converter<?, T> getConverter) {
        this(base, DuplexConverter.pair(getConverter, setConverter));
    }

    public TranslatorFieldAccessor(FieldAccessor<?> base, DuplexConverter<?, T> converterPair) {
        if (base == null) {
            throw new IllegalArgumentException("Can not construct using a null base");
        }
        if (converterPair == null) {
            throw new IllegalArgumentException("Can not construct using a null converter pair");
        }
        this.base = base;
        this.converterPair = converterPair;
    }

    @Override
    public boolean isValid() {
        return this.base.isValid();
    }

    public Object getInternal(Object instance) {
        return this.base.get(instance);
    }

    public boolean setInternal(Object instance, Object value) {
        return this.base.set(instance, value);
    }

    @Override
    public T get(Object instance) {
        return (T)this.converterPair.convert(this.getInternal(instance));
    }

    @Override
    public boolean set(Object instance, T value) {
        return this.setInternal(instance, this.converterPair.convertReverse(value));
    }

    @Override
    public T transfer(Object from, Object to) {
        return (T)this.converterPair.convert(this.base.transfer(from, to));
    }

    @Override
    public <K> TranslatorFieldAccessor<K> translate(DuplexConverter<?, K> converterPair) {
        return new TranslatorFieldAccessor<K>(this, converterPair);
    }

    @Override
    public FieldAccessor<T> ignoreInvalid(T defaultValue) {
        if (this.isValid()) {
            return this;
        }
        return new IgnoredFieldAccessor<T>(defaultValue);
    }
}

