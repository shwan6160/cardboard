/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.key;

import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKeyHolder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface CloudKeyContainer {
    public <V> @NonNull Optional<V> optional(@NonNull CloudKey<V> var1);

    public <V> @NonNull Optional<V> optional(@NonNull String var1);

    default public <V> @NonNull Optional<V> optional(@NonNull CloudKeyHolder<V> keyHolder) {
        return this.optional(keyHolder.key());
    }

    default public <V> V getOrDefault(@NonNull CloudKey<@NonNull V> key, V defaultValue) {
        return this.optional(key).orElse(defaultValue);
    }

    default public <V> V getOrDefault(@NonNull String key, V defaultValue) {
        return this.optional(key).orElse(defaultValue);
    }

    default public <V> V getOrDefault(@NonNull CloudKeyHolder<@NonNull V> keyHolder, V defaultValue) {
        return this.getOrDefault(keyHolder.key(), defaultValue);
    }

    default public <V> V getOrSupplyDefault(@NonNull CloudKey<@NonNull V> key, @NonNull Supplier<V> supplier) {
        return this.optional(key).orElseGet(supplier);
    }

    default public <V> V getOrSupplyDefault(@NonNull String key, @NonNull Supplier<V> supplier) {
        return this.optional(key).orElseGet(supplier);
    }

    default public <V> V getOrSupplyDefault(@NonNull CloudKeyHolder<@NonNull V> keyHolder, @NonNull Supplier<V> supplier) {
        return this.optional(keyHolder).orElseGet(supplier);
    }

    default public <V> V get(@NonNull CloudKey<V> key) {
        return this.optional(key).orElseThrow(() -> new NullPointerException(String.format("There is no object in the registry identified by the key '%s'", key.name())));
    }

    default public <V> V get(@NonNull String key) {
        return (V)this.optional(key).map(value -> value).orElseThrow(() -> new NullPointerException(String.format("There is no object in the registry identified by the key '%s'", key)));
    }

    default public <V> V get(@NonNull CloudKeyHolder<V> keyHolder) {
        return this.get(keyHolder.key());
    }

    public boolean contains(@NonNull CloudKey<?> var1);

    default public boolean contains(@NonNull String key) {
        return this.contains(CloudKey.of(key));
    }

    default public boolean contains(@NonNull CloudKeyHolder<?> keyHolder) {
        return this.contains(keyHolder.key());
    }

    public @NonNull Map<CloudKey<?>, ? extends @NonNull Object> all();
}

