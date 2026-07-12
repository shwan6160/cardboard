/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.meta;

import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public class SimpleCommandMeta
extends CommandMeta {
    private final Map<CloudKey<?>, Object> metaMap;

    protected SimpleCommandMeta(@NonNull Map<@NonNull CloudKey<?>, @NonNull Object> metaMap) {
        this.metaMap = Collections.unmodifiableMap(new HashMap(metaMap));
    }

    @Override
    public final <V> @NonNull Optional<V> optional(@NonNull CloudKey<V> key) {
        Object value = this.metaMap.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (!GenericTypeReflector.isSuperType(key.type().getType(), value.getClass())) {
            throw new IllegalArgumentException("Conflicting argument types between key type of " + key.type().getType().getTypeName() + " and value type of " + value.getClass());
        }
        return Optional.of(value);
    }

    @Override
    public <V> @NonNull Optional<V> optional(@NonNull String key) {
        Object value = this.metaMap.get(CloudKey.of(key));
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    @Override
    public boolean contains(@NonNull CloudKey<?> key) {
        return this.metaMap.containsKey(key);
    }

    @Override
    public final @NonNull Map<CloudKey<?>, ? extends @NonNull Object> all() {
        return new HashMap(this.metaMap);
    }

    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        SimpleCommandMeta that = (SimpleCommandMeta)other;
        return Objects.equals(this.metaMap, that.metaMap);
    }

    public final int hashCode() {
        return Objects.hashCode(this.metaMap);
    }
}

