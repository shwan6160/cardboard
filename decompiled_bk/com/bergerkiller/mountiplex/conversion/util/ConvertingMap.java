/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.util;

import com.bergerkiller.mountiplex.conversion.builtin.EntryConverter;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingCollection;
import com.bergerkiller.mountiplex.conversion.util.ConvertingSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ConvertingMap<K, V>
implements Map<K, V> {
    private final Map<Object, Object> base;
    protected final DuplexConverter<Object, K> keyConverter;
    protected final DuplexConverter<Object, V> valueConverter;

    public ConvertingMap(Map<?, ?> map, DuplexConverter<?, K> keyConverter, DuplexConverter<?, V> valueConverter) {
        this.base = map;
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
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
    public boolean containsKey(Object key) {
        return this.base.containsKey(this.keyConverter.convertReverse(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return this.base.containsValue(this.valueConverter.convertReverse(value));
    }

    @Override
    public V get(Object key) {
        return (V)this.valueConverter.convert(this.base.get(this.keyConverter.convertReverse(key)));
    }

    @Override
    public V put(K key, V value) {
        return (V)this.valueConverter.convert(this.base.put(this.keyConverter.convertReverse(key), this.valueConverter.convertReverse(value)));
    }

    @Override
    public V remove(Object key) {
        return (V)this.valueConverter.convert(this.base.remove(this.keyConverter.convertReverse(key)));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<K, V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.base.clear();
    }

    @Override
    public Set<K> keySet() {
        return new ConvertingSet<K>(this.base.keySet(), this.keyConverter);
    }

    @Override
    public Collection<V> values() {
        return new ConvertingCollection<V>(this.base.values(), this.valueConverter);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new ConvertingSet<Map.Entry<K, V>>(this.base.entrySet(), EntryConverter.create(this.keyConverter, this.valueConverter));
    }
}

