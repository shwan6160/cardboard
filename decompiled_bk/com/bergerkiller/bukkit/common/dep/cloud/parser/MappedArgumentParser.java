/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface MappedArgumentParser<C, I, O>
extends ArgumentParser<C, O> {
    public @NonNull ArgumentParser<C, I> baseParser();

    @FunctionalInterface
    public static interface Mapper<C, I, O> {
        public @NonNull CompletableFuture<ArgumentParseResult<O>> map(@NonNull CommandContext<C> var1, @NonNull ArgumentParseResult<I> var2);
    }
}

