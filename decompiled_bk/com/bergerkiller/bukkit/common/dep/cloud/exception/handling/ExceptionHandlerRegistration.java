/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception.handling;

import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionHandler;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.function.Predicate;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class ExceptionHandlerRegistration<C, T extends Throwable> {
    private final TypeToken<T> exceptionType;
    private final ExceptionHandler<C, ? extends T> exceptionHandler;
    private final Predicate<T> exceptionFilter;

    public static <C, T extends Throwable> @NonNull ExceptionHandlerRegistration<C, ? extends T> of(@NonNull TypeToken<T> exceptionType, @NonNull ExceptionHandler<C, ? extends T> exceptionHandler) {
        return ExceptionHandlerRegistration.builder(exceptionType).exceptionHandler(exceptionHandler).build();
    }

    public static <C, T extends Throwable> @NonNull ExceptionControllerBuilder<C, T> builder(@NonNull TypeToken<T> exceptionType) {
        return new ExceptionControllerBuilder(exceptionType);
    }

    private ExceptionHandlerRegistration(@NonNull TypeToken<T> exceptionType, @NonNull ExceptionHandler<C, ? extends T> exceptionHandler, @NonNull Predicate<T> exceptionFilter) {
        this.exceptionType = exceptionType;
        this.exceptionHandler = exceptionHandler;
        this.exceptionFilter = exceptionFilter;
    }

    public @NonNull TypeToken<T> exceptionType() {
        return this.exceptionType;
    }

    public @NonNull ExceptionHandler<C, ? extends T> exceptionHandler() {
        return this.exceptionHandler;
    }

    public @NonNull Predicate<T> exceptionFilter() {
        return this.exceptionFilter;
    }

    @API(status=API.Status.STABLE)
    public static final class ExceptionControllerBuilder<C, T extends Throwable> {
        private final TypeToken<T> exceptionType;
        private final ExceptionHandler<C, ? extends T> exceptionHandler;
        private final Predicate<T> exceptionFilter;

        private ExceptionControllerBuilder(@NonNull TypeToken<T> exceptionType, @NonNull ExceptionHandler<C, ? extends T> exceptionHandler, @NonNull Predicate<T> exceptionFilter) {
            this.exceptionType = exceptionType;
            this.exceptionHandler = exceptionHandler;
            this.exceptionFilter = exceptionFilter;
        }

        private ExceptionControllerBuilder(@NonNull TypeToken<T> exceptionType) {
            this(exceptionType, ExceptionHandler.noopHandler(), exception -> true);
        }

        public @NonNull ExceptionControllerBuilder<C, T> exceptionHandler(@NonNull ExceptionHandler<C, ? extends T> exceptionHandler) {
            return new ExceptionControllerBuilder<C, T>(this.exceptionType, exceptionHandler, this.exceptionFilter);
        }

        public @NonNull ExceptionControllerBuilder<C, T> exceptionFilter(@NonNull Predicate<T> exceptionFilter) {
            return new ExceptionControllerBuilder<C, T>(this.exceptionType, this.exceptionHandler, exceptionFilter);
        }

        public @NonNull ExceptionHandlerRegistration<C, ? extends T> build() {
            return new ExceptionHandlerRegistration(this.exceptionType, this.exceptionHandler, this.exceptionFilter);
        }
    }

    @FunctionalInterface
    @API(status=API.Status.STABLE)
    public static interface BuilderDecorator<C, T extends Throwable> {
        public @NonNull ExceptionControllerBuilder<C, T> decorate(@NonNull ExceptionControllerBuilder<C, T> var1);
    }
}

