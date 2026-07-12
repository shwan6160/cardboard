/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.execution;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandExecutionHandler;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
final class NullCommandExecutionHandler<C>
implements CommandExecutionHandler<C> {
    static final CommandExecutionHandler<?> INSTANCE = new NullCommandExecutionHandler();

    NullCommandExecutionHandler() {
    }

    @Override
    public void execute(@NonNull CommandContext<C> commandContext) {
    }
}

