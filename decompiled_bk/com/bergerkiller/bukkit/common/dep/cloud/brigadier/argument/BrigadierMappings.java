/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument.BrigadierMapping;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument.BrigadierMappingsImpl;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, since="2.0.0")
public interface BrigadierMappings<C, S> {
    public static <C, S> @NonNull BrigadierMappings<C, S> create() {
        return new BrigadierMappingsImpl();
    }

    public <T, K extends ArgumentParser<C, T>> @Nullable BrigadierMapping<C, K, S> mapping(@NonNull Class<K> var1);

    default public <T, K extends ArgumentParser<C, T>> void registerMapping(@NonNull Class<K> parserType, @NonNull BrigadierMapping<?, K, S> mapping) {
        this.registerMappingUnsafe(parserType, mapping);
    }

    public <K extends ArgumentParser<C, ?>> void registerMappingUnsafe(@NonNull Class<K> var1, @NonNull BrigadierMapping<?, ?, S> var2);
}

