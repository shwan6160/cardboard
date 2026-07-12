/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.services;

import com.bergerkiller.bukkit.common.dep.cloud.services.ServicePipeline;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ServicePipelineBuilder {
    private Executor executor = Executors.newSingleThreadExecutor();

    ServicePipelineBuilder() {
    }

    public @NonNull ServicePipeline build() {
        return new ServicePipeline(this.executor);
    }

    public @NonNull ServicePipelineBuilder withExecutor(@NonNull Executor executor) {
        this.executor = Objects.requireNonNull(executor, "Executor may not be null");
        return this;
    }
}

