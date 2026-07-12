/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParsingContext;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface AggregateResultMapper<C, O> {
    public @NonNull CompletableFuture<ArgumentParseResult<O>> map(@NonNull CommandContext<C> var1, @NonNull AggregateParsingContext<C> var2);

    @API(status=API.Status.STABLE)
    public static interface DirectSuccessMapper<C, O>
    extends AggregateResultMapper<C, O> {
        public @NonNull O mapSuccess(@NonNull CommandContext<C> var1, @NonNull AggregateParsingContext<C> var2);

        @Override
        default public @NonNull CompletableFuture<ArgumentParseResult<O>> map(@NonNull CommandContext<C> commandContext, @NonNull AggregateParsingContext<C> context) {
            return ArgumentParseResult.successFuture(this.mapSuccess(commandContext, context));
        }
    }
}

