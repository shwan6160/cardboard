/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.offline;

import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.bukkit.World;

public class OfflineWorldMap<V> {
    private final Map<OfflineWorld, V> values = new IdentityHashMap<OfflineWorld, V>();
    private OfflineWorld lastGetKey = OfflineWorld.NONE;
    private V lastGetValue = null;

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public int size() {
        return this.values.size();
    }

    public V get(OfflineWorld world) {
        if (world == this.lastGetKey) {
            return this.lastGetValue;
        }
        this.lastGetKey = world;
        this.lastGetValue = this.values.get(world);
        return this.lastGetValue;
    }

    public V get(World world) {
        OfflineWorld oWorld;
        if (world == this.lastGetKey.getLoadedWorld()) {
            return this.lastGetValue;
        }
        this.lastGetKey = oWorld = OfflineWorld.of(world);
        this.lastGetValue = this.values.get(oWorld);
        return this.lastGetValue;
    }

    public V getOrDefault(OfflineWorld world, V defaultValue) {
        V value = this.get(world);
        return value != null ? value : defaultValue;
    }

    public V getOrDefault(World world, V defaultValue) {
        V value = this.get(world);
        return value != null ? value : defaultValue;
    }

    public V remove(OfflineWorld world) {
        this.lastGetKey = OfflineWorld.NONE;
        return this.values.remove(world);
    }

    public V remove(World world) {
        return this.remove(OfflineWorld.of(world));
    }

    public V put(OfflineWorld world, V value) {
        this.lastGetKey = OfflineWorld.NONE;
        return this.values.put(world, value);
    }

    public V put(World world, V value) {
        return this.put(OfflineWorld.of(world), value);
    }

    public V computeIfAbsent(OfflineWorld world, Function<OfflineWorld, ? extends V> mappingFunction) {
        V value;
        if (this.lastGetKey == world && (value = this.lastGetValue) != null) {
            return value;
        }
        try {
            this.lastGetKey = world;
            this.lastGetValue = this.values.computeIfAbsent(world, mappingFunction);
            return this.lastGetValue;
        }
        catch (Error | RuntimeException e) {
            this.lastGetKey = OfflineWorld.NONE;
            throw e;
        }
    }

    public V computeIfAbsent(World world, Function<World, ? extends V> mappingFunction) {
        V value;
        if (this.lastGetKey.getLoadedWorld() == world && (value = this.lastGetValue) != null) {
            return value;
        }
        try {
            OfflineWorld offlineWorld;
            this.lastGetKey = offlineWorld = OfflineWorld.of(world);
            this.lastGetValue = this.values.computeIfAbsent(offlineWorld, (? super K unused) -> mappingFunction.apply(world));
            return this.lastGetValue;
        }
        catch (Error | RuntimeException e) {
            this.lastGetKey = OfflineWorld.NONE;
            throw e;
        }
    }

    public Collection<V> values() {
        return this.values.values();
    }

    public Set<OfflineWorld> keySet() {
        return this.values.keySet();
    }

    public Set<Map.Entry<OfflineWorld, V>> entrySet() {
        return this.values.entrySet();
    }

    public void clear() {
        this.lastGetKey = OfflineWorld.NONE;
        this.values.clear();
    }
}

