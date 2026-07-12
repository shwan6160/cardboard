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

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.CommandTree;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.CommandExecutionException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.CommandParseException;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandResult;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinator;
import com.bergerkiller.bukkit.common.dep.cloud.services.State;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestions;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
final class ExecutionCoordinatorImpl<C>
implements ExecutionCoordinator<C> {
    static final Executor NON_SCHEDULING_EXECUTOR = new NonSchedulingExecutor();
    private final @NonNull Executor parsingExecutor;
    private final @NonNull Executor suggestionsExecutor;
    private final @NonNull Executor defaultExecutionExecutor;
    private final @Nullable Semaphore executionLock;

    ExecutionCoordinatorImpl(@Nullable Executor parsingExecutor, @Nullable Executor suggestionsExecutor, @Nullable Executor defaultExecutionExecutor, boolean syncExecution) {
        this.parsingExecutor = ExecutionCoordinatorImpl.orRunNow(parsingExecutor);
        this.suggestionsExecutor = ExecutionCoordinatorImpl.orRunNow(suggestionsExecutor);
        this.defaultExecutionExecutor = ExecutionCoordinatorImpl.orRunNow(defaultExecutionExecutor);
        this.executionLock = syncExecution ? new Semaphore(1) : null;
    }

    private static @NonNull Executor orRunNow(@Nullable Executor e) {
        return e == null ? ExecutionCoordinator.nonSchedulingExecutor() : e;
    }

    @Override
    public @NonNull CompletableFuture<CommandResult<C>> coordinateExecution(@NonNull CommandTree<C> commandTree, @NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        return ((CompletableFuture)commandTree.parse(commandContext, commandInput, this.parsingExecutor).thenApplyAsync(command -> {
            boolean passedPostprocessing = commandTree.commandManager().postprocessContext(commandContext, command) == State.ACCEPTED;
            return Pair.of(command, passedPostprocessing);
        }, this.parsingExecutor)).thenComposeAsync(preprocessResult -> {
            if (!((Boolean)preprocessResult.second()).booleanValue()) {
                return CompletableFuture.completedFuture(CommandResult.of(commandContext));
            }
            if (this.executionLock != null) {
                try {
                    this.executionLock.acquire();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            CompletionStage commandResultFuture = null;
            try {
                commandResultFuture = ((CompletableFuture)((Command)preprocessResult.first()).commandExecutionHandler().executeFuture(commandContext).exceptionally(exception -> {
                    Throwable workingException = exception instanceof CompletionException ? exception.getCause() : exception;
                    if (workingException instanceof CommandParseException) {
                        throw (CommandParseException)workingException;
                    }
                    if (workingException instanceof CommandExecutionException) {
                        throw (CommandExecutionException)workingException;
                    }
                    throw new CommandExecutionException(workingException, commandContext);
                })).thenApply(v -> CommandResult.of(commandContext));
            }
            finally {
                if (this.executionLock != null) {
                    if (commandResultFuture != null) {
                        ((CompletableFuture)commandResultFuture).whenComplete(($, $$) -> this.executionLock.release());
                    } else {
                        this.executionLock.release();
                    }
                }
            }
            return commandResultFuture;
        }, this.defaultExecutionExecutor);
    }

    @Override
    public <S extends Suggestion> @NonNull CompletableFuture<@NonNull Suggestions<C, S>> coordinateSuggestions(@NonNull CommandTree<C> commandTree, @NonNull CommandContext<C> context, @NonNull CommandInput commandInput, @NonNull SuggestionMapper<S> mapper) {
        return commandTree.getSuggestions(context, commandInput, mapper, this.suggestionsExecutor);
    }

    private static final class NonSchedulingExecutor
    implements Executor {
        private NonSchedulingExecutor() {
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}

