/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bean;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.CommandFactory;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.bean.CommandProperties;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandExecutionHandler;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta;
import java.util.Collections;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public abstract class CommandBean<C>
implements CommandExecutionHandler<C>,
CommandFactory<C> {
    protected CommandBean() {
    }

    @Override
    public @NonNull List<@NonNull Command<? extends C>> createCommands(@NonNull CommandManager<C> commandManager) {
        Command.Builder builder = commandManager.commandBuilder(this.properties().name(), this.properties().aliases(), this.meta()).handler(this);
        return Collections.singletonList(this.configure(builder).build());
    }

    protected @NonNull CommandMeta meta() {
        return CommandMeta.builder().build();
    }

    protected abstract @NonNull CommandProperties properties();

    protected abstract @NonNull Command.Builder<? extends C> configure(@NonNull Command.Builder<C> var1);

    @Override
    public void execute(@NonNull CommandContext<C> commandContext) {
    }
}

