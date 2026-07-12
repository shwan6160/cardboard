/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.node;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion.SuggestionsType;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

@API(status=API.Status.INTERNAL, since="2.0.0")
@Value.Immutable
interface ArgumentMapping<S> {
    public @NonNull ArgumentType<?> argumentType();

    default public @NonNull SuggestionsType suggestionsType() {
        return SuggestionsType.BRIGADIER_SUGGESTIONS;
    }

    public @Nullable SuggestionProvider<S> suggestionProvider();
}

