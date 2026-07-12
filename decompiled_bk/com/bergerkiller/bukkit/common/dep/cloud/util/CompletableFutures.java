/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
public final class CompletableFutures {
    private CompletableFutures() {
    }

    public static <T> @NonNull CompletableFuture<T> failedFuture(@NonNull Throwable throwable) {
        CompletableFuture future = new CompletableFuture();
        future.completeExceptionally(throwable);
        return future;
    }

    public static <T> CompletableFuture<T> scheduleOn(Executor executor, Supplier<CompletableFuture<T>> futureSupplier) {
        return CompletableFuture.supplyAsync(futureSupplier, executor).thenCompose(Function.identity());
    }
}

