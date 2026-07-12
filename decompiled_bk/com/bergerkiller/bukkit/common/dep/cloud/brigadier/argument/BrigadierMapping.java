/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument.BrigadierMappingBuilder;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Objects;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.INTERNAL, since="2.0.0")
public final class BrigadierMapping<C, K extends ArgumentParser<C, ?>, S> {
    private static final SuggestionProvider<?> DELEGATE_TO_CLOUD = (c, b) -> b.buildFuture();
    private final boolean cloudSuggestions;
    private final @Nullable BrigadierMappingBuilder.SuggestionProviderSupplier<K, S> suggestionsOverride;
    private final @Nullable Function<K, ? extends ArgumentType<?>> mapper;

    public static <T> SuggestionProvider<T> delegateSuggestions() {
        return DELEGATE_TO_CLOUD;
    }

    public static <C, K extends ArgumentParser<C, ?>, S> @NonNull BrigadierMappingBuilder<K, S> builder() {
        return new BuilderImpl();
    }

    BrigadierMapping(boolean cloudSuggestions, @Nullable BrigadierMappingBuilder.SuggestionProviderSupplier<K, S> suggestionsOverride, @Nullable Function<K, ? extends ArgumentType<?>> mapper) {
        this.cloudSuggestions = cloudSuggestions;
        this.suggestionsOverride = suggestionsOverride;
        this.mapper = mapper;
    }

    public @Nullable Function<K, ? extends ArgumentType<?>> mapper() {
        return this.mapper;
    }

    public @NonNull BrigadierMapping<C, K, S> withNativeSuggestions(boolean nativeSuggestions) {
        if (nativeSuggestions && this.cloudSuggestions) {
            return new BrigadierMapping<C, K, S>(false, this.suggestionsOverride, this.mapper);
        }
        if (!nativeSuggestions && !this.cloudSuggestions) {
            return new BrigadierMapping<C, K, S>(true, this.suggestionsOverride, this.mapper);
        }
        return this;
    }

    public @Nullable SuggestionProvider<S> makeSuggestionProvider(K commandArgument) {
        if (this.cloudSuggestions) {
            return BrigadierMapping.delegateSuggestions();
        }
        return this.suggestionsOverride == null ? null : this.suggestionsOverride.provide(commandArgument, BrigadierMapping.delegateSuggestions());
    }

    private static final class BuilderImpl<C, K extends ArgumentParser<C, ?>, S>
    implements BrigadierMappingBuilder<K, S> {
        private Function<K, ? extends ArgumentType<?>> mapper;
        private boolean cloudSuggestions = false;
        private BrigadierMappingBuilder.SuggestionProviderSupplier<K, S> suggestionsOverride;

        private BuilderImpl() {
        }

        @Override
        public @This @NonNull BrigadierMappingBuilder<K, S> toConstant(ArgumentType<?> constant) {
            return this.to(parser -> constant);
        }

        @Override
        public @This @NonNull BrigadierMappingBuilder<K, S> to(Function<K, ? extends ArgumentType<?>> mapper) {
            this.mapper = mapper;
            return this;
        }

        @Override
        public @This @NonNull BrigadierMappingBuilder<K, S> nativeSuggestions() {
            this.cloudSuggestions = false;
            this.suggestionsOverride = null;
            return this;
        }

        @Override
        public @This @NonNull BrigadierMappingBuilder<K, S> cloudSuggestions() {
            this.cloudSuggestions = true;
            this.suggestionsOverride = null;
            return this;
        }

        @Override
        public @This @NonNull BrigadierMappingBuilder<K, S> suggestedByConstant(SuggestionProvider<S> provider) {
            BrigadierMappingBuilder.super.suggestedByConstant(provider);
            this.cloudSuggestions = false;
            return this;
        }

        @Override
        public @This @NonNull BrigadierMappingBuilder<K, S> suggestedBy(BrigadierMappingBuilder.SuggestionProviderSupplier<K, S> provider) {
            this.suggestionsOverride = Objects.requireNonNull(provider, "provider");
            this.cloudSuggestions = false;
            return this;
        }

        @Override
        public @NonNull BrigadierMapping<C, K, S> build() {
            return new BrigadierMapping(this.cloudSuggestions, this.suggestionsOverride, this.mapper);
        }
    }
}

