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
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKeyContainer;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKeyHolder;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public interface MutableCloudKeyContainer
extends CloudKeyContainer {
    public <V> void store(@NonNull CloudKey<V> var1, V var2);

    public <V> void store(@NonNull String var1, V var2);

    default public <V> void store(@NonNull CloudKeyHolder<V> keyHolder, V value) {
        this.store(keyHolder.key(), value);
    }

    public void remove(@NonNull CloudKey<?> var1);

    default public void remove(@NonNull String key) {
        this.remove(CloudKey.of(key));
    }

    default public void remove(@NonNull CloudKeyHolder<?> keyHolder) {
        this.remove(keyHolder.key());
    }

    default public <V> void set(@NonNull CloudKey<V> key, @Nullable V value) {
        if (value == null) {
            this.remove(key);
        } else {
            this.store(key, value);
        }
    }

    default public <V> void set(@NonNull String key, @Nullable V value) {
        if (value == null) {
            this.remove(key);
        } else {
            this.store(key, value);
        }
    }

    default public <V> void set(@NonNull CloudKeyHolder<V> keyHolder, @Nullable V value) {
        if (value == null) {
            this.remove(keyHolder);
        } else {
            this.store(keyHolder, value);
        }
    }

    public <V> V computeIfAbsent(@NonNull CloudKey<V> var1, @NonNull Function<@NonNull CloudKey<V>, V> var2);

    default public <V> V computeIfAbsent(@NonNull CloudKeyHolder<V> keyHolder, @NonNull Function<@NonNull CloudKey<V>, V> defaultFunction) {
        return this.computeIfAbsent(keyHolder.key(), defaultFunction);
    }
}

