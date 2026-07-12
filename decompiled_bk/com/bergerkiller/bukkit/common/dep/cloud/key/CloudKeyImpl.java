/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckReturnValue
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.key;

import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="CloudKey", generator="Immutables")
@Immutable
final class CloudKeyImpl<T>
extends CloudKey<T> {
    private final @NonNull String name;
    private final @NonNull TypeToken<T> type;

    private CloudKeyImpl(@NonNull String name, @NonNull TypeToken<T> type) {
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
    }

    private CloudKeyImpl(CloudKeyImpl<T> original, @NonNull String name, @NonNull TypeToken<T> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public @NonNull String name() {
        return this.name;
    }

    @Override
    public @NonNull TypeToken<T> type() {
        return this.type;
    }

    public final CloudKeyImpl<T> withName(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "name");
        if (this.name.equals(newValue)) {
            return this;
        }
        return new CloudKeyImpl<T>(this, newValue, this.type);
    }

    public final CloudKeyImpl<T> withType(@NonNull TypeToken<T> value) {
        if (this.type == value) {
            return this;
        }
        @NonNull TypeToken<T> newValue = Objects.requireNonNull(value, "type");
        return new CloudKeyImpl<T>(this, this.name, newValue);
    }

    public String toString() {
        return "CloudKey{name=" + this.name + ", type=" + this.type + "}";
    }

    public static <T> CloudKeyImpl<T> of(@NonNull String name, @NonNull TypeToken<T> type) {
        return new CloudKeyImpl<T>(name, type);
    }

    public static <T> CloudKeyImpl<T> copyOf(CloudKey<T> instance) {
        if (instance instanceof CloudKeyImpl) {
            return (CloudKeyImpl)instance;
        }
        return CloudKeyImpl.of(instance.name(), instance.type());
    }
}

