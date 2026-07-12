/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StringMap<V>
extends HashMap<String, V> {
    private static final long serialVersionUID = -245022676771481540L;

    public StringMap() {
    }

    public StringMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public StringMap(int initialCapacity) {
        super(initialCapacity);
    }

    public StringMap(Map<? extends String, ? extends V> m) {
        super(m);
    }

    public boolean containsKeyLower(Object key) {
        return this.containsKey(key instanceof String ? ((String)key).toLowerCase(Locale.ENGLISH) : null);
    }

    public boolean containsKeyUpper(Object key) {
        return this.containsKey(key instanceof String ? ((String)key).toUpperCase(Locale.ENGLISH) : null);
    }

    public V removeLower(Object key) {
        return this.remove(key instanceof String ? ((String)key).toLowerCase(Locale.ENGLISH) : null);
    }

    public V removeUpper(Object key) {
        return this.remove(key instanceof String ? ((String)key).toUpperCase(Locale.ENGLISH) : null);
    }

    public V getLower(String key) {
        return this.get(key == null ? null : key.toLowerCase(Locale.ENGLISH));
    }

    public V getUpper(String key) {
        return this.get(key == null ? null : key.toUpperCase(Locale.ENGLISH));
    }

    public V putLower(String key, V value) {
        return this.put(key == null ? null : key.toLowerCase(Locale.ENGLISH), value);
    }

    public V putUpper(String key, V value) {
        return this.put(key == null ? null : key.toUpperCase(Locale.ENGLISH), value);
    }
}

