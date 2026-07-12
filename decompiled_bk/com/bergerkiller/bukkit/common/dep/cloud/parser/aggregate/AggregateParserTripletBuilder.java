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
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Triplet;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class AggregateParserTripletBuilder<C, U, V, Z, O> {
    private final Mapper<C, U, V, Z, O> mapper;
    private final TypeToken<O> outType;
    private final TypedCommandComponent<C, U> first;
    private final TypedCommandComponent<C, V> second;
    private final TypedCommandComponent<C, Z> third;

    public static <C, U, V, Z> Mapper<C, U, V, Z, Triplet<U, V, Z>> defaultMapper() {
        return (ctx, u, v, z) -> ArgumentParseResult.successFuture(Triplet.of(u, v, z));
    }

    public AggregateParserTripletBuilder(TypedCommandComponent<C, U> first, TypedCommandComponent<C, V> second, TypedCommandComponent<C, Z> third, Mapper<C, U, V, Z, O> mapper, TypeToken<O> outType) {
        this.mapper = mapper;
        this.outType = outType;
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public <O1> AggregateParserTripletBuilder<C, U, V, Z, O1> withMapper(@NonNull TypeToken<O1> outType, @NonNull Mapper<C, U, V, Z, O1> mapper) {
        return new AggregateParserTripletBuilder<C, U, V, Z, O1>(this.first, this.second, this.third, mapper, outType);
    }

    public <O1> AggregateParserTripletBuilder<C, U, V, Z, O1> withDirectMapper(@NonNull TypeToken<O1> outType, @NonNull Mapper.DirectSuccessMapper<C, U, V, Z, O1> mapper) {
        return this.withMapper(outType, mapper);
    }

    public AggregateParser<C, O> build() {
        return new AggregateParserBuilder(Arrays.asList(this.first, this.second, this.third)).withMapper(this.outType, (CommandContext<C> commandContext, AggregateParsingContext<C> aggregateContext) -> {
            Object firstResult = aggregateContext.get(this.first.name());
            Object secondResult = aggregateContext.get(this.second.name());
            Object thirdResult = aggregateContext.get(this.third.name());
            return this.mapper.map(commandContext, firstResult, secondResult, thirdResult);
        }).build();
    }

    public static <C, U, V, Z, O> @NonNull Mapper<C, U, V, Z, O> directMapper(@NonNull Mapper.DirectSuccessMapper<C, U, V, Z, O> mapper) {
        return Objects.requireNonNull(mapper, "mapper");
    }

    public static interface Mapper<C, U, V, Z, O> {
        public @NonNull CompletableFuture<ArgumentParseResult<O>> map(@NonNull CommandContext<C> var1, @NonNull U var2, @NonNull V var3, @NonNull Z var4);

        public static interface DirectSuccessMapper<C, U, V, Z, O>
        extends Mapper<C, U, V, Z, O> {
            public @NonNull O mapSuccess(@NonNull CommandContext<C> var1, @NonNull U var2, @NonNull V var3, @NonNull Z var4);

            @Override
            default public @NonNull CompletableFuture<ArgumentParseResult<O>> map(@NonNull CommandContext<C> commandContext, @NonNull U firstResult, @NonNull V secondResult, @NonNull Z thirdResult) {
                return ArgumentParseResult.successFuture(this.mapSuccess(commandContext, firstResult, secondResult, thirdResult));
            }
        }
    }
}

