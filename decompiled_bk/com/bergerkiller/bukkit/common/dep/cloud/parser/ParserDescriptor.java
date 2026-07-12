/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.MappedArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptorImpl;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface ParserDescriptor<C, T> {
    public @NonNull ArgumentParser<C, T> parser();

    public @NonNull TypeToken<T> valueType();

    default public <O> @NonNull ParserDescriptor<C, O> flatMap(@NonNull TypeToken<O> mappedType, @NonNull MappedArgumentParser.Mapper<C, T, O> mapper) {
        return ParserDescriptor.parserDescriptor(this.parser().flatMap(mapper), mappedType);
    }

    default public <O> @NonNull ParserDescriptor<C, O> flatMap(@NonNull Class<O> mappedType, @NonNull MappedArgumentParser.Mapper<C, T, O> mapper) {
        return ParserDescriptor.parserDescriptor(this.parser().flatMap(mapper), mappedType);
    }

    default public <O> @NonNull ParserDescriptor<C, O> flatMapSuccess(@NonNull TypeToken<O> mappedType, @NonNull BiFunction<CommandContext<C>, T, CompletableFuture<ArgumentParseResult<O>>> mapper) {
        return ParserDescriptor.parserDescriptor(this.parser().flatMapSuccess(mapper), mappedType);
    }

    default public <O> @NonNull ParserDescriptor<C, O> flatMapSuccess(@NonNull Class<O> mappedType, @NonNull BiFunction<CommandContext<C>, T, CompletableFuture<ArgumentParseResult<O>>> mapper) {
        return ParserDescriptor.parserDescriptor(this.parser().flatMapSuccess(mapper), mappedType);
    }

    default public <O> @NonNull ParserDescriptor<C, O> mapSuccess(@NonNull TypeToken<O> mappedType, @NonNull BiFunction<CommandContext<C>, T, CompletableFuture<O>> mapper) {
        return ParserDescriptor.parserDescriptor(this.parser().mapSuccess(mapper), mappedType);
    }

    default public <O> @NonNull ParserDescriptor<C, O> mapSuccess(@NonNull Class<O> mappedType, @NonNull BiFunction<CommandContext<C>, T, CompletableFuture<O>> mapper) {
        return ParserDescriptor.parserDescriptor(this.parser().mapSuccess(mapper), mappedType);
    }

    public static <C, T> @NonNull ParserDescriptor<C, T> of(@NonNull ArgumentParser<C, T> parser, @NonNull TypeToken<T> valueType) {
        return ParserDescriptorImpl.of(parser, valueType);
    }

    public static <C, T> @NonNull ParserDescriptor<C, T> of(@NonNull ArgumentParser<C, T> parser, @NonNull Class<T> valueType) {
        return ParserDescriptorImpl.of(parser, TypeToken.get(valueType));
    }

    public static <C, T> @NonNull ParserDescriptor<C, T> parserDescriptor(@NonNull ArgumentParser<C, T> parser, @NonNull TypeToken<T> valueType) {
        return ParserDescriptor.of(parser, valueType);
    }

    public static <C, T> @NonNull ParserDescriptor<C, T> parserDescriptor(@NonNull ArgumentParser<C, T> parser, @NonNull Class<T> valueType) {
        return ParserDescriptor.of(parser, TypeToken.get(valueType));
    }
}

