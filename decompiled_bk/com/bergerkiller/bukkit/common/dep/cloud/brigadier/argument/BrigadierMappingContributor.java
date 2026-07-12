/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;

public interface BrigadierMappingContributor {
    public <C, S> void contribute(CommandManager<C> var1, CloudBrigadierManager<C, S> var2);
}

