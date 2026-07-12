/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.util;

import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import java.util.Map;

public class ConvertingEntry<K, V>
implements Map.Entry<K, V> {
    private final Map.Entry<Object, Object> base;
    private final DuplexConverter<Object, K> keyConverter;
    private final DuplexConverter<Object, V> valueConverter;

    public ConvertingEntry(Map.Entry<?, ?> entry, DuplexConverter<?, K> keyConverter, DuplexConverter<?, V> valueConverter) {
        this.base = entry;
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
    }

    @Override
    public K getKey() {
        return (K)this.keyConverter.convert(this.base.getKey());
    }

    @Override
    public V getValue() {
        return (V)this.valueConverter.convert(this.base.getValue());
    }

    @Override
    public V setValue(V value) {
        return (V)this.valueConverter.convert(this.base.setValue(this.valueConverter.convertReverse(value)));
    }
}

