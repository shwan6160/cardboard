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

import com.bergerkiller.bukkit.common.dep.cloud.permission.AndPermission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.OrPermission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.PermissionImpl;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface Permission {
    public static final Permission EMPTY = Permission.permission("");

    public static @NonNull Permission permission(@NonNull String permission) {
        return PermissionImpl.of(permission);
    }

    public static @NonNull Permission of(@NonNull String permission) {
        return Permission.permission(permission);
    }

    public static @NonNull Permission empty() {
        return EMPTY;
    }

    public static @NonNull Permission allOf(@NonNull Collection<@NonNull Permission> permissions) {
        HashSet<Permission> objects = new HashSet<Permission>();
        for (Permission permission : permissions) {
            if (permission instanceof AndPermission) {
                objects.addAll(permission.permissions());
                continue;
            }
            objects.add(permission);
        }
        return new AndPermission(objects);
    }

    public static @NonNull Permission allOf(Permission ... permissions) {
        return Permission.allOf(Arrays.asList(permissions));
    }

    public static @NonNull Permission anyOf(@NonNull Collection<@NonNull Permission> permissions) {
        HashSet<Permission> objects = new HashSet<Permission>();
        for (Permission permission : permissions) {
            if (permission instanceof OrPermission) {
                objects.addAll(permission.permissions());
                continue;
            }
            objects.add(permission);
        }
        return new OrPermission(objects);
    }

    public static @NonNull Permission anyOf(Permission ... permissions) {
        return Permission.anyOf(Arrays.asList(permissions));
    }

    default public @NonNull Collection<@NonNull Permission> permissions() {
        return Collections.singleton(this);
    }

    public @NonNull String permissionString();

    @API(status=API.Status.STABLE)
    default public boolean isEmpty() {
        return this.permissionString().isEmpty();
    }

    @API(status=API.Status.STABLE)
    default public @NonNull Permission or(@NonNull Permission other) {
        Objects.requireNonNull(other, "other");
        HashSet<Permission> permission = new HashSet<Permission>(2);
        permission.add(this);
        permission.add(other);
        return Permission.anyOf(permission);
    }

    @API(status=API.Status.STABLE)
    default public @NonNull Permission or(Permission ... other) {
        Objects.requireNonNull(other, "other");
        HashSet<Permission> permission = new HashSet<Permission>(other.length + 1);
        permission.add(this);
        permission.addAll(Arrays.asList(other));
        return Permission.anyOf(permission);
    }

    @API(status=API.Status.STABLE)
    default public @NonNull Permission and(@NonNull Permission other) {
        Objects.requireNonNull(other, "other");
        HashSet<Permission> permission = new HashSet<Permission>(2);
        permission.add(this);
        permission.add(other);
        return Permission.allOf(permission);
    }

    @API(status=API.Status.STABLE)
    default public @NonNull Permission and(Permission ... other) {
        Objects.requireNonNull(other, "other");
        HashSet<Permission> permission = new HashSet<Permission>(other.length + 1);
        permission.add(this);
        permission.addAll(Arrays.asList(other));
        return Permission.allOf(permission);
    }
}

