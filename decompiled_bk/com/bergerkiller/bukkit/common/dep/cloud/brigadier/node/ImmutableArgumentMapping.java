/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.node;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.node.ArgumentMapping;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion.SuggestionsType;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Generated;

@API(status=API.Status.STABLE, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="ArgumentMapping", generator="Immutables")
final class ImmutableArgumentMapping<S>
implements ArgumentMapping<S> {
    private final @NonNull ArgumentType<?> argumentType;
    private final @NonNull SuggestionsType suggestionsType;
    private final @Nullable SuggestionProvider<S> suggestionProvider;

    private ImmutableArgumentMapping(@NonNull ArgumentType<?> argumentType, @NonNull SuggestionsType suggestionsType, @Nullable SuggestionProvider<S> suggestionProvider) {
        this.argumentType = Objects.requireNonNull(argumentType, "argumentType");
        this.suggestionsType = Objects.requireNonNull(suggestionsType, "suggestionsType");
        this.suggestionProvider = suggestionProvider;
    }

    private ImmutableArgumentMapping(Builder<S> builder) {
        this.argumentType = ((Builder)builder).argumentType;
        this.suggestionProvider = ((Builder)builder).suggestionProvider;
        this.suggestionsType = ((Builder)builder).suggestionsType != null ? ((Builder)builder).suggestionsType : Objects.requireNonNull(ArgumentMapping.super.suggestionsType(), "suggestionsType");
    }

    private ImmutableArgumentMapping(ImmutableArgumentMapping<S> original, @NonNull ArgumentType<?> argumentType, @NonNull SuggestionsType suggestionsType, @Nullable SuggestionProvider<S> suggestionProvider) {
        this.argumentType = argumentType;
        this.suggestionsType = suggestionsType;
        this.suggestionProvider = suggestionProvider;
    }

    @Override
    public @NonNull ArgumentType<?> argumentType() {
        return this.argumentType;
    }

    @Override
    public @NonNull SuggestionsType suggestionsType() {
        return this.suggestionsType;
    }

    @Override
    public @Nullable SuggestionProvider<S> suggestionProvider() {
        return this.suggestionProvider;
    }

    public final ImmutableArgumentMapping<S> withArgumentType(@NonNull ArgumentType<?> value) {
        if (this.argumentType == value) {
            return this;
        }
        @NonNull ArgumentType<?> newValue = Objects.requireNonNull(value, "argumentType");
        return new ImmutableArgumentMapping<S>(this, newValue, this.suggestionsType, this.suggestionProvider);
    }

    public final ImmutableArgumentMapping<S> withSuggestionsType(@NonNull SuggestionsType value) {
        @NonNull SuggestionsType newValue = Objects.requireNonNull(value, "suggestionsType");
        if (this.suggestionsType == newValue) {
            return this;
        }
        return new ImmutableArgumentMapping<S>(this, this.argumentType, newValue, this.suggestionProvider);
    }

    public final ImmutableArgumentMapping<S> withSuggestionProvider(@Nullable SuggestionProvider<S> value) {
        if (this.suggestionProvider == value) {
            return this;
        }
        return new ImmutableArgumentMapping<S>(this, this.argumentType, this.suggestionsType, value);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ImmutableArgumentMapping && this.equalsByValue((ImmutableArgumentMapping)another);
    }

    private boolean equalsByValue(ImmutableArgumentMapping<?> another) {
        return this.argumentType.equals(another.argumentType) && this.suggestionsType.equals((Object)another.suggestionsType) && Objects.equals(this.suggestionProvider, another.suggestionProvider);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.argumentType.hashCode();
        h += (h << 5) + this.suggestionsType.hashCode();
        h += (h << 5) + Objects.hashCode(this.suggestionProvider);
        return h;
    }

    public String toString() {
        return "ArgumentMapping{argumentType=" + this.argumentType + ", suggestionsType=" + (Object)((Object)this.suggestionsType) + ", suggestionProvider=" + this.suggestionProvider + "}";
    }

    public static <S> ImmutableArgumentMapping<S> of(@NonNull ArgumentType<?> argumentType, @NonNull SuggestionsType suggestionsType, @Nullable SuggestionProvider<S> suggestionProvider) {
        return new ImmutableArgumentMapping<S>(argumentType, suggestionsType, suggestionProvider);
    }

    public static <S> ImmutableArgumentMapping<S> copyOf(ArgumentMapping<S> instance) {
        if (instance instanceof ImmutableArgumentMapping) {
            return (ImmutableArgumentMapping)instance;
        }
        return ImmutableArgumentMapping.builder().from(instance).build();
    }

    public static <S> Builder<S> builder() {
        return new Builder();
    }

    @Generated(from="ArgumentMapping", generator="Immutables")
    static final class Builder<S> {
        private static final long INIT_BIT_ARGUMENT_TYPE = 1L;
        private long initBits = 1L;
        private @NonNull ArgumentType<?> argumentType;
        private @NonNull SuggestionsType suggestionsType;
        private @Nullable SuggestionProvider<S> suggestionProvider;

        private Builder() {
        }

        public final Builder<S> from(ArgumentMapping<S> instance) {
            Objects.requireNonNull(instance, "instance");
            this.argumentType(instance.argumentType());
            this.suggestionsType(instance.suggestionsType());
            @Nullable SuggestionProvider<S> suggestionProviderValue = instance.suggestionProvider();
            if (suggestionProviderValue != null) {
                this.suggestionProvider(suggestionProviderValue);
            }
            return this;
        }

        public final Builder<S> argumentType(@NonNull ArgumentType<?> argumentType) {
            this.argumentType = Objects.requireNonNull(argumentType, "argumentType");
            this.initBits &= 0xFFFFFFFFFFFFFFFEL;
            return this;
        }

        public final Builder<S> suggestionsType(@NonNull SuggestionsType suggestionsType) {
            this.suggestionsType = Objects.requireNonNull(suggestionsType, "suggestionsType");
            return this;
        }

        public final Builder<S> suggestionProvider(@Nullable SuggestionProvider<S> suggestionProvider) {
            this.suggestionProvider = suggestionProvider;
            return this;
        }

        public ImmutableArgumentMapping<S> build() {
            if (this.initBits != 0L) {
                throw new IllegalStateException(this.formatRequiredAttributesMessage());
            }
            return new ImmutableArgumentMapping(this);
        }

        private String formatRequiredAttributesMessage() {
            ArrayList<String> attributes = new ArrayList<String>();
            if ((this.initBits & 1L) != 0L) {
                attributes.add("argumentType");
            }
            return "Cannot build ArgumentMapping, some of required attributes are not set " + attributes;
        }
    }
}

