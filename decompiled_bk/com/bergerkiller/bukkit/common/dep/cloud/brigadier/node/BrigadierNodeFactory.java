/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.node;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.permission.BrigadierPermissionChecker;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.brigadier.*"})
public interface BrigadierNodeFactory<C, S, N extends com.mojang.brigadier.tree.CommandNode<S>> {
    public @NonNull N createNode(@NonNull String var1, @NonNull CommandNode<C> var2, @NonNull com.mojang.brigadier.Command<S> var3, @NonNull BrigadierPermissionChecker<C> var4);

    public @NonNull N createNode(@NonNull String var1, @NonNull Command<C> var2, @NonNull com.mojang.brigadier.Command<S> var3, @NonNull BrigadierPermissionChecker<C> var4);

    public @NonNull N createNode(@NonNull String var1, @NonNull Command<C> var2, @NonNull com.mojang.brigadier.Command<S> var3);
}

