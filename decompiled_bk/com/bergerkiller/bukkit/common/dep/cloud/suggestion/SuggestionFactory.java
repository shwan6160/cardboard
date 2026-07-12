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
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.MappingSuggestionFactory;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface SuggestionFactory<C, S extends Suggestion> {
    public @NonNull CompletableFuture<@NonNull Suggestions<C, S>> suggest(@NonNull CommandContext<C> var1, @NonNull String var2);

    public @NonNull CompletableFuture<@NonNull Suggestions<C, S>> suggest(@NonNull C var1, @NonNull String var2);

    default public @NonNull Suggestions<C, S> suggestImmediately(@NonNull C sender, @NonNull String input) {
        try {
            return this.suggest(sender, input).join();
        }
        catch (CompletionException completionException) {
            Throwable cause = completionException.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw completionException;
        }
    }

    default public <S2 extends Suggestion> @NonNull SuggestionFactory<C, S2> mapped(@NonNull SuggestionMapper<S2> mapper) {
        return new MappingSuggestionFactory(this, mapper);
    }
}

