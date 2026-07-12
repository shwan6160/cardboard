/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface BlockingSuggestionProvider<C>
extends SuggestionProvider<C> {
    public @NonNull Iterable<? extends @NonNull Suggestion> suggestions(@NonNull CommandContext<C> var1, @NonNull CommandInput var2);

    @Override
    default public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> context, @NonNull CommandInput input) {
        return CompletableFuture.completedFuture(this.suggestions(context, input));
    }

    @FunctionalInterface
    @API(status=API.Status.STABLE)
    public static interface Strings<C>
    extends BlockingSuggestionProvider<C> {
        public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> var1, @NonNull CommandInput var2);

        @Override
        default public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> context, @NonNull CommandInput input) {
            return StreamSupport.stream(this.stringSuggestions(context, input).spliterator(), false).map(Suggestion::suggestion).collect(Collectors.toList());
        }
    }
}

