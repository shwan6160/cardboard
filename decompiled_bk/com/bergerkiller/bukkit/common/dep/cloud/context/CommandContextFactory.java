/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.context;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface CommandContextFactory<C> {
    @API(status=API.Status.STABLE)
    public @NonNull CommandContext<C> create(boolean var1, @NonNull C var2);
}

