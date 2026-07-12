/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClassMap<V> {
    private final Object lock = new Object();
    private LinkedHashMap<Class<?>, V> classes = new LinkedHashMap();
    private Map<Class<?>, V> classesGetCache = Collections.emptyMap();

    public void put(ClassTemplate<?> typeTemplate, V value) {
        if (typeTemplate == null) {
            return;
        }
        this.put(typeTemplate.getType(), value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void put(Class<?> type, V value) {
        if (type == null) {
            return;
        }
        Object object = this.lock;
        synchronized (object) {
            LinkedHashMap newClasses = new LinkedHashMap(this.classes);
            newClasses.put(type, value);
            this.classes = newClasses;
            this.classesGetCache = this.classes;
        }
    }

    public V get(Class<?> type) {
        return this.getOrDefault(type, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V getOrDefault(Class<?> type, V defaultValue) {
        V value = this.classesGetCache.get(type);
        if (value != null) {
            return value;
        }
        if (type == null) {
            return defaultValue;
        }
        for (Map.Entry<Class<?>, V> entry : this.classes.entrySet()) {
            if (!entry.getKey().isAssignableFrom(type)) continue;
            value = entry.getValue();
            Object object = this.lock;
            synchronized (object) {
                HashMap newCache = new HashMap(this.classesGetCache);
                newCache.putIfAbsent(type, value);
                this.classesGetCache = newCache;
            }
            return value;
        }
        return defaultValue;
    }

    public V get(Object instance) {
        if (instance == null) {
            return null;
        }
        return this.get(instance.getClass());
    }

    public Map<Class<?>, V> getData() {
        return Collections.unmodifiableMap(this.classes);
    }
}

