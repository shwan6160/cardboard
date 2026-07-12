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

import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinator;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinatorImpl;
import java.util.Objects;
import java.util.concurrent.Executor;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
final class ExecutionCoordinatorBuilderImpl<C>
implements ExecutionCoordinator.Builder<C> {
    private @Nullable Executor parsingExecutor;
    private @Nullable Executor suggestionsExecutor;
    private @Nullable Executor executionSchedulingExecutor;
    private boolean synchronizeExecution = false;

    ExecutionCoordinatorBuilderImpl() {
    }

    @Override
    public @NonNull ExecutionCoordinator.Builder<C> parsingExecutor(@NonNull Executor executor) {
        Objects.requireNonNull(executor, "executor");
        this.parsingExecutor = executor;
        return this;
    }

    @Override
    public @NonNull ExecutionCoordinator.Builder<C> suggestionsExecutor(@NonNull Executor executor) {
        Objects.requireNonNull(executor, "executor");
        this.suggestionsExecutor = executor;
        return this;
    }

    @Override
    public @NonNull ExecutionCoordinator.Builder<C> executionSchedulingExecutor(@NonNull Executor executor) {
        Objects.requireNonNull(executor, "executor");
        this.executionSchedulingExecutor = executor;
        return this;
    }

    @Override
    public @NonNull ExecutionCoordinator.Builder<C> synchronizeExecution(boolean synchronizeExecution) {
        this.synchronizeExecution = synchronizeExecution;
        return this;
    }

    @Override
    public @NonNull ExecutionCoordinator<C> build() {
        return new ExecutionCoordinatorImpl(this.parsingExecutor, this.suggestionsExecutor, this.executionSchedulingExecutor, this.synchronizeExecution);
    }
}

