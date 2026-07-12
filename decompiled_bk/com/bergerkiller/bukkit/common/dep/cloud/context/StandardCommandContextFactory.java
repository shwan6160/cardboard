/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.context;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContextFactory;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class StandardCommandContextFactory<C>
implements CommandContextFactory<C> {
    private final CommandManager<C> commandManager;

    public StandardCommandContextFactory(@NonNull CommandManager<C> commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public @NonNull CommandContext<C> create(boolean suggestions, @NonNull C sender) {
        return new CommandContext<C>(suggestions, sender, this.commandManager);
    }
}

