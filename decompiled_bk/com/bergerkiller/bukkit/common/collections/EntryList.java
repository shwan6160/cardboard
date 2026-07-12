/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class EntryList<K, V>
extends ArrayList<Map.Entry<K, V>> {
    private static final long serialVersionUID = 1L;

    public void add(K key, V value) {
        this.add(new AbstractMap.SimpleEntry<K, V>(key, value));
    }
}

