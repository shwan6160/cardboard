/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.help;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.help.CommandPredicate;
import com.bergerkiller.bukkit.common.dep.cloud.help.HelpHandler;
import com.bergerkiller.bukkit.common.dep.cloud.help.HelpHandlerFactory;
import com.bergerkiller.bukkit.common.dep.cloud.help.StandardHelpHandler;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
final class StandardHelpHandlerFactory<C>
implements HelpHandlerFactory<C> {
    private final CommandManager<C> commandManager;

    StandardHelpHandlerFactory(@NonNull CommandManager<C> commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public @NonNull HelpHandler<C> createHelpHandler(@NonNull CommandPredicate<C> filter) {
        return new StandardHelpHandler<C>(this.commandManager, filter);
    }
}

