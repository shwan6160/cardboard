/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 *  org.checkerframework.dataflow.qual.Pure
 */
package com.bergerkiller.bukkit.common.dep.cloud.execution;

import com.bergerkiller.bukkit.common.dep.cloud.CommandTree;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandResult;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinatorBuilderImpl;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinatorImpl;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.checkerframework.dataflow.qual.Pure;

@API(status=API.Status.STABLE)
public interface ExecutionCoordinator<C> {
    @Pure
    public static <C> @NonNull Builder<C> builder() {
        return new ExecutionCoordinatorBuilderImpl();
    }

    @Pure
    public static <C> @NonNull ExecutionCoordinator<C> simpleCoordinator() {
        return ExecutionCoordinator.builder().build();
    }

    @Pure
    public static <C> @NonNull ExecutionCoordinator<C> coordinatorFor(@NonNull Executor executor) {
        return ExecutionCoordinator.builder().executor(executor).build();
    }

    @Pure
    public static <C> @NonNull ExecutionCoordinator<C> asyncCoordinator() {
        return ExecutionCoordinator.builder().commonPoolExecutor().build();
    }

    public @NonNull CompletableFuture<CommandResult<C>> coordinateExecution(@NonNull CommandTree<C> var1, @NonNull CommandContext<C> var2, @NonNull CommandInput var3);

    public <S extends Suggestion> @NonNull CompletableFuture<@NonNull Suggestions<C, S>> coordinateSuggestions(@NonNull CommandTree<C> var1, @NonNull CommandContext<C> var2, @NonNull CommandInput var3, @NonNull SuggestionMapper<S> var4);

    @Pure
    public static @NonNull Executor nonSchedulingExecutor() {
        return ExecutionCoordinatorImpl.NON_SCHEDULING_EXECUTOR;
    }

    @API(status=API.Status.STABLE)
    public static interface Builder<C> {
        default public @This @NonNull Builder<C> executor(@NonNull Executor executor) {
            return this.parsingExecutor(executor).suggestionsExecutor(executor).executionSchedulingExecutor(executor);
        }

        default public @This @NonNull Builder<C> commonPoolExecutor() {
            return this.executor(ForkJoinPool.commonPool());
        }

        public @This @NonNull Builder<C> parsingExecutor(@NonNull Executor var1);

        public @This @NonNull Builder<C> suggestionsExecutor(@NonNull Executor var1);

        public @This @NonNull Builder<C> executionSchedulingExecutor(@NonNull Executor var1);

        default public @This @NonNull Builder<C> synchronizeExecution() {
            return this.synchronizeExecution(true);
        }

        public @This @NonNull Builder<C> synchronizeExecution(boolean var1);

        public @NonNull ExecutionCoordinator<C> build();
    }
}

