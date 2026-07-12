/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.execution;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandResult;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface CommandExecutor<C> {
    default public @NonNull CompletableFuture<CommandResult<C>> executeCommand(@NonNull C commandSender, @NonNull String input) {
        return this.executeCommand(commandSender, input, context -> {});
    }

    public @NonNull CompletableFuture<CommandResult<C>> executeCommand(@NonNull C var1, @NonNull String var2, @NonNull Consumer<CommandContext<C>> var3);

    public @NonNull ExecutionCoordinator<C> executionCoordinator();
}

