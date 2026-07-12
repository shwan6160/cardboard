/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ObjectCache<T> {
    private final Supplier<T> _allocator;
    private final Consumer<T> _resetMethod;
    private final Entry<T> _root;
    private static final ObjectCache<?> HASHSET_CACHE = ObjectCache.create(HashSet::new, HashSet::clear);
    private static final ObjectCache<?> ARRAYLIST_CACHE = ObjectCache.create(ArrayList::new, ArrayList::clear);
    private static final ObjectCache<?> HASHMAP_CACHE = ObjectCache.create(HashMap::new, HashMap::clear);

    private ObjectCache(Supplier<?> allocator, Consumer<?> resetMethod) {
        this._allocator = (Supplier)LogicUtil.unsafeCast(allocator);
        this._resetMethod = (Consumer)LogicUtil.unsafeCast(resetMethod);
        this._root = new Entry(this, null);
    }

    public static <T> Entry<Set<T>> newHashSet() {
        return HASHSET_CACHE.create();
    }

    public static <T> Entry<List<T>> newArrayList() {
        return ARRAYLIST_CACHE.create();
    }

    public static <K, V> Entry<Map<K, V>> newHashMap() {
        return HASHMAP_CACHE.create();
    }

    public static <T, I> ObjectCache<T> create(Supplier<I> allocator, Consumer<I> resetMethod) {
        return new ObjectCache<T>(allocator, resetMethod);
    }

    public synchronized Entry<T> create() {
        Entry result = this._root._next;
        if (result == null) {
            return new Entry(this, this._allocator.get());
        }
        this._root._next = result._next;
        result._next = null;
        return result;
    }

    public synchronized void clear() {
        this._root._next = null;
    }

    public static void clearDefaultCaches() {
        HASHSET_CACHE.clear();
        ARRAYLIST_CACHE.clear();
        HASHMAP_CACHE.clear();
    }

    public static final class Entry<T>
    implements AutoCloseable {
        private final ObjectCache<T> _cache;
        private final T _value;
        protected Entry<T> _next;

        private Entry(ObjectCache<T> cache, T value) {
            this._cache = cache;
            this._value = value;
            this._next = null;
        }

        public T get() {
            return this._value;
        }

        public ObjectCache<T> getCache() {
            return this._cache;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() {
            ((ObjectCache)this._cache)._resetMethod.accept(this._value);
            ObjectCache<T> objectCache = this._cache;
            synchronized (objectCache) {
                this._next = ((ObjectCache)this._cache)._root._next;
                ((ObjectCache)this._cache)._root._next = this;
            }
        }
    }
}

