/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class FastIdentityHashMap<K, V> {
    private K getLastKey = null;
    private V getLastValue = null;
    private final IdentityHashMap<K, V> valueByIdentityKey = new IdentityHashMap();
    private final HashMap<K, Bin<K>> binByKey = new HashMap();
    private final ArrayList<V> values = new ArrayList();
    private final Collection<V> valuesReadOnly = Collections.unmodifiableCollection(this.values);

    public V get(K key) {
        if (this.getLastKey == key) {
            return this.getLastValue;
        }
        V byIdentityKeyResult = this.valueByIdentityKey.get(key);
        if (byIdentityKeyResult != null) {
            this.getLastKey = key;
            this.getLastValue = byIdentityKeyResult;
            return byIdentityKeyResult;
        }
        Bin<K> bin = this.binByKey.get(key);
        if (bin != null) {
            if (bin.keyIdentities.size() <= 1) {
                bin.keyIdentities = new ArrayList(bin.keyIdentities);
            }
            bin.keyIdentities.add(key);
            V value = this.values.get(bin.valueIndex);
            this.valueByIdentityKey.put(key, value);
            this.getLastKey = key;
            this.getLastValue = value;
            return value;
        }
        return null;
    }

    public V put(K key, V value) {
        Bin<K> bin = this.binByKey.get(key);
        if (bin != null) {
            V oldValue = this.values.get(bin.valueIndex);
            this.values.set(bin.valueIndex, value);
            for (Object keyIdentity : bin.keyIdentities) {
                this.valueByIdentityKey.put(keyIdentity, value);
            }
            if (key == this.getLastKey) {
                this.getLastValue = value;
            }
            return oldValue;
        }
        bin = new Bin<K>(key, this.values.size());
        this.values.add(value);
        this.binByKey.put(key, bin);
        this.valueByIdentityKey.put(key, value);
        return null;
    }

    public V remove(K key) {
        Bin<K> bin = this.binByKey.remove(key);
        if (bin != null) {
            int removedValueIndex = bin.valueIndex;
            V removedValue = this.values.remove(removedValueIndex);
            for (Object k : bin.keyIdentities) {
                this.valueByIdentityKey.remove(k);
            }
            for (Bin bin2 : this.binByKey.values()) {
                int index = bin2.valueIndex;
                if (index <= removedValueIndex) continue;
                bin2.valueIndex = index - 1;
            }
            this.getLastKey = null;
            this.getLastValue = null;
            return removedValue;
        }
        return null;
    }

    public int size() {
        return this.values.size();
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public void clear() {
        this.getLastKey = null;
        this.getLastValue = null;
        this.valueByIdentityKey.clear();
        this.binByKey.clear();
        this.values.clear();
    }

    public void optimize() {
        for (Bin<K> bin : this.binByKey.values()) {
            int numIdentities = bin.keyIdentities.size();
            if (numIdentities <= 1) continue;
            --numIdentities;
            for (int i = 0; i < numIdentities; ++i) {
                this.valueByIdentityKey.remove(bin.keyIdentities.get(i));
            }
            bin.keyIdentities = Collections.singletonList(bin.keyIdentities.get(numIdentities));
        }
    }

    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.binByKey.keySet());
    }

    public Collection<V> values() {
        return this.valuesReadOnly;
    }

    private static class Bin<K> {
        public List<K> keyIdentities;
        public int valueIndex;

        public Bin(K key, int valueIndex) {
            this.keyIdentities = Collections.singletonList(key);
            this.valueIndex = valueIndex;
        }
    }
}

