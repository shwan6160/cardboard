/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor.CommandPostprocessingContextImpl;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface CommandPostprocessingContext<C> {
    public static <C> @NonNull CommandPostprocessingContext<C> of(@NonNull CommandContext<C> commandContext, @NonNull Command<C> command) {
        return CommandPostprocessingContextImpl.of(commandContext, command);
    }

    public @NonNull CommandContext<@NonNull C> commandContext();

    public @NonNull Command<@NonNull C> command();
}

