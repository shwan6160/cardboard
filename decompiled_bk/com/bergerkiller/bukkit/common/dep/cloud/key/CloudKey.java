/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.key;

import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKeyImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public abstract class CloudKey<T> {
    @API(status=API.Status.STABLE)
    public static <T> CloudKey<T> of(@NonNull String name, @NonNull TypeToken<T> type) {
        return CloudKeyImpl.of(name, type);
    }

    @API(status=API.Status.STABLE)
    public static <T> CloudKey<T> of(@NonNull String name, @NonNull Class<T> type) {
        return CloudKeyImpl.of(name, TypeToken.get(type));
    }

    @API(status=API.Status.STABLE)
    public static @NonNull CloudKey<Void> of(@NonNull String name) {
        return CloudKeyImpl.of(name, TypeToken.get(Void.TYPE));
    }

    @API(status=API.Status.STABLE)
    public static <T> CloudKey<T> cloudKey(@NonNull String name, @NonNull TypeToken<T> type) {
        return CloudKeyImpl.of(name, type);
    }

    @API(status=API.Status.STABLE)
    public static <T> CloudKey<T> cloudKey(@NonNull String name, @NonNull Class<T> type) {
        return CloudKeyImpl.of(name, TypeToken.get(type));
    }

    @API(status=API.Status.STABLE)
    public static @NonNull CloudKey<Void> cloudKey(@NonNull String name) {
        return CloudKeyImpl.of(name, TypeToken.get(Void.TYPE));
    }

    public abstract @NonNull String name();

    public abstract @NonNull TypeToken<@NonNull T> type();

    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        CloudKey that = (CloudKey)other;
        return Objects.equals(this.name(), that.name());
    }

    public final int hashCode() {
        return Objects.hashCode(this.name());
    }
}

