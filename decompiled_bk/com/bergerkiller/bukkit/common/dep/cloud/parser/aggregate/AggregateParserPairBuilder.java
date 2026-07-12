/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate;

import com.bergerkiller.bukkit.common.dep.cloud.component.TypedCommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParserBuilder;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParsingContext;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class AggregateParserPairBuilder<C, U, V, O> {
    private final Mapper<C, U, V, O> mapper;
    private final TypeToken<O> outType;
    private final TypedCommandComponent<C, U> first;
    private final TypedCommandComponent<C, V> second;

    public static <C, U, V> Mapper<C, U, V, Pair<U, V>> defaultMapper() {
        return (ctx, u, v) -> ArgumentParseResult.successFuture(Pair.of(u, v));
    }

    public AggregateParserPairBuilder(TypedCommandComponent<C, U> first, TypedCommandComponent<C, V> second, Mapper<C, U, V, O> mapper, TypeToken<O> outType) {
        this.mapper = mapper;
        this.outType = outType;
        this.first = first;
        this.second = second;
    }

    public <O1> AggregateParserPairBuilder<C, U, V, O1> withMapper(@NonNull TypeToken<O1> outType, @NonNull Mapper<C, U, V, O1> mapper) {
        return new AggregateParserPairBuilder<C, U, V, O1>(this.first, this.second, mapper, outType);
    }

    public <O1> AggregateParserPairBuilder<C, U, V, O1> withDirectMapper(@NonNull TypeToken<O1> outType, @NonNull Mapper.DirectSuccessMapper<C, U, V, O1> mapper) {
        return this.withMapper(outType, mapper);
    }

    public AggregateParser<C, O> build() {
        return new AggregateParserBuilder(Arrays.asList(this.first, this.second)).withMapper(this.outType, (CommandContext<C> commandContext, AggregateParsingContext<C> aggregateContext) -> {
            Object firstResult = aggregateContext.get(this.first.name());
            Object secondResult = aggregateContext.get(this.second.name());
            return this.mapper.map(commandContext, firstResult, secondResult);
        }).build();
    }

    public static <C, U, V, O> @NonNull Mapper<C, U, V, O> directMapper(@NonNull Mapper.DirectSuccessMapper<C, U, V, O> mapper) {
        return Objects.requireNonNull(mapper, "mapper");
    }

    public static interface Mapper<C, U, V, O> {
        public @NonNull CompletableFuture<ArgumentParseResult<O>> map(@NonNull CommandContext<C> var1, @NonNull U var2, @NonNull V var3);

        public static interface DirectSuccessMapper<C, U, V, O>
        extends Mapper<C, U, V, O> {
            public @NonNull O mapSuccess(@NonNull CommandContext<C> var1, @NonNull U var2, @NonNull V var3);

            @Override
            default public @NonNull CompletableFuture<ArgumentParseResult<O>> map(@NonNull CommandContext<C> commandContext, @NonNull U firstResult, @NonNull V secondResult) {
                return ArgumentParseResult.successFuture(this.mapSuccess(commandContext, firstResult, secondResult));
            }
        }
    }
}

