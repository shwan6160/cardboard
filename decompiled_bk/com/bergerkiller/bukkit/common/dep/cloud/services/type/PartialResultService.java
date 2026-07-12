/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.services.type;

import com.bergerkiller.bukkit.common.dep.cloud.services.ChunkedRequestContext;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.Service;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface PartialResultService<Context, Result, Chunked extends ChunkedRequestContext<Context, Result>>
extends Service<Chunked, Map<Context, Result>> {
    @Override
    default public @Nullable Map<@NonNull Context, @NonNull Result> handle(@NonNull Chunked context) {
        if (!((ChunkedRequestContext)context).isCompleted()) {
            this.handleRequests(((ChunkedRequestContext)context).remaining()).forEach((arg_0, arg_1) -> context.storeResult(arg_0, arg_1));
        }
        if (((ChunkedRequestContext)context).isCompleted()) {
            return ((ChunkedRequestContext)context).availableResults();
        }
        return null;
    }

    public @NonNull Map<@NonNull Context, @NonNull Result> handleRequests(@NonNull List<Context> var1);
}

