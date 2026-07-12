/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import java.util.function.BiFunction;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
@FunctionalInterface
public interface ComponentPreprocessor<C> {
    public static <C> @NonNull ComponentPreprocessor<C> wrap(@NonNull BiFunction<@NonNull CommandContext<C>, @NonNull CommandInput, @NonNull ArgumentParseResult<Boolean>> function) {
        return function::apply;
    }

    public @NonNull ArgumentParseResult<Boolean> preprocess(@NonNull CommandContext<C> var1, @NonNull CommandInput var2);
}

