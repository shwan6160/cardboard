/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.NonNull;

final class NoSuggestions
implements SuggestionProvider<Object> {
    private static final SuggestionProvider<?> INSTANCE = new NoSuggestions();
    private final CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> result = CompletableFuture.completedFuture(Collections.emptyList());

    private NoSuggestions() {
    }

    @Override
    public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<Object> context, @NonNull CommandInput input) {
        return this.result;
    }

    static <C> @NonNull SuggestionProvider<C> instance() {
        return INSTANCE;
    }
}

