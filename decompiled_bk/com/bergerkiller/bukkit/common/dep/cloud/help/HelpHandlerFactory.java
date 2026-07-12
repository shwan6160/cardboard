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
import com.bergerkiller.bukkit.common.dep.cloud.help.StandardHelpHandlerFactory;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface HelpHandlerFactory<C> {
    public static <C> @NonNull HelpHandlerFactory<C> standard(@NonNull CommandManager<C> commandManager) {
        return new StandardHelpHandlerFactory<C>(commandManager);
    }

    public @NonNull HelpHandler<C> createHelpHandler(@NonNull CommandPredicate<C> var1);
}

