/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.MonotonicNonNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.execution;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandExecutionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL)
final class MulticastDelegateFutureCommandExecutionHandler<C>
implements CommandExecutionHandler.FutureCommandExecutionHandler<C> {
    private final List<CommandExecutionHandler<C>> handlers;

    MulticastDelegateFutureCommandExecutionHandler(@NonNull List<@NonNull CommandExecutionHandler<C>> handlers) {
        ArrayList<CommandExecutionHandler<C>> unwrappedHandlers = new ArrayList<CommandExecutionHandler<C>>();
        for (CommandExecutionHandler<C> handler : handlers) {
            if (handler instanceof MulticastDelegateFutureCommandExecutionHandler) {
                unwrappedHandlers.addAll(((MulticastDelegateFutureCommandExecutionHandler)handler).handlers);
                continue;
            }
            unwrappedHandlers.add(handler);
        }
        this.handlers = Collections.unmodifiableList(unwrappedHandlers);
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(@NonNull CommandContext<C> commandContext) {
        @MonotonicNonNull CompletionStage<@Nullable Object> composedHandler = null;
        if (this.handlers.isEmpty()) {
            composedHandler = CompletableFuture.completedFuture(null);
        } else {
            for (CommandExecutionHandler handler : this.handlers) {
                if (composedHandler == null) {
                    composedHandler = handler.executeFuture(commandContext);
                    continue;
                }
                composedHandler = composedHandler.thenCompose(ignore -> handler.executeFuture(commandContext));
            }
        }
        return composedHandler;
    }
}

