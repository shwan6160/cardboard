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
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.NoSuggestions;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
@FunctionalInterface
public interface SuggestionProvider<C> {
    public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> var1, @NonNull CommandInput var2);

    public static <C> @NonNull SuggestionProvider<C> noSuggestions() {
        return NoSuggestions.instance();
    }

    public static <C> @NonNull SuggestionProvider<C> blocking(@NonNull BlockingSuggestionProvider<C> blockingSuggestionProvider) {
        return blockingSuggestionProvider;
    }

    public static <C> @NonNull SuggestionProvider<C> blockingStrings(@NonNull BlockingSuggestionProvider.Strings<C> blockingStringsSuggestionProvider) {
        return blockingStringsSuggestionProvider;
    }

    public static <C> @NonNull SuggestionProvider<C> suggesting(Suggestion ... suggestions) {
        return SuggestionProvider.suggesting(Arrays.asList(suggestions));
    }

    public static <C> @NonNull SuggestionProvider<C> suggestingStrings(String ... suggestions) {
        return SuggestionProvider.suggestingStrings(Arrays.asList(suggestions));
    }

    public static <C> @NonNull SuggestionProvider<C> suggesting(@NonNull Iterable<? extends @NonNull Suggestion> suggestions) {
        return SuggestionProvider.blocking((ctx, input) -> suggestions);
    }

    public static <C> @NonNull SuggestionProvider<C> suggestingStrings(@NonNull Iterable<@NonNull String> suggestions) {
        return SuggestionProvider.blockingStrings((ctx, input) -> suggestions);
    }
}

