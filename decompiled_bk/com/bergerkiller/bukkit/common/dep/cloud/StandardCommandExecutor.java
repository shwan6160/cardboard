/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContextFactory;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionController;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandExecutor;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandResult;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinator;
import com.bergerkiller.bukkit.common.dep.cloud.services.State;
import com.bergerkiller.bukkit.common.dep.cloud.util.CompletableFutures;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

final class StandardCommandExecutor<C>
implements CommandExecutor<C> {
    private final CommandManager<C> commandManager;
    private final ExecutionCoordinator<C> executionCoordinator;
    private final CommandContextFactory<C> commandContextFactory;

    StandardCommandExecutor(@NonNull CommandManager<C> commandManager, @NonNull ExecutionCoordinator<C> executionCoordinator, @NonNull CommandContextFactory<C> commandContextFactory) {
        this.commandManager = commandManager;
        this.executionCoordinator = executionCoordinator;
        this.commandContextFactory = commandContextFactory;
    }

    @Override
    public @NonNull CompletableFuture<CommandResult<C>> executeCommand(@NonNull C commandSender, @NonNull String input, @NonNull Consumer<CommandContext<C>> contextConsumer) {
        CommandContext<C> context = this.commandContextFactory.create(false, commandSender);
        contextConsumer.accept(context);
        CommandInput commandInput = CommandInput.of(input);
        return this.executeCommand(context, commandInput).whenComplete((result, throwable) -> {
            if (throwable == null) {
                return;
            }
            try {
                this.commandManager.exceptionController().handleException(context, ExceptionController.unwrapCompletionException(throwable));
            }
            catch (RuntimeException runtimeException) {
                throw runtimeException;
            }
            catch (Throwable e) {
                throw new CompletionException(e);
            }
        });
    }

    private @NonNull CompletableFuture<CommandResult<C>> executeCommand(@NonNull CommandContext<C> context, @NonNull CommandInput commandInput) {
        context.store("__raw_input__", commandInput.copy());
        try {
            if (this.commandManager.preprocessContext(context, commandInput) == State.ACCEPTED) {
                return this.executionCoordinator().coordinateExecution(this.commandManager.commandTree(), context, commandInput);
            }
        }
        catch (Exception e) {
            return CompletableFutures.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NonNull ExecutionCoordinator<C> executionCoordinator() {
        return this.executionCoordinator;
    }
}

