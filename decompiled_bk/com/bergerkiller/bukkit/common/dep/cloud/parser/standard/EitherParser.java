/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.standard;

import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.type.Either;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeFactory;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class EitherParser<C, U, V>
implements ArgumentParser.FutureArgumentParser<C, Either<U, V>>,
SuggestionProvider<C> {
    private final ParserDescriptor<C, U> primary;
    private final ParserDescriptor<C, V> fallback;

    public static <C, U, V> ParserDescriptor<C, Either<U, V>> eitherParser(@NonNull ParserDescriptor<C, U> primary, @NonNull ParserDescriptor<C, V> fallback) {
        return ParserDescriptor.of(new EitherParser<C, U, V>(primary, fallback), TypeToken.get(TypeFactory.parameterizedClass(Either.class, primary.valueType().getType(), fallback.valueType().getType())));
    }

    public EitherParser(@NonNull ParserDescriptor<C, U> primary, @NonNull ParserDescriptor<C, V> fallback) {
        this.primary = Objects.requireNonNull(primary, "primary");
        this.fallback = Objects.requireNonNull(fallback, "fallback");
    }

    public @NonNull ParserDescriptor<C, U> primary() {
        return this.primary;
    }

    public @NonNull ParserDescriptor<C, V> fallback() {
        return this.fallback;
    }

    @Override
    public @NonNull CompletableFuture<@NonNull ArgumentParseResult<Either<U, V>>> parseFuture(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.peekString();
        int originalCursor = commandInput.cursor();
        return this.primary.parser().parseFuture(commandContext, commandInput).thenCompose(primaryResult -> {
            if (primaryResult.parsedValue().isPresent()) {
                return ArgumentParseResult.successFuture(Either.ofPrimary(primaryResult.parsedValue().get()));
            }
            commandInput.cursor(originalCursor);
            return this.fallback.parser().parseFuture(commandContext, commandInput).thenApply(fallbackResult -> {
                if (fallbackResult.parsedValue().isPresent()) {
                    return ArgumentParseResult.success(Either.ofFallback(fallbackResult.parsedValue().get()));
                }
                return ArgumentParseResult.failure(new EitherParseException(primaryResult.failure().get(), fallbackResult.failure().get(), this.primary.valueType(), this.fallback.valueType(), commandContext, input));
            });
        });
    }

    @Override
    public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> context, @NonNull CommandInput input) {
        if (!(this.primary.parser() instanceof SuggestionProvider)) {
            if (!(this.fallback.parser() instanceof SuggestionProvider)) {
                return CompletableFuture.completedFuture(Collections.emptyList());
            }
            return ((SuggestionProvider)((Object)this.fallback.parser())).suggestionsFuture(context, input);
        }
        if (!(this.fallback.parser() instanceof SuggestionProvider)) {
            return ((SuggestionProvider)((Object)this.primary.parser())).suggestionsFuture(context, input);
        }
        CompletableFuture[] suggestionFutures = new CompletableFuture[]{((SuggestionProvider)((Object)this.primary.parser())).suggestionsFuture(context, input.copy()), ((SuggestionProvider)((Object)this.fallback.parser())).suggestionsFuture(context, input)};
        return CompletableFuture.allOf(suggestionFutures).thenApply(ignored -> Stream.concat(StreamSupport.stream(((Iterable)suggestionFutures[0].getNow(Collections.emptyList())).spliterator(), false), StreamSupport.stream(((Iterable)suggestionFutures[1].getNow(Collections.emptyList())).spliterator(), false)).collect(Collectors.toList()));
    }

    public static final class EitherParseException
    extends ParserException {
        private final Throwable primaryFailure;
        private final Throwable fallbackFailure;
        private final TypeToken<?> primaryType;
        private final TypeToken<?> fallbackType;

        private EitherParseException(@NonNull Throwable primaryFailure, @NonNull Throwable fallbackFailure, @NonNull TypeToken<?> primaryType, @NonNull TypeToken<?> fallbackType, @NonNull CommandContext<?> context, @NonNull String input) {
            super(fallbackFailure, EitherParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_EITHER, CaptionVariable.of("input", input), CaptionVariable.of("primary", GenericTypeReflector.erase(primaryType.getType()).getSimpleName()), CaptionVariable.of("fallback", GenericTypeReflector.erase(fallbackType.getType()).getSimpleName()));
            this.primaryFailure = primaryFailure;
            this.fallbackFailure = fallbackFailure;
            this.primaryType = primaryType;
            this.fallbackType = fallbackType;
        }

        public @NonNull Throwable primaryFailure() {
            return this.primaryFailure;
        }

        public @NonNull Throwable fallbackFailure() {
            return this.fallbackFailure;
        }

        public @NonNull TypeToken<?> primaryType() {
            return this.primaryType;
        }

        public @NonNull TypeToken<?> fallbackType() {
            return this.fallbackType;
        }
    }
}

