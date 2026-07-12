/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception.handling;

import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionContext;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface ExceptionHandler<C, T extends Throwable> {
    public static <C, T extends Throwable> @NonNull ExceptionHandler<C, T> noopHandler() {
        return ctx -> {};
    }

    public static <C, T extends Throwable> @NonNull ExceptionHandler<C, T> passThroughHandler() {
        return ctx -> {
            throw ctx.exception();
        };
    }

    public static <C, T extends Throwable> @NonNull ExceptionHandler<C, T> passThroughHandler(@NonNull Consumer<ExceptionContext<C, T>> consumer) {
        return ctx -> {
            consumer.accept(ctx);
            throw ctx.exception();
        };
    }

    public static <C, T extends Throwable> @NonNull ExceptionHandler<C, T> unwrappingHandler(@NonNull Predicate<Throwable> predicate) {
        return ctx -> {
            Throwable cause = ((Throwable)ctx.exception()).getCause();
            if (cause != null && predicate.test(cause)) {
                throw cause;
            }
            throw ctx.exception();
        };
    }

    public static <C, T extends Throwable> @NonNull ExceptionHandler<C, T> unwrappingHandler(@NonNull Class<? extends Throwable> causeClass) {
        return ExceptionHandler.unwrappingHandler(causeClass::isInstance);
    }

    public static <C, T extends Throwable> @NonNull ExceptionHandler<C, T> unwrappingHandler() {
        return ExceptionHandler.unwrappingHandler((Throwable throwable) -> true);
    }

    public void handle(@NonNull ExceptionContext<C, T> var1) throws Throwable;
}

