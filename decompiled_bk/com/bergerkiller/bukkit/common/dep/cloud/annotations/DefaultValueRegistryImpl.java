/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.DefaultValueFactory;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.DefaultValueRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

final class DefaultValueRegistryImpl<C>
implements DefaultValueRegistry<C> {
    private final Map<String, DefaultValueFactory<C, ?>> factories = new HashMap();

    DefaultValueRegistryImpl() {
    }

    @Override
    public <T> @This @NonNull DefaultValueRegistry<C> register(@NonNull String name, @NonNull DefaultValueFactory<C, T> defaultValue) {
        this.factories.put(Objects.requireNonNull(name, "name"), Objects.requireNonNull(defaultValue, "defaultValue"));
        return this;
    }

    @Override
    public @NonNull Optional<@NonNull DefaultValueFactory<C, ?>> named(@NonNull String name) {
        return Optional.ofNullable(this.factories.get(Objects.requireNonNull(name, "name")));
    }
}

