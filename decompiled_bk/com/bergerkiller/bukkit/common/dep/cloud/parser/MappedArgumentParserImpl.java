/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.MappedArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL)
public final class MappedArgumentParserImpl<C, I, O>
implements MappedArgumentParser<C, I, O>,
ArgumentParser.FutureArgumentParser<C, O> {
    private final ArgumentParser<C, I> base;
    private final MappedArgumentParser.Mapper<C, I, O> mapper;

    MappedArgumentParserImpl(ArgumentParser<C, I> base, MappedArgumentParser.Mapper<C, I, O> mapper) {
        this.base = base;
        this.mapper = mapper;
    }

    @Override
    public @NonNull ArgumentParser<C, I> baseParser() {
        return this.base;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull ArgumentParseResult<O>> parseFuture(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        return this.base.parseFuture(commandContext, commandInput).thenCompose(result -> this.mapper.map(commandContext, (ArgumentParseResult<I>)result));
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return this.base.suggestionProvider();
    }

    @Override
    public <O1> @NonNull ArgumentParser.FutureArgumentParser<C, O1> flatMap(MappedArgumentParser.Mapper<C, O, O1> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return new MappedArgumentParserImpl(this.base, (ctx, orig) -> this.mapper.map(ctx, orig).thenCompose(mapped -> mapper.map(ctx, (ArgumentParseResult)mapped)));
    }

    public int hashCode() {
        return 31 + this.base.hashCode() + 7 * this.mapper.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (!(other instanceof MappedArgumentParserImpl)) {
            return false;
        }
        MappedArgumentParserImpl that = (MappedArgumentParserImpl)other;
        return this.base.equals(that.base) && this.mapper.equals(that.mapper);
    }

    public String toString() {
        return "MappedArgumentParserImpl{base=" + this.base + ',' + "mapper=" + this.mapper + '}';
    }
}

