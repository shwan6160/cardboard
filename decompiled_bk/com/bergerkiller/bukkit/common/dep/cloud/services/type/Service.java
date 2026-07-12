/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.services.type;

import com.bergerkiller.bukkit.common.dep.cloud.services.ExecutionOrder;
import com.bergerkiller.bukkit.common.dep.cloud.services.PipelineException;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@FunctionalInterface
public interface Service<Context, Result>
extends Function<Context, Result> {
    public @Nullable Result handle(@NonNull Context var1) throws Exception;

    @Override
    default public @Nullable Result apply(@NonNull Context context) {
        try {
            return this.handle(context);
        }
        catch (Exception exception) {
            throw new PipelineException(exception);
        }
    }

    default public @Nullable ExecutionOrder order() {
        return null;
    }
}

