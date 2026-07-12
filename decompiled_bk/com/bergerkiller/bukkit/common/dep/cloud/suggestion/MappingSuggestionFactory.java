/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionFactory;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;

final class MappingSuggestionFactory<C, S extends Suggestion>
implements SuggestionFactory<C, S> {
    private final SuggestionFactory<C, ?> other;
    private final SuggestionMapper<S> suggestionMapper;

    MappingSuggestionFactory(@NonNull SuggestionFactory<C, ?> other, @NonNull SuggestionMapper<S> suggestionMapper) {
        this.other = other;
        this.suggestionMapper = suggestionMapper;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Suggestions<C, S>> suggest(@NonNull CommandContext<C> context, @NonNull String input) {
        return this.map(this.other.suggest(context, input));
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Suggestions<C, S>> suggest(@NonNull C sender, @NonNull String input) {
        return this.map(this.other.suggest(sender, input));
    }

    @Override
    public <S2 extends Suggestion> @NonNull SuggestionFactory<C, S2> mapped(@NonNull SuggestionMapper<S2> mapper) {
        return new MappingSuggestionFactory<C, S2>(this.other, this.suggestionMapper.then(mapper));
    }

    private <S1 extends Suggestion> @NonNull CompletableFuture<@NonNull Suggestions<C, S>> map(@NonNull CompletableFuture<Suggestions<C, S1>> future) {
        return future.thenApply(suggestions -> Suggestions.create(suggestions.commandContext(), suggestions.list().stream().map(this.suggestionMapper::map).collect(Collectors.toList()), suggestions.commandInput()));
    }
}

