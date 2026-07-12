/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParsingContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;

final class AggregateParsingContextImpl<C>
implements AggregateParsingContext<C> {
    private final Map<CloudKey<?>, Object> storage = new HashMap();
    private final Collection<@NonNull String> validKeys;

    AggregateParsingContextImpl(@NonNull AggregateParser<C, ?> parser) {
        this.validKeys = parser.components().stream().map(CommandComponent::name).collect(Collectors.toList());
    }

    @Override
    public <V> void store(@NonNull CloudKey<V> key, @NonNull V value) {
        this.storage.put(key, value);
    }

    @Override
    public <V> void store(@NonNull String key, @NonNull V value) {
        this.storage.put(CloudKey.of(key), value);
    }

    @Override
    public void remove(@NonNull CloudKey<?> key) {
        this.storage.remove(key);
    }

    @Override
    public <V> V computeIfAbsent(@NonNull CloudKey<V> key, @NonNull Function<@NonNull CloudKey<V>, V> defaultFunction) {
        return (V)this.storage.computeIfAbsent(key, (? super K k) -> defaultFunction.apply((CloudKey)k));
    }

    @Override
    public <V> @NonNull Optional<V> optional(@NonNull CloudKey<V> key) {
        Object value = this.storage.get(key);
        if (value != null) {
            Object castedValue = value;
            return Optional.of(castedValue);
        }
        return Optional.empty();
    }

    @Override
    public <V> @NonNull Optional<V> optional(@NonNull String key) {
        Object value = this.storage.get(CloudKey.of(key));
        if (value != null) {
            Object castedValue = value;
            return Optional.of(castedValue);
        }
        return Optional.empty();
    }

    @Override
    public <V> @NonNull V get(@NonNull CloudKey<V> key) {
        if (!this.validKeys.contains(key.name())) {
            throw new NullPointerException("No value with the given key has been stored in the context");
        }
        Object value = Objects.requireNonNull(this.storage.get(key));
        return (V)value;
    }

    @Override
    public <V> @NonNull V get(@NonNull String key) {
        if (!this.validKeys.contains(key)) {
            throw new NullPointerException("No value with the given key has been stored in the context");
        }
        Object value = Objects.requireNonNull(this.storage.get(CloudKey.of(key)));
        return (V)value;
    }

    @Override
    public boolean contains(@NonNull CloudKey<?> key) {
        return this.storage.containsKey(key);
    }

    @Override
    public boolean contains(@NonNull String key) {
        return this.storage.containsKey(CloudKey.of(key));
    }

    @Override
    public @NonNull Map<CloudKey<?>, ? extends @NonNull Object> all() {
        return this.storage;
    }
}

