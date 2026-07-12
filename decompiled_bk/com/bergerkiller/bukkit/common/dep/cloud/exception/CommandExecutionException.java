/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public class CommandExecutionException
extends IllegalArgumentException {
    private final CommandContext<?> commandContext;

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public CommandExecutionException(@NonNull Throwable cause) {
        this(cause, null);
    }

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public CommandExecutionException(@NonNull Throwable cause, @Nullable CommandContext<?> commandContext) {
        super(cause);
        this.commandContext = commandContext;
    }

    @API(status=API.Status.STABLE)
    public @Nullable CommandContext<?> context() {
        return this.commandContext;
    }
}

