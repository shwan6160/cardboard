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
 *  org.bukkit.NamespacedKey
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.parser;

import com.bergerkiller.bukkit.common.dep.cloud.paper.parser.RegistryEntryParser;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.bukkit.NamespacedKey;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="RegistryEntryParser.RegistryEntry", generator="Immutables")
@Immutable
final class RegistryEntryImpl<E>
implements RegistryEntryParser.RegistryEntry<E> {
    private final E value;
    private final NamespacedKey key;

    private RegistryEntryImpl(E value, NamespacedKey key) {
        this.value = Objects.requireNonNull(value, "value");
        this.key = Objects.requireNonNull(key, "key");
    }

    private RegistryEntryImpl(RegistryEntryImpl<E> original, E value, NamespacedKey key) {
        this.value = value;
        this.key = key;
    }

    @Override
    public E value() {
        return this.value;
    }

    @Override
    public NamespacedKey key() {
        return this.key;
    }

    public final RegistryEntryImpl<E> withValue(E value) {
        if (this.value == value) {
            return this;
        }
        E newValue = Objects.requireNonNull(value, "value");
        return new RegistryEntryImpl<E>(this, newValue, this.key);
    }

    public final RegistryEntryImpl<E> withKey(NamespacedKey value) {
        if (this.key == value) {
            return this;
        }
        NamespacedKey newValue = Objects.requireNonNull(value, "key");
        return new RegistryEntryImpl<E>(this, this.value, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof RegistryEntryImpl && this.equalsByValue((RegistryEntryImpl)another);
    }

    private boolean equalsByValue(RegistryEntryImpl<?> another) {
        return this.value.equals(another.value) && this.key.equals((Object)another.key);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.value.hashCode();
        h += (h << 5) + this.key.hashCode();
        return h;
    }

    public String toString() {
        return "RegistryEntry{value=" + this.value + ", key=" + this.key + "}";
    }

    public static <E> RegistryEntryImpl<E> of(E value, NamespacedKey key) {
        return new RegistryEntryImpl<E>(value, key);
    }

    public static <E> RegistryEntryImpl<E> copyOf(RegistryEntryParser.RegistryEntry<E> instance) {
        if (instance instanceof RegistryEntryImpl) {
            return (RegistryEntryImpl)instance;
        }
        return RegistryEntryImpl.of(instance.value(), instance.key());
    }
}

