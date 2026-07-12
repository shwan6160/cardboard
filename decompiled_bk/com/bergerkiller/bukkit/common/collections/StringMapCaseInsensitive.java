/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.mountiplex.conversion.builtin.EntryConverter;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StringMapCaseInsensitive<V>
implements Map<String, V> {
    private static final DuplexConverter<StringWrap, String> pair = new DuplexConverter<StringWrap, String>(StringWrap.class, String.class){

        @Override
        public String convertInput(StringWrap value) {
            return value.key;
        }

        @Override
        public StringWrap convertOutput(String value) {
            return new StringWrap(value);
        }
    };
    private final HashMap<StringWrap, V> base = new HashMap();
    private final StringWrap tmpWrap = new StringWrap("");

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
        if (key instanceof String) {
            return this.base.containsKey(this.tmpWrap.fill((String)key));
        }
        if (key == null) {
            return this.base.containsKey(key);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return this.base.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) {
            return this.base.get(this.tmpWrap.fill((String)key));
        }
        if (key == null) {
            return this.base.get(null);
        }
        return null;
    }

    @Override
    public V put(String key, V value) {
        return this.base.put(key == null ? null : new StringWrap(key), value);
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            return this.base.remove(key);
        }
        if (key instanceof String) {
            return this.base.remove(this.tmpWrap.fill((String)key));
        }
        return null;
    }

    @Override
    public void clear() {
        this.base.clear();
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for (Map.Entry<String, V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if (key == null) {
            return this.base.getOrDefault(null, defaultValue);
        }
        if (key instanceof String) {
            return this.base.getOrDefault(this.tmpWrap.fill((String)key), defaultValue);
        }
        return defaultValue;
    }

    @Override
    public Set<String> keySet() {
        return new ConvertingSet<String>(this.base.keySet(), pair);
    }

    @Override
    public Collection<V> values() {
        return this.base.values();
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        EntryConverter converter = EntryConverter.create(pair, DuplexConversion.NONE);
        return new ConvertingSet<Map.Entry<String, V>>(this.base.entrySet(), converter);
    }

    private static class StringWrap {
        private String key;
        private int hashCode;

        public StringWrap(String key) {
            this.fill(key);
        }

        public StringWrap fill(String key) {
            this.key = key;
            this.hashCode = 0;
            for (int i = 0; i < key.length(); ++i) {
                this.hashCode = 31 * this.hashCode + Character.toLowerCase(key.charAt(i));
            }
            return this;
        }

        public boolean equals(Object o) {
            return o instanceof StringWrap && this.key.equalsIgnoreCase(((StringWrap)o).key);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }
}

