/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckReturnValue
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.type;

import com.bergerkiller.bukkit.common.dep.cloud.type.Either;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="Either", generator="Immutables")
@Immutable
final class EitherImpl<U, V>
implements Either<U, V> {
    @Nullable
    private final U primary;
    @Nullable
    private final V fallback;

    private EitherImpl(Optional<? extends U> primary, Optional<? extends V> fallback) {
        this.primary = primary.orElse(null);
        this.fallback = fallback.orElse(null);
    }

    private EitherImpl(@Nullable U primary, @Nullable V fallback) {
        this.primary = primary;
        this.fallback = fallback;
    }

    private EitherImpl(EitherImpl<U, V> original, @Nullable U primary, @Nullable V fallback) {
        this.primary = primary;
        this.fallback = fallback;
    }

    @Override
    public @NonNull Optional<U> primary() {
        return Optional.ofNullable(this.primary);
    }

    @Override
    public @NonNull Optional<V> fallback() {
        return Optional.ofNullable(this.fallback);
    }

    public final EitherImpl<U, V> withPrimary(@Nullable U value) {
        U newValue = value;
        if (this.primary == newValue) {
            return this;
        }
        return new EitherImpl<U, V>(this, newValue, this.fallback);
    }

    public final EitherImpl<U, V> withPrimary(Optional<? extends U> optional) {
        U value = optional.orElse(null);
        if (this.primary == value) {
            return this;
        }
        return new EitherImpl<Object, V>(this, value, this.fallback);
    }

    public final EitherImpl<U, V> withFallback(@Nullable V value) {
        V newValue = value;
        if (this.fallback == newValue) {
            return this;
        }
        return new EitherImpl<U, V>(this, this.primary, newValue);
    }

    public final EitherImpl<U, V> withFallback(Optional<? extends V> optional) {
        V value = optional.orElse(null);
        if (this.fallback == value) {
            return this;
        }
        return new EitherImpl<U, Object>(this, this.primary, value);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof EitherImpl && this.equalTo(0, (EitherImpl)another);
    }

    private boolean equalTo(int synthetic, EitherImpl<?, ?> another) {
        return Objects.equals(this.primary, another.primary) && Objects.equals(this.fallback, another.fallback);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(this.primary);
        h += (h << 5) + Objects.hashCode(this.fallback);
        return h;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Either{");
        if (this.primary != null) {
            builder.append("primary=").append(this.primary);
        }
        if (this.fallback != null) {
            if (builder.length() > 7) {
                builder.append(", ");
            }
            builder.append("fallback=").append(this.fallback);
        }
        return builder.append("}").toString();
    }

    public static <U, V> EitherImpl<U, V> of(Optional<? extends U> primary, Optional<? extends V> fallback) {
        return new EitherImpl<Optional<? extends U>, Optional<? extends V>>(primary, fallback);
    }

    public static <U, V> EitherImpl<U, V> of(@Nullable U primary, @Nullable V fallback) {
        return new EitherImpl<U, V>(primary, fallback);
    }

    public static <U, V> EitherImpl<U, V> copyOf(Either<U, V> instance) {
        if (instance instanceof EitherImpl) {
            return (EitherImpl)instance;
        }
        return EitherImpl.of(instance.primary(), instance.fallback());
    }
}

