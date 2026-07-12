/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.entity.Entity;

public class EntityMap<K extends Entity, V>
extends WeakHashMap<K, V> {
    public EntityMap() {
        this.register();
    }

    public EntityMap(int initialCapacity) {
        super(initialCapacity);
        this.register();
    }

    public EntityMap(Map<? extends K, ? extends V> m) {
        super(m);
        this.register();
    }

    private void register() {
        CommonPlugin.getInstance().registerMap(this);
    }
}

