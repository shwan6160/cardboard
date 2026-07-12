/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class TypeMap<T> {
    private final HashMap<TypeDeclaration, Bin> map;

    public TypeMap() {
        this.map = new HashMap();
    }

    protected TypeMap(TypeMap<T> values) {
        this.map = new HashMap(values.map.size());
        for (Map.Entry<TypeDeclaration, Bin> e : values.map.entrySet()) {
            this.putAll(e.getKey(), e.getValue().values);
        }
    }

    public T get(Class<?> type) {
        return this.get(TypeDeclaration.fromClass(type));
    }

    public T get(TypeDeclaration type) {
        Iterator iterator = this.getBin(type).getCache().iterator();
        if (iterator.hasNext()) {
            Object value = iterator.next();
            return value;
        }
        return null;
    }

    public Collection<T> getAll(Class<?> type) {
        return this.getAll(TypeDeclaration.fromClass(type));
    }

    public Collection<T> getAll(TypeDeclaration type) {
        return this.getBin(type).getCache();
    }

    public void add(Class<?> type, T value) {
        this.add(TypeDeclaration.fromClass(type), value);
    }

    public void add(TypeDeclaration type, T value) {
        Bin bin = this.getBin(type);
        if (bin.values.isEmpty()) {
            bin.values = Collections.singletonList(value);
        } else {
            Object[] newValues = new Object[bin.values.size() + 1];
            bin.values.toArray(newValues);
            newValues[newValues.length - 1] = value;
            bin.values = Arrays.asList(newValues);
        }
        bin.invalidateCache(1);
    }

    public void remove(Class<?> type, T value) {
        this.remove(TypeDeclaration.fromClass(type), value);
    }

    public void remove(TypeDeclaration type, T value) {
        Bin bin = this.getBin(type);
        List values = bin.values;
        int index = values.indexOf(value);
        if (index != -1) {
            int numValues = values.size();
            if (numValues == 1) {
                bin.values = Collections.emptyList();
            } else {
                int i;
                Object[] newValues = new Object[numValues - 1];
                for (i = 0; i < index; ++i) {
                    newValues[i] = values.get(i);
                }
                for (i = index + 1; i < numValues; ++i) {
                    newValues[i - 1] = values.get(i);
                }
                bin.values = Arrays.asList(newValues);
            }
            bin.invalidateCache(-1);
        }
    }

    public void put(Class<?> type, T value) {
        this.put(TypeDeclaration.fromClass(type), value);
    }

    public void put(TypeDeclaration type, T value) {
        Bin bin = this.getBin(type);
        int sizeChange = 1 - bin.values.size();
        bin.values = Collections.singletonList(value);
        bin.invalidateCache(sizeChange);
    }

    public void putAll(Class<?> type, Collection<T> values) {
        this.putAll(TypeDeclaration.fromClass(type), values);
    }

    public void putAll(TypeDeclaration type, Collection<T> values) {
        Bin bin = this.getBin(type);
        int sizeChange = -bin.values.size();
        bin.values = MountiplexUtil.createUmodifiableList(values);
        bin.invalidateCache(sizeChange += bin.values.size());
    }

    public boolean amend(TypeDeclaration type, T value) {
        Bin bin = this.getBin(type);
        if (bin.values.isEmpty()) {
            bin.values = Collections.singletonList(value);
            bin.invalidateCache(1);
            return true;
        }
        return false;
    }

    public boolean amendAll(TypeDeclaration type, Collection<T> values) {
        Bin bin = this.getBin(type);
        if (bin.values.isEmpty()) {
            bin.values = MountiplexUtil.createUmodifiableList(values);
            bin.invalidateCache(bin.values.size());
            return true;
        }
        return false;
    }

    public boolean containsKey(TypeDeclaration type) {
        return !this.getBin(type).isEmpty();
    }

    public void clear() {
        this.map.clear();
    }

    public Collection<T> values() {
        if (this.map.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList result = new ArrayList();
        for (Bin bin : this.map.values()) {
            result.addAll(bin.values);
        }
        return result;
    }

    private final Bin getBin(TypeDeclaration type) {
        Bin bin = this.map.get(type);
        if (bin == null) {
            bin = new Bin(type);
            for (Map.Entry<TypeDeclaration, Bin> entry : this.map.entrySet()) {
                if (this.isParentTypeOf(type, entry.getKey())) {
                    entry.getValue().link(bin);
                    continue;
                }
                if (!this.isParentTypeOf(entry.getKey(), type)) continue;
                bin.link(entry.getValue());
            }
            this.map.put(type, bin);
        }
        return bin;
    }

    protected abstract boolean isParentTypeOf(TypeDeclaration var1, TypeDeclaration var2);

    private class Bin
    implements Comparable<Bin> {
        public final TypeDeclaration type;
        public List<T> values = Collections.emptyList();
        private final ArrayList<Bin> parents = new ArrayList(1);
        private final ArrayList<Bin> children = new ArrayList(1);
        private ArrayList<T> cache = null;
        private int expectedCacheSize = 0;

        public Bin(TypeDeclaration type) {
            this.type = type;
        }

        public final void link(Bin other) {
            this.parents.add(other);
            Collections.sort(this.parents);
            this.invalidateOwnCache(other.values.size());
            other.children.add(this);
        }

        public void invalidateCache(int cacheChange) {
            this.invalidateOwnCache(cacheChange);
            for (Bin parent : this.children) {
                parent.invalidateOwnCache(cacheChange);
            }
        }

        private void invalidateOwnCache(int cacheChange) {
            this.cache = null;
            this.expectedCacheSize += cacheChange;
        }

        public boolean isEmpty() {
            return this.values.isEmpty() && this.getCache().isEmpty();
        }

        public Collection<T> getCache() {
            if (this.cache == null) {
                this.cache = new ArrayList(this.expectedCacheSize);
                this.cache.addAll(this.values);
                for (Bin parent : this.parents) {
                    this.cache.addAll(parent.values);
                }
                this.expectedCacheSize = this.cache.size();
            }
            return this.cache;
        }

        @Override
        public int compareTo(Bin o) {
            if (o.type.equals(this.type)) {
                return 0;
            }
            if (this.type.isAssignableFrom(o.type)) {
                return 1;
            }
            return -1;
        }
    }
}

