/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.permission;

import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.INTERNAL, since="2.0.0")
public interface BrigadierPermissionChecker<C> {
    public boolean hasPermission(@NonNull C var1, @NonNull Permission var2);
}

