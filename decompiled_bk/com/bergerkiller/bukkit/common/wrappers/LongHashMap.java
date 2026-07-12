/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.LongObjectHashMapHandle;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.LongFunction;

public class LongHashMap<V>
extends BasicWrapper<LongObjectHashMapHandle>
implements Cloneable {
    public LongHashMap() {
        this.setHandle(LongObjectHashMapHandle.createNew());
    }

    public LongHashMap(int initialCapacity) {
        this.setHandle(LongObjectHashMapHandle.createNew());
    }

    public LongHashMap(Object handle) {
        this.setHandle(LongObjectHashMapHandle.createHandle(handle));
    }

    private LongHashMap(LongObjectHashMapHandle handle) {
        this.setHandle(handle);
    }

    public int size() {
        return ((LongObjectHashMapHandle)this.handle).size();
    }

    public void clear() {
        ((LongObjectHashMapHandle)this.handle).clear();
    }

    public boolean contains(int msw, int lsw) {
        return this.contains(MathUtil.longHashToLong(msw, lsw));
    }

    public boolean contains(long key) {
        return ((LongObjectHashMapHandle)this.handle).containsKey(key);
    }

    public V get(int msw, int lsw) {
        return this.get(MathUtil.longHashToLong(msw, lsw));
    }

    public V get(long key) {
        return (V)((LongObjectHashMapHandle)this.handle).get(key);
    }

    public V remove(int msw, int lsw) {
        return this.remove(MathUtil.longHashToLong(msw, lsw));
    }

    public V remove(long key) {
        return (V)((LongObjectHashMapHandle)this.handle).remove(key);
    }

    public void put(int msw, int lsw, V value) {
        this.put(MathUtil.longHashToLong(msw, lsw), value);
    }

    public void put(long key, V value) {
        ((LongObjectHashMapHandle)this.handle).put(key, value);
    }

    public V getAndPut(int msw, int lsw, V value) {
        return (V)((LongObjectHashMapHandle)this.handle).put(MathUtil.longHashToLong(msw, lsw), value);
    }

    public V getAndPut(long key, V value) {
        return (V)((LongObjectHashMapHandle)this.handle).put(key, value);
    }

    public V merge(long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return (V)((LongObjectHashMapHandle)this.handle).merge(key, value, remappingFunction);
    }

    public V computeIfAbsent(long key, LongFunction<? extends V> mappingFunction) {
        return (V)((LongObjectHashMapHandle)this.handle).computeIfAbsent(key, mappingFunction);
    }

    public V getOrDefault(long key, V defaultValue) {
        return (V)((LongObjectHashMapHandle)this.handle).getOrDefault(key, defaultValue);
    }

    public Collection<V> getValues() {
        return this.values();
    }

    public Collection<V> values() {
        return ((LongObjectHashMapHandle)this.handle).values();
    }

    public long[] getKeys() {
        Set<Long> keys = ((LongObjectHashMapHandle)this.handle).keySet();
        long[] result = new long[keys.size()];
        int i = 0;
        for (Long key : keys) {
            result[i++] = key;
        }
        return result;
    }

    public LongHashMap<V> clone() {
        return new LongHashMap<V>(((LongObjectHashMapHandle)this.handle).cloneMap());
    }
}

