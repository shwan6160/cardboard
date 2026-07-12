/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
final class AggregateSuggestionProvider<C>
implements SuggestionProvider<C> {
    private final AggregateParser<C, ?> parser;

    AggregateSuggestionProvider(@NonNull AggregateParser<C, ?> parser) {
        this.parser = parser;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Iterable<@NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> context, @NonNull CommandInput input) {
        CommandInput originalInput = input.copy();
        return ((CompletableFuture)new ParsingInstance(context, input).parseComponent().thenCompose(component -> component.suggestionProvider().suggestionsFuture(context, input.skipWhitespace(1, false).copy()))).thenApply(suggestions -> {
            String prefix = originalInput.difference(input, true);
            ArrayList<Suggestion> prefixedSuggestions = new ArrayList<Suggestion>();
            for (Suggestion suggestion : suggestions) {
                prefixedSuggestions.add(suggestion.withSuggestion(String.format("%s%s", prefix, suggestion.suggestion())));
            }
            return prefixedSuggestions;
        });
    }

    private final class ParsingInstance {
        private final Iterator<CommandComponent<C>> components;
        private final CommandContext<C> context;
        private final CommandInput input;
        private CommandComponent<C> component;
        private int previousCursor;

        private ParsingInstance(@NonNull CommandContext<C> context, CommandInput input) {
            this.components = AggregateSuggestionProvider.this.parser.components().iterator();
            this.context = context;
            this.input = input;
        }

        private @NonNull CompletableFuture<CommandComponent<C>> parseComponent() {
            if (!this.components.hasNext()) {
                return CompletableFuture.completedFuture(this.component);
            }
            this.component = this.components.next();
            this.previousCursor = this.input.cursor();
            return this.component.parser().parseFuture(this.context, this.input.skipWhitespace(1)).thenCompose(this::handleResult);
        }

        private @NonNull CompletableFuture<CommandComponent<C>> handleResult(@NonNull ArgumentParseResult<?> result) {
            boolean consumedAll = this.input.isEmpty();
            if (result.failure().isPresent() || !this.components.hasNext() || this.input.isEmpty()) {
                this.input.cursor(this.previousCursor);
            }
            if (result.failure().isPresent()) {
                return CompletableFuture.completedFuture(this.component);
            }
            result.parsedValue().ifPresent(value -> this.context.store(this.component.name(), value));
            if (consumedAll) {
                return CompletableFuture.completedFuture(this.component);
            }
            return this.parseComponent();
        }
    }
}

