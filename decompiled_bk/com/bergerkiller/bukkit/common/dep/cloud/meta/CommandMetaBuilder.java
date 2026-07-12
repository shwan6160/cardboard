/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.meta;

import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta;
import com.bergerkiller.bukkit.common.dep.cloud.meta.SimpleCommandMeta;
import java.util.HashMap;
import java.util.Map;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.STABLE)
public class CommandMetaBuilder {
    private final Map<CloudKey<?>, Object> map = new HashMap();

    CommandMetaBuilder() {
    }

    public @This @NonNull CommandMetaBuilder with(@NonNull CommandMeta commandMeta) {
        this.map.putAll(commandMeta.all());
        return this;
    }

    public <V> @This @NonNull CommandMetaBuilder with(@NonNull CloudKey<V> key, @NonNull V value) {
        this.map.put(key, value);
        return this;
    }

    public @NonNull CommandMeta build() {
        return new SimpleCommandMeta(this.map);
    }
}

