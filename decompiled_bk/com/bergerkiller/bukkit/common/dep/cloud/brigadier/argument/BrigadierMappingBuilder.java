/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument.BrigadierMapping;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Objects;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.returnsreceiver.qual.This;

public interface BrigadierMappingBuilder<K extends ArgumentParser<?, ?>, S> {
    public @This @NonNull BrigadierMappingBuilder<K, S> toConstant(ArgumentType<?> var1);

    public @This @NonNull BrigadierMappingBuilder<K, S> to(Function<K, ? extends ArgumentType<?>> var1);

    public @This @NonNull BrigadierMappingBuilder<K, S> nativeSuggestions();

    public @This @NonNull BrigadierMappingBuilder<K, S> cloudSuggestions();

    default public @This @NonNull BrigadierMappingBuilder<K, S> suggestedByConstant(SuggestionProvider<S> provider) {
        Objects.requireNonNull(provider, "provider");
        return this.suggestedBy((argument, useCloud) -> provider);
    }

    public @This @NonNull BrigadierMappingBuilder<K, S> suggestedBy(SuggestionProviderSupplier<K, S> var1);

    public @NonNull BrigadierMapping<?, K, S> build();

    @FunctionalInterface
    public static interface SuggestionProviderSupplier<K extends ArgumentParser<?, ?>, S> {
        public @Nullable SuggestionProvider<? super S> provide(@NonNull K var1, SuggestionProvider<S> var2);
    }
}

