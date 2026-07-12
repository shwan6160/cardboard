/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.parser;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.parser.CloudStringReader;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion.TooltipSuggestion;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class WrappedBrigadierParser<C, T>
implements ArgumentParser<C, T>,
SuggestionProvider<C> {
    public static final String COMMAND_CONTEXT_BRIGADIER_NATIVE_SENDER = "_cloud_brigadier_native_sender";
    private final Supplier<ArgumentType<T>> nativeType;
    private final @Nullable ParseFunction<T> parse;

    public WrappedBrigadierParser(ArgumentType<T> argumentType) {
        this(() -> argumentType);
    }

    public WrappedBrigadierParser(Supplier<ArgumentType<T>> argumentTypeSupplier) {
        this(argumentTypeSupplier, null);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public WrappedBrigadierParser(Supplier<ArgumentType<T>> argumentTypeSupplier, @Nullable ParseFunction<T> parse) {
        Objects.requireNonNull(argumentTypeSupplier, "brigadierType");
        this.nativeType = argumentTypeSupplier;
        this.parse = parse;
    }

    public final ArgumentType<T> nativeArgumentType() {
        return this.nativeType.get();
    }

    @Override
    public final @NonNull ArgumentParseResult<@NonNull T> parse(@NonNull com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        CloudStringReader reader = CloudStringReader.of(commandInput);
        try {
            T result = this.parse != null ? this.parse.apply(this.nativeType.get(), reader) : this.nativeType.get().parse(reader);
            return ArgumentParseResult.success(result);
        }
        catch (CommandSyntaxException ex) {
            return ArgumentParseResult.failure(ex);
        }
    }

    @Override
    public final @NonNull CompletableFuture<@NonNull Iterable<@NonNull Suggestion>> suggestionsFuture(@NonNull com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext<C> commandContext, @NonNull CommandInput input) {
        CommandContext<C> reverseMappedContext = new CommandContext<C>(commandContext.getOrDefault(COMMAND_CONTEXT_BRIGADIER_NATIVE_SENDER, commandContext.sender()), input.input(), Collections.emptyMap(), null, null, Collections.emptyList(), StringRange.at(input.cursor()), null, null, false);
        return this.nativeType.get().listSuggestions(reverseMappedContext, new SuggestionsBuilder(input.input(), input.cursor())).thenApply(suggestions -> {
            ArrayList<TooltipSuggestion> cloud = new ArrayList<TooltipSuggestion>();
            for (com.mojang.brigadier.suggestion.Suggestion suggestion : suggestions.getList()) {
                String beforeSuggestion = input.input().substring(input.cursor(), suggestion.getRange().getStart());
                String afterSuggestion = input.input().substring(suggestion.getRange().getEnd());
                if (beforeSuggestion.isEmpty() && afterSuggestion.isEmpty()) {
                    cloud.add(TooltipSuggestion.suggestion(suggestion.getText(), suggestion.getTooltip()));
                    continue;
                }
                cloud.add(TooltipSuggestion.suggestion(beforeSuggestion + suggestion.getText() + afterSuggestion, suggestion.getTooltip()));
            }
            return cloud;
        });
    }

    @API(status=API.Status.STABLE, since="1.8.0")
    @FunctionalInterface
    public static interface ParseFunction<T> {
        public T apply(ArgumentType<T> var1, StringReader var2) throws CommandSyntaxException;
    }
}

