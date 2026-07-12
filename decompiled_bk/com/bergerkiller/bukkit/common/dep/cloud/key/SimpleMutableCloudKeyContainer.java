/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.key;

import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.key.MutableCloudKeyContainer;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL)
public final class SimpleMutableCloudKeyContainer
implements MutableCloudKeyContainer {
    private final Map<CloudKey<?>, Object> map;

    public SimpleMutableCloudKeyContainer(Map<CloudKey<?>, Object> map) {
        this.map = map;
    }

    @Override
    public <V> @NonNull Optional<V> optional(@NonNull CloudKey<V> key) {
        return Optional.ofNullable(this.map.get(key));
    }

    @Override
    public <V> @NonNull Optional<V> optional(@NonNull String key) {
        return this.optional(CloudKey.of(key));
    }

    @Override
    public boolean contains(@NonNull CloudKey<?> key) {
        return this.map.containsKey(key);
    }

    @Override
    public @NonNull Map<CloudKey<?>, ? extends @NonNull Object> all() {
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public <V> void store(@NonNull CloudKey<V> key, @NonNull V value) {
        this.map.put(key, value);
    }

    @Override
    public <V> void store(@NonNull String key, @NonNull V value) {
        this.map.put(CloudKey.of(key), value);
    }

    @Override
    public void remove(@NonNull CloudKey<?> key) {
        this.map.remove(key);
    }

    @Override
    public <V> V computeIfAbsent(@NonNull CloudKey<V> key, @NonNull Function<@NonNull CloudKey<V>, V> defaultFunction) {
        return (V)this.map.computeIfAbsent(key, (? super K $) -> defaultFunction.apply(key));
    }

    public <V> @Nullable V getOrNull(CloudKey<V> key) {
        return (V)this.map.get(key);
    }
}

