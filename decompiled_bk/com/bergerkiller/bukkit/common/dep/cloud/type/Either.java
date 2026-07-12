/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.type;

import com.bergerkiller.bukkit.common.dep.cloud.type.EitherImpl;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface Either<U, V> {
    public static <U, V> @NonNull Either<U, V> ofPrimary(@NonNull U value) {
        return EitherImpl.of(Objects.requireNonNull(value, "value"), null);
    }

    public static <U, V> @NonNull Either<U, V> ofFallback(@NonNull V value) {
        return EitherImpl.of(null, Objects.requireNonNull(value, "value"));
    }

    public @NonNull Optional<U> primary();

    public @NonNull Optional<V> fallback();

    default public @NonNull U primaryOrMapFallback(@NonNull Function<V, U> mapFallback) {
        return (U)this.primary().orElseGet(() -> mapFallback.apply(this.fallback().get()));
    }

    default public @NonNull V fallbackOrMapPrimary(@NonNull Function<U, V> mapPrimary) {
        return (V)this.fallback().orElseGet(() -> mapPrimary.apply(this.primary().get()));
    }

    default public <R> @NonNull R mapEither(@NonNull Function<U, R> mapPrimary, @NonNull Function<V, R> mapFallback) {
        return (R)this.primary().map(mapPrimary).orElseGet(() -> this.fallback().map(mapFallback).get());
    }
}

