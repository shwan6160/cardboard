/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.execution;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.MulticastDelegateFutureCommandExecutionHandler;
import com.bergerkiller.bukkit.common.dep.cloud.execution.NullCommandExecutionHandler;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface CommandExecutionHandler<C> {
    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandExecutionHandler<C> noOpCommandExecutionHandler() {
        return NullCommandExecutionHandler.INSTANCE;
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandExecutionHandler<C> delegatingExecutionHandler(List<CommandExecutionHandler<C>> handlers) {
        return new MulticastDelegateFutureCommandExecutionHandler<C>(handlers);
    }

    public void execute(@NonNull CommandContext<C> var1);

    @API(status=API.Status.STABLE)
    default public CompletableFuture<@Nullable Void> executeFuture(@NonNull CommandContext<C> commandContext) {
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        try {
            this.execute(commandContext);
            future.complete(null);
        }
        catch (Throwable throwable) {
            future.completeExceptionally(throwable);
        }
        return future;
    }

    @FunctionalInterface
    @API(status=API.Status.STABLE)
    public static interface FutureCommandExecutionHandler<C>
    extends CommandExecutionHandler<C> {
        @Override
        default public void execute(@NonNull CommandContext<C> commandContext) {
            throw new UnsupportedOperationException("execute should not be called on FutureCommandExecutionHandlers, call executeFuture instead.");
        }

        @Override
        public CompletableFuture<@Nullable Void> executeFuture(@NonNull CommandContext<C> var1);
    }
}

