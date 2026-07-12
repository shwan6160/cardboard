/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.permission;

import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.PermissionResultImpl;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface PermissionResult {
    public boolean allowed();

    default public boolean denied() {
        return !this.allowed();
    }

    public @NonNull Permission permission();

    public static @NonNull PermissionResult of(boolean result, @NonNull Permission permission) {
        return PermissionResultImpl.of(result, permission);
    }

    public static @NonNull PermissionResult allowed(@NonNull Permission permission) {
        return PermissionResultImpl.of(true, permission);
    }

    public static @NonNull PermissionResult denied(@NonNull Permission permission) {
        return PermissionResultImpl.of(false, permission);
    }
}

