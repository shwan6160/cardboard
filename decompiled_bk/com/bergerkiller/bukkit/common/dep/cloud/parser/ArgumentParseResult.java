/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionController;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public abstract class ArgumentParseResult<T> {
    private ArgumentParseResult() {
    }

    public static <T> @NonNull ArgumentParseResult<T> failure(@NonNull Throwable failure) {
        return new ParseFailure(failure);
    }

    @API(status=API.Status.STABLE)
    public static <T> @NonNull CompletableFuture<@NonNull ArgumentParseResult<T>> failureFuture(@NonNull Throwable failure) {
        return new ParseFailure(failure).asFuture();
    }

    public static <T> @NonNull ArgumentParseResult<T> success(@NonNull T value) {
        return new ParseSuccess(value);
    }

    @API(status=API.Status.STABLE)
    public static <T> @NonNull CompletableFuture<@NonNull ArgumentParseResult<T>> successFuture(@NonNull T value) {
        return ArgumentParseResult.success(value).asFuture();
    }

    @API(status=API.Status.STABLE)
    public abstract @NonNull Optional<T> parsedValue();

    @API(status=API.Status.STABLE)
    public abstract @NonNull Optional<Throwable> failure();

    @API(status=API.Status.STABLE)
    public final @NonNull CompletableFuture<ArgumentParseResult<T>> asFuture() {
        return CompletableFuture.completedFuture(this);
    }

    public abstract <O> @NonNull CompletableFuture<ArgumentParseResult<O>> flatMapSuccessFuture(@NonNull Function<T, CompletableFuture<ArgumentParseResult<O>>> var1);

    public abstract <O> @NonNull CompletableFuture<ArgumentParseResult<O>> mapSuccessFuture(@NonNull Function<T, CompletableFuture<O>> var1);

    public abstract <O> @NonNull ArgumentParseResult<O> flatMapSuccess(@NonNull Function<T, ArgumentParseResult<O>> var1);

    public abstract <O> @NonNull ArgumentParseResult<O> mapSuccess(@NonNull Function<T, O> var1);

    private static final class ParseFailure<T>
    extends ArgumentParseResult<T> {
        private final Throwable failure;

        private ParseFailure(@NonNull Throwable failure) {
            this.failure = ExceptionController.unwrapCompletionException(failure);
        }

        @Override
        public @NonNull Optional<T> parsedValue() {
            return Optional.empty();
        }

        @Override
        public @NonNull Optional<Throwable> failure() {
            return Optional.of(this.failure);
        }

        @Override
        public <O> @NonNull CompletableFuture<ArgumentParseResult<O>> flatMapSuccessFuture(@NonNull Function<T, CompletableFuture<ArgumentParseResult<O>>> mapper) {
            return CompletableFuture.completedFuture(this.self());
        }

        @Override
        public <O> @NonNull CompletableFuture<ArgumentParseResult<O>> mapSuccessFuture(@NonNull Function<T, CompletableFuture<O>> mapper) {
            return CompletableFuture.completedFuture(this.self());
        }

        @Override
        public <O> @NonNull ArgumentParseResult<O> flatMapSuccess(@NonNull Function<T, ArgumentParseResult<O>> mapper) {
            return this.self();
        }

        @Override
        public <O> @NonNull ArgumentParseResult<O> mapSuccess(@NonNull Function<T, O> mapper) {
            return this.self();
        }

        private <O> @NonNull ArgumentParseResult<O> self() {
            return this;
        }
    }

    private static final class ParseSuccess<T>
    extends ArgumentParseResult<T> {
        private final T value;

        private ParseSuccess(@NonNull T value) {
            this.value = value;
        }

        @Override
        public @NonNull Optional<T> parsedValue() {
            return Optional.of(this.value);
        }

        @Override
        public @NonNull Optional<Throwable> failure() {
            return Optional.empty();
        }

        @Override
        public <O> @NonNull CompletableFuture<ArgumentParseResult<O>> flatMapSuccessFuture(@NonNull Function<T, CompletableFuture<ArgumentParseResult<O>>> mapper) {
            return mapper.apply(this.value);
        }

        @Override
        public <O> @NonNull CompletableFuture<ArgumentParseResult<O>> mapSuccessFuture(@NonNull Function<T, CompletableFuture<O>> mapper) {
            return mapper.apply(this.value).thenApply(ArgumentParseResult::success);
        }

        @Override
        public <O> @NonNull ArgumentParseResult<O> flatMapSuccess(@NonNull Function<T, ArgumentParseResult<O>> mapper) {
            return mapper.apply(this.value);
        }

        @Override
        public <O> @NonNull ArgumentParseResult<O> mapSuccess(@NonNull Function<T, O> mapper) {
            return ArgumentParseResult.success(mapper.apply(this.value));
        }
    }
}

