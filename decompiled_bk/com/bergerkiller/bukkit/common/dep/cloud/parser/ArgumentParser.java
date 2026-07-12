/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.MappedArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.MappedArgumentParserImpl;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.EitherParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProviderHolder;
import com.bergerkiller.bukkit.common.dep.cloud.type.Either;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface ArgumentParser<C, T>
extends SuggestionProviderHolder<C> {
    public @NonNull ArgumentParseResult<@NonNull T> parse(@NonNull CommandContext<@NonNull C> var1, @NonNull CommandInput var2);

    @API(status=API.Status.STABLE)
    default public @NonNull CompletableFuture<@NonNull ArgumentParseResult<T>> parseFuture(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        return CompletableFuture.completedFuture(this.parse(commandContext, commandInput));
    }

    @API(status=API.Status.STABLE)
    default public <O> @NonNull FutureArgumentParser<C, O> flatMap(MappedArgumentParser.Mapper<C, T, O> mapper) {
        return new MappedArgumentParserImpl<C, T, O>(this, Objects.requireNonNull(mapper, "mapper"));
    }

    @API(status=API.Status.STABLE)
    default public <O> @NonNull FutureArgumentParser<C, O> flatMapSuccess(@NonNull BiFunction<CommandContext<C>, T, CompletableFuture<ArgumentParseResult<O>>> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return this.flatMap((ctx, result) -> result.flatMapSuccessFuture(value -> (CompletableFuture)mapper.apply(ctx, value)));
    }

    @API(status=API.Status.STABLE)
    default public <O> @NonNull FutureArgumentParser<C, O> mapSuccess(@NonNull BiFunction<CommandContext<C>, T, CompletableFuture<O>> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return this.flatMap((ctx, result) -> result.mapSuccessFuture(value -> (CompletableFuture)mapper.apply(ctx, value)));
    }

    @Override
    default public @NonNull SuggestionProvider<C> suggestionProvider() {
        if (this instanceof SuggestionProvider) {
            return (SuggestionProvider)((Object)this);
        }
        return SuggestionProvider.noSuggestions();
    }

    public static <C, U, V> @NonNull ParserDescriptor<C, Either<U, V>> firstOf(@NonNull ParserDescriptor<C, U> primary, @NonNull ParserDescriptor<C, V> fallback) {
        return EitherParser.eitherParser(primary, fallback);
    }

    @FunctionalInterface
    @API(status=API.Status.STABLE)
    public static interface FutureArgumentParser<C, T>
    extends ArgumentParser<C, T> {
        @Override
        default public @NonNull ArgumentParseResult<@NonNull T> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
            throw new UnsupportedOperationException("parse should not be called on a FutureArgumentParser. Call parseFuture instead.");
        }

        @Override
        public @NonNull CompletableFuture<@NonNull ArgumentParseResult<T>> parseFuture(@NonNull CommandContext<C> var1, @NonNull CommandInput var2);
    }
}

