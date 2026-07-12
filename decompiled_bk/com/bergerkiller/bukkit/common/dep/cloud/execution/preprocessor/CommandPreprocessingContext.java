/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessingContextImpl;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface CommandPreprocessingContext<C> {
    public static <C> @NonNull CommandPreprocessingContext<C> of(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        return CommandPreprocessingContextImpl.of(commandContext, commandInput);
    }

    public @NonNull CommandContext<@NonNull C> commandContext();

    public @NonNull CommandInput commandInput();
}

